package vn.techbox.techbox_store.order.dto;

import jakarta.validation.Valid;
import jakarta.validation.constraints.*;
import lombok.Data;
import vn.techbox.techbox_store.payment.model.PaymentMethod;

import java.util.List;

@Data
public class CreateOrderRequest {

    @NotNull(message = "Order items are required")
    @NotEmpty(message = "Order items cannot be empty")
    @Valid
    private List<OrderItemRequest> orderItems;

    @NotNull(message = "Shipping info is required")
    @Valid
    private ShippingInfoRequest shippingInfo;

    @NotNull(message = "Payment info is required")
    @Valid
    private PaymentInfoRequest paymentInfo;

    @Size(max = 1000, message = "Note must not exceed 1000 characters")
    private String note;

    private String voucherCode;

    @Data
    public static class OrderItemRequest {
        @NotNull(message = "Product variation ID is required")
        private Long productVariationId;

        @NotNull(message = "Quantity is required")
        @Min(value = 1, message = "Quantity must be at least 1")
        @Max(value = 999, message = "Quantity must not exceed 999")
        private Integer quantity;
    }

    @Data
    public static class ShippingInfoRequest {
        @NotBlank(message = "Shipping name is required")
        @Size(max = 100, message = "Shipping name must not exceed 100 characters")
        private String shippingName;

        @NotBlank(message = "Shipping phone is required")
        @Pattern(regexp = "^(\\+84|0)[3-9][0-9]{8}$", message = "Invalid phone number format")
        private String shippingPhone;

        @Email(message = "Invalid email format")
        private String shippingEmail;

        @NotBlank(message = "Shipping address is required")
        @Size(max = 500, message = "Shipping address must not exceed 500 characters")
        private String shippingAddress;

        private String shippingWard;
        private String shippingDistrict;
        private String shippingCity;
        private String shippingPostalCode;
        private String shippingCountry;
        private String shippingMethod;
        private String deliveryInstructions;
    }

    @Data
    public static class PaymentInfoRequest {
        @NotNull(message = "Payment method is required")
        private PaymentMethod paymentMethod;
    }

    // Backward compatibility methods
    public PaymentMethod getPaymentMethod() {
        return paymentInfo != null ? paymentInfo.getPaymentMethod() : null;
    }

    public String getShippingName() {
        return shippingInfo != null ? shippingInfo.getShippingName() : null;
    }

    public String getShippingPhone() {
        return shippingInfo != null ? shippingInfo.getShippingPhone() : null;
    }

    public String getShippingEmail() {
        return shippingInfo != null ? shippingInfo.getShippingEmail() : null;
    }

    public String getShippingAddress() {
        return shippingInfo != null ? shippingInfo.getShippingAddress() : null;
    }

    public String getShippingWard() {
        return shippingInfo != null ? shippingInfo.getShippingWard() : null;
    }

    public String getShippingDistrict() {
        return shippingInfo != null ? shippingInfo.getShippingDistrict() : null;
    }

    public String getShippingCity() {
        return shippingInfo != null ? shippingInfo.getShippingCity() : null;
    }

    public String getShippingPostalCode() {
        return shippingInfo != null ? shippingInfo.getShippingPostalCode() : null;
    }

    public String getShippingCountry() {
        return shippingInfo != null ? shippingInfo.getShippingCountry() : null;
    }

    public String getShippingMethod() {
        return shippingInfo != null ? shippingInfo.getShippingMethod() : null;
    }

    public String getDeliveryInstructions() {
        return shippingInfo != null ? shippingInfo.getDeliveryInstructions() : null;
    }
}
