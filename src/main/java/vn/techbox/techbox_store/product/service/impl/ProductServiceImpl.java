package vn.techbox.techbox_store.product.service.impl;

import lombok.RequiredArgsConstructor;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Pageable;
import org.springframework.data.domain.Sort;
import org.springframework.data.jpa.domain.Specification;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import vn.techbox.techbox_store.product.dto.*;
import vn.techbox.techbox_store.product.model.*;
import vn.techbox.techbox_store.product.repository.*;
import vn.techbox.techbox_store.product.service.ProductService;
import vn.techbox.techbox_store.product.specification.ProductSpecification;
import vn.techbox.techbox_store.promotion.model.Promotion;
import vn.techbox.techbox_store.promotion.model.PromotionType;
import vn.techbox.techbox_store.promotion.repository.PromotionRepository;
import vn.techbox.techbox_store.review.repository.ReviewRepository;

import java.math.BigDecimal;
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
    private final WishListRepository wishListRepository;
    private final ProductAttributeRepository productAttributeRepository;
    private final ProductVariationRepository productVariationRepository;
    private final VariationAttributeRepository variationAttributeRepository;
    private final ProductVariationImageRepository productVariationImageRepository;
    private final ReviewRepository reviewRepository;
    private final PromotionRepository promotionRepository;
    
    
    
    @Override
    @Transactional(readOnly = true)
    public Optional<ProductResponse> getProductById(Integer id) {
        return productRepository.findById(id)
                .map(this::convertToResponse);
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
        
        product.delete(); // Soft delete
        productRepository.save(product);
    }
    
    @Override
    public void restoreProduct(Integer id) {
        Product product = productRepository.findById(id)
                .orElseThrow(() -> new IllegalArgumentException("Product not found with id: " + id));
        
        product.restore();
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
    @Transactional(readOnly = true)
    public Page<ProductListResponse> filterProducts(ProductFilterRequest filterRequest, Integer userId) {
        // Build sort
        Sort sort = buildSort(filterRequest.getSortBy(), filterRequest.getSortDirection());
        
        // Build pageable
        int page = filterRequest.getPage() != null ? filterRequest.getPage() : 0;
        int size = filterRequest.getSize() != null ? filterRequest.getSize() : 20;
        Pageable pageable = PageRequest.of(page, size, sort);
        
        // Build specification
        Specification<Product> spec = ProductSpecification.filterProducts(filterRequest);
        
        // Query with specification
        Page<Product> productsPage = productRepository.findAll(spec, pageable);
        
        // Map to response
        return productsPage.map(product -> convertToListResponse(product, userId));
    }
    
    @Override
    @Transactional(readOnly = true)
    public Page<ProductListResponse> getAllProducts(Pageable pageable) {
        Page<Product> productsPage = productRepository.findAllActive(pageable);
        return productsPage.map(product -> convertToListResponse(product, null));
    }
    
    @Override
    @Transactional(readOnly = true)
    public Page<ProductListResponse> getDeletedProductsForAdmin(Pageable pageable) {
        Page<Product> productsPage = productRepository.findAllDeleted(pageable);
        return productsPage.map(product -> convertToListResponse(product, null));
    }
    
    @Override
    @Transactional(readOnly = true)
    public Page<ProductListResponse> getProductsByCampaign(Integer campaignId, Pageable pageable, Integer userId) {
        // Lấy tất cả promotions thuộc campaign này
        List<Promotion> promotions = promotionRepository.findByCampaignId(campaignId);

        if (promotions.isEmpty()) {
            // Trả về trang rỗng nếu không có promotion nào
            return Page.empty(pageable);
        }

        // Lấy danh sách productVariationIds từ promotions
        List<Integer> variationIds = promotions.stream()
                .map(Promotion::getProductVariationId)
                .distinct()
                .collect(Collectors.toList());

        // Lấy danh sách productIds từ variations
        List<Integer> productIds = productVariationRepository.findAllById(variationIds)
                .stream()
                .map(ProductVariation::getProductId)
                .distinct()
                .collect(Collectors.toList());

        if (productIds.isEmpty()) {
            return Page.empty(pageable);
        }

        // Lấy danh sách products với phân trang
        Page<Product> productsPage = productRepository.findByIdInAndDeletedAtIsNull(productIds, pageable);

        // Map to response
        return productsPage.map(product -> convertToListResponse(product, userId));
    }

    @Override
    @Transactional(readOnly = true)
    public Optional<ProductDetailResponse> getProductDetailById(Integer id, Integer userId) {
        return productRepository.findActiveById(id)
                .map(product -> convertToDetailResponse(product, userId));
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
    
    // Helper method để convert sang ProductListResponse
    private ProductListResponse convertToListResponse(Product product, Integer userId) {
        ProductListResponse.ProductListResponseBuilder builder = ProductListResponse.builder()
                .id(product.getId())
                .name(product.getName())
                .imageUrl(product.getImageUrl())
                .status(product.getStatus())
                .warrantyMonths(product.getWarrantyMonths())
                .displayOriginalPrice(product.getDisplayOriginalPrice())
                .displaySalePrice(product.getDisplaySalePrice())
                .discountType(product.getDiscountType())
                .discountValue(product.getDiscountValue())
                .averageRating(product.getAverageRating())
                .totalRatings(product.getTotalRatings());

        
        // Check wishlist nếu user đã đăng nhập
        if (userId != null) {
            boolean inWishlist = wishListRepository.existsByUserIdAndProductId(userId, product.getId());
            builder.inWishlist(inWishlist);
        } else {
            builder.inWishlist(false);
        }
        
        return builder.build();
    }
    
    // Helper method để convert sang ProductDetailResponse
    private ProductDetailResponse convertToDetailResponse(Product product, Integer userId) {
        // Get category and brand names
        String categoryName = product.getCategoryId() != null 
                ? categoryRepository.findById(product.getCategoryId())
                    .map(Category::getName).orElse(null)
                : null;
        
        String brandName = product.getBrandId() != null
                ? brandRepository.findById(product.getBrandId())
                    .map(Brand::getName).orElse(null)
                : null;
        
        // Check wishlist
        boolean inWishlist = userId != null && 
                wishListRepository.existsByUserIdAndProductId(userId, product.getId());
        
        // Get product attributes
        List<ProductDetailResponse.AttributeDto> productAttributes = 
                productAttributeRepository.findByProductId(product.getId())
                    .stream()
                    .map(pa -> ProductDetailResponse.AttributeDto.builder()
                            .id(pa.getAttributeId())
                            .name(pa.getAttribute().getName())
                            .value(pa.getValue())
                            .build())
                    .collect(Collectors.toList());
        
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
        
        // Convert variations to DTOs with pre-loaded data
        List<ProductDetailResponse.VariationDto> variations = 
                productVariations.stream()
                    .map(variation -> convertToVariationDto(
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
                .inWishlist(inWishlist)
                .attributes(productAttributes)
                .variations(variations)
                .build();
    }
    
    // Helper method để convert ProductVariation sang VariationDto with pre-loaded data
    private ProductDetailResponse.VariationDto convertToVariationDto(
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
    
    private ProductResponse convertToResponse(Product product) {
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
                .build();
        
        // Set category name if categoryId exists
        if (product.getCategoryId() != null) {
            categoryRepository.findById(product.getCategoryId())
                    .ifPresent(category -> response.setCategoryName(category.getName()));
        }
        
        // Set brand name if brandId exists
        if (product.getBrandId() != null) {
            brandRepository.findById(product.getBrandId())
                    .ifPresent(brand -> response.setBrandName(brand.getName()));
        }
        
        return response;
    }
    
    // Helper method to build Sort object
    private Sort buildSort(String sortBy, String sortDirection) {
        // Map sortBy field names
        String field = switch (sortBy != null ? sortBy.toLowerCase() : "id") {
            case "price" -> "displaySalePrice";
            case "rating" -> "averageRating";
            case "reviewcount", "review_count" -> "totalRatings";
            case "createdat", "created_at", "date" -> "createdAt";
            case "name" -> "name";
            default -> "id";
        };

        Sort.Direction direction = "desc".equalsIgnoreCase(sortDirection) 
                ? Sort.Direction.DESC
                : Sort.Direction.ASC;
        
        return Sort.by(direction, field);
    }
}

