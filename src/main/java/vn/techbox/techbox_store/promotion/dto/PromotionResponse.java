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
    
    private Integer productVariationId;
    
    private PromotionType discountType;
    
    private BigDecimal discountValue;
    
    private LocalDateTime createdAt;
    
    private LocalDateTime updatedAt;
    
    // Computed fields
    private boolean isActive;
}