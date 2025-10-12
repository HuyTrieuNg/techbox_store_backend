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
import org.springframework.web.bind.annotation.*;
import vn.techbox.techbox_store.inventory.dto.CreateStockExportFromOrderRequest;
import vn.techbox.techbox_store.inventory.dto.CreateStockExportRequest;
import vn.techbox.techbox_store.inventory.dto.StockExportDTO;
import vn.techbox.techbox_store.inventory.dto.StockExportDetailDTO;
import vn.techbox.techbox_store.inventory.dto.StockExportReportDTO;
import vn.techbox.techbox_store.inventory.service.StockExportService;

import java.time.LocalDate;

@RestController
@RequestMapping("/stock-exports")
@RequiredArgsConstructor
@Slf4j
public class StockExportController {
    
    private final StockExportService stockExportService;
    
    /**
     * Get all stock exports with filters and pagination
     * 
     * GET /api/stock-exports?page=0&size=20&fromDate=2025-01-01&toDate=2025-12-31&userId=1&orderId=1&documentCode=EXP
     */
    @GetMapping
    public ResponseEntity<Page<StockExportDTO>> getAllStockExports(
            @RequestParam(defaultValue = "0") int page,
            @RequestParam(defaultValue = "20") int size,
            @RequestParam(required = false) @DateTimeFormat(iso = DateTimeFormat.ISO.DATE) LocalDate fromDate,
            @RequestParam(required = false) @DateTimeFormat(iso = DateTimeFormat.ISO.DATE) LocalDate toDate,
            @RequestParam(required = false) Integer userId,
            @RequestParam(required = false) Integer orderId,
            @RequestParam(required = false) String documentCode) {
        
        log.info("GET /api/stock-exports - page: {}, size: {}, fromDate: {}, toDate: {}, userId: {}, orderId: {}, documentCode: {}", 
                page, size, fromDate, toDate, userId, orderId, documentCode);
        
        Pageable pageable = PageRequest.of(page, size, Sort.by(Sort.Direction.DESC, "exportDate"));
        Page<StockExportDTO> stockExports = stockExportService.getAllStockExports(
                fromDate, toDate, userId, orderId, documentCode, pageable);
        
        return ResponseEntity.ok(stockExports);
    }
    
    /**
     * Get stock export detail by ID
     * 
     * GET /api/stock-exports/{id}
     */
    @GetMapping("/{id}")
    public ResponseEntity<StockExportDetailDTO> getStockExportById(@PathVariable Integer id) {
        log.info("GET /api/stock-exports/{}", id);
        
        StockExportDetailDTO stockExport = stockExportService.getStockExportById(id);
        return ResponseEntity.ok(stockExport);
    }
    
    /**
     * Create new stock export
     * 
     * POST /api/stock-exports
     */
    @PostMapping
    public ResponseEntity<StockExportDetailDTO> createStockExport(
            @Valid @RequestBody CreateStockExportRequest request) {
        
        log.info("POST /api/stock-exports - items count: {}", 
                request.getItems() != null ? request.getItems().size() : 0);
        
        // TODO: Get current user ID from authentication context
        // For now, using a placeholder user ID
        Integer currentUserId = 1;
        
        StockExportDetailDTO createdStockExport = stockExportService.createStockExport(request, currentUserId);
        return ResponseEntity.status(HttpStatus.CREATED).body(createdStockExport);
    }
    
    /**
     * Create stock export from order
     * 
     * POST /api/stock-exports/from-order/{orderId}
     */
    @PostMapping("/from-order/{orderId}")
    public ResponseEntity<StockExportDetailDTO> createStockExportFromOrder(
            @PathVariable Integer orderId,
            @Valid @RequestBody CreateStockExportFromOrderRequest request) {
        
        log.info("POST /api/stock-exports/from-order/{}", orderId);
        
        // TODO: Get current user ID from authentication context
        // For now, using a placeholder user ID
        Integer currentUserId = 1;
        
        StockExportDetailDTO createdStockExport = stockExportService.createStockExportFromOrder(
                orderId, request, currentUserId);
        return ResponseEntity.status(HttpStatus.CREATED).body(createdStockExport);
    }
    
    /**
     * Generate stock export report
     * 
     * GET /api/stock-exports/report?fromDate=2025-01-01&toDate=2025-12-31&groupBy=day
     */
    @GetMapping("/report")
    public ResponseEntity<StockExportReportDTO> generateReport(
            @RequestParam(required = false) @DateTimeFormat(iso = DateTimeFormat.ISO.DATE) LocalDate fromDate,
            @RequestParam(required = false) @DateTimeFormat(iso = DateTimeFormat.ISO.DATE) LocalDate toDate,
            @RequestParam(defaultValue = "day") String groupBy) {
        
        log.info("GET /api/stock-exports/report - fromDate: {}, toDate: {}, groupBy: {}", 
                fromDate, toDate, groupBy);
        
        // Validate groupBy parameter
        if (!groupBy.matches("(?i)day|month|product")) {
            throw new IllegalArgumentException("Invalid groupBy parameter. Must be 'day', 'month', or 'product'");
        }
        
        StockExportReportDTO report = stockExportService.generateReport(fromDate, toDate, groupBy);
        return ResponseEntity.ok(report);
    }
}
