package vn.techbox.techbox_store.order.controller;

import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.tags.Tag;
import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Pageable;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.security.core.annotation.AuthenticationPrincipal;
import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.web.bind.annotation.*;
import vn.techbox.techbox_store.order.dto.*;
import vn.techbox.techbox_store.order.model.OrderStatus;
import vn.techbox.techbox_store.order.service.OrderService;
import vn.techbox.techbox_store.user.repository.UserRepository;

import java.util.HashMap;
import java.util.Map;

@RestController
@RequestMapping("/orders")
@RequiredArgsConstructor
@Slf4j
@Tag(name = "Order Management", description = "APIs for managing orders")
public class OrderController {

    private final OrderService orderService;
    private final UserRepository userRepository;

    @PostMapping
    @PreAuthorize("hasRole('CUSTOMER')")
    @Operation(summary = "Create new order", description = "Create a new order with items and shipping information")
    public ResponseEntity<OrderResponse> createOrder(
            @Valid @RequestBody CreateOrderRequest request,
            @AuthenticationPrincipal UserDetails userDetails) {

        try {
            Integer userId = getUserId(userDetails);
            log.info("Creating order for user: {}", userId);

            OrderResponse response = orderService.createOrder(request, userId);

            return ResponseEntity.status(HttpStatus.CREATED).body(response);
        } catch (Exception e) {
            log.error("Error creating order: {}", e.getMessage(), e);
            throw e;
        }
    }

    @GetMapping("/{orderId}")
    @PreAuthorize("hasRole('CUSTOMER')")
    @Operation(summary = "Get order by ID", description = "Get order details by order ID")
    public ResponseEntity<OrderResponse> getOrderById(
            @PathVariable Long orderId,
            @AuthenticationPrincipal UserDetails userDetails) {

        Integer userId = getUserId(userDetails);
        OrderResponse response = orderService.getOrderById(orderId, userId);

        return ResponseEntity.ok(response);
    }

    @GetMapping("/code/{orderCode}")
    @PreAuthorize("hasRole('CUSTOMER')")
    @Operation(summary = "Get order by code", description = "Get order details by order code")
    public ResponseEntity<OrderResponse> getOrderByCode(
            @PathVariable String orderCode,
            @AuthenticationPrincipal UserDetails userDetails) {

        Integer userId = getUserId(userDetails);
        OrderResponse response = orderService.getOrderByCode(orderCode, userId);

        return ResponseEntity.ok(response);
    }

    @GetMapping
    @PreAuthorize("hasRole('CUSTOMER')")
    @Operation(summary = "Get user orders", description = "Get paginated list of user orders")
    public ResponseEntity<Page<OrderResponse>> getUserOrders(
            @RequestParam(defaultValue = "0") int page,
            @RequestParam(defaultValue = "10") int size,
            @RequestParam(required = false) OrderStatus status,
            @AuthenticationPrincipal UserDetails userDetails) {

        Integer userId = getUserId(userDetails);
        Pageable pageable = PageRequest.of(page, size);
        Page<OrderResponse> orders;

        if (status != null) {
            orders = orderService.getUserOrdersByStatus(userId, status, pageable);
        } else {
            orders = orderService.getUserOrders(userId, pageable);
        }

        return ResponseEntity.ok(orders);
    }

    @PutMapping("/{orderId}/cancel")
    @PreAuthorize("hasRole('CUSTOMER')")
    @Operation(summary = "Cancel order", description = "Cancel an order by user")
    public ResponseEntity<OrderResponse> cancelOrder(
            @PathVariable Long orderId,
            @AuthenticationPrincipal UserDetails userDetails) {

        Integer userId = getUserId(userDetails);
        log.info("Cancelling order {} by user: {}", orderId, userId);

        OrderResponse response = orderService.cancelOrder(orderId, userId);

        return ResponseEntity.ok(response);
    }

    @PutMapping("/{orderId}/status")
    @PreAuthorize("hasRole('ADMIN') or hasRole('STAFF')")
    @Operation(summary = "Update order status", description = "Update order status (Admin/Staff only)")
    public ResponseEntity<OrderResponse> updateOrderStatus(
            @PathVariable Long orderId,
            @RequestBody Map<String, String> request) {

        OrderStatus status = OrderStatus.valueOf(request.get("status"));

        log.info("Updating order {} status to: {}", orderId, status);

        OrderResponse response = orderService.updateOrderStatus(orderId, status);

        return ResponseEntity.ok(response);
    }

    private Integer getUserId(UserDetails userDetails) {
        try {
            log.debug("Retrieving user ID for: {}", userDetails.getUsername());
            return userRepository.findByAccountEmail(userDetails.getUsername())
                    .map(user -> {
                        log.debug("Found user ID: {} for email: {}", user.getId(), userDetails.getUsername());
                        return user.getId();
                    })
                    .orElseThrow(() -> {
                        log.error("User not found for email: {}", userDetails.getUsername());
                        return new RuntimeException("User not found with email: " + userDetails.getUsername());
                    });
        } catch (Exception e) {
            log.error("Error retrieving user ID for email {}: {}", userDetails.getUsername(), e.getMessage());
            throw e;
        }
    }
}
