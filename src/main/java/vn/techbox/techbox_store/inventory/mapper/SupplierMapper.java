package vn.techbox.techbox_store.inventory.mapper;

import org.springframework.stereotype.Component;
import vn.techbox.techbox_store.inventory.dto.CreateSupplierRequest;
import vn.techbox.techbox_store.inventory.dto.SupplierDTO;
import vn.techbox.techbox_store.inventory.dto.UpdateSupplierRequest;
import vn.techbox.techbox_store.inventory.model.Supplier;

@Component
public class SupplierMapper {
    
    /**
     * Convert Supplier entity to SupplierDTO
     */
    public SupplierDTO toDTO(Supplier supplier) {
        if (supplier == null) {
            return null;
        }
        
        return SupplierDTO.builder()
                .supplierId(supplier.getSupplierId())
                .name(supplier.getName())
                .phone(supplier.getPhone())
                .email(supplier.getEmail())
                .address(supplier.getAddress())
                .taxCode(supplier.getTaxCode())
                .createdAt(supplier.getCreatedAt())
                .updatedAt(supplier.getUpdatedAt())
                .deletedAt(supplier.getDeletedAt())
                .deleted(supplier.isDeleted())
                .build();
    }
    
    /**
     * Convert CreateSupplierRequest to Supplier entity
     */
    public Supplier toEntity(CreateSupplierRequest request) {
        if (request == null) {
            return null;
        }
        
        return Supplier.builder()
                .name(request.getName())
                .phone(request.getPhone())
                .email(request.getEmail())
                .address(request.getAddress())
                .taxCode(request.getTaxCode())
                .build();
    }
    
    /**
     * Update Supplier entity from UpdateSupplierRequest
     */
    public void updateEntity(Supplier supplier, UpdateSupplierRequest request) {
        if (supplier == null || request == null) {
            return;
        }
        
        if (request.getName() != null) {
            supplier.setName(request.getName());
        }
        if (request.getPhone() != null) {
            supplier.setPhone(request.getPhone());
        }
        if (request.getEmail() != null) {
            supplier.setEmail(request.getEmail());
        }
        if (request.getAddress() != null) {
            supplier.setAddress(request.getAddress());
        }
        if (request.getTaxCode() != null) {
            supplier.setTaxCode(request.getTaxCode());
        }
    }
}
