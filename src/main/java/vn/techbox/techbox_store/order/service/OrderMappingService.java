package vn.techbox.techbox_store.order.service;

import org.springframework.stereotype.Service;
import vn.techbox.techbox_store.order.dto.OrderResponse;
import vn.techbox.techbox_store.order.model.Order;
import vn.techbox.techbox_store.order.model.OrderItem;

import java.util.List;
import java.util.stream.Collectors;

@Service
public class OrderMappingService {

    public OrderResponse toOrderResponse(Order order) {
        OrderResponse response = new OrderResponse();

        response.setId(order.getId());
        response.setOrderCode(order.getOrderCode());
        response.setStatus(order.getStatus());
        response.setNote(order.getNote());
        response.setCreatedAt(order.getCreatedAt());
        response.setUpdatedAt(order.getUpdatedAt());

        // Map payment information
        if (order.getPaymentInfo() != null) {
            response.setPaymentMethod(order.getPaymentInfo().getPaymentMethod());
            response.setPaymentStatus(order.getPaymentInfo().getPaymentStatus());
            response.setTotalAmount(order.getPaymentInfo().getTotalAmount());
            response.setDiscountAmount(order.getPaymentInfo().getDiscountAmount());
            response.setShippingFee(order.getPaymentInfo().getShippingFee());
            response.setFinalAmount(order.getPaymentInfo().getFinalAmount());
            response.setPaymentTransactionId(order.getPaymentInfo().getPaymentTransactionId());
        }

        // Map shipping information
        if (order.getShippingInfo() != null) {
            response.setShippingName(order.getShippingInfo().getShippingName());
            response.setShippingPhone(order.getShippingInfo().getShippingPhone());
            response.setShippingAddress(order.getShippingInfo().getShippingAddress());
            response.setShippingWard(order.getShippingInfo().getShippingWard());
            response.setShippingDistrict(order.getShippingInfo().getShippingDistrict());
            response.setShippingCity(order.getShippingInfo().getShippingCity());
        }

        // Map order items
        if (order.getOrderItems() != null) {
            List<OrderResponse.OrderItemResponse> orderItems = order.getOrderItems().stream()
                    .map(this::toOrderItemResponse)
                    .collect(Collectors.toList());
            response.setOrderItems(orderItems);
        }

        return response;
    }

    private OrderResponse.OrderItemResponse toOrderItemResponse(OrderItem item) {
        OrderResponse.OrderItemResponse response = new OrderResponse.OrderItemResponse();

        response.setId(item.getId());
        response.setProductVariationId(item.getProductVariation().getId().longValue());
        response.setProductName(item.getProductName());
        response.setProductVariationName(item.getProductVariationName());
        response.setQuantity(item.getQuantity());
        response.setUnitPrice(item.getUnitPrice());
        response.setTotalPrice(item.getTotalPrice());
        response.setDiscountAmount(item.getDiscountAmount());

        return response;
    }
}
