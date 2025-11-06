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
import vn.techbox.techbox_store.product.service.CategoryService;
import vn.techbox.techbox_store.product.service.ProductService;
import vn.techbox.techbox_store.product.specification.ProductSpecification;
import vn.techbox.techbox_store.promotion.model.Promotion;
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
    private final ProductVariationRepository productVariationRepository;
    private final VariationAttributeRepository variationAttributeRepository;
    private final ProductVariationImageRepository productVariationImageRepository;
    private final ReviewRepository reviewRepository;
    private final PromotionRepository promotionRepository;
    private final ProductMapper productMapper;
    private final ProductFilterHelper productFilterHelper;
    private final SortHelper sortHelper;
    private final ProductSpecification productSpecification;
    
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
        
        // Get all product variations
        List<ProductVariation> productVariations = 
                productVariationRepository.findByProductId(product.getId());
        
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
    
    
    @Override
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
        
        Product product = Product.builder()
                .name(request.getName())
                .description(request.getDescription())
                .categoryId(request.getCategoryId())
                .brandId(request.getBrandId())
                .imageUrl(request.getImageUrl())
                .imagePublicId(request.getImagePublicId())
                .status(request.getStatus() != null ? request.getStatus() : ProductStatus.DRAFT)
                .warrantyMonths(request.getWarrantyMonths())
                .build();
        
        Product savedProduct = productRepository.save(product);
        return convertToResponse(savedProduct);
    }
    
    @Override
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

        if (request.getStatus() != null) {
            product.setStatus(request.getStatus());
        }

        if (request.getWarrantyMonths() != null) {
            product.setWarrantyMonths(request.getWarrantyMonths());
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
    
    
}

