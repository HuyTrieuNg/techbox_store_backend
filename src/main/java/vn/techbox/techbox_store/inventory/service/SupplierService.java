package vn.techbox.techbox_store.inventory.service;

import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import vn.techbox.techbox_store.inventory.dto.CreateSupplierRequest;
import vn.techbox.techbox_store.inventory.dto.SupplierDTO;
import vn.techbox.techbox_store.inventory.dto.UpdateSupplierRequest;

public interface SupplierService {
    
    /**
     * Get all suppliers with optional keyword search and pagination
     * 
     * @param keyword Search keyword for name, phone, or email
     * @param includeDeleted Include soft-deleted suppliers
     * @param pageable Pagination information
     * @return Page of SupplierDTO
     */
    Page<SupplierDTO> getAllSuppliers(String keyword, boolean includeDeleted, Pageable pageable);
    
    /**
     * Get supplier by ID
     * 
     * @param supplierId Supplier ID
     * @return SupplierDTO
     * @throws RuntimeException if supplier not found
     */
    SupplierDTO getSupplierById(Integer supplierId);
    
    /**
     * Create new supplier
     * 
     * @param request CreateSupplierRequest
     * @return Created SupplierDTO
     * @throws RuntimeException if email or tax code already exists
     */
    SupplierDTO createSupplier(CreateSupplierRequest request);
    
    /**
     * Update supplier
     * 
     * @param supplierId Supplier ID
     * @param request UpdateSupplierRequest
     * @return Updated SupplierDTO
     * @throws RuntimeException if supplier not found or email/tax code already exists
     */
    SupplierDTO updateSupplier(Integer supplierId, UpdateSupplierRequest request);
    
    /**
     * Soft delete supplier
     * 
     * @param supplierId Supplier ID
     * @throws RuntimeException if supplier not found
     */
    void deleteSupplier(Integer supplierId);
    
    /**
     * Restore soft-deleted supplier
     * 
     * @param supplierId Supplier ID
     * @return Restored SupplierDTO
     * @throws RuntimeException if supplier not found or not deleted
     */
    SupplierDTO restoreSupplier(Integer supplierId);
}
