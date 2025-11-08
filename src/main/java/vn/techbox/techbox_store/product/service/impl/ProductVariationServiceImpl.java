package vn.techbox.techbox_store.product.service.impl;

import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import vn.techbox.techbox_store.product.dto.productDto.ProductVariationCreateRequest;
import vn.techbox.techbox_store.product.dto.productDto.ProductVariationManagementResponse;
import vn.techbox.techbox_store.product.dto.productDto.ProductVariationResponse;
import vn.techbox.techbox_store.product.dto.productDto.ProductVariationUpdateRequest;
import vn.techbox.techbox_store.product.mapper.ProductVariationMapper;
import vn.techbox.techbox_store.product.model.Product;
import vn.techbox.techbox_store.product.model.ProductVariation;
import vn.techbox.techbox_store.product.model.ProductVariationImage;
import vn.techbox.techbox_store.product.model.VariationAttribute;
import vn.techbox.techbox_store.product.repository.ProductVariationRepository;
import vn.techbox.techbox_store.product.repository.VariationAttributeRepository;
import vn.techbox.techbox_store.product.repository.ProductVariationImageRepository;
import vn.techbox.techbox_store.product.repository.ProductRepository;
import vn.techbox.techbox_store.product.service.ProductVariationService;

import java.util.List;
import java.util.Map;
import java.util.Optional;
import java.util.stream.Collectors;

@Service
@RequiredArgsConstructor
@Transactional
public class ProductVariationServiceImpl implements ProductVariationService {

    private final VariationAttributeRepository variationAttributeRepository;
    private final ProductVariationRepository productVariationRepository;
    private final ProductRepository productRepository;
    private final ProductVariationImageRepository productVariationImageRepository;
    private final ProductVariationMapper productVariationMapper;
    
    @Override
    @Transactional(readOnly = true)
    public List<ProductVariationResponse> getAllProductVariations() {
        return productVariationRepository.findAll()
                .stream()
                .map(productVariationMapper::toResponse)
                .collect(Collectors.toList());
    }
    
    @Override
    @Transactional(readOnly = true)
    public List<ProductVariationResponse> getAllActiveProductVariations() {
        return productVariationRepository.findAllActive()
                .stream()
                .map(productVariationMapper::toResponse)
                .collect(Collectors.toList());
    }
    
    @Override
    @Transactional(readOnly = true)
    public Optional<ProductVariationResponse> getProductVariationById(Integer id) {
        return productVariationRepository.findById(id)
                .map(productVariationMapper::toResponse);
    }
    
    @Override
    @Transactional(readOnly = true)
    public Optional<ProductVariationResponse> getActiveProductVariationById(Integer id) {
        return productVariationRepository.findActiveById(id)
                .map(productVariationMapper::toResponse);
    }
    
    @Override
    public ProductVariationResponse createProductVariation(ProductVariationCreateRequest request) {
        if (request.getSku() != null && existsBySku(request.getSku())) {
            throw new IllegalArgumentException("SKU already exists: " + request.getSku());
        }

        Product product = productRepository.findActiveById(request.getProductId())
                .orElseThrow(() -> new IllegalArgumentException("Active product not found with id: " + request.getProductId()));

        ProductVariation productVariation = ProductVariation.builder()
            .variationName(request.getVariationName())
            .productId(request.getProductId())
            .price(request.getPrice())
            .sku(request.getSku())
            .avgCostPrice(request.getAvgCostPrice())
            .stockQuantity(request.getStockQuantity() != null ? request.getStockQuantity() : 0)
            .reservedQuantity(request.getReservedQuantity() != null ? request.getReservedQuantity() : 0)
            .build();

        ProductVariation savedVariation = productVariationRepository.save(productVariation);

        // Save variation attributes if provided
        if (request.getVariationAttributes() != null && !request.getVariationAttributes().isEmpty()) {
            saveVariationAttributes(savedVariation.getId(), request.getVariationAttributes());
        }

        // Save images if provided
        if (request.getImageUrls() != null && !request.getImageUrls().isEmpty()) {
            saveProductVariationImages(savedVariation.getId(), request.getImageUrls(), request.getImagePublicIds());
        }

        return productVariationMapper.toResponse(savedVariation);
    }
    
