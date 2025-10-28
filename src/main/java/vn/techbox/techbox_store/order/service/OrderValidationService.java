package vn.techbox.techbox_store.order.service;

import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import vn.techbox.techbox_store.order.dto.CreateOrderRequest;
import vn.techbox.techbox_store.order.model.Order;
import vn.techbox.techbox_store.order.model.OrderStatus;
import vn.techbox.techbox_store.payment.model.PaymentMethod;
import vn.techbox.techbox_store.product.repository.ProductVariationRepository;

import java.util.List;
import java.util.Set;

@Service
@RequiredArgsConstructor
public class OrderValidationService {

    private final ProductVariationRepository productVariationRepository;

    private static final Set<OrderStatus> CANCELLABLE_STATUSES = Set.of(
        OrderStatus.PENDING,
        OrderStatus.CONFIRMED
    );

    private static final Set<OrderStatus> TERMINAL_STATUSES = Set.of(
        OrderStatus.DELIVERED,
        OrderStatus.CANCELLED,
        OrderStatus.RETURNED
    );

    public void validateCreateOrderRequest(CreateOrderRequest request) {
        validateOrderItems(request.getOrderItems());
        validatePaymentMethod(request.getPaymentMethod());
        validateShippingInformation(request);
    }

    public void validateStatusTransition(OrderStatus currentStatus, OrderStatus newStatus) {
        if (currentStatus == newStatus) {
            throw new RuntimeException("Order is already in " + newStatus + " status");
        }

        if (TERMINAL_STATUSES.contains(currentStatus)) {
            throw new RuntimeException("Cannot change status from " + currentStatus);
        }

        switch (currentStatus) {
            case PENDING:
                if (!Set.of(OrderStatus.CONFIRMED, OrderStatus.CANCELLED).contains(newStatus)) {
                    throw new RuntimeException("Invalid status transition from " + currentStatus + " to " + newStatus);
                }
                break;
            case CONFIRMED:
                if (!Set.of(OrderStatus.PROCESSING, OrderStatus.CANCELLED).contains(newStatus)) {
                    throw new RuntimeException("Invalid status transition from " + currentStatus + " to " + newStatus);
                }
                break;
            case PROCESSING:
                if (!Set.of(OrderStatus.SHIPPING, OrderStatus.CANCELLED).contains(newStatus)) {
                    throw new RuntimeException("Invalid status transition from " + currentStatus + " to " + newStatus);
                }
                break;
            case SHIPPING:
                if (!Set.of(OrderStatus.DELIVERED, OrderStatus.RETURNED).contains(newStatus)) {
                    throw new RuntimeException("Invalid status transition from " + currentStatus + " to " + newStatus);
                }
                break;
            default:
                throw new RuntimeException("Invalid status transition from " + currentStatus + " to " + newStatus);
        }
    }

    public void validateCancellation(Order order) {
        if (!CANCELLABLE_STATUSES.contains(order.getStatus())) {
            throw new RuntimeException("Cannot cancel order in " + order.getStatus() + " status");
        }
    }

    private void validateOrderItems(List<CreateOrderRequest.OrderItemRequest> orderItems) {
        if (orderItems == null || orderItems.isEmpty()) {
            throw new RuntimeException("Order items cannot be empty");
        }

        for (CreateOrderRequest.OrderItemRequest item : orderItems) {
            if (!productVariationRepository.existsById(item.getProductVariationId().intValue())) {
                throw new RuntimeException("Product variation not found: " + item.getProductVariationId());
            }

            if (item.getQuantity() <= 0) {
                throw new RuntimeException("Quantity must be greater than 0");
            }

            // TODO: Validate stock availability
        }
    }

    private void validatePaymentMethod(PaymentMethod paymentMethod) {
        if (paymentMethod == null) {
            throw new RuntimeException("Payment method is required");
        }
    }

    private void validateShippingInformation(CreateOrderRequest request) {
        if (request.getShippingName() == null || request.getShippingName().trim().isEmpty()) {
            throw new RuntimeException("Shipping name is required");
        }

        if (request.getShippingPhone() == null || request.getShippingPhone().trim().isEmpty()) {
            throw new RuntimeException("Shipping phone is required");
        }

        if (request.getShippingAddress() == null || request.getShippingAddress().trim().isEmpty()) {
            throw new RuntimeException("Shipping address is required");
        }
    }
}
