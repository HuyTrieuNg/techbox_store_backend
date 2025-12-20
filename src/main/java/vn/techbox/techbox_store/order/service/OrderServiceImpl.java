package vn.techbox.techbox_store.order.service;

import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Pageable;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import vn.techbox.techbox_store.order.dto.*;
import vn.techbox.techbox_store.order.exception.OrderException;
import vn.techbox.techbox_store.order.model.*;
import vn.techbox.techbox_store.payment.model.Payment;
import vn.techbox.techbox_store.payment.model.PaymentMethod;
import vn.techbox.techbox_store.payment.model.PaymentStatus;
import vn.techbox.techbox_store.payment.repository.PaymentRepository;
import vn.techbox.techbox_store.payment.service.factory.PaymentServiceFactory;
import vn.techbox.techbox_store.order.repository.OrderRepository;
import vn.techbox.techbox_store.order.repository.OrderShippingInfoRepository;
import vn.techbox.techbox_store.product.model.ProductVariation;
import vn.techbox.techbox_store.product.repository.ProductVariationRepository;
import vn.techbox.techbox_store.user.model.User;
import vn.techbox.techbox_store.user.repository.UserRepository;
import vn.techbox.techbox_store.inventory.service.InventoryReservationService;
import vn.techbox.techbox_store.voucher.service.VoucherReservationService;

import java.math.BigDecimal;
import java.time.LocalDateTime;
import java.util.ArrayList;
import java.util.List;
import java.util.UUID;

@Service
@RequiredArgsConstructor
@Slf4j
@Transactional
public class OrderServiceImpl implements OrderService {

    private final OrderRepository orderRepository;
    private final UserRepository userRepository;
    private final ProductVariationRepository productVariationRepository;
    private final PaymentServiceFactory paymentServiceFactory;
    private final OrderValidationService orderValidationService;
    private final OrderCalculationService orderCalculationService;
    private final OrderMappingService orderMappingService;
    private final OrderShippingInfoRepository orderShippingInfoRepository;
    private final PaymentRepository payment;
    private final InventoryReservationService inventoryReservationService;
    private final VoucherReservationService voucherReservationService;

