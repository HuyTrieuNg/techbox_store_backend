package vn.techbox.techbox_store.product.dto.productDto;

import jakarta.validation.constraints.PositiveOrZero;
import jakarta.validation.constraints.Size;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;
import jakarta.validation.Valid;

import java.util.List;

@Data
@NoArgsConstructor
@AllArgsConstructor
@Builder
public class ProductUpdateRequest {
    
    @Size(max = 255, message = "Product name must not exceed 255 characters")
    private String name;
    
    @Size(max = 5000, message = "Description must not exceed 5000 characters")
    private String description;
    
    private Integer categoryId;
    
    private Integer brandId;
    
    @Size(max = 255, message = "Image URL must not exceed 255 characters")
    private String imageUrl;
    
    @Size(max = 255, message = "Image public ID must not exceed 255 characters")
    private String imagePublicId;

    @PositiveOrZero(message = "Base price must be zero or positive")
    private Integer warrantyMonths;

    @Valid
    private List<AttributeRequest> attributes; // List of product attributes
}