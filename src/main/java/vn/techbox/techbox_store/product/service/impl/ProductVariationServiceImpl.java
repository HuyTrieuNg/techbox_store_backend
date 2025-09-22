package vn.techbox.techbox_store.product.service.impl;

import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import vn.techbox.techbox_store.product.dto.ProductVariationCreateRequest;
import vn.techbox.techbox_store.product.dto.ProductVariationResponse;
import vn.techbox.techbox_store.product.dto.ProductVariationUpdateRequest;
import vn.techbox.techbox_store.product.model.ProductVariation;
import vn.techbox.techbox_store.product.repository.ProductVariationRepository;
import vn.techbox.techbox_store.product.repository.ProductRepository;
import vn.techbox.techbox_store.product.service.ProductVariationService;

import java.util.List;
import java.util.Optional;
import java.util.stream.Collectors;

@Service
@RequiredArgsConstructor
@Transactional
public class ProductVariationServiceImpl implements ProductVariationService {
    
    private final ProductVariationRepository productVariationRepository;
    private final ProductRepository productRepository;
    
    @Override
    @Transactional(readOnly = true)
    public List<ProductVariationResponse> getAllProductVariations() {
        return productVariationRepository.findAll()
                .stream()
                .map(this::convertToResponse)
                .collect(Collectors.toList());
    }
    
    @Override
    @Transactional(readOnly = true)
    public List<ProductVariationResponse> getAllActiveProductVariations() {
        return productVariationRepository.findAllActive()
                .stream()
                .map(this::convertToResponse)
                .collect(Collectors.toList());
    }
    
    @Override
    @Transactional(readOnly = true)
    public Optional<ProductVariationResponse> getProductVariationById(Integer id) {
        return productVariationRepository.findById(id)
                .map(this::convertToResponse);
    }
    
    @Override
    @Transactional(readOnly = true)
    public Optional<ProductVariationResponse> getActiveProductVariationById(Integer id) {
        return productVariationRepository.findActiveById(id)
                .map(this::convertToResponse);
    }
    
    @Override
    public ProductVariationResponse createProductVariation(ProductVariationCreateRequest request) {
        if (request.getSku() != null && existsBySku(request.getSku())) {
            throw new IllegalArgumentException("SKU already exists: " + request.getSku());
        }
        
        // Verify product exists and is active
        if (!productRepository.findActiveById(request.getProductId()).isPresent()) {
            throw new IllegalArgumentException("Active product not found with id: " + request.getProductId());
        }
        
        ProductVariation productVariation = ProductVariation.builder()
                .variationName(request.getVariationName())
                .productId(request.getProductId())
                .price(request.getPrice())
                .sku(request.getSku())
                .imageUrl(request.getImageUrl())
                .quantity(request.getQuantity())
                .build();
        
        ProductVariation savedVariation = productVariationRepository.save(productVariation);
        return convertToResponse(savedVariation);
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
        
        if (request.getImageUrl() != null) {
            variation.setImageUrl(request.getImageUrl());
        }
        
        if (request.getQuantity() != null) {
            variation.setQuantity(request.getQuantity());
        }
        
        ProductVariation updatedVariation = productVariationRepository.save(variation);
        return convertToResponse(updatedVariation);
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
                .map(this::convertToResponse)
                .collect(Collectors.toList());
    }
    
    @Override
    @Transactional(readOnly = true)
    public List<ProductVariationResponse> getInStockVariations() {
        return productVariationRepository.findInStockVariations()
                .stream()
                .map(this::convertToResponse)
                .collect(Collectors.toList());
    }
    
    @Override
    @Transactional(readOnly = true)
    public List<ProductVariationResponse> getInStockVariationsByProductId(Integer productId) {
        return productVariationRepository.findInStockByProductId(productId)
                .stream()
                .map(this::convertToResponse)
                .collect(Collectors.toList());
    }
    
    @Override
    @Transactional(readOnly = true)
    public List<ProductVariationResponse> getLowStockVariations(Integer threshold) {
        return productVariationRepository.findLowStockVariations(threshold)
                .stream()
                .map(this::convertToResponse)
                .collect(Collectors.toList());
    }
    
    @Override
    @Transactional(readOnly = true)
    public Optional<ProductVariationResponse> getVariationBySku(String sku) {
        return productVariationRepository.findBySku(sku)
                .map(this::convertToResponse);
    }
    
    @Override
    public ProductVariationResponse updateStock(Integer id, Integer quantity) {
        ProductVariation variation = productVariationRepository.findById(id)
                .orElseThrow(() -> new IllegalArgumentException("Product variation not found with id: " + id));
        
        variation.setQuantity(quantity);
        ProductVariation updatedVariation = productVariationRepository.save(variation);
        return convertToResponse(updatedVariation);
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
    
    private ProductVariationResponse convertToResponse(ProductVariation variation) {
        ProductVariationResponse response = ProductVariationResponse.builder()
                .id(variation.getId())
                .variationName(variation.getVariationName())
                .productId(variation.getProductId())
                .price(variation.getPrice())
                .sku(variation.getSku())
                .imageUrl(variation.getImageUrl())
                .quantity(variation.getQuantity())
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
}