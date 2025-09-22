package vn.techbox.techbox_store.product.dto;

import jakarta.validation.constraints.DecimalMin;
import jakarta.validation.constraints.Min;
import jakarta.validation.constraints.Size;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.math.BigDecimal;

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
    
    private String imageUrl; // JSON string for multiple images
    
    @Min(value = 0, message = "Quantity must be non-negative")
    private Integer quantity;
}