package vn.techbox.techbox_store.inventory.dto;

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
public class StockAdjustmentItemRequest {

    @NotNull(message = "Product variation ID is required")
    private Integer productVariationId;

    @NotNull(message = "Real quantity is required")
    @Min(value = 0, message = "Real quantity must be greater than or equal to 0")
    private Integer realQty;

    @NotNull(message = "Cost price is required")
    private BigDecimal costPrice;
}