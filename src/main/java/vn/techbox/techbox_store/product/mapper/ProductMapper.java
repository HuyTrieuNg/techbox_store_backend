package vn.techbox.techbox_store.product.mapper;

import java.math.BigDecimal;
import java.util.List;
import java.util.Map;
import java.util.Optional;
import java.util.stream.Collectors;

import org.springframework.stereotype.Component;

import vn.techbox.techbox_store.product.dto.productDto.ProductDetailResponse;
import vn.techbox.techbox_store.product.dto.productDto.ProductListResponse;
import vn.techbox.techbox_store.product.dto.productDto.ProductResponse;
import vn.techbox.techbox_store.product.model.Product;
import vn.techbox.techbox_store.product.model.ProductAttribute;
import vn.techbox.techbox_store.product.model.ProductVariation;
import vn.techbox.techbox_store.product.model.ProductVariationImage;
import vn.techbox.techbox_store.product.model.VariationAttribute;
import vn.techbox.techbox_store.promotion.model.Promotion;
import vn.techbox.techbox_store.promotion.model.PromotionType;

@Component
public class ProductMapper {
    
    /**
     * Convert Product entity to ProductListResponse DTO
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
     * Convert Product entity to ProductResponse DTO
     */
    public ProductResponse toResponse(Product product, String categoryName, String brandName) {
        if (product == null) return null;
        
        ProductResponse response = ProductResponse.builder()
                .id(product.getId())
                .name(product.getName())
                .description(product.getDescription())
                .categoryId(product.getCategoryId())
                .brandId(product.getBrandId())
                .imageUrl(product.getImageUrl())
                .imagePublicId(product.getImagePublicId())
                .status(product.getStatus())
                .warrantyMonths(product.getWarrantyMonths())
                .createdAt(product.getCreatedAt())
                .updatedAt(product.getUpdatedAt())
                .deletedAt(product.getDeletedAt())
                .categoryName(categoryName)
                .brandName(brandName)
                .build();
        
        return response;
    }

    /**
     * Convert Product entity to ProductDetailResponse DTO with pre-loaded data
     */
    public ProductDetailResponse toDetailResponse(
            Product product, 
            String categoryName,
            String brandName,
            List<ProductAttribute> productAttributes,
            List<ProductVariation> productVariations,
            Map<Integer, List<ProductVariationImage>> imagesMap,
            Map<Integer, List<VariationAttribute>> variationAttributesMap,
            Map<Integer, List<Promotion>> promotionsMap) {
        
        if (product == null) return null;
        
        // Convert product attributes to DTOs
        List<ProductDetailResponse.AttributeDto> productAttributeDtos = 
                productAttributes.stream()
                    .map(pa -> ProductDetailResponse.AttributeDto.builder()
                            .id(pa.getAttributeId())
                            .name(pa.getAttribute().getName())
                            .value(pa.getValue())
                            .build())
                    .collect(Collectors.toList());
        
        // Convert variations to DTOs with pre-loaded data
        List<ProductDetailResponse.VariationDto> variations = 
                productVariations.stream()
                    .map(variation -> toVariationDto(
                            variation,
                            imagesMap.getOrDefault(variation.getId(), List.of()),
                            variationAttributesMap.getOrDefault(variation.getId(), List.of()),
                            promotionsMap.getOrDefault(variation.getId(), List.of())
                    ))
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
                .warrantyMonths(product.getWarrantyMonths())
                .averageRating(product.getAverageRating())
                .totalRatings(product.getTotalRatings())
                .displayOriginalPrice(product.getDisplayOriginalPrice())
                .displaySalePrice(product.getDisplaySalePrice())
                .discountType(product.getDiscountType())
                .discountValue(product.getDiscountValue())
                .attributes(productAttributeDtos)
                .variations(variations)
                .build();
    }
    
    /**
     * Convert ProductVariation to VariationDto with pre-loaded related data
     */
    private ProductDetailResponse.VariationDto toVariationDto(
            ProductVariation variation,
            List<ProductVariationImage> images,
            List<VariationAttribute> variationAttributes,
            List<Promotion> promotions) {
        
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
                .availableQuantity(availableQuantity)
                .salePrice(salePrice)
                .discountType(discountType)
                .discountValue(discountValue)
                .images(imageDtos)
                .attributes(attributeDtos)
                .build();
    }
    

}
