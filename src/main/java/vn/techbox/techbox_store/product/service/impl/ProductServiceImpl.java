package vn.techbox.techbox_store.product.service.impl;

import lombok.RequiredArgsConstructor;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Pageable;
import org.springframework.data.domain.Sort;
import org.springframework.data.jpa.domain.Specification;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import vn.techbox.techbox_store.product.dto.productDto.*;
import vn.techbox.techbox_store.product.helpers.ProductFilterHelper;
import vn.techbox.techbox_store.product.helpers.SortHelper;
import vn.techbox.techbox_store.product.model.*;
import vn.techbox.techbox_store.product.repository.*;
import vn.techbox.techbox_store.product.mapper.ProductMapper;
import vn.techbox.techbox_store.product.service.ProductPriceUpdateService;
import vn.techbox.techbox_store.product.service.ProductService;
import vn.techbox.techbox_store.product.service.ProductVariationService;
import vn.techbox.techbox_store.product.specification.ProductSpecification;
import vn.techbox.techbox_store.promotion.model.Promotion;
import vn.techbox.techbox_store.promotion.model.PromotionType;
import vn.techbox.techbox_store.promotion.repository.PromotionRepository;
import vn.techbox.techbox_store.review.repository.ReviewRepository;

import java.util.List;
import java.util.Map;
import java.util.Optional;
import java.util.stream.Collectors;

@Service
@RequiredArgsConstructor
@Transactional
public class ProductServiceImpl implements ProductService {
    
    private final ProductRepository productRepository;
    private final CategoryRepository categoryRepository;
    private final BrandRepository brandRepository;
    private final ProductAttributeRepository productAttributeRepository;
    private final ProductVariationService productVariationService;
    private final VariationAttributeRepository variationAttributeRepository;
    private final ProductVariationImageRepository productVariationImageRepository;
    private final ReviewRepository reviewRepository;
    private final PromotionRepository promotionRepository;
    private final ProductMapper productMapper;
    private final ProductFilterHelper productFilterHelper;
    private final SortHelper sortHelper;
    private final ProductSpecification productSpecification;
    private final ProductVariationRepository productVariationRepository;
    private final AttributeRepository attributeRepository;
    private final ProductPriceUpdateService productPriceUpdateService;

    @Override
    @Transactional(readOnly = true)
    public Page<ProductListResponse> filterProducts(ProductFilterRequest filterRequest) {
        ProductFilterRequest filter = productFilterHelper.prepareFilter(filterRequest);

        Sort sort = sortHelper.buildSort(filter.getSortBy(), filter.getSortDirection());

        // Build pageable
        int page = filter.getPage() != null ? filter.getPage() : 0;
        int size = filter.getSize() != null ? filter.getSize() : 20;
        Pageable pageable = PageRequest.of(page, size, sort);
        
        Specification<Product> spec = productSpecification.buildFilterSpecification(filter);
        
        // Query with specification
        Page<Product> productsPage = productRepository.findAll(spec, pageable);
        
        // Map to response
        return productsPage.map(productMapper::toListResponse);
    }
    

    @Override
    @Transactional(readOnly = true)
    public Page<ProductManagementListResponse> filterProductsForManagement(ProductFilterRequest filterRequest) {
        ProductFilterRequest filter = productFilterHelper.prepareManagementFilter(filterRequest);

        Sort sort = sortHelper.buildSort(filter.getSortBy(), filter.getSortDirection());

        // Build pageable
        int page = filter.getPage() != null ? filter.getPage() : 0;
        int size = filter.getSize() != null ? filter.getSize() : 20;
        Pageable pageable = PageRequest.of(page, size, sort);
        
        Specification<Product> spec = productSpecification.buildFilterSpecification(filter);
        
        // Query with specification
        Page<Product> productsPage = productRepository.findAll(spec, pageable);
        
        // Map to response
        return productsPage.map(productMapper::toManagementListResponse);
    }

