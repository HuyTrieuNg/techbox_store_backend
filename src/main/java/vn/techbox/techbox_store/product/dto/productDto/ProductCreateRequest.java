package vn.techbox.techbox_store.product.dto.productDto;

import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.Size;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;
import vn.techbox.techbox_store.product.model.ProductStatus;

@Data
@NoArgsConstructor
@AllArgsConstructor
@Builder
public class ProductCreateRequest {
    
    @NotBlank(message = "Product name is required")
    @Size(max = 255, message = "Product name must not exceed 255 characters")
    private String name;
    
    @Size(max = 5000, message = "Description must not exceed 5000 characters")
    private String description;
    
    private Integer categoryId;
    
    private Integer brandId;

    private ProductStatus status; // PUBLISHED, DRAFT, ARCHIVED

    private Integer warrantyMonths; // in months
    
    @Size(max = 255, message = "Image URL must not exceed 255 characters")
    private String imageUrl;
    
    @Size(max = 255, message = "Image public ID must not exceed 255 characters")
    private String imagePublicId;
}