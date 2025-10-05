package vn.techbox.techbox_store.inventory.dto;

import jakarta.validation.constraints.DecimalMin;
import jakarta.validation.constraints.Min;
import jakarta.validation.constraints.NotNull;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.math.BigDecimal;

@Data
@NoArgsConstructor
@AllArgsConstructor
@Builder
public class StockImportItemRequest {
    
    @NotNull(message = "Product variation ID is required")
    private Integer productVariationId;
    
    @NotNull(message = "Quantity is required")
    @Min(value = 1, message = "Quantity must be greater than 0")
    private Integer quantity;
    
    @NotNull(message = "Cost price is required")
    @DecimalMin(value = "0.0", inclusive = true, message = "Cost price must be greater than or equal to 0")
    private BigDecimal costPrice;
}
