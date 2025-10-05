package vn.techbox.techbox_store.inventory.controller;

import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Pageable;
import org.springframework.data.domain.Sort;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;
import vn.techbox.techbox_store.inventory.dto.CreateSupplierRequest;
import vn.techbox.techbox_store.inventory.dto.SupplierDTO;
import vn.techbox.techbox_store.inventory.dto.UpdateSupplierRequest;
import vn.techbox.techbox_store.inventory.service.SupplierService;

@RestController
@RequestMapping("/api/suppliers")
@RequiredArgsConstructor
@Slf4j
public class SupplierController {
    
    private final SupplierService supplierService;
    
    /**
     * Get all suppliers with pagination and search
     * 
     * GET /api/suppliers?page=0&size=20&keyword=abc&includeDeleted=false
     */
    @GetMapping
    public ResponseEntity<Page<SupplierDTO>> getAllSuppliers(
            @RequestParam(defaultValue = "0") int page,
            @RequestParam(defaultValue = "20") int size,
            @RequestParam(required = false) String keyword,
            @RequestParam(defaultValue = "false") boolean includeDeleted) {
        
        log.info("GET /api/suppliers - page: {}, size: {}, keyword: {}, includeDeleted: {}", 
                page, size, keyword, includeDeleted);
        
        Pageable pageable = PageRequest.of(page, size, Sort.by(Sort.Direction.DESC, "createdAt"));
        Page<SupplierDTO> suppliers = supplierService.getAllSuppliers(keyword, includeDeleted, pageable);
        
        return ResponseEntity.ok(suppliers);
    }
    
    /**
     * Get supplier by ID
     * 
     * GET /api/suppliers/{supplierId}
     */
    @GetMapping("/{supplierId}")
    public ResponseEntity<SupplierDTO> getSupplierById(@PathVariable Integer supplierId) {
        log.info("GET /api/suppliers/{}", supplierId);
        
        SupplierDTO supplier = supplierService.getSupplierById(supplierId);
        return ResponseEntity.ok(supplier);
    }
    
    /**
     * Create new supplier
     * 
     * POST /api/suppliers
     */
    @PostMapping
    public ResponseEntity<SupplierDTO> createSupplier(@Valid @RequestBody CreateSupplierRequest request) {
        log.info("POST /api/suppliers - name: {}", request.getName());
        
        SupplierDTO createdSupplier = supplierService.createSupplier(request);
        return ResponseEntity.status(HttpStatus.CREATED).body(createdSupplier);
    }
    
    /**
     * Update supplier
     * 
     * PUT /api/suppliers/{supplierId}
     */
    @PutMapping("/{supplierId}")
    public ResponseEntity<SupplierDTO> updateSupplier(
            @PathVariable Integer supplierId,
            @Valid @RequestBody UpdateSupplierRequest request) {
        
        log.info("PUT /api/suppliers/{}", supplierId);
        
        SupplierDTO updatedSupplier = supplierService.updateSupplier(supplierId, request);
        return ResponseEntity.ok(updatedSupplier);
    }
    
    /**
     * Soft delete supplier
     * 
     * DELETE /api/suppliers/{supplierId}
     */
    @DeleteMapping("/{supplierId}")
    public ResponseEntity<Void> deleteSupplier(@PathVariable Integer supplierId) {
        log.info("DELETE /api/suppliers/{}", supplierId);
        
        supplierService.deleteSupplier(supplierId);
        return ResponseEntity.noContent().build();
    }
    
    /**
     * Restore soft-deleted supplier
     * 
     * POST /api/suppliers/{supplierId}/restore
     */
    @PostMapping("/{supplierId}/restore")
    public ResponseEntity<SupplierDTO> restoreSupplier(@PathVariable Integer supplierId) {
        log.info("POST /api/suppliers/{}/restore", supplierId);
        
        SupplierDTO restoredSupplier = supplierService.restoreSupplier(supplierId);
        return ResponseEntity.ok(restoredSupplier);
    }
}
