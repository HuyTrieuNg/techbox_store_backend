package vn.techbox.techbox_store.reports.service;

import vn.techbox.techbox_store.reports.dto.InventoryStatsDTO;
import vn.techbox.techbox_store.reports.dto.StockMovementDTO;

import java.time.LocalDateTime;
import java.util.List;

public interface InventoryStatsService {
    
    /**
     * Get overall inventory statistics
     */
    InventoryStatsDTO getInventoryOverview();
    
    /**
     * Get stock movements (imports and exports) for a specific period
     * @param startDate Start date of the period
     * @param endDate End date of the period
     */
    List<StockMovementDTO> getStockMovements(LocalDateTime startDate, LocalDateTime endDate);
}
