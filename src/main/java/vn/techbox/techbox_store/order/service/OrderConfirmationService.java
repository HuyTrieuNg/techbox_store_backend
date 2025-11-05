package vn.techbox.techbox_store.order.service;

import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import vn.techbox.techbox_store.order.model.Order;
import vn.techbox.techbox_store.order.model.OrderItem;
import vn.techbox.techbox_store.order.model.OrderStatus;
import vn.techbox.techbox_store.order.repository.OrderRepository;
import vn.techbox.techbox_store.payment.model.PaymentMethod;
import vn.techbox.techbox_store.product.model.ProductVariation;
import vn.techbox.techbox_store.product.repository.ProductVariationRepository;
import vn.techbox.techbox_store.voucher.model.UserVoucher;
import vn.techbox.techbox_store.voucher.model.Voucher;
import vn.techbox.techbox_store.voucher.repository.UserVoucherRepository;
import vn.techbox.techbox_store.voucher.repository.VoucherRepository;

import java.time.LocalDateTime;

@Service
@RequiredArgsConstructor
@Slf4j
public class OrderConfirmationService {

    private final OrderRepository orderRepository;
    private final ProductVariationRepository productVariationRepository;
    private final UserVoucherRepository userVoucherRepository;
    private final VoucherRepository voucherRepository;

    @Transactional
    public Order confirmCodOrder(Integer orderId) {
        log.info("Confirming COD order: {}", orderId);

        Order order = orderRepository.findById(orderId.longValue())
                .orElseThrow(() -> new IllegalArgumentException("Order not found: " + orderId));

        if (order.getStatus() != OrderStatus.PENDING) {
            throw new IllegalStateException("Order is not in PENDING status: " + order.getStatus());
        }

        // For COD orders, reduce inventory directly when order is confirmed
        if (order.getPaymentInfo() != null &&
            order.getPaymentInfo().getPaymentMethod() == PaymentMethod.COD) {

            // Reduce inventory for each order item
            for (OrderItem orderItem : order.getOrderItems()) {
                Integer productVariationId = orderItem.getProductVariation().getId();
                ProductVariation productVariation = productVariationRepository.findById(productVariationId)
                        .orElseThrow(() -> new IllegalStateException("Product variation not found: " + productVariationId));

                // Check if there's enough stock
                if (productVariation.getAvailableQuantity() < orderItem.getQuantity()) {
                    throw new IllegalArgumentException(
                        String.format("Insufficient stock for product variation %d. Available: %d, Requested: %d",
                                    productVariationId,
                                    productVariation.getAvailableQuantity(),
                                    orderItem.getQuantity()));
                }

                // Reduce stock quantity directly
                productVariation.setStockQuantity(productVariation.getStockQuantity() - orderItem.getQuantity());
                productVariationRepository.save(productVariation);

                log.info("Reduced stock for product variation {}: {} units",
                        productVariationId, orderItem.getQuantity());
            }

            // Mark voucher as used if applicable
            if (order.getVoucherCode() != null && !order.getVoucherCode().trim().isEmpty()) {
                Voucher voucher = voucherRepository.findByCodeAndNotDeleted(order.getVoucherCode())
                        .orElseThrow(() -> new IllegalArgumentException("Voucher not found: " + order.getVoucherCode()));

                UserVoucher userVoucher = UserVoucher.builder()
                        .userId(order.getUserId())
                        .voucherCode(order.getVoucherCode())
                        .usedAt(LocalDateTime.now())
                        .orderId(orderId)
                        .build();

                userVoucherRepository.save(userVoucher);
                log.info("Marked voucher {} as used for user {}", order.getVoucherCode(), order.getUserId());
            }

            // Update order status
            order.setStatus(OrderStatus.CONFIRMED);
            Order saved = orderRepository.save(order);

            log.info("Successfully confirmed COD order: {}", orderId);
            return saved;
        } else {
            throw new IllegalStateException("Order confirmation method not supported for payment method: " +
                                          (order.getPaymentInfo() != null ? order.getPaymentInfo().getPaymentMethod() : "UNKNOWN"));
        }
    }
}
