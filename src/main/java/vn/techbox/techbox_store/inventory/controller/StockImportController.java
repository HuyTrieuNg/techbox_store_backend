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
import vn.techbox.techbox_store.inventory.dto.CreateStockImportRequest;
import vn.techbox.techbox_store.inventory.dto.StockImportDTO;
import vn.techbox.techbox_store.inventory.dto.StockImportDetailDTO;
import vn.techbox.techbox_store.inventory.dto.StockImportReportDTO;
import vn.techbox.techbox_store.inventory.service.StockImportService;

import java.time.LocalDate;

@RestController
@RequestMapping("/api/stock-imports")
@RequiredArgsConstructor
@Slf4j
public class StockImportController {
    
    private final StockImportService stockImportService;
    
    /**
     * Get all stock imports with filters and pagination
     * 
     * GET /api/stock-imports?page=0&size=20&fromDate=2025-01-01&toDate=2025-12-31&supplierId=1&userId=1&documentCode=IMP
     */
    @GetMapping
    public ResponseEntity<Page<StockImportDTO>> getAllStockImports(
            @RequestParam(defaultValue = "0") int page,
            @RequestParam(defaultValue = "20") int size,
            @RequestParam(required = false) @DateTimeFormat(iso = DateTimeFormat.ISO.DATE) LocalDate fromDate,
            @RequestParam(required = false) @DateTimeFormat(iso = DateTimeFormat.ISO.DATE) LocalDate toDate,
            @RequestParam(required = false) Integer supplierId,
            @RequestParam(required = false) Integer userId,
            @RequestParam(required = false) String documentCode) {
        
        log.info("GET /api/stock-imports - page: {}, size: {}, fromDate: {}, toDate: {}, supplierId: {}, userId: {}, documentCode: {}", 
                page, size, fromDate, toDate, supplierId, userId, documentCode);
        
        Pageable pageable = PageRequest.of(page, size, Sort.by(Sort.Direction.DESC, "importDate"));
        Page<StockImportDTO> stockImports = stockImportService.getAllStockImports(
                fromDate, toDate, supplierId, userId, documentCode, pageable);
        
        return ResponseEntity.ok(stockImports);
    }
    
    /**
     * Get stock import detail by ID
     * 
     * GET /api/stock-imports/{id}
     */
    @GetMapping("/{id}")
    public ResponseEntity<StockImportDetailDTO> getStockImportById(@PathVariable Integer id) {
        log.info("GET /api/stock-imports/{}", id);
        
        StockImportDetailDTO stockImport = stockImportService.getStockImportById(id);
        return ResponseEntity.ok(stockImport);
    }
    
    /**
     * Create new stock import
     * 
     * POST /api/stock-imports
     */
    @PostMapping
    public ResponseEntity<StockImportDetailDTO> createStockImport(
            @Valid @RequestBody CreateStockImportRequest request) {
        
        log.info("POST /api/stock-imports - items count: {}", 
                request.getItems() != null ? request.getItems().size() : 0);
        
        // TODO: Get current user ID from authentication context
        // For now, using a placeholder user ID
        Integer currentUserId = 1;
        
        StockImportDetailDTO createdStockImport = stockImportService.createStockImport(request, currentUserId);
        return ResponseEntity.status(HttpStatus.CREATED).body(createdStockImport);
    }
    
    /**
     * Find stock import by document code
     * 
     * GET /api/stock-imports/by-code/{documentCode}
     */
    @GetMapping("/by-code/{documentCode}")
    public ResponseEntity<StockImportDetailDTO> getStockImportByDocumentCode(
            @PathVariable String documentCode) {
        
        log.info("GET /api/stock-imports/by-code/{}", documentCode);
        
        StockImportDetailDTO stockImport = stockImportService.getStockImportByDocumentCode(documentCode);
        return ResponseEntity.ok(stockImport);
    }
    
    /**
     * Generate stock import report
     * 
     * GET /api/stock-imports/report?fromDate=2025-01-01&toDate=2025-12-31&supplierId=1&groupBy=day
     */
    @GetMapping("/report")
    public ResponseEntity<StockImportReportDTO> generateReport(
            @RequestParam(required = false) @DateTimeFormat(iso = DateTimeFormat.ISO.DATE) LocalDate fromDate,
            @RequestParam(required = false) @DateTimeFormat(iso = DateTimeFormat.ISO.DATE) LocalDate toDate,
            @RequestParam(required = false) Integer supplierId,
            @RequestParam(defaultValue = "day") String groupBy) {
        
        log.info("GET /api/stock-imports/report - fromDate: {}, toDate: {}, supplierId: {}, groupBy: {}", 
                fromDate, toDate, supplierId, groupBy);
        
        // Validate groupBy parameter
        if (!groupBy.matches("(?i)day|month|supplier")) {
            throw new IllegalArgumentException("Invalid groupBy parameter. Must be 'day', 'month', or 'supplier'");
        }
        
        StockImportReportDTO report = stockImportService.generateReport(fromDate, toDate, supplierId, groupBy);
        return ResponseEntity.ok(report);
    }
}
