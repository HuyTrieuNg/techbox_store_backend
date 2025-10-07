package vn.techbox.techbox_store.inventory.dto;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.math.BigDecimal;
import java.time.LocalDateTime;

@Data
@NoArgsConstructor
@AllArgsConstructor
@Builder
public class StockImportDTO {
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
    private Integer totalItems;
}
