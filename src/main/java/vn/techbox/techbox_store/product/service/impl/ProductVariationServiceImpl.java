package vn.techbox.techbox_store.product.service.impl;

import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import vn.techbox.techbox_store.product.dto.productDto.ProductVariationCreateRequest;
import vn.techbox.techbox_store.product.dto.productDto.ProductVariationManagementResponse;
import vn.techbox.techbox_store.product.dto.productDto.ProductVariationResponse;
import vn.techbox.techbox_store.product.dto.productDto.ProductVariationUpdateRequest;
import vn.techbox.techbox_store.product.mapper.ProductVariationMapper;
import vn.techbox.techbox_store.product.model.Attribute;
import vn.techbox.techbox_store.product.repository.AttributeRepository;
import vn.techbox.techbox_store.product.dto.productDto.VariationAttributeRequest;
import vn.techbox.techbox_store.product.model.Product;
import vn.techbox.techbox_store.product.model.ProductStatus;
import vn.techbox.techbox_store.product.model.ProductVariation;
import vn.techbox.techbox_store.product.model.ProductVariationImage;
import vn.techbox.techbox_store.product.model.VariationAttribute;
import vn.techbox.techbox_store.product.repository.ProductVariationRepository;
import vn.techbox.techbox_store.product.repository.ProductRepository;
import vn.techbox.techbox_store.product.service.ProductPriceUpdateService;
import vn.techbox.techbox_store.product.service.ProductVariationService;

import java.math.BigDecimal;
import java.util.List;
import java.util.Optional;
import java.util.stream.Collectors;

@Service
@RequiredArgsConstructor
@Transactional
public class ProductVariationServiceImpl implements ProductVariationService {

    private final ProductVariationRepository productVariationRepository;
    private final ProductRepository productRepository;
    private final ProductVariationMapper productVariationMapper;
    private final AttributeRepository attributeRepository;
    private final ProductPriceUpdateService productPriceUpdateService;

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

        // Ensure the parent product exists and is not soft-deleted
        Product product = productRepository.findById(request.getProductId())
                .orElseThrow(() -> new IllegalArgumentException("Product not found with id: " + request.getProductId()));

        // Check if product is soft-deleted
        if (product.getDeletedAt() != null) {
            throw new IllegalArgumentException("Cannot add variations to deleted products");
        }

        // Check if product is in DRAFT or PUBLISHED status
        if (product.getStatus() != ProductStatus.DRAFT && product.getStatus() != ProductStatus.PUBLISHED) {
            throw new IllegalStateException("Can only add variations to products in DRAFT or PUBLISHED status");
        }

        // Build the ProductVariation entity from the request
        ProductVariation productVariation = ProductVariation.builder()
            .variationName(request.getVariationName())
            .productId(request.getProductId())
            .price(request.getPrice())
            .sku(request.getSku())
            .avgCostPrice(BigDecimal.valueOf(0))
            .stockQuantity(0)
            .reservedQuantity(0)
            .build();

        // Save ProductVariation first to get ID
        ProductVariation savedVariation = productVariationRepository.save(productVariation);

        // Add variation attributes after ProductVariation is saved (so we have variationId)
        if (request.getVariationAttributes() != null && !request.getVariationAttributes().isEmpty()) {
            for (VariationAttributeRequest attrReq : request.getVariationAttributes()) {
                Attribute attribute = attributeRepository.findById(attrReq.getAttributeId())
                        .orElseThrow(() -> new IllegalArgumentException("Attribute not found with id: " + attrReq.getAttributeId()));
                
                VariationAttribute variationAttribute = VariationAttribute.builder()
                        .productVariationId(savedVariation.getId())  // Set productVariationId for composite key
                        .attributeId(attribute.getId())              // Set attributeId for composite key
                        .value(attrReq.getValue())
                        .build();
                
                // Set bidirectional relationships
                variationAttribute.setProductVariation(savedVariation);
                variationAttribute.setAttribute(attribute);
                
                // Add to variation's collection
                savedVariation.getVariationAttributes().add(variationAttribute);
            }

            // Save again to persist the attributes (cascade should work now)
            savedVariation = productVariationRepository.save(savedVariation);
        }

        // Add images after ProductVariation is saved (so we have variationId)
        if (request.getImageUrls() != null && !request.getImageUrls().isEmpty()) {
            for (int i = 0; i < request.getImageUrls().size(); i++) {
                String publicId = (request.getImagePublicIds() != null && i < request.getImagePublicIds().size()) ? request.getImagePublicIds().get(i) : null;
                ProductVariationImage image = ProductVariationImage.builder()
                        .productVariationId(savedVariation.getId())  // Set productVariationId for foreign key
                        .imageUrl(request.getImageUrls().get(i))
                        .imagePublicId(publicId)
                        .build();
                // Set bidirectional relationship
                image.setProductVariation(savedVariation);
                // Add to variation's collection
                savedVariation.getImages().add(image);
            }

            // Save again to persist the images (cascade should work now)
            savedVariation = productVariationRepository.save(savedVariation);
        }

        // Update product display prices if product is published
        if (product.getStatus() == ProductStatus.PUBLISHED) {
            Integer id = product.getId();
            productPriceUpdateService.updateProductPricing(id);
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

        // Efficiently update variation attributes using orphanRemoval
        if (request.getVariationAttributes() != null) {
            // Clear the existing collection. orphanRemoval=true will delete the old attributes.
            variation.getVariationAttributes().clear();
            // Add the new attributes
            for (VariationAttributeRequest attrReq : request.getVariationAttributes()) {
                Attribute attribute = attributeRepository.findById(attrReq.getAttributeId())
                        .orElseThrow(() -> new IllegalArgumentException("Attribute not found with id: " + attrReq.getAttributeId()));
                
                VariationAttribute variationAttribute = VariationAttribute.builder()
                        .attributeId(attribute.getId())
                        .value(attrReq.getValue())
                        .build();
                
                variation.addVariationAttribute(variationAttribute);
            }
        }

        // Handle image deletions using orphanRemoval by removing them from the collection
        if (request.getDeleteImageIds() != null && !request.getDeleteImageIds().isEmpty()) {
            variation.getImages().removeIf(image -> request.getDeleteImageIds().contains(image.getImagePublicId()));
        }

        // Handle new image additions
        if (request.getImageUrls() != null && !request.getImageUrls().isEmpty()) {
            for (int i = 0; i < request.getImageUrls().size(); i++) {
                String publicId = (request.getImagePublicIds() != null && i < request.getImagePublicIds().size()) ? request.getImagePublicIds().get(i) : null;
                ProductVariationImage image = ProductVariationImage.builder()
                        .imageUrl(request.getImageUrls().get(i))
                        .imagePublicId(publicId)
                        .build();
                variation.addImage(image);
            }
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
    
    @Override
    public void deleteProductVariationHard(Integer id) {
        productVariationRepository.findById(id).ifPresent(variation -> {
            productVariationRepository.delete(variation);
        });
    }
}