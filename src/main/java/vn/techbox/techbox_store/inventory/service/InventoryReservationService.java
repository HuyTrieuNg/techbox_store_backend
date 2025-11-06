package vn.techbox.techbox_store.inventory.service;

import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.dao.OptimisticLockingFailureException;
import org.springframework.retry.annotation.Backoff;
import org.springframework.retry.annotation.Retryable;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import vn.techbox.techbox_store.inventory.model.InventoryReservation;
import vn.techbox.techbox_store.inventory.model.ReservationStatus;
import vn.techbox.techbox_store.inventory.repository.InventoryReservationRepository;
import vn.techbox.techbox_store.product.model.ProductVariation;
import vn.techbox.techbox_store.product.repository.ProductVariationRepository;
import vn.techbox.techbox_store.order.repository.OrderRepository;
import vn.techbox.techbox_store.order.model.OrderStatus;
import vn.techbox.techbox_store.payment.model.PaymentMethod;

import java.time.LocalDateTime;
import java.util.*;

@Service
@RequiredArgsConstructor
@Slf4j
public class InventoryReservationService {

    private final InventoryReservationRepository inventoryReservationRepository;
    private final ProductVariationRepository productVariationRepository;
    private final OrderRepository orderRepository;

    @Transactional
    @Retryable(retryFor = {OptimisticLockingFailureException.class}, maxAttempts = 3, backoff = @Backoff(delay = 100))
    public void reserveInventory(Integer orderId, Integer productVariationId, Integer quantity) {
        log.info("Reserving inventory for order: {}, productVariation: {}, quantity: {}",
                orderId, productVariationId, quantity);

        ProductVariation productVariation = productVariationRepository.findById(productVariationId)
                .orElseThrow(() -> new IllegalArgumentException("Product variation not found: " + productVariationId));

        // Check if there's enough available stock
        Integer availableQuantity = productVariation.getAvailableQuantity();
        if (availableQuantity < quantity) {
            throw new IllegalArgumentException(
                String.format("Insufficient stock. Available: %d, Requested: %d", availableQuantity, quantity));
        }

        // Update reserved quantity in product variation with optimistic locking
        productVariation.setReservedQuantity(productVariation.getReservedQuantity() + quantity);
        productVariationRepository.save(productVariation);

        // Create inventory reservation record
        InventoryReservation reservation = InventoryReservation.builder()
                .orderId(orderId)
                .productVariationId(productVariationId)
                .quantity(quantity)
                .status(ReservationStatus.RESERVED)
                .reservedAt(LocalDateTime.now())
                .expiresAt(LocalDateTime.now().plusMinutes(15))
                .build();

        inventoryReservationRepository.save(reservation);
    }

    @Transactional
    public void confirmReservations(Integer orderId) {
        log.info("Confirming inventory reservations for order: {}", orderId);

        List<InventoryReservation> reservations = inventoryReservationRepository
                .findByOrderIdAndStatus(orderId, ReservationStatus.RESERVED);

        for (InventoryReservation reservation : reservations) {
            // Confirm the reservation
            reservation.confirm();

            // Reduce actual stock and remove from reserved
            ProductVariation productVariation = productVariationRepository.findById(reservation.getProductVariationId())
                    .orElseThrow(() -> new IllegalStateException("Product variation not found: " + reservation.getProductVariationId()));

            productVariation.setStockQuantity(productVariation.getStockQuantity() - reservation.getQuantity());
            productVariation.setReservedQuantity(productVariation.getReservedQuantity() - reservation.getQuantity());

            productVariationRepository.save(productVariation);
            inventoryReservationRepository.save(reservation);
        }
    }

    @Transactional
    public void releaseReservations(Integer orderId) {
        log.info("Releasing inventory reservations for order: {}", orderId);

        List<InventoryReservation> reservations = inventoryReservationRepository
                .findByOrderIdAndStatus(orderId, ReservationStatus.RESERVED);

        for (InventoryReservation reservation : reservations) {
            // Release the reservation
            reservation.release();

            // Remove from reserved quantity
            ProductVariation productVariation = productVariationRepository.findById(reservation.getProductVariationId())
                    .orElseThrow(() -> new IllegalStateException("Product variation not found: " + reservation.getProductVariationId()));

            productVariation.setReservedQuantity(productVariation.getReservedQuantity() - reservation.getQuantity());

            productVariationRepository.save(productVariation);
            inventoryReservationRepository.save(reservation);
        }

        // Also consider auto-cancel order if no more active reservations
        autoCancelOrderIfPendingVnpayAndNoActiveReservations(orderId);
    }

    @Transactional
    public void cleanUpExpiredReservations() {
        log.info("Cleaning up expired inventory reservations");

        List<InventoryReservation> expiredReservations = inventoryReservationRepository
                .findExpiredReservations(ReservationStatus.RESERVED, LocalDateTime.now());

        Set<Integer> impactedOrderIds = new HashSet<>();

        for (InventoryReservation reservation : expiredReservations) {
            reservation.setStatus(ReservationStatus.EXPIRED);

            // Remove from reserved quantity
            ProductVariation productVariation = productVariationRepository.findById(reservation.getProductVariationId())
                    .orElse(null);

            if (productVariation != null) {
                productVariation.setReservedQuantity(productVariation.getReservedQuantity() - reservation.getQuantity());
                productVariationRepository.save(productVariation);
            }

            inventoryReservationRepository.save(reservation);
            impactedOrderIds.add(reservation.getOrderId());
        }

        // After expiring reservations, cancel related orders if appropriate
        for (Integer orderIdInt : impactedOrderIds) {
            if (orderIdInt == null) continue;
            autoCancelOrderIfPendingVnpayAndNoActiveReservations(orderIdInt);
        }

        log.info("Cleaned up {} expired inventory reservations", expiredReservations.size());
    }

    @Transactional
    public int purgeOldReleasedAndExpiredReservations(LocalDateTime cutoff) {
        log.info("Purging old inventory reservations before {}", cutoff);
        int deleted = inventoryReservationRepository.deleteReleasedOrExpiredBefore(cutoff);
        log.info("Purged {} old inventory reservations", deleted);
        return deleted;
    }

    private void autoCancelOrderIfPendingVnpayAndNoActiveReservations(Integer orderId) {
        try {
            // If there are still active reservations for this order, skip cancelling
            List<InventoryReservation> stillReserved = inventoryReservationRepository
                    .findByOrderIdAndStatus(orderId, ReservationStatus.RESERVED);
            if (stillReserved != null && !stillReserved.isEmpty()) {
                return;
            }

            orderRepository.findById(orderId.longValue()).ifPresent(order -> {
                try {
                    boolean isPendingOrder = order.getStatus() == OrderStatus.PENDING;
                    boolean isVnpay = order.getPaymentMethod() == PaymentMethod.VNPAY;
                    boolean paymentPending = "PENDING".equalsIgnoreCase(order.getPaymentStatus());
                    if (isPendingOrder && isVnpay && paymentPending) {
                        order.setStatus(OrderStatus.CANCELLED);
                        order.setUpdatedAt(LocalDateTime.now());
                        orderRepository.save(order);
                        log.info("Order {} cancelled due to no active reservations and unpaid VNPAY.", order.getId());
                    }
                } catch (Exception ex) {
                    log.warn("Failed to auto-cancel order {} after reservation update: {}", orderId, ex.getMessage());
                }
            });
        } catch (Exception e) {
            log.warn("Auto-cancel evaluation failed for order {}: {}", orderId, e.getMessage());
        }
    }
}
