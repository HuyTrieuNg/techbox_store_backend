package vn.techbox.techbox_store.product.service.impl;

import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import vn.techbox.techbox_store.product.dto.ProductCreateRequest;
import vn.techbox.techbox_store.product.dto.ProductResponse;
import vn.techbox.techbox_store.product.dto.ProductUpdateRequest;
import vn.techbox.techbox_store.product.model.Product;
import vn.techbox.techbox_store.product.repository.ProductRepository;
import vn.techbox.techbox_store.product.repository.CategoryRepository;
import vn.techbox.techbox_store.product.repository.BrandRepository;
import vn.techbox.techbox_store.product.service.ProductService;

import java.util.List;
import java.util.Optional;
import java.util.stream.Collectors;

@Service
@RequiredArgsConstructor
@Transactional
public class ProductServiceImpl implements ProductService {
    
    private final ProductRepository productRepository;
    private final CategoryRepository categoryRepository;
    private final BrandRepository brandRepository;
    
    @Override
    @Transactional(readOnly = true)
    public List<ProductResponse> getAllProducts() {
        return productRepository.findAll()
                .stream()
                .map(this::convertToResponse)
                .collect(Collectors.toList());
    }
    
    @Override
    @Transactional(readOnly = true)
    public List<ProductResponse> getAllActiveProducts() {
        return productRepository.findAllActive()
                .stream()
                .map(this::convertToResponse)
                .collect(Collectors.toList());
    }
    
    @Override
    @Transactional(readOnly = true)
    public Optional<ProductResponse> getProductById(Integer id) {
        return productRepository.findById(id)
                .map(this::convertToResponse);
    }
    
    @Override
    @Transactional(readOnly = true)
    public Optional<ProductResponse> getActiveProductById(Integer id) {
        return productRepository.findActiveById(id)
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
    public List<ProductResponse> getProductsByCategory(Integer categoryId) {
        return productRepository.findByCategoryId(categoryId)
                .stream()
                .map(this::convertToResponse)
                .collect(Collectors.toList());
    }
    
    @Override
    @Transactional(readOnly = true)
    public List<ProductResponse> getProductsByBrand(Integer brandId) {
        return productRepository.findByBrandId(brandId)
                .stream()
                .map(this::convertToResponse)
                .collect(Collectors.toList());
    }
    
    @Override
    @Transactional(readOnly = true)
    public List<ProductResponse> searchProductsByName(String keyword) {
        return productRepository.searchByName(keyword)
                .stream()
                .map(this::convertToResponse)
                .collect(Collectors.toList());
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
    
    private ProductResponse convertToResponse(Product product) {
        ProductResponse response = ProductResponse.builder()
                .id(product.getId())
                .name(product.getName())
                .description(product.getDescription())
                .categoryId(product.getCategoryId())
                .brandId(product.getBrandId())
                .imageUrl(product.getImageUrl())
                .imagePublicId(product.getImagePublicId())
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
}