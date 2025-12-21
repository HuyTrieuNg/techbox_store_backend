package vn.techbox.techbox_store.inventory.service;

import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import vn.techbox.techbox_store.inventory.dto.*;

import java.time.LocalDate;

public interface StockExportService {
    
    /**
     * Get all stock exports with filters and pagination
     */
    Page<StockExportDTO> getAllStockExports(
            LocalDate fromDate,
            LocalDate toDate,
            Integer userId,
            Integer orderId,
            String documentCode,
            Pageable pageable);
    
    /**
     * Get stock export detail by ID
     */
    StockExportDetailDTO getStockExportById(Integer id);
    
    /**
     * Create new stock export
     * Automatically uses avg_cost_price from ProductVariation
     * Decreases stock_quantity and validates sufficient stock
     */
    StockExportDetailDTO createStockExport(CreateStockExportRequest request, Integer currentUserId);
    
    /**
     * Create stock export from order
     * Automatically gets items from order_items
     */
    StockExportDetailDTO createStockExportFromOrder(Integer orderId, CreateStockExportFromOrderRequest request, Integer currentUserId);
    
}