    @Override
    public ProductVariationResponse updateProductVariation(Integer id, ProductVariationUpdateRequest request) {
        ProductVariation variation = productVariationRepository.findById(id)
                .orElseThrow(() -> new IllegalArgumentException("Product variation not found with id: " + id));

        if (request.getVariationName() != null) {
            variation.setVariationName(request.getVariationName());
        }

        if (request.getPrice() != null) {
            variation.setPrice(request.getPrice());
        }

        if (request.getSku() != null) {
            if (existsBySkuAndIdNot(request.getSku(), id)) {
                throw new IllegalArgumentException("SKU already exists: " + request.getSku());
            }
            variation.setSku(request.getSku());
        }

        if (request.getStockQuantity() != null) {
            variation.setStockQuantity(request.getStockQuantity());
        }

        if (request.getReservedQuantity() != null) {
            variation.setReservedQuantity(request.getReservedQuantity());
        }

        if (request.getAvgCostPrice() != null) {
            variation.setAvgCostPrice(request.getAvgCostPrice());
        }

        // Update variation attributes if provided
        if (request.getVariationAttributes() != null && !request.getVariationAttributes().isEmpty()) {
            updateVariationAttributes(id, request.getVariationAttributes());
        }

        // Handle image operations
        if (request.getDeleteImageIds() != null && !request.getDeleteImageIds().isEmpty()) {
            for (String publicId : request.getDeleteImageIds()) {
                productVariationImageRepository.deleteByImagePublicId(publicId);
            }
        }

        if (request.getImageUrls() != null && !request.getImageUrls().isEmpty()) {
            saveProductVariationImages(id, request.getImageUrls(), request.getImagePublicIds());
        }

        ProductVariation updatedVariation = productVariationRepository.save(variation);
        return productVariationMapper.toResponse(updatedVariation);
    }
    
    @Override
    public void deleteProductVariation(Integer id) {
        ProductVariation variation = productVariationRepository.findById(id)
                .orElseThrow(() -> new IllegalArgumentException("Product variation not found with id: " + id));
        
        variation.delete(); // Soft delete
        productVariationRepository.save(variation);
    }
    
    @Override
    public void restoreProductVariation(Integer id) {
        ProductVariation variation = productVariationRepository.findById(id)
                .orElseThrow(() -> new IllegalArgumentException("Product variation not found with id: " + id));
        
        variation.restore();
        productVariationRepository.save(variation);
    }
    
    @Override
    @Transactional(readOnly = true)
    public List<ProductVariationResponse> getVariationsByProductId(Integer productId) {
        return productVariationRepository.findByProductId(productId)
                .stream()
                .map(productVariationMapper::toResponse)
                .collect(Collectors.toList());
    }
    
    @Override
    @Transactional(readOnly = true)
    public List<ProductVariationResponse> getActiveVariationsByProductId(Integer productId) {
        return productVariationRepository.findByProductIdAndDeletedAtIsNull(productId)
                .stream()
                .map(productVariationMapper::toResponse)
                .collect(Collectors.toList());
    }
    
    @Override
    @Transactional(readOnly = true)
    public List<ProductVariationResponse> getInStockVariations() {
        return productVariationRepository.findInStockVariations()
                .stream()
                .map(productVariationMapper::toResponse)
                .collect(Collectors.toList());
    }
    
    @Override
    @Transactional(readOnly = true)
    public List<ProductVariationResponse> getInStockVariationsByProductId(Integer productId) {
        return productVariationRepository.findInStockByProductId(productId)
                .stream()
                .map(productVariationMapper::toResponse)
                .collect(Collectors.toList());
    }
    
    @Override
    @Transactional(readOnly = true)
    public List<ProductVariationResponse> getLowStockVariations(Integer threshold) {
        return productVariationRepository.findLowStockVariations(threshold)
                .stream()
                .map(productVariationMapper::toResponse)
                .collect(Collectors.toList());
    }
    
    @Override
    @Transactional(readOnly = true)
    public Optional<ProductVariationResponse> getVariationBySku(String sku) {
        return productVariationRepository.findBySku(sku)
                .map(productVariationMapper::toResponse);
    }
    