    @Override
    @Transactional
    public OrderResponse createOrder(CreateOrderRequest request, Integer userId) {
        log.info("Creating order for user: {}", userId);

        User user = userRepository.findById(userId)
                .orElseThrow(() -> new OrderException("User not found"));

        orderValidationService.validateCreateOrderRequest(request, userId);

        OrderShippingInfo shippingInfo = OrderShippingInfo.builder()
                .shippingName(request.getShippingName())
                .shippingPhone(request.getShippingPhone())
                .shippingEmail(request.getShippingEmail())
                .shippingAddress(request.getShippingAddress())
                .shippingWard(request.getShippingWard())
                .shippingDistrict(request.getShippingDistrict())
                .shippingCity(request.getShippingCity())
                .shippingPostalCode(request.getShippingPostalCode())
                .shippingCountry(request.getShippingCountry())
                .shippingMethod(request.getShippingMethod())
                .estimatedDeliveryDate(java.time.LocalDate.now().plusDays(3))
                .deliveryInstructions(request.getDeliveryInstructions())
                .build();
        OrderShippingInfo savedShippingInfo = orderShippingInfoRepository.save(shippingInfo);

        // Initialize payment entity
        Payment payment = paymentServiceFactory.getPaymentService(request.getPaymentMethod())
                .initiatePayment(null);
        payment.setVoucherCode(request.getVoucherCode());

        List<OrderItem> orderItems = new ArrayList<>();
        for (CreateOrderRequest.OrderItemRequest itemRequest : request.getOrderItems()) {
            ProductVariation productVariation = productVariationRepository.findById(itemRequest.getProductVariationId().intValue())
                    .orElseThrow(() -> new OrderException("Product variation not found"));

            OrderItem orderItem = OrderItem.builder()
                    .productVariation(productVariation)
                    .productName(productVariation.getProduct().getName())
                    .productVariationName(productVariation.getVariationName())
                    .quantity(itemRequest.getQuantity())
                    .unitPrice(productVariation.getPrice())
                    .totalPrice(productVariation.getPrice().multiply(BigDecimal.valueOf(itemRequest.getQuantity())))
                    .discountAmount(BigDecimal.ZERO)
                    .build();

            orderItems.add(orderItem);
        }

        orderCalculationService.calculateOrderAmounts(null, payment, orderItems, request.getVoucherCode());
        Payment savedPaymentInfo = this.payment.save(payment);

        Order order = Order.builder()
                .orderCode(generateOrderCode())
                .user(user)
                .status(OrderStatus.PENDING)
                .note(request.getNote())
                .shippingInfo(savedShippingInfo)
                .paymentInfo(savedPaymentInfo)
                .createdAt(LocalDateTime.now())
                .updatedAt(LocalDateTime.now())
                .build();

        orderItems.forEach(item -> item.setOrder(order));
        order.setOrderItems(orderItems);

        Order savedOrder = orderRepository.save(order);

        // If order is COD, immediately transition to CONFIRMED and create permanent reservations
        if (request.getPaymentMethod() == PaymentMethod.COD) {
            try {
                for (OrderItem orderItem : savedOrder.getOrderItems()) {
                    inventoryReservationService.reserveInventoryPermanent(
                            savedOrder.getId().intValue(),
                            orderItem.getProductVariation().getId(),
                            orderItem.getQuantity()
                    );
                }
                if (savedOrder.getVoucherCode() != null && !savedOrder.getVoucherCode().trim().isEmpty()) {
                    voucherReservationService.reserveVoucherByCode(
                            savedOrder.getId().intValue(),
                            savedOrder.getVoucherCode(),
                            savedOrder.getUserId()
                    );
                }

                // Update order status to CONFIRMED for COD orders
                savedOrder.setStatus(OrderStatus.CONFIRMED);
                orderRepository.save(savedOrder);
                log.info("COD order {} automatically confirmed and permanent reservations created", savedOrder.getId());
            } catch (Exception e) {
                log.error("Failed to create permanent reservations for COD order {}: {}", savedOrder.getId(), e.getMessage(), e);
                throw new OrderException("Failed to reserve inventory/voucher for COD order: " + e.getMessage());
            }
        }

        // Create reservations for VNPAY orders (temporary, expires in 15 min)
        // Note: Permanent reservations will be created when status changes to CONFIRMED
        if (request.getPaymentMethod() == PaymentMethod.VNPAY) {
            try {
                for (OrderItem orderItem : savedOrder.getOrderItems()) {
                    // Reserve inventory for each order item 15 minutes
                    inventoryReservationService.reserveInventory(
                            savedOrder.getId().intValue(),
                            orderItem.getProductVariation().getId(),
                            orderItem.getQuantity()
                    );
                }
                if (savedOrder.getVoucherCode() != null && !savedOrder.getVoucherCode().trim().isEmpty()) {
                    voucherReservationService.reserveVoucherByCode(
                            savedOrder.getId().intValue(),
                            savedOrder.getVoucherCode(),
                            savedOrder.getUserId()
                    );
                }
            } catch (Exception e) {
                log.error("Failed to create reservations for order {}: {}", savedOrder.getId(), e.getMessage(), e);
                throw new OrderException("Failed to reserve inventory/voucher: " + e.getMessage());
            }
        }

        PaymentResponse paymentResponse = null;
        try {
            PaymentRequest paymentRequest = new PaymentRequest();
            paymentRequest.setOrderId(savedOrder.getId());
            paymentRequest.setPaymentMethod(request.getPaymentMethod());
            paymentRequest.setAmount(savedPaymentInfo.getFinalAmount());
            paymentRequest.setReturnUrl(request.getReturnUrl());
            paymentRequest.setCancelUrl(null);

            var processor = paymentServiceFactory.getPaymentService(request.getPaymentMethod());
            paymentResponse = processor.generatePaymentUrl(paymentRequest);

            if (paymentResponse != null && ("PENDING".equals(paymentResponse.getStatus()) || "SUCCESS".equals(paymentResponse.getStatus()))) {
                savedOrder.getPaymentInfo().setPaymentTransactionId(paymentResponse.getTransactionId());
                orderRepository.save(savedOrder);
            }
        } catch (Exception ex) {
            log.error("Auto process payment failed for order {}: {}", savedOrder.getId(), ex.getMessage(), ex);
        }

        log.info("Order created successfully with code: {}", savedOrder.getOrderCode());

        OrderResponse response = orderMappingService.toOrderResponse(savedOrder);
        if (paymentResponse != null) {
            response.setPaymentUrl(paymentResponse.getPaymentUrl());
        }
        return response;
    }

    @Override
    @Transactional(readOnly = true)
    public OrderResponse getOrderById(Long orderId, Integer userId) {
        Order order = orderRepository.findById(orderId)
                .orElseThrow(() -> new OrderException("Order not found"));
        
        if (!order.getUser().getId().equals(userId)) {
            throw new OrderException("Access denied");
        }

        return orderMappingService.toOrderResponse(order);
    }

    @Override
    @Transactional(readOnly = true)
    public OrderResponse getOrderByCode(String orderCode, Integer userId) {
        Order order = orderRepository.findByOrderCode(orderCode)
                .orElseThrow(() -> new OrderException("Order not found"));

        if (!order.getUser().getId().equals(userId)) {
            throw new OrderException("Access denied");
        }

        return orderMappingService.toOrderResponse(order);
    }

