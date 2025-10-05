package vn.techbox.techbox_store.inventory.dto;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.math.BigDecimal;
import java.time.LocalDateTime;
import java.util.List;

@Data
@NoArgsConstructor
@AllArgsConstructor
@Builder
public class StockImportDetailDTO {
    private Integer id;
    private String documentCode;
    private Integer userId;
    private String userName;
    private LocalDateTime importDate;
    private Integer supplierId;
    private String supplierName;
    private BigDecimal totalCostValue;
    private String note;
    private LocalDateTime createdAt;
    private List<StockImportItemDTO> items;
}
