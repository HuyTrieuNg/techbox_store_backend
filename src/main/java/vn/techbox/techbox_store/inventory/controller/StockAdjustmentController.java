package vn.techbox.techbox_store.inventory.controller;

import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Pageable;
import org.springframework.data.domain.Sort;
import org.springframework.format.annotation.DateTimeFormat;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.security.core.annotation.AuthenticationPrincipal;
import org.springframework.web.bind.annotation.*;
import vn.techbox.techbox_store.inventory.dto.CreateStockAdjustmentRequest;
import vn.techbox.techbox_store.inventory.dto.StockAdjustmentDTO;
import vn.techbox.techbox_store.inventory.dto.StockAdjustmentDetailDTO;
import vn.techbox.techbox_store.inventory.dto.UpdateStockAdjustmentRequest;
import vn.techbox.techbox_store.inventory.service.StockAdjustmentService;
import vn.techbox.techbox_store.user.security.UserPrincipal;

import java.time.LocalDate;

@RestController
@RequestMapping("/stock-adjustments")
@RequiredArgsConstructor
@Slf4j
public class StockAdjustmentController {

    private final StockAdjustmentService stockAdjustmentService;

    /**
     * Get all stock adjustments with filters and pagination
     *
     * GET /api/stock-adjustments?page=0&size=20&fromDate=2025-01-01&toDate=2025-12-31&userId=1&checkName=Monthly
     */
    @PreAuthorize("hasAuthority('INVENTORY:READ')")
    @GetMapping
    public ResponseEntity<Page<StockAdjustmentDTO>> getAllStockAdjustments(
            @RequestParam(defaultValue = "0") int page,
            @RequestParam(defaultValue = "20") int size,
            @RequestParam(required = false) @DateTimeFormat(iso = DateTimeFormat.ISO.DATE) LocalDate fromDate,
            @RequestParam(required = false) @DateTimeFormat(iso = DateTimeFormat.ISO.DATE) LocalDate toDate,
            @RequestParam(required = false) Integer userId,
            @RequestParam(required = false) String checkName) {

        log.info("GET /api/stock-adjustments - page: {}, size: {}, fromDate: {}, toDate: {}, userId: {}, checkName: {}",
                page, size, fromDate, toDate, userId, checkName);

        Pageable pageable = PageRequest.of(page, size, Sort.by(Sort.Direction.DESC, "adjustmentDate"));
        Page<StockAdjustmentDTO> stockAdjustments = stockAdjustmentService.getAllStockAdjustments(
                fromDate, toDate, userId, checkName, pageable);

        return ResponseEntity.ok(stockAdjustments);
    }

    /**
     * Get stock adjustment detail by ID
     *
     * GET /api/stock-adjustments/{id}
     */
    @PreAuthorize("hasAuthority('INVENTORY:READ')")
    @GetMapping("/{id}")
    public ResponseEntity<StockAdjustmentDetailDTO> getStockAdjustmentById(@PathVariable Integer id) {
        log.info("GET /api/stock-adjustments/{}", id);

        StockAdjustmentDetailDTO stockAdjustment = stockAdjustmentService.getStockAdjustmentDetailById(id);
        return ResponseEntity.ok(stockAdjustment);
    }

    /**
     * Create new stock adjustment
     *
     * POST /api/stock-adjustments
     */
    @PreAuthorize("hasAuthority('INVENTORY:WRITE')")
    @PostMapping
    public ResponseEntity<StockAdjustmentDetailDTO> createStockAdjustment(
            @Valid @RequestBody CreateStockAdjustmentRequest request,
            @AuthenticationPrincipal UserPrincipal userPrincipal) {

        log.info("POST /api/stock-adjustments - items count: {}",
                request.getItems() != null ? request.getItems().size() : 0);

        Integer currentUserId = userPrincipal.getId();

        StockAdjustmentDetailDTO createdStockAdjustment = stockAdjustmentService.createStockAdjustment(request, currentUserId);
        return ResponseEntity.status(HttpStatus.CREATED).body(createdStockAdjustment);
    }

    /**
     * Update stock adjustment
     *
     * PUT /api/stock-adjustments/{id}
     */
    @PreAuthorize("hasAuthority('INVENTORY:WRITE')")
    @PutMapping("/{id}")
    public ResponseEntity<StockAdjustmentDTO> updateStockAdjustment(
            @PathVariable Integer id,
            @Valid @RequestBody UpdateStockAdjustmentRequest request) {

        log.info("PUT /api/stock-adjustments/{} - items count: {}", id,
                request.getItems() != null ? request.getItems().size() : 0);

        StockAdjustmentDTO updatedStockAdjustment = stockAdjustmentService.updateStockAdjustment(id, request);
        return ResponseEntity.ok(updatedStockAdjustment);
    }

    /**
     * Delete stock adjustment
     *
     * DELETE /api/stock-adjustments/{id}
     */
    @PreAuthorize("hasAuthority('INVENTORY:DELETE')")
    @DeleteMapping("/{id}")
    public ResponseEntity<Void> deleteStockAdjustment(@PathVariable Integer id) {
        log.info("DELETE /api/stock-adjustments/{}", id);

        stockAdjustmentService.deleteStockAdjustment(id);
        return ResponseEntity.noContent().build();
    }

}