    @Override
    public ProductVariationResponse updateStock(Integer id, Integer stockQuantity) {
        ProductVariation variation = productVariationRepository.findById(id)
                .orElseThrow(() -> new IllegalArgumentException("Product variation not found with id: " + id));
        variation.setStockQuantity(stockQuantity);
        ProductVariation updatedVariation = productVariationRepository.save(variation);
        return productVariationMapper.toResponse(updatedVariation);
    }
    
    @Override
    @Transactional(readOnly = true)
    public boolean existsBySku(String sku) {
        return productVariationRepository.existsBySku(sku);
    }
    
    @Override
    @Transactional(readOnly = true)
    public boolean existsBySkuAndIdNot(String sku, Integer id) {
        return productVariationRepository.existsBySkuAndIdNot(sku, id);
    }
    
    /**
     * Internal method for service-to-service communication
     * Returns entities to avoid N+1 query issues
     */
    @Override
    @Transactional(readOnly = true)
    public List<ProductVariation> getActiveVariationEntitiesByProductId(Integer productId) {
        return productVariationRepository.findByProductIdAndDeletedAtIsNull(productId);
    }
    
    /**
     * Count active variations for a product
     */
    @Override
    @Transactional(readOnly = true)
    public int countActiveVariationsByProductId(Integer productId) {
        return productVariationRepository.findByProductIdAndDeletedAtIsNull(productId).size();
    }

    @Override
    @Transactional(readOnly = true)
    public int countTotalVariationsByProductId(Integer productId) {
        return productVariationRepository.findAllByProductId(productId).size();
    }

    
    /**
     * Get all variations for management with optional deleted filter
     * Used for admin/management view and edit
     * 
     * @param productId The ID of the product
     * @param deleted Filter parameter:
     *                - null (default): return all variations
     *                - false: return only active variations (deletedAt IS NULL)
     *                - true: return only soft-deleted variations (deletedAt IS NOT NULL)
     * @return List of variations matching the filter criteria
     */
    @Override
    @Transactional(readOnly = true)
    public List<ProductVariationManagementResponse> getVariationsForManagement(Integer productId, Boolean deleted) {
        List<ProductVariation> variations;
        
        if (deleted == null) {
            // Return all variations (no filter)
            variations = productVariationRepository.findAllByProductId(productId);
        } else if (deleted) {
            // Return only soft-deleted variations (deletedAt IS NOT NULL)
            variations = productVariationRepository.findDeletedByProductId(productId);
        } else {
            // Return only active variations (deletedAt IS NULL)
            variations = productVariationRepository.findByProductId(productId);
        }
        
        // Map to management response using ProductVariationMapper
        return variations.stream()
                .map(productVariationMapper::toManagementResponse)
                .collect(Collectors.toList());
    }
    
    // Helper methods for image handling
    private void saveProductVariationImages(Integer variationId, List<String> imageUrls, List<String> imagePublicIds) {
        for (int i = 0; i < imageUrls.size(); i++) {
            String publicId = (imagePublicIds != null && i < imagePublicIds.size()) ? imagePublicIds.get(i) : null;
            ProductVariationImage image = ProductVariationImage.builder()
                    .productVariationId(variationId)
                    .imageUrl(imageUrls.get(i))
                    .imagePublicId(publicId)
                    .build();
            productVariationImageRepository.save(image);
        }
    }

    private void saveVariationAttributes(Integer variationId, Map<Integer, String> attributes) {
        attributes.forEach((key, value) -> {
            try {
                String attributeValue = value != null ? value : ""; // Ensure value is a String
                VariationAttribute attribute = VariationAttribute.builder()
                    .productVariationId(variationId)
                    .attributeId(key)
                    .value(attributeValue) // Corrected field name
                    .build();
                variationAttributeRepository.save(attribute);
            } catch (NumberFormatException e) {
                throw new IllegalArgumentException("Invalid attribute ID: " + key + " is not a valid integer.", e);
            }
        });
    }

    private void updateVariationAttributes(Integer variationId, Map<Integer, String> attributes) {
        variationAttributeRepository.deleteByProductVariationId(variationId);
        saveVariationAttributes(variationId, attributes);
    }

    @Override
    public void deleteProductVariationHard(Integer id) {
        productVariationRepository.findById(id).ifPresent(variation -> {
            productVariationRepository.delete(variation);
        });
    }
}