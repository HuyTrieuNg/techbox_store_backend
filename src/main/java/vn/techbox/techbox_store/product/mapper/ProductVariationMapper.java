package vn.techbox.techbox_store.product.mapper;

import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Component;
import vn.techbox.techbox_store.product.dto.productDto.ProductVariationResponse;
import vn.techbox.techbox_store.product.model.ProductVariation;
import vn.techbox.techbox_store.product.model.ProductVariationImage;
import vn.techbox.techbox_store.product.repository.ProductRepository;
import vn.techbox.techbox_store.product.repository.ProductVariationImageRepository;

import java.util.List;
import java.util.stream.Collectors;

@Component
@RequiredArgsConstructor
public class ProductVariationMapper {
    
    private final ProductVariationImageRepository productVariationImageRepository;
    private final ProductRepository productRepository;
    
    /**
     * Convert ProductVariation entity to ProductVariationResponse DTO
     */
    public ProductVariationResponse toResponse(ProductVariation variation) {
        if (variation == null) {
            return null;
        }
        
        ProductVariationResponse response = ProductVariationResponse.builder()
                .id(variation.getId())
                .variationName(variation.getVariationName())
                .productId(variation.getProductId())
                .price(variation.getPrice())
                .sku(variation.getSku())
                .imageUrls(getProductVariationImageUrls(variation.getId()))
                .stockQuantity(variation.getStockQuantity())
                .reservedQuantity(variation.getReservedQuantity())
                .avgCostPrice(variation.getAvgCostPrice())
                .createdAt(variation.getCreatedAt())
                .updatedAt(variation.getUpdatedAt())
                .deletedAt(variation.getDeletedAt())
                .build();
        
        // Set product name if productId exists
        if (variation.getProductId() != null) {
            productRepository.findById(variation.getProductId())
                    .ifPresent(product -> response.setProductName(product.getName()));
        }
        
        return response;
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
