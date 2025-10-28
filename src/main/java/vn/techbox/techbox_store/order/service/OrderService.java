package vn.techbox.techbox_store.order.service;

import vn.techbox.techbox_store.order.dto.CreateOrderRequest;
import vn.techbox.techbox_store.order.dto.OrderResponse;
import vn.techbox.techbox_store.order.dto.PaymentRequest;
import vn.techbox.techbox_store.order.dto.PaymentResponse;
import vn.techbox.techbox_store.order.model.OrderStatus;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;

public interface OrderService {

    OrderResponse createOrder(CreateOrderRequest request, Integer userId);
    OrderResponse getOrderById(Long orderId, Integer userId);
    OrderResponse getOrderByCode(String orderCode, Integer userId);
    Page<OrderResponse> getUserOrders(Integer userId, Pageable pageable);
    Page<OrderResponse> getUserOrdersByStatus(Integer userId, OrderStatus status, Pageable pageable);
    OrderResponse updateOrderStatus(Long orderId, OrderStatus status);
    OrderResponse cancelOrder(Long orderId, Integer userId);
    PaymentResponse processPayment(PaymentRequest request);
    boolean confirmPayment(Long orderId, String transactionId, String signature);
}
