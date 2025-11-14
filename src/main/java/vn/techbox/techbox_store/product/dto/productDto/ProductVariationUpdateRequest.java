package vn.techbox.techbox_store.product.dto.productDto;

import jakarta.validation.Valid;
import jakarta.validation.constraints.DecimalMin;
import jakarta.validation.constraints.Size;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.math.BigDecimal;
import java.util.List;

@Data
@NoArgsConstructor
@AllArgsConstructor
@Builder
public class ProductVariationUpdateRequest {
    
    @Size(max = 255, message = "Variation name must not exceed 255 characters")
    private String variationName;
    
    @DecimalMin(value = "0.0", inclusive = false, message = "Price must be greater than 0")
    private BigDecimal price;
    
    @Size(max = 255, message = "SKU must not exceed 255 characters")
    private String sku;
    
    private List<String> imageUrls; // List of new image URLs
    private List<String> imagePublicIds; // List of new Cloudinary public IDs
    private List<String> deleteImageIds; // List of public image IDs to delete

    @Valid
    private List<VariationAttributeRequest> variationAttributes;

}