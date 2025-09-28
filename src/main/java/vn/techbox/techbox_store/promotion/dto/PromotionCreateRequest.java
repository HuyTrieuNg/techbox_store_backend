package vn.techbox.techbox_store.promotion.dto;

import jakarta.validation.constraints.DecimalMin;
import jakarta.validation.constraints.Min;
import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.NotNull;
import jakarta.validation.constraints.Size;
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
    
    @NotBlank(message = "Rule name is required")
    @Size(min = 3, max = 255, message = "Rule name must be between 3 and 255 characters")
    private String ruleName;
    
    @NotNull(message = "Product variation ID is required")
    private Integer productVariationId;
    
    @NotNull(message = "Discount type is required")
    private PromotionType discountType;
    
    @NotNull(message = "Discount value is required")
    @DecimalMin(value = "0.01", message = "Discount value must be greater than 0")
    private BigDecimal discountValue;
    
    @Min(value = 1, message = "Min quantity must be at least 1")
    @Builder.Default
    private Integer minQuantity = 1;
    
    @DecimalMin(value = "0.00", message = "Min order amount must be non-negative")
    @Builder.Default
    private BigDecimal minOrderAmount = BigDecimal.ZERO;
    
    @DecimalMin(value = "0.01", message = "Max discount amount must be greater than 0")
    private BigDecimal maxDiscountAmount;
}