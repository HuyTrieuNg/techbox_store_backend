package vn.techbox.techbox_store.product.dto;

import jakarta.validation.constraints.DecimalMin;
import jakarta.validation.constraints.Min;
import jakarta.validation.constraints.NotNull;
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
public class ProductVariationCreateRequest {
    
    @Size(max = 255, message = "Variation name must not exceed 255 characters")
    private String variationName;
    
    @NotNull(message = "Product ID is required")
    private Integer productId;
    
    @NotNull(message = "Price is required")
    @DecimalMin(value = "0.0", inclusive = false, message = "Price must be greater than 0")
    private BigDecimal price;
    
    @Size(max = 255, message = "SKU must not exceed 255 characters")
    private String sku;
    
    private List<String> imageUrls; // List of image URLs
    private List<String> imagePublicIds; // List of Cloudinary public IDs

    @DecimalMin(value = "0.0", inclusive = false, message = "Avg cost price must be greater than 0")
    private BigDecimal avgCostPrice;

    private Integer warrantyMonths;
}