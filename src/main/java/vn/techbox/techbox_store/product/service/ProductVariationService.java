package vn.techbox.techbox_store.product.service;

import vn.techbox.techbox_store.product.dto.productDto.ProductVariationCreateRequest;
import vn.techbox.techbox_store.product.dto.productDto.ProductVariationManagementResponse;
import vn.techbox.techbox_store.product.dto.productDto.ProductVariationResponse;
import vn.techbox.techbox_store.product.dto.productDto.ProductVariationUpdateRequest;
import vn.techbox.techbox_store.product.model.ProductVariation;

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
    
    List<ProductVariationResponse> getActiveVariationsByProductId(Integer productId);
    
    List<ProductVariationResponse> getInStockVariations();
    
    List<ProductVariationResponse> getInStockVariationsByProductId(Integer productId);
    
    List<ProductVariationResponse> getLowStockVariations(Integer threshold);
    
    Optional<ProductVariationResponse> getVariationBySku(String sku);
    
    ProductVariationResponse updateStock(Integer id, Integer stockQuantity);
    
    boolean existsBySku(String sku);
    
    boolean existsBySkuAndIdNot(String sku, Integer id);
    
    // Internal methods for service-to-service communication (returns entities to avoid N+1)
    List<ProductVariation> getActiveVariationEntitiesByProductId(Integer productId);
    
    int countActiveVariationsByProductId(Integer productId);
    int countTotalVariationsByProductId(Integer productId);
    
    // Management methods with optional deleted filter
    // deleted = null (default) -> all variations
    // deleted = false -> only active variations (deletedAt IS NULL)
    // deleted = true -> only soft-deleted variations (deletedAt IS NOT NULL)
    List<ProductVariationManagementResponse> getVariationsForManagement(Integer productId, Boolean deleted);

    /**
     * Hard delete a product variation by its ID.
     * @param id The ID of the product variation to delete.
     */
    void deleteProductVariationHard(Integer id);
}