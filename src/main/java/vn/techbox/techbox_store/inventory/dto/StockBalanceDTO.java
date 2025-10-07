package vn.techbox.techbox_store.inventory.dto;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.math.BigDecimal;

@Data
@Builder
@NoArgsConstructor
@AllArgsConstructor
public class StockBalanceDTO {
    private Integer productVariationId;
    private String productName;
    private String variationName;
    private String sku;
    private Integer stockQuantity;
    private BigDecimal avgCostPrice;
    private BigDecimal totalStockValue; // stockQuantity * avgCostPrice
    private Integer lowStockThreshold;
    private Boolean isLowStock;
}
