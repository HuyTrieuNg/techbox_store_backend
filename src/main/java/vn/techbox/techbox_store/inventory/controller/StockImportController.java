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
import org.springframework.web.bind.annotation.*;
import vn.techbox.techbox_store.inventory.dto.CreateStockImportRequest;
import vn.techbox.techbox_store.inventory.dto.StockImportDTO;
import vn.techbox.techbox_store.inventory.dto.StockImportDetailDTO;
import vn.techbox.techbox_store.inventory.service.StockImportService;

import org.springframework.security.core.annotation.AuthenticationPrincipal;
import vn.techbox.techbox_store.user.security.UserPrincipal;

import java.time.LocalDate;

@RestController
@RequestMapping("/stock-imports")
@RequiredArgsConstructor
@Slf4j
public class StockImportController {
    
    private final StockImportService stockImportService;
    
    /**
     * Get all stock imports with filters and pagination
     * 
     * GET /api/stock-imports?page=1&size=20&fromDate=2025-01-01&toDate=2025-12-31&supplierId=1&userId=1&documentCode=IMP
     */
    @PreAuthorize("hasAuthority('INVENTORY:READ')")
    @GetMapping
    public ResponseEntity<Page<StockImportDTO>> getAllStockImports(
            @RequestParam(defaultValue = "1") int page,
            @RequestParam(defaultValue = "20") int size,
            @RequestParam(required = false) @DateTimeFormat(iso = DateTimeFormat.ISO.DATE) LocalDate fromDate,
            @RequestParam(required = false) @DateTimeFormat(iso = DateTimeFormat.ISO.DATE) LocalDate toDate,
            @RequestParam(required = false) Integer supplierId,
            @RequestParam(required = false) Integer userId,
            @RequestParam(required = false) String documentCode) {
        
        log.info("GET /api/stock-imports - page: {}, size: {}, fromDate: {}, toDate: {}, supplierId: {}, userId: {}, documentCode: {}", 
                page, size, fromDate, toDate, supplierId, userId, documentCode);
        
        // Convert 1-based page to 0-based page for Spring Data JPA
        int zeroBasedPage = page - 1;
        if (zeroBasedPage < 0) {
            zeroBasedPage = 0;
        }
        
        Pageable pageable = PageRequest.of(zeroBasedPage, size, Sort.by(Sort.Direction.DESC, "importDate"));
        Page<StockImportDTO> stockImports = stockImportService.getAllStockImports(
                fromDate, toDate, supplierId, userId, documentCode, pageable);
        
        return ResponseEntity.ok(stockImports);
    }
    
    /**
     * Get stock import detail by ID
     * 
     * GET /api/stock-imports/{id}
     */
    @PreAuthorize("hasAuthority('INVENTORY:READ')")
    @GetMapping("/{id}")
    public ResponseEntity<StockImportDetailDTO> getStockImportById(@PathVariable Integer id) {
        log.info("GET /api/stock-imports/{}", id);
        
        StockImportDetailDTO stockImport = stockImportService.getStockImportById(id);
        return ResponseEntity.ok(stockImport);
    }
    
    @PreAuthorize("hasAuthority('INVENTORY:WRITE')")
    @PostMapping
    public ResponseEntity<StockImportDetailDTO> createStockImport(
            @Valid @RequestBody CreateStockImportRequest request,
            @AuthenticationPrincipal UserPrincipal userPrincipal) {
        
        log.info("POST /api/stock-imports - items count: {}", 
                request.getItems() != null ? request.getItems().size() : 0);
        
        Integer currentUserId = userPrincipal.getId();
        
        StockImportDetailDTO createdStockImport = stockImportService.createStockImport(request, currentUserId);
        return ResponseEntity.status(HttpStatus.CREATED).body(createdStockImport);
    }
    
    /**
     * Find stock import by document code
     * 
     * GET /api/stock-imports/by-code/{documentCode}
     */
    @PreAuthorize("hasAuthority('INVENTORY:READ')")
    @GetMapping("/by-code/{documentCode}")
    public ResponseEntity<StockImportDetailDTO> getStockImportByDocumentCode(
            @PathVariable String documentCode) {
        
        log.info("GET /api/stock-imports/by-code/{}", documentCode);
        
        StockImportDetailDTO stockImport = stockImportService.getStockImportByDocumentCode(documentCode);
        return ResponseEntity.ok(stockImport);
    }
}
