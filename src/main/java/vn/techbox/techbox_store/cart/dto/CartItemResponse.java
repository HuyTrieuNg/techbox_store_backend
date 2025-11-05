package vn.techbox.techbox_store.cart.dto;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.math.BigDecimal;
import java.time.LocalDateTime;

@Data
@Builder
@NoArgsConstructor
@AllArgsConstructor
public class CartItemResponse {

    private Integer id;
    private Integer productVariationId;
    private String productName;
    private String productImage;
    private String variantName;
    private Integer quantity;
    
    // Pricing information
    private BigDecimal originalPrice;    // Giá gốc (để hiển thị)
    private BigDecimal unitPrice;        // Giá bán sau khuyến mãi (để tính toán)
    private BigDecimal totalPrice;       // Tổng tiền = unitPrice * quantity
    
    // Promotion info (optional)
    private String discountType;         // PERCENTAGE hoặc FIXED
    private BigDecimal discountValue;  

    private LocalDateTime addedAt;
    private LocalDateTime updatedAt;

    private String sku;
    private Integer stockQuantity;
    private boolean isAvailable;
}
