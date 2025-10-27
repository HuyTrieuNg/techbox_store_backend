package vn.techbox.techbox_store.product.dto;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.math.BigDecimal;

/**
 * DTO for product list display - contains basic information for listing products
 */
@Data
@NoArgsConstructor
@AllArgsConstructor
@Builder
public class ProductListResponse {
    
    private Integer id;
    
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
    
    // Có trong wishlist không (false nếu chưa đăng nhập)
    @Builder.Default
    private Boolean inWishlist = false;
}
