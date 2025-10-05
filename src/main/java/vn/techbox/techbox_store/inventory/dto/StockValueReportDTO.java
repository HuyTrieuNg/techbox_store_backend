package vn.techbox.techbox_store.inventory.dto;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.math.BigDecimal;
import java.time.LocalDate;

@Data
@Builder
@NoArgsConstructor
@AllArgsConstructor
public class StockValueReportDTO {
    private LocalDate date;
    private BigDecimal totalStockValue;
    private BigDecimal totalImportValue;
    private BigDecimal totalExportValue;
    private BigDecimal netChange; // totalImportValue - totalExportValue
}