    @Override
    @Transactional(readOnly = true)
    public Optional<ProductDetailResponse> getProductDetailById(Integer id) {
        Optional<Product> productOpt = productRepository.findActiveById(id);
        
        if (productOpt.isEmpty()) {
            return Optional.empty();
        }
        
        Product product = productOpt.get();
        
        // Get category and brand names
        String categoryName = product.getCategoryId() != null 
                ? categoryRepository.findById(product.getCategoryId())
                    .map(Category::getName).orElse(null)
                : null;
        
        String brandName = product.getBrandId() != null
                ? brandRepository.findById(product.getBrandId())
                    .map(Brand::getName).orElse(null)
                : null;
        
        // Get product attributes with their attribute details
        List<ProductAttribute> productAttributes = 
                productAttributeRepository.findByProductId(product.getId());
        
        // Get all active product variations using variation service
        List<ProductVariation> productVariations = 
                productVariationService.getActiveVariationEntitiesByProductId(product.getId());
        
        // Batch load all related data to avoid N+1 queries
        List<Integer> variationIds = productVariations.stream()
                .map(ProductVariation::getId)
                .collect(Collectors.toList());
        
        // Batch fetch variation images
        Map<Integer, List<ProductVariationImage>> imagesMap = 
                productVariationImageRepository.findByProductVariationIdIn(variationIds)
                    .stream()
                    .collect(Collectors.groupingBy(img -> img.getProductVariationId()));
        
        // Batch fetch variation attributes
        Map<Integer, List<VariationAttribute>> variationAttributesMap = 
                variationAttributeRepository.findByProductVariationIdIn(variationIds)
                    .stream()
                    .collect(Collectors.groupingBy(va -> va.getProductVariationId()));
        
        // Batch fetch promotions
        Map<Integer, List<Promotion>> promotionsMap = 
                promotionRepository.findByProductVariationIdIn(variationIds)
                    .stream()
                    .collect(Collectors.groupingBy(Promotion::getProductVariationId));
        
        // Use mapper to convert to response
        ProductDetailResponse response = productMapper.toDetailResponse(
                product, 
                categoryName, 
                brandName,
                productAttributes,
                productVariations,
                imagesMap,
                variationAttributesMap,
                promotionsMap
        );
        
        return Optional.of(response);
    }

    
    @Override
    @Transactional(readOnly = true)
    public Optional<ProductResponse> getProductById(Integer id) {
        Optional<Product> productOpt = productRepository.findById(id);
        
        if (productOpt.isEmpty()) {
            return Optional.empty();
        }
        
        Product product = productOpt.get();
        
        // Get category and brand names
        String categoryName = product.getCategoryId() != null 
                ? categoryRepository.findById(product.getCategoryId())
                    .map(Category::getName).orElse(null)
                : null;
        
        String brandName = product.getBrandId() != null
                ? brandRepository.findById(product.getBrandId())
                    .map(Brand::getName).orElse(null)
                : null;
        
        // Use mapper to convert to response
        ProductResponse response = productMapper.toResponse(product, categoryName, brandName);
        
        return Optional.of(response);
    }
    
    /**
     * Get full product details for management view
     * Includes all information for admin to view and edit
     * Does NOT filter soft deleted products
     * Does NOT include variations (separate API)
     */
    @Override
    @Transactional(readOnly = true)
    public ProductManagementDetailResponse getProductForManagement(Integer id) {
        // Find product without filtering deleted (management can see deleted products)
        Product product = productRepository.findById(id)
                .orElseThrow(() -> new IllegalArgumentException("Product not found with id: " + id));
        
        // Use mapper to convert to management detail response
        return productMapper.toManagementDetailResponse(product);
    }
    
    
    @Override
    @Transactional // Ensure all steps are in the same transaction
    public ProductResponse createProduct(ProductCreateRequest request) {
        if (existsByName(request.getName())) {
            throw new IllegalArgumentException("Product name already exists: " + request.getName());
        }

        // Validate category exists if provided
        if (request.getCategoryId() != null && !categoryRepository.existsById(request.getCategoryId())) {
            throw new IllegalArgumentException("Category not found with id: " + request.getCategoryId());
        }

        // Validate brand exists if provided
        if (request.getBrandId() != null && !brandRepository.existsById(request.getBrandId())) {
            throw new IllegalArgumentException("Brand not found with id: " + request.getBrandId());
        }

        // Create Product from DTO
        Product product = Product.builder()
                .name(request.getName())
                .description(request.getDescription())
                .categoryId(request.getCategoryId())
                .brandId(request.getBrandId())
                .imageUrl(request.getImageUrl())
                .imagePublicId(request.getImagePublicId())
                .status(ProductStatus.DRAFT) // Default status is DRAFT
                .warrantyMonths(request.getWarrantyMonths())
                .build();

        // Save Product first to get ID
        Product savedProduct = productRepository.save(product);

        // Add Attributes after Product is saved (so we have productId)
        if (request.getAttributes() != null) {
            for (AttributeRequest attrReq : request.getAttributes()) {
                // Validate attribute existence
                Attribute attribute = attributeRepository.findById(attrReq.getAttributeId())
                        .orElseThrow(() -> new IllegalArgumentException("Attribute not found with id: " + attrReq.getAttributeId()));

                // Create ProductAttribute with composite key
                ProductAttribute productAttribute = ProductAttribute.builder()
                        .productId(savedProduct.getId())  // Set productId for composite key
                        .attributeId(attribute.getId())    // Set attributeId for composite key
                        .value(attrReq.getValue())
                        .build();

                // Set bidirectional relationship
                productAttribute.setProduct(savedProduct);
                productAttribute.setAttribute(attribute);

                // Add to product's collection
                savedProduct.getProductAttributes().add(productAttribute);
            }

            // Save again to persist the attributes (cascade should work now)
            savedProduct = productRepository.save(savedProduct);
        }

        // Return ProductResponse
        return convertToResponse(savedProduct);
    }

