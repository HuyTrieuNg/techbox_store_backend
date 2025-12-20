package vn.techbox.techbox_store.order.service;

import vn.techbox.techbox_store.order.dto.CreateOrderRequest;
import vn.techbox.techbox_store.order.dto.OrderResponse;
import vn.techbox.techbox_store.order.model.OrderStatus;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;

import java.util.List;

public interface OrderService {

    OrderResponse createOrder(CreateOrderRequest request, Integer userId);
    OrderResponse getOrderById(Long orderId, Integer userId);
    OrderResponse getOrderByCode(String orderCode, Integer userId);
    // Admin/Staff methods to get orders by id or code without user ownership check
    OrderResponse getOrderByIdForAdmin(Long orderId);
    OrderResponse getOrderByCodeForAdmin(String orderCode);
    Page<OrderResponse> getUserOrders(Integer userId, Pageable pageable);
    Page<OrderResponse> getUserOrdersByStatus(Integer userId, OrderStatus status, Pageable pageable);
    Page<OrderResponse> getAllOrders(Pageable pageable);
    Page<OrderResponse> getAllOrdersByStatus(OrderStatus status, Pageable pageable);
    Page<OrderResponse> getOrdersByUserId(Integer userId, Pageable pageable);
    Page<OrderResponse> getOrdersByUserIdAndStatus(Integer userId, OrderStatus status, Pageable pageable);
    OrderResponse updateOrderStatus(Long orderId, OrderStatus status);
    OrderResponse cancelOrder(Long orderId, Integer userId);
    List<String> getRecentProductSpus(Integer userId, int k);
    Page<OrderResponse> searchOrdersByCode(String searchTerm, Pageable pageable);
}
