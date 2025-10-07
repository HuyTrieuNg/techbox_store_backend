package vn.techbox.techbox_store.inventory.dto;

import jakarta.validation.constraints.Email;
import jakarta.validation.constraints.Size;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@NoArgsConstructor
@AllArgsConstructor
@Builder
public class UpdateSupplierRequest {
    
    @Size(max = 255, message = "Supplier name must not exceed 255 characters")
    private String name;
    
    @Size(max = 50, message = "Phone number must not exceed 50 characters")
    private String phone;
    
    @Email(message = "Invalid email format")
    @Size(max = 100, message = "Email must not exceed 100 characters")
    private String email;
    
    @Size(max = 255, message = "Address must not exceed 255 characters")
    private String address;
    
    @Size(max = 50, message = "Tax code must not exceed 50 characters")
    private String taxCode;
}
