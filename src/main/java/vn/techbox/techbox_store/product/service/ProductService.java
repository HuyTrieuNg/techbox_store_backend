package vn.techbox.techbox_store.product.service;

import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import vn.techbox.techbox_store.product.dto.*;

import java.util.Optional;

public interface ProductService {
    
    // Chi tiết sản phẩm với đầy đủ thông tin
    Optional<ProductDetailResponse> getProductDetailById(Integer id, Integer userId);
    
    // Search & Filter với nhiều tiêu chí + phân trang
    Page<ProductListResponse> filterProducts(ProductFilterRequest filterRequest, Integer userId);
    
    // Public: Xem tất cả sản phẩm active với phân trang (không cần authentication)
    Page<ProductListResponse> getAllProducts(Pageable pageable);
    
    // Admin: Xem chỉ sản phẩm đã xóa mềm với phân trang
    Page<ProductListResponse> getDeletedProductsForAdmin(Pageable pageable);
    
    // Lấy danh sách sản phẩm theo campaign
    Page<ProductListResponse> getProductsByCampaign(Integer campaignId, Pageable pageable, Integer userId);

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