package vn.techbox.techbox_store.promotion.dto;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;
import vn.techbox.techbox_store.promotion.model.PromotionType;

import java.math.BigDecimal;
import java.time.LocalDateTime;

@Data
@Builder
@NoArgsConstructor
@AllArgsConstructor
public class PromotionResponse {
    
    private Integer id;
    
    private Integer campaignId;
    
    private String campaignName;
    
    private String ruleName;
    
    private Integer productVariationId;
    
    private PromotionType discountType;
    
    private BigDecimal discountValue;
    
    private Integer minQuantity;
    
    private BigDecimal minOrderAmount;
    
    private BigDecimal maxDiscountAmount;
    
    private LocalDateTime createdAt;
    
    private LocalDateTime updatedAt;
    
    // Computed fields
    private String discountDisplay;
    
    private boolean isActive;
}