package vn.techbox.techbox_store.product.mapper;

import java.math.BigDecimal;
import java.util.List;
import java.util.Map;
import java.util.Optional;
import java.util.stream.Collectors;

import org.springframework.stereotype.Component;

import lombok.RequiredArgsConstructor;
import vn.techbox.techbox_store.product.dto.productDto.ProductDetailResponse;
import vn.techbox.techbox_store.product.dto.productDto.ProductListResponse;
import vn.techbox.techbox_store.product.dto.productDto.ProductManagementDetailResponse;
import vn.techbox.techbox_store.product.dto.productDto.ProductManagementListResponse;
import vn.techbox.techbox_store.product.dto.productDto.ProductResponse;
import vn.techbox.techbox_store.product.model.Product;
import vn.techbox.techbox_store.product.model.ProductAttribute;
import vn.techbox.techbox_store.product.model.ProductVariation;
import vn.techbox.techbox_store.product.model.ProductVariationImage;
import vn.techbox.techbox_store.product.model.VariationAttribute;
import vn.techbox.techbox_store.product.repository.BrandRepository;
import vn.techbox.techbox_store.product.repository.CategoryRepository;
import vn.techbox.techbox_store.product.repository.ProductAttributeRepository;
import vn.techbox.techbox_store.product.service.ProductVariationService;
import vn.techbox.techbox_store.promotion.model.Promotion;
import vn.techbox.techbox_store.promotion.model.PromotionType;

@Component
@RequiredArgsConstructor
public class ProductMapper {
    
    private final CategoryRepository categoryRepository;
    private final BrandRepository brandRepository;
    private final ProductAttributeRepository productAttributeRepository;
    private final ProductVariationService productVariationService;
    
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

    public ProductManagementListResponse toManagementListResponse(Product product) {
        if (product == null) return null;

        return ProductManagementListResponse.builder()
                .id(product.getId())
                .name(product.getName())
                .spu(product.getSpu())
                .imageUrl(product.getImageUrl())
                .displayOriginalPrice(product.getDisplayOriginalPrice())
                .displaySalePrice(product.getDisplaySalePrice())
                .discountType(product.getDiscountType())
                .discountValue(product.getDiscountValue())
                .averageRating(product.getAverageRating())
                .totalRatings(product.getTotalRatings())
                .status(product.getStatus())
                .createdAt(product.getCreatedAt())
                .updatedAt(product.getUpdatedAt())
                .deleteAt(product.getDeletedAt())
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
                    .filter(pa -> pa.getAttribute() != null)
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
                .spu(product.getSpu())
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
                .filter(va -> va.getAttribute() != null)
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
                .sku(variation.getSku())
                .price(variation.getPrice())
                .availableQuantity(availableQuantity)
                .salePrice(salePrice)
                .discountType(discountType)
                .discountValue(discountValue)
                .images(imageDtos)
                .attributes(attributeDtos)
                .build();
    }
    
    /**
     * Convert Product entity to ProductManagementDetailResponse DTO
     * Includes full information for admin/management view and edit
     */
    public ProductManagementDetailResponse toManagementDetailResponse(Product product) {
        if (product == null) return null;
        
        ProductManagementDetailResponse response = new ProductManagementDetailResponse();
        
        // Basic product information
        response.setId(product.getId());
        response.setSpu(product.getSpu());
        response.setName(product.getName());
        response.setDescription(product.getDescription());
        response.setImageUrl(product.getImageUrl());
        response.setImagePublicId(product.getImagePublicId());
        response.setStatus(product.getStatus());
        response.setWarrantyMonths(product.getWarrantyMonths());
        
        // Category information
        if (product.getCategoryId() != null) {
            categoryRepository.findById(product.getCategoryId()).ifPresent(category -> {
                response.setCategoryId(category.getId());
                response.setCategoryName(category.getName());
            });
        }
        
        // Brand information
        if (product.getBrandId() != null) {
            brandRepository.findById(product.getBrandId()).ifPresent(brand -> {
                response.setBrandId(brand.getId());
                response.setBrandName(brand.getName());
            });
        }
        
        // Product attributes
        List<ProductAttribute> productAttributes = productAttributeRepository.findByProductId(product.getId());
        List<ProductManagementDetailResponse.ProductAttributeDetail> attributeDetails = productAttributes.stream()
                .map(pa -> {
                    ProductManagementDetailResponse.ProductAttributeDetail detail = 
                        new ProductManagementDetailResponse.ProductAttributeDetail();
                    detail.setAttributeId(pa.getAttributeId());
                    detail.setAttributeValue(pa.getValue());
                    // Get attribute name from relationship
                    if (pa.getAttribute() != null) {
                        detail.setAttributeName(pa.getAttribute().getName());
                    }
                    return detail;
                })
                .collect(Collectors.toList());
        response.setAttributes(attributeDetails);
        
        // Display prices
        response.setDisplayOriginalPrice(product.getDisplayOriginalPrice());
        response.setDisplaySalePrice(product.getDisplaySalePrice());
        
        // Rating and review stats
        response.setAverageRating(product.getAverageRating());
        response.setTotalRatings(product.getTotalRatings());
        
        // Variations summary (count only, not full details)
        int totalVariations = productVariationService.countTotalVariationsByProductId(product.getId());
        int activeVariations = productVariationService.countActiveVariationsByProductId(product.getId());
        response.setTotalVariations(totalVariations);
        response.setActiveVariations(activeVariations);
        
        // Metadata
        response.setCreatedAt(product.getCreatedAt());
        response.setUpdatedAt(product.getUpdatedAt());
        response.setDeletedAt(product.getDeletedAt());
        
        return response;
    }

}
