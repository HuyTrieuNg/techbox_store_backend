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
    private BigDecimal unitPrice;
    private BigDecimal totalPrice;
    private LocalDateTime addedAt;
    private LocalDateTime updatedAt;

    private String sku;
    private Integer stockQuantity;
    private boolean isAvailable;
}
