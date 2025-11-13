package vn.techbox.techbox_store.product.dto.productDto;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;
import vn.techbox.techbox_store.promotion.model.PromotionType;

import java.math.BigDecimal;
import java.time.LocalDateTime;
import java.util.List;

/**
 * DTO for Product Variation Management Detail
 * Contains full variation information for admin/management view and edit
 * Used with Product Management Detail API
 */
@Data
@NoArgsConstructor
@AllArgsConstructor
@Builder
public class ProductVariationManagementResponse {
    
    // Basic Variation Information
    private Integer id;
    private Integer productId;
    private String sku;  // Stock Keeping Unit
    private String variationName;
    
    // Pricing
    private BigDecimal price;  // Original price
    private BigDecimal salePrice;  // Price after promotion (if any)
    
    // Stock Information
    private Integer stock;  // Total stock quantity
    private Integer reservedQuantity;  // Reserved for pending orders
    private Integer availableQuantity;  // Available = stock - reserved
    
    // Promotion Information (if active promotion exists)
    private Integer promotionId;
    private String promotionName;
    private PromotionType discountType;  // PERCENTAGE or FIXED
    private BigDecimal discountValue;
    private LocalDateTime promotionStartDate;
    private LocalDateTime promotionEndDate;
    
    // Images
    private List<ImageDetail> images;
    
    // Variation Attributes (Color, Size, etc.)
    private List<AttributeDetail> attributes;
    
    // Metadata
    private LocalDateTime createdAt;
    private LocalDateTime updatedAt;
    private LocalDateTime deletedAt;  // For soft delete tracking
    
    /**
     * Inner class for Image details
     */
    @Data
    @NoArgsConstructor
    @AllArgsConstructor
    @Builder
    public static class ImageDetail {
        private Integer id;
        private String imageUrl;
        private String imagePublicId;
    }
    
    /**
     * Inner class for Variation Attribute details
     */
    @Data
    @NoArgsConstructor
    @AllArgsConstructor
    @Builder
    public static class AttributeDetail {
        private Integer attributeId;
        private String attributeName;  // e.g., "Color", "Size"
        private String attributeValue; // e.g., "Red", "XL"
    }
}
