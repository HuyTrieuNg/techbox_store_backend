package vn.techbox.techbox_store.order.dto;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.math.BigDecimal;
import java.util.List;

@Data
@Builder
@NoArgsConstructor
@AllArgsConstructor
public class DiscountCalculationResponse {

    private BigDecimal totalAmount;
    private BigDecimal promotionDiscount;
    private BigDecimal voucherDiscount;
    private BigDecimal totalDiscount;
    private BigDecimal finalAmount;
    private BigDecimal shippingFee;
    private BigDecimal taxAmount;

    private List<ItemDiscountDetail> itemDiscounts;
    private VoucherDiscountDetail voucherDetails;

    @Data
    @Builder
    @NoArgsConstructor
    @AllArgsConstructor
    public static class ItemDiscountDetail {
        private Integer productVariationId;
        private String productName;
        private Integer quantity;
        private BigDecimal unitPrice;
        private BigDecimal originalAmount;
        private BigDecimal promotionDiscount;
        private BigDecimal finalAmount;
        private String promotionName;
    }

    @Data
    @Builder
    @NoArgsConstructor
    @AllArgsConstructor
    public static class VoucherDiscountDetail {
        private String voucherCode;
        private String voucherType;
        private BigDecimal discountAmount;
        private BigDecimal minOrderAmount;
        private String discountDescription;
        private boolean isValid;
        private String validationMessage;
    }
}
