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
public class ProductVariationDTO {
    private Integer id;
    private String productName;
    private String variationName;
    private String sku;
    private Integer stockQuantity;
    private BigDecimal avgCostPrice;
    private Integer lowStockThreshold;
}
