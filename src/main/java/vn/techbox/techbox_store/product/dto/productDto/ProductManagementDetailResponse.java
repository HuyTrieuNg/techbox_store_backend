package vn.techbox.techbox_store.product.dto.productDto;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;
import vn.techbox.techbox_store.product.model.ProductStatus;

import java.math.BigDecimal;
import java.time.LocalDateTime;
import java.util.List;

/**
 * DTO for Product Management Detail
 * Contains full product information for admin/management view and edit
 * Does NOT include variations (separate API)
 */
@Data
@NoArgsConstructor
@AllArgsConstructor
public class ProductManagementDetailResponse {
    
    // Basic Product Information
    private Integer id;
    private String spu;  // Stock Keeping Unit for Product
    private String name;
    private String description;
    private String imageUrl;
    private String imagePublicId;
    private ProductStatus status;
    private Integer warrantyMonths;
    
    // Category Information
    private Integer categoryId;
    private String categoryName;
    
    // Brand Information
    private Integer brandId;
    private String brandName;
    
    // Product Attributes (Color, Size, Material, etc.)
    private List<ProductAttributeDetail> attributes;
    
    // Display Prices (calculated from variations)
    private BigDecimal displayOriginalPrice;
    private BigDecimal displaySalePrice;
    
    // Rating & Review Stats
    private Double averageRating;
    private Integer totalRatings;
    
    // Variations Summary (not full details)
    private Integer totalVariations;
    private Integer activeVariations;
    
    // Metadata
    private LocalDateTime createdAt;
    private LocalDateTime updatedAt;
    private LocalDateTime deletedAt;  // For soft delete tracking
    
    /**
     * Inner class for Product Attribute details
     */
    @Data
    @NoArgsConstructor
    @AllArgsConstructor
    public static class ProductAttributeDetail {
        private Integer attributeId;
        private String attributeName;
        private String attributeValue;
    }
}
