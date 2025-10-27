package vn.techbox.techbox_store.promotion.dto;

import jakarta.validation.constraints.DecimalMin;
import jakarta.validation.constraints.NotNull;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;
import vn.techbox.techbox_store.promotion.model.PromotionType;

import java.math.BigDecimal;

@Data
@Builder
@NoArgsConstructor
@AllArgsConstructor
public class PromotionCreateRequest {
    
    @NotNull(message = "Campaign ID is required")
    private Integer campaignId;
    
    @NotNull(message = "Product variation ID is required")
    private Integer productVariationId;
    
    @NotNull(message = "Discount type is required")
    private PromotionType discountType;
    
    @NotNull(message = "Discount value is required")
    @DecimalMin(value = "0.01", message = "Discount value must be greater than 0")
    private BigDecimal discountValue;
}