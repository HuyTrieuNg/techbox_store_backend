package vn.techbox.techbox_store.product.dto;

import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.Size;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@NoArgsConstructor
@AllArgsConstructor
@Builder
public class AttributeCreateRequest {
    
    @NotBlank(message = "Attribute name is required")
    @Size(max = 255, message = "Attribute name must not exceed 255 characters")
    private String name;
}