    @Override
    @Transactional
    public ProductResponse createProductWithAttributes(ProductWithAttributesRequest request) {
        if (existsByName(request.getName())) {
            throw new IllegalArgumentException("Product name already exists: " + request.getName());
        }

        // Validate category exists if provided
        if (request.getCategoryId() != null && !categoryRepository.existsById(request.getCategoryId())) {
            throw new IllegalArgumentException("Category not found with id: " + request.getCategoryId());
        }

        // Validate brand exists if provided
        if (request.getBrandId() != null && !brandRepository.existsById(request.getBrandId())) {
            throw new IllegalArgumentException("Brand not found with id: " + request.getBrandId());
        }

        // Create Product from DTO
        Product product = Product.builder()
                .name(request.getName())
                .description(request.getDescription())
                .categoryId(request.getCategoryId())
                .brandId(request.getBrandId())
                .imageUrl(request.getImageUrl())
                .imagePublicId(request.getImagePublicId())
                .status(ProductStatus.DRAFT) // Default status is DRAFT
                .warrantyMonths(request.getWarrantyMonths())
                .build();

        // Save Product first to get ID
        Product savedProduct = productRepository.save(product);

        // Add Attributes after Product is saved (so we have productId)
        if (request.getAttributes() != null) {
            for (AttributeRequest attrReq : request.getAttributes()) {
                // Validate attribute existence
                Attribute attribute = attributeRepository.findById(attrReq.getAttributeId())
                        .orElseThrow(() -> new IllegalArgumentException("Attribute not found with id: " + attrReq.getAttributeId()));

                // Create ProductAttribute entity with both IDs
                ProductAttribute productAttribute = ProductAttribute.builder()
                        .productId(savedProduct.getId())
                        .attributeId(attribute.getId())
                        .value(attrReq.getValue())
                        .build();

                // Save ProductAttribute
                productAttributeRepository.save(productAttribute);
            }
        }

        // Return ProductResponse
        return convertToResponse(savedProduct);
    }    @Override
    @Transactional
    public ProductResponse updateProduct(Integer id, ProductUpdateRequest request) {
        Product product = productRepository.findById(id)
                .orElseThrow(() -> new IllegalArgumentException("Product not found with id: " + id));
        
        if (request.getName() != null) {
            if (existsByNameAndIdNot(request.getName(), id)) {
                throw new IllegalArgumentException("Product name already exists: " + request.getName());
            }
            product.setName(request.getName());
        }
        
        if (request.getDescription() != null) {
            product.setDescription(request.getDescription());
        }
        
        if (request.getCategoryId() != null) {
            if (!categoryRepository.existsById(request.getCategoryId())) {
                throw new IllegalArgumentException("Category not found with id: " + request.getCategoryId());
            }
            product.setCategoryId(request.getCategoryId());
        }
        
        if (request.getBrandId() != null) {
            if (!brandRepository.existsById(request.getBrandId())) {
                throw new IllegalArgumentException("Brand not found with id: " + request.getBrandId());
            }
            product.setBrandId(request.getBrandId());
        }
        
        // Handle image URL and public ID updates (including deletion)
        if (request.getImageUrl() != null || request.getImagePublicId() != null) {
            product.setImageUrl(request.getImageUrl());
            product.setImagePublicId(request.getImagePublicId());
        }

        if (request.getWarrantyMonths() != null) {
            product.setWarrantyMonths(request.getWarrantyMonths());
        }

        // Update attributes if provided
        if (request.getAttributes() != null) {
            // Clear existing attributes
            productAttributeRepository.deleteByProductId(product.getId());
            // Save new attributes
            for (AttributeRequest attrReq : request.getAttributes()) {
                // Validate attribute existence
                Attribute attribute = attributeRepository.findById(attrReq.getAttributeId())
                        .orElseThrow(() -> new IllegalArgumentException("Attribute not found with id: " + attrReq.getAttributeId()));

                // Create and save ProductAttribute
                ProductAttribute productAttribute = ProductAttribute.builder()
                        .productId(product.getId())
                        .attributeId(attribute.getId())
                        .value(attrReq.getValue())
                        .build();

                productAttributeRepository.save(productAttribute);
            }
        }

        Product updatedProduct = productRepository.save(product);
        return convertToResponse(updatedProduct);
    }
    
