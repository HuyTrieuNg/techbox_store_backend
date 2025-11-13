package vn.techbox.techbox_store.product.mapper;

import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Component;
import vn.techbox.techbox_store.product.dto.productDto.ProductVariationResponse;
import vn.techbox.techbox_store.product.model.ProductVariation;
import vn.techbox.techbox_store.product.model.ProductVariationImage;
import vn.techbox.techbox_store.product.repository.ProductRepository;
import vn.techbox.techbox_store.product.repository.ProductVariationImageRepository;
import vn.techbox.techbox_store.promotion.model.Promotion;
import vn.techbox.techbox_store.promotion.model.PromotionType;
import vn.techbox.techbox_store.promotion.repository.PromotionRepository;

import java.math.BigDecimal;
import java.math.RoundingMode;
import java.time.LocalDateTime;
import java.util.List;
import java.util.Optional;
import java.util.stream.Collectors;

@Component
@RequiredArgsConstructor
public class ProductVariationMapper {
    
    private final ProductVariationImageRepository productVariationImageRepository;
    private final ProductRepository productRepository;
    private final PromotionRepository promotionRepository;
    
    /**
     * Convert ProductVariation entity to ProductVariationResponse DTO
     */
    public ProductVariationResponse toResponse(ProductVariation variation) {
        if (variation == null) {
            return null;
        }
        
        // Calculate available quantity
        Integer availableQuantity = variation.getStockQuantity() - variation.getReservedQuantity();
        
        // Get active promotion for this variation
        List<Promotion> activePromotions = promotionRepository.findActivePromotionsByProductVariationId(
            variation.getId(), 
            LocalDateTime.now()
        );
        
        // Find best promotion (highest discount)
        Optional<Promotion> bestPromotion = activePromotions.stream()
            .filter(p -> p.getCampaign() != null && p.getCampaign().isActive() && p.isValid())
            .max((p1, p2) -> {
                BigDecimal discount1 = calculateDiscount(variation.getPrice(), p1);
                BigDecimal discount2 = calculateDiscount(variation.getPrice(), p2);
                return discount1.compareTo(discount2);
            });
        
        // Build response with pricing information
        ProductVariationResponse.ProductVariationResponseBuilder builder = ProductVariationResponse.builder()
                .id(variation.getId())
                .variationName(variation.getVariationName())
                .productId(variation.getProductId())
                .price(variation.getPrice())
                .imageUrls(getProductVariationImageUrls(variation.getId()))
                .availableQuantity(availableQuantity);
        
        // Set promotion pricing if available
        if (bestPromotion.isPresent()) {
            Promotion promotion = bestPromotion.get();
            BigDecimal salePrice = calculateSalePrice(variation.getPrice(), promotion);
            
            builder.salePrice(salePrice)
                   .discountType(promotion.getDiscountType().name())
                   .discountValue(promotion.getDiscountValue());
        } else {
            // No promotion, salePrice equals original price
            builder.salePrice(variation.getPrice())
                   .discountType(null)
                   .discountValue(null);
        }
        
        ProductVariationResponse response = builder.build();
        
        // Set product name if productId exists
        if (variation.getProductId() != null) {
            productRepository.findById(variation.getProductId())
                    .ifPresent(product -> response.setProductName(product.getName()));
        }
        
        return response;
    }
    
    /**
     * Calculate discount amount for a promotion
     */
    private BigDecimal calculateDiscount(BigDecimal price, Promotion promotion) {
        if (promotion.getDiscountType() == PromotionType.PERCENTAGE) {
            return price.multiply(promotion.getDiscountValue())
                       .divide(BigDecimal.valueOf(100), 2, RoundingMode.HALF_UP);
        } else { // FIXED
            return promotion.getDiscountValue();
        }
    }
    
    /**
     * Calculate sale price after applying promotion
     */
    private BigDecimal calculateSalePrice(BigDecimal price, Promotion promotion) {
        BigDecimal discount = calculateDiscount(price, promotion);
        BigDecimal salePrice = price.subtract(discount);
        return salePrice.max(BigDecimal.ZERO); // Ensure non-negative
    }
    
    /**
     * Helper method to get product variation image URLs
     */
    private List<String> getProductVariationImageUrls(Integer variationId) {
        return productVariationImageRepository
                .findByProductVariationId(variationId)
                .stream()
                .map(ProductVariationImage::getImageUrl)
                .collect(Collectors.toList());
    }
}
