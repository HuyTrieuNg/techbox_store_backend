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
public class ReportItemDTO {
    private String groupKey;        // date, month, or supplier name
    private Integer documentCount;
    private Integer totalQuantity;
    private BigDecimal totalValue;
    private Integer supplierId;
    private String supplierName;
}
