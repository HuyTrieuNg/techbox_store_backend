package vn.techbox.techbox_store.product.service;

import org.springframework.data.domain.Page;
import vn.techbox.techbox_store.product.dto.productDto.*;

import java.util.Map;
import java.util.Optional;

public interface ProductService {
    
    // Chi tiết sản phẩm với đầy đủ thông tin
    Optional<ProductDetailResponse> getProductDetailById(Integer id);
    
    // Chi tiết sản phẩm cho management (full info, không filter soft delete)
    ProductManagementDetailResponse getProductForManagement(Integer id);
    
    // Search & Filter với nhiều tiêu chí + phân trang
    Page<ProductListResponse> filterProducts(ProductFilterRequest filterRequest);
    
    Page<ProductManagementListResponse> filterProductsForManagement(ProductFilterRequest filterRequest);

    void addAttributesToProduct(Integer productId, Map<Integer, String> attributes);
    




    ProductResponse createProduct(ProductCreateRequest request);

    ProductResponse createProductWithAttributes(ProductWithAttributesRequest request);

    ProductResponse updateProduct(Integer id, ProductUpdateRequest request);
    
    void deleteProduct(Integer id);
    
    void restoreProduct(Integer id);
    
    // Status management
    ProductResponse publishProduct(Integer id);
    
    ProductResponse draftProduct(Integer id);
    
    ProductResponse deleteProductSoft(Integer id);
    
    boolean existsByName(String name);
    
    boolean existsByNameAndIdNot(String name, Integer id);
    
    // Cập nhật rating của sản phẩm
    void updateProductRating(Integer productId);
    
    // Internal use
    Optional<ProductResponse> getProductById(Integer id);
    
    void deleteProductHard(Integer id);
}