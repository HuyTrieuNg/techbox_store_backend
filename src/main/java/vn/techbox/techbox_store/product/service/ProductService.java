package vn.techbox.techbox_store.product.service;

import vn.techbox.techbox_store.product.dto.ProductCreateRequest;
import vn.techbox.techbox_store.product.dto.ProductResponse;
import vn.techbox.techbox_store.product.dto.ProductUpdateRequest;

import java.util.List;
import java.util.Optional;

public interface ProductService {
    
    List<ProductResponse> getAllProducts();
    
    List<ProductResponse> getAllActiveProducts();
    
    Optional<ProductResponse> getProductById(Integer id);
    
    Optional<ProductResponse> getActiveProductById(Integer id);
    
    ProductResponse createProduct(ProductCreateRequest request);
    
    ProductResponse updateProduct(Integer id, ProductUpdateRequest request);
    
    void deleteProduct(Integer id);
    
    void restoreProduct(Integer id);
    
    List<ProductResponse> getProductsByCategory(Integer categoryId);
    
    List<ProductResponse> getProductsByBrand(Integer brandId);
    
    List<ProductResponse> searchProductsByName(String keyword);
    
    boolean existsByName(String name);
    
    boolean existsByNameAndIdNot(String name, Integer id);
}