    @Override
    public void deleteProduct(Integer id) {
        Product product = productRepository.findById(id)
                .orElseThrow(() -> new IllegalArgumentException("Product not found with id: " + id));
        
        // Soft delete: set both deletedAt and status
        product.delete(); // Sets deletedAt
        product.setStatus(ProductStatus.DELETED); // Set status to DELETED
        productRepository.save(product);
    }
    
    @Override
    public void restoreProduct(Integer id) {
        Product product = productRepository.findById(id)
                .orElseThrow(() -> new IllegalArgumentException("Product not found with id: " + id));
        
        if (!product.isDeleted()) {
            throw new IllegalArgumentException("Product is not deleted, cannot restore");
        }
        
        // Restore: clear deletedAt and set status to DRAFT (admin needs to review before publishing)
        product.restore(); // Clears deletedAt
        product.setStatus(ProductStatus.DRAFT); // Set status to DRAFT after restore
        productRepository.save(product);
    }
    

    
    @Override
    @Transactional(readOnly = true)
    public boolean existsByName(String name) {
        return productRepository.existsByName(name);
    }

    @Override
    @Transactional(readOnly = true)
    public boolean existsByNameAndIdNot(String name, Integer id) {
        return productRepository.existsByNameAndIdNot(name, id);
    }
    
   
    
    
    @Override
    public void updateProductRating(Integer productId) {
        Product product = productRepository.findById(productId)
                .orElseThrow(() -> new IllegalArgumentException("Product not found with id: " + productId));
        
        // Tính toán rating mới từ reviews
        List<Object[]> ratingStats = reviewRepository.calculateAverageRatingAndCount(productId);
        
        if (!ratingStats.isEmpty()) {
            Object[] stats = ratingStats.get(0);
            Double avgRating = stats[0] != null ? ((Number) stats[0]).doubleValue() : 0.0;
            Long count = stats[1] != null ? ((Number) stats[1]).longValue() : 0L;
            
            product.setAverageRating(avgRating);
            product.setTotalRatings(count.intValue());
        } else {
            product.setAverageRating(0.0);
            product.setTotalRatings(0);
        }
        
        productRepository.save(product);
    }
    
    /**
     * Publish product - Change status to PUBLISHED
     * Requirements:
     * - Product must exist
     * - Product must NOT be DELETED (must go to DRAFT first)
     * - Product must have at least 1 variation
     * - Updates product's display prices based on lowest price variation
     */
    @Override
    public ProductResponse publishProduct(Integer id) {
        Product product = productRepository.findById(id)
                .orElseThrow(() -> new IllegalArgumentException("Product not found with id: " + id));
        
        // Validate: Cannot publish DELETED product directly
        if (product.getStatus() == ProductStatus.DELETED) {
            throw new IllegalStateException("Cannot publish a deleted product. Please restore to draft first.");
        }
        
        // Check if product has at least 1 active variation using variation service
        int activeVariationsCount = productVariationService.countActiveVariationsByProductId(id);
        if (activeVariationsCount == 0) {
            throw new IllegalStateException("Cannot publish product without variations. Please add at least one product variation.");
        }
        
        // Update status to PUBLISHED
        product.setStatus(ProductStatus.PUBLISHED);
        product.setDeletedAt(null); // Clear deleted timestamp if any (should already be null for DRAFT)

        productPriceUpdateService.updateProductPricing(id);
        productRepository.save(product);
        
        return convertToResponse(product);
    }
    
