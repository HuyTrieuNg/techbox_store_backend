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
public class StockExportItemDTO {
    private Integer id;
    private Integer productVariationId;
    private String productName;
    private String variationName;
    private String sku;
    private Integer quantity;
    private BigDecimal costPrice;  // Giá vốn (COGS)
    private BigDecimal totalValue;
}
