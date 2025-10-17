package vn.techbox.techbox_store.inventory.service;

import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import vn.techbox.techbox_store.inventory.dto.CreateSupplierRequest;
import vn.techbox.techbox_store.inventory.dto.SupplierDTO;
import vn.techbox.techbox_store.inventory.dto.UpdateSupplierRequest;
import vn.techbox.techbox_store.inventory.mapper.SupplierMapper;
import vn.techbox.techbox_store.inventory.model.Supplier;
import vn.techbox.techbox_store.inventory.repository.SupplierRepository;
import vn.techbox.techbox_store.inventory.service.impl.SupplierService;

@Service
@RequiredArgsConstructor
@Slf4j
@Transactional
public class SupplierServiceImpl implements SupplierService {
    
    private final SupplierRepository supplierRepository;
    private final SupplierMapper supplierMapper;
    
    @Override
    @Transactional(readOnly = true)
    public Page<SupplierDTO> getAllSuppliers(String keyword, boolean includeDeleted, Pageable pageable) {
        log.info("Getting all suppliers with keyword: {}, includeDeleted: {}, page: {}", 
                keyword, includeDeleted, pageable.getPageNumber());
        
        Page<Supplier> suppliers;
        
        if (includeDeleted) {
            suppliers = supplierRepository.findAllIncludingDeleted(keyword, pageable);
        } else {
            suppliers = supplierRepository.findAllNotDeleted(keyword, pageable);
        }
        
        return suppliers.map(supplierMapper::toDTO);
    }
    
    @Override
    @Transactional(readOnly = true)
    public SupplierDTO getSupplierById(Integer supplierId) {
        log.info("Getting supplier by ID: {}", supplierId);
        
        Supplier supplier = supplierRepository.findById(supplierId)
                .orElseThrow(() -> new RuntimeException("Supplier not found with ID: " + supplierId));
        
        return supplierMapper.toDTO(supplier);
    }
    
    @Override
    public SupplierDTO createSupplier(CreateSupplierRequest request) {
        log.info("Creating new supplier with name: {}", request.getName());
        
        // Validate email uniqueness
        if (request.getEmail() != null && !request.getEmail().isEmpty()) {
            if (supplierRepository.existsByEmail(request.getEmail(), null)) {
                throw new RuntimeException("Email already exists: " + request.getEmail());
            }
        }
        
        // Validate tax code uniqueness
        if (request.getTaxCode() != null && !request.getTaxCode().isEmpty()) {
            if (supplierRepository.existsByTaxCode(request.getTaxCode(), null)) {
                throw new RuntimeException("Tax code already exists: " + request.getTaxCode());
            }
        }
        
        Supplier supplier = supplierMapper.toEntity(request);
        Supplier savedSupplier = supplierRepository.save(supplier);
        
        log.info("Created supplier with ID: {}", savedSupplier.getSupplierId());
        return supplierMapper.toDTO(savedSupplier);
    }
    
    @Override
    public SupplierDTO updateSupplier(Integer supplierId, UpdateSupplierRequest request) {
        log.info("Updating supplier with ID: {}", supplierId);
        
        Supplier supplier = supplierRepository.findByIdAndNotDeleted(supplierId)
                .orElseThrow(() -> new RuntimeException("Supplier not found with ID: " + supplierId));
        
        // Validate email uniqueness (excluding current supplier)
        if (request.getEmail() != null && !request.getEmail().isEmpty()) {
            if (supplierRepository.existsByEmail(request.getEmail(), supplierId)) {
                throw new RuntimeException("Email already exists: " + request.getEmail());
            }
        }
        
        // Validate tax code uniqueness (excluding current supplier)
        if (request.getTaxCode() != null && !request.getTaxCode().isEmpty()) {
            if (supplierRepository.existsByTaxCode(request.getTaxCode(), supplierId)) {
                throw new RuntimeException("Tax code already exists: " + request.getTaxCode());
            }
        }
        
        supplierMapper.updateEntity(supplier, request);
        Supplier updatedSupplier = supplierRepository.save(supplier);
        
        log.info("Updated supplier with ID: {}", supplierId);
        return supplierMapper.toDTO(updatedSupplier);
    }
    
    @Override
    public void deleteSupplier(Integer supplierId) {
        log.info("Soft deleting supplier with ID: {}", supplierId);
        
        Supplier supplier = supplierRepository.findByIdAndNotDeleted(supplierId)
                .orElseThrow(() -> new RuntimeException("Supplier not found with ID: " + supplierId));
        
        supplier.softDelete();
        supplierRepository.save(supplier);
        
        log.info("Soft deleted supplier with ID: {}", supplierId);
    }
    
    @Override
    public SupplierDTO restoreSupplier(Integer supplierId) {
        log.info("Restoring supplier with ID: {}", supplierId);
        
        Supplier supplier = supplierRepository.findById(supplierId)
                .orElseThrow(() -> new RuntimeException("Supplier not found with ID: " + supplierId));
        
        if (!supplier.isDeleted()) {
            throw new RuntimeException("Supplier is not deleted. ID: " + supplierId);
        }
        
        supplier.setDeletedAt(null);
        Supplier restoredSupplier = supplierRepository.save(supplier);
        
        log.info("Restored supplier with ID: {}", supplierId);
        return supplierMapper.toDTO(restoredSupplier);
    }
}
