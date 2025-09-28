package vn.techbox.techbox_store.product.dto;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.math.BigDecimal;
import java.time.LocalDateTime;
import java.util.List;

@Data
@NoArgsConstructor
@AllArgsConstructor
@Builder
public class ProductVariationResponse {
    
    private Integer id;
    private String variationName;
    private Integer productId;
    private BigDecimal price;
    private String sku;
    private List<String> imageUrls;
    private Integer quantity;
    private LocalDateTime createdAt;
    private LocalDateTime updatedAt;
    private LocalDateTime deletedAt;
    
    // Additional fields for related entities
    private String productName;
    
    // Helper methods
    public boolean isDeleted() {
        return deletedAt != null;
    }
    
    public boolean isInStock() {
        return quantity != null && quantity > 0;
    }
}