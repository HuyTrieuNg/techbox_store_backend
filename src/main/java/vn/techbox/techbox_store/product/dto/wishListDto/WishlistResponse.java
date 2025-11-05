package vn.techbox.techbox_store.product.dto.wishListDto;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.math.BigDecimal;
import java.time.LocalDateTime;

/**
 * DTO for wishlist items - extends product list information with wishlist metadata
 */
@Data
@NoArgsConstructor
@AllArgsConstructor
@Builder
public class WishlistResponse {
    
    // Wishlist metadata
    private Integer wishlistId;
    private LocalDateTime addedAt;
    
    // Product information (same as ProductListResponse)
    private Integer productId;
    private String name;
    private String imageUrl;
    
    // Giá gốc của biến thể có giá thấp nhất
    private BigDecimal displayOriginalPrice;
    
    // Giá sau khi giảm của biến thể có giá thấp nhất
    private BigDecimal displaySalePrice;
    
    // Loại giảm giá: PERCENTAGE hoặc FIXED
    private String discountType;
    
    // Mức giảm giá
    private BigDecimal discountValue;
    
    // Số sao trung bình
    private Double averageRating;
    
    // Tổng số đánh giá
    private Integer totalRatings;
    
    // Luôn true cho wishlist items
    @Builder.Default
    private Boolean inWishlist = true;
}
