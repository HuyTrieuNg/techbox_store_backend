package vn.techbox.techbox_store.order.dto;

import lombok.Data;
import vn.techbox.techbox_store.order.model.OrderStatus;
import vn.techbox.techbox_store.payment.model.PaymentMethod;

import java.math.BigDecimal;
import java.time.LocalDateTime;
import java.util.List;

@Data
public class OrderResponse {

    private Long id;
    private String orderCode;
    private OrderStatus status;
    private PaymentMethod paymentMethod;
    private String paymentStatus;

    private BigDecimal totalAmount;
    private BigDecimal discountAmount;
    private BigDecimal shippingFee;
    private BigDecimal finalAmount;

    private String shippingName;
    private String shippingPhone;
    private String shippingAddress;
    private String shippingWard;
    private String shippingDistrict;
    private String shippingCity;

    private String note;
    private String paymentTransactionId;
    private String paymentUrl;

    private LocalDateTime createdAt;
    private LocalDateTime updatedAt;

    private List<OrderItemResponse> orderItems;

    @Data
    public static class OrderItemResponse {
        private Long id;
        private Long productVariationId;
        private String productName;
        private String productVariationName;
        private Integer quantity;
        private BigDecimal unitPrice;
        private BigDecimal totalAmount;
        private BigDecimal discountAmount;
    }
}
