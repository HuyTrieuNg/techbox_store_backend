package vn.techbox.techbox_store.reports.dto;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@Builder
@NoArgsConstructor
@AllArgsConstructor
public class LowStockProductDTO {
    private Integer productId;
    private String productName;
    private String spu;
    private Integer variationId;
    private String variationSku;
    private String variationName;
    private Integer currentStock;
    private Integer threshold;
}
