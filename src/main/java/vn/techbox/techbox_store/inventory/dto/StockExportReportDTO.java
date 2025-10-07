package vn.techbox.techbox_store.inventory.dto;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.math.BigDecimal;
import java.util.List;

@Data
@NoArgsConstructor
@AllArgsConstructor
@Builder
public class StockExportReportDTO {
    private Integer totalDocuments;
    private Integer totalQuantity;
    private BigDecimal totalCogsValue;
    private List<ReportItemDTO> details;
}