    @Override
    @Transactional(readOnly = true)
    public OrderResponse getOrderByIdForAdmin(Long orderId) {
        Order order = orderRepository.findById(orderId)
                .orElseThrow(() -> new OrderException("Order not found"));
        return orderMappingService.toOrderResponse(order);
    }

    @Override
    @Transactional(readOnly = true)
    public OrderResponse getOrderByCodeForAdmin(String orderCode) {
        Order order = orderRepository.findByOrderCode(orderCode)
                .orElseThrow(() -> new OrderException("Order not found"));
        return orderMappingService.toOrderResponse(order);
    }

    @Override
    @Transactional(readOnly = true)
    public Page<OrderResponse> getUserOrders(Integer userId, Pageable pageable) {
        Page<Order> orders = orderRepository.findByUser_IdOrderByCreatedAtDesc(userId, pageable);
        return orders.map(orderMappingService::toOrderResponse);
    }

    @Override
    @Transactional(readOnly = true)
    public Page<OrderResponse> getUserOrdersByStatus(Integer userId, OrderStatus status, Pageable pageable) {
        Page<Order> orders = orderRepository.findByUserIdAndStatusOrderByCreatedAtDesc(userId, status, pageable);
        return orders.map(orderMappingService::toOrderResponse);
    }

    @Override
    @Transactional(readOnly = true)
    public Page<OrderResponse> getAllOrders(Pageable pageable) {
        // For admin 'get all' endpoint, exclude PENDING orders by default
        Page<Order> orders = orderRepository.findByStatusNotOrderByCreatedAtDesc(OrderStatus.PENDING, pageable);
        return orders.map(orderMappingService::toOrderResponse);
    }

    @Override
    @Transactional(readOnly = true)
    public Page<OrderResponse> getAllOrdersByStatus(OrderStatus status, Pageable pageable) {
        Page<Order> orders = orderRepository.findByStatusOrderByCreatedAtDesc(status, pageable);
        return orders.map(orderMappingService::toOrderResponse);
    }

    @Override
    @Transactional(readOnly = true)
    public Page<OrderResponse> getOrdersByUserId(Integer userId, Pageable pageable) {
        Page<Order> orders = orderRepository.findByUser_IdOrderByCreatedAtDesc(userId, pageable);
        return orders.map(orderMappingService::toOrderResponse);
    }

    @Override
    @Transactional(readOnly = true)
    public Page<OrderResponse> getOrdersByUserIdAndStatus(Integer userId, OrderStatus status, Pageable pageable) {
        Page<Order> orders = orderRepository.findByUserIdAndStatusOrderByCreatedAtDesc(userId, status, pageable);
        return orders.map(orderMappingService::toOrderResponse);
    }

