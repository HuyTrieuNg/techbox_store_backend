package vn.techbox.techbox_store.reports.dto;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.math.BigDecimal;
import java.util.List;

@Data
@Builder
@NoArgsConstructor
@AllArgsConstructor
public class InventoryStatsDTO {
    private BigDecimal totalInventoryValue;
    private Long totalStockImports;
    private Long totalStockExports;
    private Integer totalProductVariations;
    private Integer lowStockVariations;
    private List<StockMovementDTO> recentMovements;
}
