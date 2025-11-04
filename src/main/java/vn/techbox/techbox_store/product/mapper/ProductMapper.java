package vn.techbox.techbox_store.product.mapper;

import java.math.BigDecimal;
import java.util.List;
import java.util.Optional;
import java.util.stream.Collectors;

import org.springframework.stereotype.Component;

import vn.techbox.techbox_store.product.dto.ProductDetailResponse;
import vn.techbox.techbox_store.product.dto.ProductListResponse;
import vn.techbox.techbox_store.product.model.Product;
import vn.techbox.techbox_store.product.model.ProductVariation;
import vn.techbox.techbox_store.product.model.ProductVariationImage;
import vn.techbox.techbox_store.product.model.VariationAttribute;
import vn.techbox.techbox_store.promotion.model.Promotion;
import vn.techbox.techbox_store.promotion.model.PromotionType;

@Component
public class ProductMapper {
    
    /**
     * Map Product entity to ProductListResponse DTO
     * Used for product listing/filtering
     */
    public ProductListResponse toListResponse(Product product) {
        if (product == null) return null;

        return ProductListResponse.builder()
                .id(product.getId())
                .name(product.getName())
                .imageUrl(product.getImageUrl())
                .displayOriginalPrice(product.getDisplayOriginalPrice())
                .displaySalePrice(product.getDisplaySalePrice())
                .discountType(product.getDiscountType())
                .discountValue(product.getDiscountValue())
                .averageRating(product.getAverageRating())
                .totalRatings(product.getTotalRatings())
                .build();
    }

    /**
     * Map Product entity (fully fetched with @EntityGraph) to ProductDetailResponse DTO
     * This method expects Product to be fully loaded with all relationships:
     * - category, brand, productAttributes, variations, images, variationAttributes, promotions
     * 
     * @param product Fully fetched Product entity
     * @return ProductDetailResponse with complete product details
     */
    public ProductDetailResponse toDetailResponse(Product product) {
        if (product == null) return null;

        // Get category and brand names (safe because of JOIN FETCH)
        String categoryName = (product.getCategory() != null) 
                ? product.getCategory().getName() 
                : null;
        
        String brandName = (product.getBrand() != null) 
                ? product.getBrand().getName() 
                : null;
        
        // Get product attributes (safe because of JOIN FETCH)
        List<ProductDetailResponse.AttributeDto> productAttributes = 
                product.getProductAttributes().stream()
                        .map(pa -> ProductDetailResponse.AttributeDto.builder()
                                .id(pa.getAttribute().getId())
                                .name(pa.getAttribute().getName())
                                .value(pa.getValue())
                                .build())
                        .collect(Collectors.toList());
        
        // Convert variations (safe because of JOIN FETCH)
        List<ProductDetailResponse.VariationDto> variations = 
                product.getProductVariations().stream()
                        .map(this::convertToVariationDto)
                        .collect(Collectors.toList());

        return ProductDetailResponse.builder()
                .id(product.getId())
                .name(product.getName())
                .description(product.getDescription())
                .categoryId(product.getCategoryId())
                .categoryName(categoryName)
                .brandId(product.getBrandId())
                .brandName(brandName)
                .imageUrl(product.getImageUrl())
                .imagePublicId(product.getImagePublicId())
                .status(product.getStatus())
                .warrantyMonths(product.getWarrantyMonths())
                .averageRating(product.getAverageRating())
                .totalRatings(product.getTotalRatings())
                .displayOriginalPrice(product.getDisplayOriginalPrice())
                .displaySalePrice(product.getDisplaySalePrice())
                .discountType(product.getDiscountType())
                .discountValue(product.getDiscountValue())
                .createdAt(product.getCreatedAt())
                .updatedAt(product.getUpdatedAt())
                .attributes(productAttributes)
                .variations(variations)
                .build();
    }
    
    /**
     * Convert ProductVariation entity to VariationDto
     * Works with pre-loaded relationships (images, variationAttributes, promotions)
     * 
     * @param variation ProductVariation entity with pre-loaded relationships
     * @return VariationDto with all details including calculated sale price
     */
    private ProductDetailResponse.VariationDto convertToVariationDto(ProductVariation variation) {
        
        // Get pre-loaded relationships (already fetched via @EntityGraph)
        List<ProductVariationImage> images = variation.getImages();
        List<VariationAttribute> variationAttributes = variation.getVariationAttributes();
        List<Promotion> promotions = variation.getPromotions();
        
        // Convert images to DTOs
        List<ProductDetailResponse.ImageDto> imageDtos = images.stream()
                .map(img -> ProductDetailResponse.ImageDto.builder()
                        .id(img.getId())
                        .imageUrl(img.getImageUrl())
                        .build())
                .collect(Collectors.toList());
        
        // Convert variation attributes to DTOs
        List<ProductDetailResponse.AttributeDto> attributeDtos = variationAttributes.stream()
                .map(va -> ProductDetailResponse.AttributeDto.builder()
                        .id(va.getAttributeId())
                        .name(va.getAttribute().getName())
                        .value(va.getValue())
                        .build())
                .collect(Collectors.toList());
        
        // Calculate realtime pricing with active promotions
        BigDecimal salePrice = variation.getPrice();
        String discountType = null;
        BigDecimal discountValue = null;
        
        // Get the first active promotion (assuming only one active promotion per variation at a time)
        Optional<Promotion> activePromotion = promotions.stream()
                .filter(Promotion::isActive)
                .findFirst();
        
        if (activePromotion.isPresent()) {
            Promotion promo = activePromotion.get();
            discountType = promo.getDiscountType().name();
            discountValue = promo.getDiscountValue();
            
            // Calculate sale price based on promotion type
            if (promo.getDiscountType() == PromotionType.PERCENTAGE) {
                // Percentage discount: price - (price * discountValue / 100)
                BigDecimal discountAmount = variation.getPrice()
                        .multiply(discountValue)
                        .divide(BigDecimal.valueOf(100), 2, java.math.RoundingMode.HALF_UP);
                salePrice = variation.getPrice().subtract(discountAmount);
            } else if (promo.getDiscountType() == PromotionType.FIXED) {
                // Fixed discount: price - discountValue
                salePrice = variation.getPrice().subtract(discountValue);
                // Ensure price doesn't go below zero
                if (salePrice.compareTo(BigDecimal.ZERO) < 0) {
                    salePrice = BigDecimal.ZERO;
                }
            }
        }
        
        // Calculate available quantity (stock - reserved)
        Integer availableQuantity = variation.getAvailableQuantity();
        
        return ProductDetailResponse.VariationDto.builder()
                .id(variation.getId())
                .variationName(variation.getVariationName())
                .price(variation.getPrice())
                .sku(variation.getSku())
                .availableQuantity(availableQuantity)
                .createdAt(variation.getCreatedAt())
                .updatedAt(variation.getUpdatedAt())
                .salePrice(salePrice)
                .discountType(discountType)
                .discountValue(discountValue)
                .images(imageDtos)
                .attributes(attributeDtos)
                .build();
    }
}