    @Override
    public OrderResponse updateOrderStatus(Long orderId, OrderStatus status) {
        Order order = orderRepository.findById(orderId)
                .orElseThrow(() -> new OrderException("Order not found"));

        orderValidationService.validateStatusTransition(order.getStatus(), status);

        if (status == OrderStatus.CONFIRMED && order.getPaymentMethod() == PaymentMethod.COD) {
            // Create permanent reservations with null expiry for COD orders
            try {
                for (OrderItem orderItem : order.getOrderItems()) {
                    inventoryReservationService.reserveInventoryPermanent(
                            order.getId().intValue(),
                            orderItem.getProductVariation().getId(),
                            orderItem.getQuantity()
                    );
                }
                if (order.getVoucherCode() != null && !order.getVoucherCode().trim().isEmpty()) {
                    voucherReservationService.reserveVoucherByCode(
                            order.getId().intValue(),
                            order.getVoucherCode(),
                            order.getUserId()
                    );
                }
                log.info("Permanent reservations created for COD order {}", orderId);
            } catch (Exception e) {
                log.error("Failed to create permanent reservations for COD order {}: {}", orderId, e.getMessage(), e);
                throw new OrderException("Failed to create reservations: " + e.getMessage());
            }
        }

        // If status changes to CONFIRMED and VNPAY, set reservations expiry to null
        if (status == OrderStatus.CONFIRMED && order.getPaymentMethod() == PaymentMethod.VNPAY) {
            try {
                inventoryReservationService.setReservationsExpiryNull(orderId.intValue());
                voucherReservationService.setReservationsExpiryNull(orderId.intValue());
                log.info("Reservations expiry set to null for VNPAY order {}", orderId);
            } catch (Exception e) {
                log.error("Failed to set reservations expiry for VNPAY order {}: {}", orderId, e.getMessage(), e);
                throw new OrderException("Failed to update reservations: " + e.getMessage());
            }
        }

        // If status changes to PROCESSING, confirm reservations (deduct stock) and create stock export
        if (status == OrderStatus.PROCESSING) {
            try {
                inventoryReservationService.confirmReservations(orderId.intValue());
                voucherReservationService.confirmReservations(orderId.intValue());
                // Stock export is created in confirmReservations method of InventoryReservationService
                log.info("Reservations confirmed, stock deducted, and stock export created for order {}", orderId);
            } catch (Exception e) {
                log.error("Failed to confirm reservations for order {}: {}", orderId, e.getMessage(), e);
                throw new OrderException("Failed to deduct stock from reservations: " + e.getMessage());
            }
        }

        if (status == OrderStatus.CANCELLED) {
            try {
                inventoryReservationService.releaseReservations(orderId.intValue());
                if (order.getVoucherCode() != null && !order.getVoucherCode().trim().isEmpty()) {
                    voucherReservationService.releaseReservations(orderId.intValue());
                }
                log.info("Reservations released for cancelled order: {}", orderId);
            } catch (Exception e) {
                log.error("Failed to release reservations for cancelled order {}: {}", orderId, e.getMessage(), e);
            }
        }

        // Nếu trạng thái chuyển sang DELIVERED, lưu ngày giao thực tế
        if (status == OrderStatus.DELIVERED) {
            if (order.getShippingInfo() != null) {
                order.getShippingInfo().setActualDeliveryDate(LocalDateTime.now());
                orderShippingInfoRepository.save(order.getShippingInfo());
                log.info("Order {} delivered. Actual delivery date saved: {}", orderId, LocalDateTime.now());
            }

            // Nếu là COD thì tự động chuyển payment status thành PAID
            if (order.getPaymentMethod() == PaymentMethod.COD) {
                Payment paymentInfo = order.getPaymentInfo();
                if (paymentInfo != null) {
                    paymentInfo.setPaymentStatus(PaymentStatus.PAID);
                    paymentInfo.setPaymentCompletedAt(LocalDateTime.now());
                    payment.save(paymentInfo);
                    log.info("Order {} is COD and delivered. Payment status updated to PAID.", orderId);
                }
            }
        }

        order.setStatus(status);
        Order savedOrder = orderRepository.save(order);
        log.info("Order {} status updated to: {}", orderId, status);
        return orderMappingService.toOrderResponse(savedOrder);
    }

    @Override
    public OrderResponse cancelOrder(Long orderId, Integer userId) {
        Order order = orderRepository.findById(orderId)
                .orElseThrow(() -> new OrderException("Order not found"));

        if (!order.getUser().getId().equals(userId)) {
            throw new OrderException("Access denied");
        }

        if (order.getStatus() != OrderStatus.PENDING && order.getStatus() != OrderStatus.CONFIRMED) {
            throw new OrderException("Cannot cancel order with status: " + order.getStatus());
        }

        // Release reservations for both COD and VNPAY orders
        try {
            inventoryReservationService.releaseReservations(order.getId().intValue());
            if (order.getVoucherCode() != null && !order.getVoucherCode().trim().isEmpty()) {
                voucherReservationService.releaseReservations(order.getId().intValue());
            }
            log.info("Reservations released for cancelled order: {}", orderId);
        } catch (Exception e) {
            log.warn("Failed to release reservations for cancelled order {}: {}", orderId, e.getMessage());
        }

        order.setStatus(OrderStatus.CANCELLED);
        order.setUpdatedAt(LocalDateTime.now());

        Order savedOrder = orderRepository.save(order);

        log.info("Order {} cancelled successfully", orderId);

        return orderMappingService.toOrderResponse(savedOrder);
    }

    @Override
    @Transactional(readOnly = true)
    public List<String> getRecentProductSpus(Integer userId, int k) {
        List<Order> recentOrders = orderRepository.findTopOrdersByUserId(userId, PageRequest.of(0, 10)).getContent(); // Get top 10 recent orders
        List<String> spus = new ArrayList<>();
        for (Order order : recentOrders) {
            for (OrderItem item : order.getOrderItems()) {
                if (spus.size() >= k) break;
                String spu = item.getProductVariation().getProduct().getSpu();
                if (!spus.contains(spu)) {
                    spus.add(spu);
                }
            }
            if (spus.size() >= k) break;
        }
        return spus;
    }

    @Override
    @Transactional(readOnly = true)
    public Page<OrderResponse> searchOrdersByCode(String searchTerm, Pageable pageable) {
        Page<Order> orders = orderRepository.searchByOrderCode(searchTerm, pageable);
        return orders.map(orderMappingService::toOrderResponse);
    }

    private String generateOrderCode() {
        String prefix = "ORD";
        String timestamp = String.valueOf(System.currentTimeMillis());
        String random = UUID.randomUUID().toString().substring(0, 8).toUpperCase();
        return prefix + timestamp.substring(timestamp.length() - 6) + random;
    }
}
