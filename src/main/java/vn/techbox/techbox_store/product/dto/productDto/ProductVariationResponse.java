package vn.techbox.techbox_store.product.dto.productDto;

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
public class ProductVariationResponse {
    
    private Integer id;
    private String variationName;
    private Integer productId;
    private BigDecimal price;

    private List<String> imageUrls;
    private Integer availableQuantity;

    // Pricing with promotion (calculated realtime)
    private BigDecimal salePrice;        // Giá sau khi giảm
    private String discountType;         // PERCENTAGE hoặc FIXED
    private BigDecimal discountValue;    // Mức giảm
    
    // Additional fields for related entities
    private String productName;
}