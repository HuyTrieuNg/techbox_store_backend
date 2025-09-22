package vn.techbox.techbox_store.product.dto;

import jakarta.validation.constraints.Size;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;
import vn.techbox.techbox_store.product.model.AttributeDataType;

@Data
@NoArgsConstructor
@AllArgsConstructor
@Builder
public class AttributeUpdateRequest {
    
    @Size(max = 255, message = "Attribute name must not exceed 255 characters")
    private String name;
    
    private AttributeDataType dataType;
}