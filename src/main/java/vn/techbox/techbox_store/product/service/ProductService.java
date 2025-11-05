package vn.techbox.techbox_store.product.service;

import org.springframework.data.domain.Page;
import vn.techbox.techbox_store.product.dto.productDto.*;

import java.util.Optional;

public interface ProductService {
    
    // Chi tiết sản phẩm với đầy đủ thông tin
    Optional<ProductDetailResponse> getProductDetailById(Integer id);
    
    // Search & Filter với nhiều tiêu chí + phân trang
    Page<ProductListResponse> filterProducts(ProductFilterRequest filterRequest);
    





    ProductResponse createProduct(ProductCreateRequest request);
    
    ProductResponse updateProduct(Integer id, ProductUpdateRequest request);
    
    void deleteProduct(Integer id);
    
    void restoreProduct(Integer id);
    
    boolean existsByName(String name);
    
    boolean existsByNameAndIdNot(String name, Integer id);
    
    // Cập nhật rating của sản phẩm
    void updateProductRating(Integer productId);
    
    // Internal use
    Optional<ProductResponse> getProductById(Integer id);
}