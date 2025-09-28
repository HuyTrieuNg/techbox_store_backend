package vn.techbox.techbox_store.promotion.dto;

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
public class PromotionCalculationResponse {
    
    private Integer productVariationId;
    
    private BigDecimal originalPrice;
    
    private BigDecimal originalTotal;
    
    private Integer quantity;
    
    private BigDecimal orderAmount;
    
    private BigDecimal totalDiscount;
    
    private BigDecimal finalPrice;
    
    private BigDecimal finalTotal;
    
    private List<AppliedPromotion> appliedPromotions;
    
    @Data
    @Builder
    @NoArgsConstructor
    @AllArgsConstructor
    public static class AppliedPromotion {
        private Integer promotionId;
        private String ruleName;
        private String campaignName;
        private String discountType;
        private BigDecimal discountAmount;
        private String discountDisplay;
    }
}