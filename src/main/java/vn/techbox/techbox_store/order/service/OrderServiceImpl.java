package vn.techbox.techbox_store.order.service;

import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import vn.techbox.techbox_store.order.dto.*;
import vn.techbox.techbox_store.order.exception.OrderException;
import vn.techbox.techbox_store.order.model.*;
import vn.techbox.techbox_store.payment.model.Payment;
import vn.techbox.techbox_store.payment.repository.PaymentRepository;
import vn.techbox.techbox_store.payment.service.factory.PaymentServiceFactory;
import vn.techbox.techbox_store.order.repository.OrderRepository;
import vn.techbox.techbox_store.order.repository.OrderShippingInfoRepository;
import vn.techbox.techbox_store.product.model.ProductVariation;
import vn.techbox.techbox_store.product.repository.ProductVariationRepository;
import vn.techbox.techbox_store.user.model.User;
import vn.techbox.techbox_store.user.repository.UserRepository;

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

    @Override
    @Transactional
    public OrderResponse createOrder(CreateOrderRequest request, Integer userId) {
        log.info("Creating order for user: {}", userId);

        User user = userRepository.findById(userId)
                .orElseThrow(() -> new OrderException("User not found"));

        orderValidationService.validateCreateOrderRequest(request);

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
                .deliveryInstructions(request.getDeliveryInstructions())
                .build();
        OrderShippingInfo savedShippingInfo = orderShippingInfoRepository.save(shippingInfo);

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

        PaymentResponse paymentResponse = null;
        try {
            PaymentRequest paymentRequest = new PaymentRequest();
            paymentRequest.setOrderId(savedOrder.getId());
            paymentRequest.setPaymentMethod(request.getPaymentMethod());
            paymentRequest.setAmount(savedPaymentInfo.getFinalAmount());
            paymentRequest.setReturnUrl(null);
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
    public Page<OrderResponse> getUserOrders(Integer userId, Pageable pageable) {
        Page<Order> orders = orderRepository.findByUserIdOrderByCreatedAtDesc(userId, pageable);
        return orders.map(orderMappingService::toOrderResponse);
    }

    @Override
    @Transactional(readOnly = true)
    public Page<OrderResponse> getUserOrdersByStatus(Integer userId, OrderStatus status, Pageable pageable) {
        Page<Order> orders = orderRepository.findByUserIdAndStatusOrderByCreatedAtDesc(userId, status, pageable);
        return orders.map(orderMappingService::toOrderResponse);
    }

    @Override
    public OrderResponse updateOrderStatus(Long orderId, OrderStatus status) {
        Order order = orderRepository.findById(orderId)
                .orElseThrow(() -> new OrderException("Order not found"));

        orderValidationService.validateStatusTransition(order.getStatus(), status);

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

        if (order.getStatus() != OrderStatus.PENDING) {
            throw new OrderException("Cannot cancel order with status: " + order.getStatus());
        }

        order.setStatus(OrderStatus.CANCELLED);
        order.setUpdatedAt(LocalDateTime.now());

        Order savedOrder = orderRepository.save(order);

        log.info("Order {} cancelled successfully", orderId);

        return orderMappingService.toOrderResponse(savedOrder);
    }

    @Override
    public PaymentResponse processPayment(PaymentRequest request) {
        Order order = orderRepository.findById(request.getOrderId())
                .orElseThrow(() -> new OrderException("Order not found"));

        var processor = paymentServiceFactory.getPaymentService(request.getPaymentMethod());
        PaymentResponse response = processor.generatePaymentUrl(request);

        if ("PENDING".equals(response.getStatus()) || "SUCCESS".equals(response.getStatus())) {
            order.getPaymentInfo().setPaymentTransactionId(response.getTransactionId());
            orderRepository.save(order);
        }

        return response;
    }

    @Override
    public boolean confirmPayment(Long orderId, String transactionId, String signature) {
        Order order = orderRepository.findById(orderId)
                .orElseThrow(() -> new OrderException("Order not found"));

        var processor = paymentServiceFactory.getPaymentService(order.getPaymentMethod());
        boolean isValid = processor.verifyPayment(transactionId, signature);

        if (isValid) {
            order.getPaymentInfo().setPaymentStatus("PAID");
            order.setStatus(OrderStatus.CONFIRMED);
            orderRepository.save(order);

            log.info("Payment confirmed for order: {}", orderId);
        }

        return isValid;
    }

    private String generateOrderCode() {
        String prefix = "ORD";
        String timestamp = String.valueOf(System.currentTimeMillis());
        String random = UUID.randomUUID().toString().substring(0, 8).toUpperCase();
        return prefix + timestamp.substring(timestamp.length() - 6) + random;
    }
}
