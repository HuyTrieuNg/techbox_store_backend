package vn.techbox.techbox_store.inventory.dto;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.util.List;

@Data
@NoArgsConstructor
@AllArgsConstructor
@Builder
public class StockAdjustmentReportDTO {
    private Integer totalDocuments;
    private Integer totalItems;
    private Integer totalPositiveAdjustments;
    private Integer totalNegativeAdjustments;
    private List<ReportItemDTO> details;
}