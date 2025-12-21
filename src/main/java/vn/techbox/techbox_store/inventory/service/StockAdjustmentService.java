package vn.techbox.techbox_store.inventory.service;

import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import vn.techbox.techbox_store.inventory.dto.*;

import java.time.LocalDate;
import java.util.List;

public interface StockAdjustmentService {

    /**
     * Get all stock adjustments with filters
     */
    Page<StockAdjustmentDTO> getAllStockAdjustments(
            LocalDate fromDate,
            LocalDate toDate,
            Integer userId,
            String checkName,
            Pageable pageable);

    /**
     * Get stock adjustment detail by ID
     */
    StockAdjustmentDetailDTO getStockAdjustmentDetailById(Integer id);

    /**
     * Create new stock adjustment
     */
    StockAdjustmentDetailDTO createStockAdjustment(CreateStockAdjustmentRequest request, Integer userId);

    /**
     * Update stock adjustment
     */
    StockAdjustmentDTO updateStockAdjustment(Integer id, UpdateStockAdjustmentRequest request);

    /**
     * Delete stock adjustment
     */
    void deleteStockAdjustment(Integer id);

  
}