    /**
     * Draft product - Change status to DRAFT
     * Can be used to:
     * 1. Restore a deleted product (DELETED -> DRAFT)
     * 2. Hide a published product (PUBLISHED -> DRAFT)
     */
    @Override
    public ProductResponse draftProduct(Integer id) {
        Product product = productRepository.findById(id)
                .orElseThrow(() -> new IllegalArgumentException("Product not found with id: " + id));
        
        // Update status to DRAFT
        product.setStatus(ProductStatus.DRAFT);
        product.setDeletedAt(null); // Clear deleted timestamp if restoring from DELETED
        
        Product savedProduct = productRepository.save(product);
        return convertToResponse(savedProduct);
    }
    
    /**
     * Soft delete product - Change status to DELETED
     * Requirements:
     * - Product must be in DRAFT or PUBLISHED status
     * - Cannot delete a product that is already DELETED
     */
    @Override
    public ProductResponse deleteProductSoft(Integer id) {
        Product product = productRepository.findById(id)
                .orElseThrow(() -> new IllegalArgumentException("Product not found with id: " + id));
        
        // Check if product is already deleted
        if (product.getStatus() == ProductStatus.DELETED) {
            throw new IllegalStateException("Product is already deleted");
        }
        
        // Only allow deletion if product is DRAFT or PUBLISHED
        if (product.getStatus() != ProductStatus.DRAFT && product.getStatus() != ProductStatus.PUBLISHED) {
            throw new IllegalStateException("Cannot delete product with status: " + product.getStatus());
        }
        
        // Update status to DELETED
        product.setStatus(ProductStatus.DELETED);
        product.setDeletedAt(java.time.LocalDateTime.now());
        
        // Soft delete all active variations
        List<ProductVariation> variations = productVariationService.getActiveVariationEntitiesByProductId(id);
        for (ProductVariation variation : variations) {
            variation.delete();
            productVariationRepository.save(variation);
        }
        
        Product savedProduct = productRepository.save(product);
        return convertToResponse(savedProduct);
    }
    

    

    private ProductResponse convertToResponse(Product product) {
        // Get category and brand names
        String categoryName = product.getCategoryId() != null 
                ? categoryRepository.findById(product.getCategoryId())
                    .map(Category::getName).orElse(null)
                : null;
        
        String brandName = product.getBrandId() != null
                ? brandRepository.findById(product.getBrandId())
                    .map(Brand::getName).orElse(null)
                : null;
        
        // Use mapper to convert to response
        return productMapper.toResponse(product, categoryName, brandName);
    }

    @Override
    public void deleteProductHard(Integer id) {
        if (!productRepository.existsById(id)) {
            throw new IllegalArgumentException("Product not found with id: " + id);
        }
        productRepository.deleteById(id);            
    }

    // New method to add attributes to a product
    @Override
    public void addAttributesToProduct(Integer productId, Map<Integer, String> attributeRequests) {
        Product product = productRepository.findById(productId)
                .orElseThrow(() -> new IllegalArgumentException("Product not found with id: " + productId));
        for (Map.Entry<Integer, String> entry : attributeRequests.entrySet()) {
            Integer attributeId = entry.getKey();
            String value = entry.getValue();

            // Validate attribute existence
            Attribute attribute = attributeRepository.findById(attributeId)
                    .orElseThrow(() -> new IllegalArgumentException("Attribute not found with id: " + attributeId));
            // Create ProductAttribute entity
            ProductAttribute productAttribute = ProductAttribute.builder()
                    .productId(productId)
                    .attributeId(attribute.getId())
                    .value(value)
                    .build();
            // Use the helper method to establish the bidirectional link
            product.addProductAttribute(productAttribute);
        }
        productRepository.save(product);
    }
}
