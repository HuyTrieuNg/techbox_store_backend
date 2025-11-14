package vn.techbox.techbox_store.product.service;

import vn.techbox.techbox_store.product.dto.productDto.ProductVariationCreateRequest;
import vn.techbox.techbox_store.product.dto.productDto.ProductVariationResponse;
import vn.techbox.techbox_store.product.dto.productDto.ProductVariationUpdateRequest;

import java.util.List;
import java.util.Optional;

public interface ProductVariationService {
    
    List<ProductVariationResponse> getAllProductVariations();
    
    List<ProductVariationResponse> getAllActiveProductVariations();
    
    Optional<ProductVariationResponse> getProductVariationById(Integer id);
    
    Optional<ProductVariationResponse> getActiveProductVariationById(Integer id);
    
    ProductVariationResponse createProductVariation(ProductVariationCreateRequest request);
    
    ProductVariationResponse updateProductVariation(Integer id, ProductVariationUpdateRequest request);
    
    void deleteProductVariation(Integer id);
    
    void restoreProductVariation(Integer id);
    
    List<ProductVariationResponse> getVariationsByProductId(Integer productId);
    
    List<ProductVariationResponse> getInStockVariations();
    
    List<ProductVariationResponse> getInStockVariationsByProductId(Integer productId);
    
    List<ProductVariationResponse> getLowStockVariations(Integer threshold);
    
    Optional<ProductVariationResponse> getVariationBySku(String sku);
    
    ProductVariationResponse updateStock(Integer id, Integer stockQuantity);
    
    boolean existsBySku(String sku);
    
    boolean existsBySkuAndIdNot(String sku, Integer id);
}