package vn.techbox.techbox_store.promotion.dto;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;
import vn.techbox.techbox_store.promotion.model.PromotionType;

import java.math.BigDecimal;

@Data
@Builder
@NoArgsConstructor
@AllArgsConstructor
public class PromotionVariantResponse {
    private Integer promotionId;
    private Integer campaignId;
    private String campaignName;

    // Product info
    private Integer productId;
    private String productName;
    private String productSpu;

    // Variation info
    private Integer variationId;
    private String sku;
    private String variationName;

    // Prices
    private BigDecimal originalPrice;
    private BigDecimal discountedPrice;

    // Promotion details
    private PromotionType discountType;
    private BigDecimal discountValue;
}
