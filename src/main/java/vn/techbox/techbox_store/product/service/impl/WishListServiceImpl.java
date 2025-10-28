package vn.techbox.techbox_store.product.service.impl;

import lombok.RequiredArgsConstructor;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import vn.techbox.techbox_store.product.dto.ProductListResponse;
import vn.techbox.techbox_store.product.dto.WishListRequest;
import vn.techbox.techbox_store.product.model.Product;
import vn.techbox.techbox_store.product.model.WishList;
import vn.techbox.techbox_store.product.repository.ProductRepository;
import vn.techbox.techbox_store.product.repository.WishListRepository;
import vn.techbox.techbox_store.product.service.WishListService;

@Service
@RequiredArgsConstructor
@Transactional
public class WishListServiceImpl implements WishListService {

    private final WishListRepository wishListRepository;
    private final ProductRepository productRepository;

    @Override
    public ProductListResponse addToWishList(Integer userId, WishListRequest wishListRequest) {
        Integer productId = wishListRequest.getProductId();
        
        // Check if product exists and not deleted
        Product product = productRepository.findById(productId)
                .orElseThrow(() -> new RuntimeException("Product not found with id: " + productId));
        
        if (product.isDeleted()) {
            throw new IllegalArgumentException("Cannot add deleted product to wishlist");
        }
        
        // Check if already in wishlist
        if (wishListRepository.existsByUserIdAndProductId(userId, productId)) {
            throw new IllegalArgumentException("Product is already in wishlist");
        }
        
        // Create new wishlist item
        WishList wishList = WishList.builder()
                .userId(userId)
                .productId(productId)
                .build();
        
        wishListRepository.save(wishList);
        
        return convertToResponse(product);
    }

    @Override
    @Transactional(readOnly = true)
    public Page<ProductListResponse> getWishListByUserId(Integer userId, Pageable pageable) {
        Page<WishList> wishListsPage = wishListRepository.findByUserId(userId, pageable);
        
        return wishListsPage.map(wishList -> {
            Product product = wishList.getProduct();
            // Only include non-deleted products
            if (product != null && !product.isDeleted()) {
                return convertToResponse(product);
            }
            return null;
        });
    }

    @Override
    public void removeFromWishList(Integer userId, WishListRequest wishListRequest) {
        Integer productId = wishListRequest.getProductId();
        
        // Check if exists in wishlist
        WishList wishList = wishListRepository.findByUserIdAndProductId(userId, productId)
                .orElseThrow(() -> new RuntimeException(
                        "Product not found in wishlist. User ID: " + userId + ", Product ID: " + productId));
        
        wishListRepository.delete(wishList);
    }

    /**
     * Convert Product entity to ProductListResponse DTO for wishlist items
     */
    private ProductListResponse convertToResponse(Product product) {
        return ProductListResponse.builder()
                .id(product.getId())
                .name(product.getName())
                .imageUrl(product.getImageUrl())
                .displayOriginalPrice(product.getDisplayOriginalPrice())
                .displaySalePrice(product.getDisplaySalePrice())
                .discountType(product.getDiscountType())
                .discountValue(product.getDiscountValue())
                .averageRating(product.getAverageRating())
                .totalRatings(product.getTotalRatings())
                .inWishlist(true)
                .build();
    }
}
