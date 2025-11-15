package vn.techbox.techbox_store.inventory.dto;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.math.BigDecimal;

@Data
@NoArgsConstructor
@AllArgsConstructor
@Builder
public class StockAdjustmentItemDTO {
    private Integer id;
    private Integer productVariationId;
    private String productName;
    private String variationName;
    private String sku;
    private Integer systemQty;
    private Integer realQty;
    private Integer diffQty;
    private BigDecimal costPrice;
    private BigDecimal diffValue;
}