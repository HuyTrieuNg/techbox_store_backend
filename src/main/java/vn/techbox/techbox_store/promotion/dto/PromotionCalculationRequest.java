package vn.techbox.techbox_store.promotion.dto;

import jakarta.validation.constraints.DecimalMin;import jakarta.validation.constraints.NotNull;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.math.BigDecimal;

@Data
@Builder
@NoArgsConstructor
@AllArgsConstructor
public class PromotionCalculationRequest {
    
    @NotNull(message = "Product variation ID is required")
    private Integer productVariationId;
    
    @NotNull(message = "Original price is required")
    @DecimalMin(value = "1.00", message = "Original price must be greater than 0")
    private BigDecimal originalPrice;
}