package vn.techbox.techbox_store.product.dto.productDto;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;
import vn.techbox.techbox_store.product.model.ProductStatus;

import java.math.BigDecimal;
import java.sql.Date;
import java.time.LocalDateTime;

/**
 * DTO for product management list display - contains basic information for listing products
 */
@Data
@NoArgsConstructor
@AllArgsConstructor
@Builder
public class ProductManagementListResponse {
    
    private Integer id;
    
    private String name;
    
    private String imageUrl;

    private String spu;

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

    private ProductStatus status;

    private LocalDateTime createdAt;

    private LocalDateTime updatedAt;

    private LocalDateTime deleteAt;
    
}
