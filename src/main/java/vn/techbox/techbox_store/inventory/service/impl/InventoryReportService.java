package vn.techbox.techbox_store.inventory.service.impl;

import vn.techbox.techbox_store.inventory.dto.*;

import java.time.LocalDate;
import java.util.List;

public interface InventoryReportService {
    
    /**
     * Get current stock balance with filters
     */
    List<StockBalanceDTO> getStockBalance(
            Integer categoryId,
            Integer brandId,
            String keyword,
            Boolean lowStock,
            Boolean outOfStock
    );
    
    /**
     * Get stock movement history for a product variation
     */
    List<StockMovementDTO> getProductHistory(
            Integer productVariationId,
            LocalDate fromDate,
            LocalDate toDate
    );
    
    /**
     * Get stock value report over time
     */
    List<StockValueReportDTO> getStockValueReport(
            LocalDate fromDate,
            LocalDate toDate,
            String groupBy
    );
    
    /**
     * Get top products by import/export quantity
     */
    List<TopProductDTO> getTopProducts(
            LocalDate fromDate,
            LocalDate toDate,
            String type,
            Integer limit
    );
    
    /**
     * Get inventory alerts (out of stock, low stock, overstock)
     */
    InventoryAlertsDTO getInventoryAlerts();
}
