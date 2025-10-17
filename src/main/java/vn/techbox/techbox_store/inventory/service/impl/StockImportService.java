package vn.techbox.techbox_store.inventory.service.impl;

import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import vn.techbox.techbox_store.inventory.dto.*;

import java.time.LocalDate;

public interface StockImportService {
    
    /**
     * Get all stock imports with filters and pagination
     */
    Page<StockImportDTO> getAllStockImports(
            LocalDate fromDate,
            LocalDate toDate,
            Integer supplierId,
            Integer userId,
            String documentCode,
            Pageable pageable);
    
    /**
     * Get stock import detail by ID
     */
    StockImportDetailDTO getStockImportById(Integer id);
    
    /**
     * Create new stock import
     * Automatically updates stock_quantity and avg_cost_price of ProductVariation
     */
    StockImportDetailDTO createStockImport(CreateStockImportRequest request, Integer currentUserId);
    
    /**
     * Find stock import by document code
     */
    StockImportDetailDTO getStockImportByDocumentCode(String documentCode);
    
    /**
     * Generate stock import report
     */
    StockImportReportDTO generateReport(
            LocalDate fromDate,
            LocalDate toDate,
            Integer supplierId,
            String groupBy);
}
