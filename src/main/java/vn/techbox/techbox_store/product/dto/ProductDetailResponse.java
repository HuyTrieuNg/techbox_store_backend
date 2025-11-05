package vn.techbox.techbox_store.product.dto;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;
import vn.techbox.techbox_store.product.model.ProductStatus;

import java.math.BigDecimal;
import java.time.LocalDateTime;
import java.util.List;

/**
 * DTO for detailed product view - contains full product information including variations and attributes
 */
@Data
@NoArgsConstructor
@AllArgsConstructor
@Builder
public class ProductDetailResponse {
    
    // Basic product information
    private Integer id;
    private String name;
    private String description;
    private Integer categoryId;
    private String categoryName;
    private Integer brandId;
    private String brandName;
    private String imageUrl;
    private String imagePublicId;
    private ProductStatus status;
    private Integer warrantyMonths;
    
    // Rating information
    private Double averageRating;
    private Integer totalRatings;
    
    // Pricing information (for the lowest price variant)
    private BigDecimal displayOriginalPrice;
    private BigDecimal displaySalePrice;
    private String discountType;
    private BigDecimal discountValue;
    
    // Timestamps
    private LocalDateTime createdAt;
    private LocalDateTime updatedAt;
    
    // Product-level attributes (thuộc tính chung của sản phẩm)
    private List<AttributeDto> attributes;
    
    // All variations of this product
    private List<VariationDto> variations;
    
    /**
     * DTO for product attribute (thuộc tính của sản phẩm)
     */
    @Data
    @NoArgsConstructor
    @AllArgsConstructor
    @Builder
    public static class AttributeDto {
        private Integer id;          // attribute_id
        private String name;         // attribute name
        private String value;        // attribute value
    }
    
    /**
     * DTO for product variation with full details
     */
    @Data
    @NoArgsConstructor
    @AllArgsConstructor
    @Builder
    public static class VariationDto {
        // Basic variation information
        private Integer id;
        private String variationName;
        private BigDecimal price;
        private String sku;
        private Integer availableQuantity;  // Số lượng khả dụng (stockQuantity - reservedQuantity)
        private LocalDateTime createdAt;
        private LocalDateTime updatedAt;
        
        // Pricing with promotion (calculated realtime)
        private BigDecimal salePrice;        // Giá sau khi giảm
        private String discountType;         // PERCENTAGE hoặc FIXED
        private BigDecimal discountValue;    // Mức giảm
        
        // Variation images
        private List<ImageDto> images;
        
        // Variation-specific attributes (thuộc tính riêng của biến thể)
        private List<AttributeDto> attributes;
    }
    
    /**
     * DTO for variation image
     */
    @Data
    @NoArgsConstructor
    @AllArgsConstructor
    @Builder
    public static class ImageDto {
        private Integer id;
        private String imageUrl;
    }
}
