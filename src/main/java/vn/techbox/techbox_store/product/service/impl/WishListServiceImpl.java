package vn.techbox.techbox_store.product.service.impl;

import lombok.RequiredArgsConstructor;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageImpl;
import org.springframework.data.domain.Pageable;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import vn.techbox.techbox_store.product.dto.productDto.ProductListResponse;
import vn.techbox.techbox_store.product.dto.wishListDto.WishListRequest;
import vn.techbox.techbox_store.product.mapper.ProductMapper;
import vn.techbox.techbox_store.product.model.Product;
import vn.techbox.techbox_store.product.model.ProductStatus;
import vn.techbox.techbox_store.product.model.WishList;
import vn.techbox.techbox_store.product.repository.ProductRepository;
import vn.techbox.techbox_store.product.repository.WishListRepository;
import vn.techbox.techbox_store.product.service.WishListService;

import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.stream.Collectors;

@Service
@RequiredArgsConstructor
@Transactional
public class WishListServiceImpl implements WishListService {

    private final WishListRepository wishListRepository;
    private final ProductRepository productRepository;
    private final ProductMapper productMapper;

    @Override
    public ProductListResponse addToWishList(Integer userId, WishListRequest wishListRequest) {
        Integer productId = wishListRequest.getProductId();
        
        // Check if product exists and is published
        Product product = productRepository.findById(productId)
                .orElseThrow(() -> new RuntimeException("Product not found with id: " + productId));
        
        if (product.getStatus() != ProductStatus.PUBLISHED) {
            throw new IllegalArgumentException("Cannot add unpublished product to wishlist");
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
        
        return productMapper.toListResponse(product);
    }

    @Override
    @Transactional(readOnly = true)
    public Page<ProductListResponse> getWishListByUserId(Integer userId, Pageable pageable) {
        Page<WishList> wishListsPage = wishListRepository.findByUserId(userId, pageable);
        
        // Filter out null and non-published products, then convert to response
        List<ProductListResponse> productResponses = wishListsPage.getContent().stream()
                .map(WishList::getProduct)
                .filter(product -> product != null && product.getStatus() == ProductStatus.PUBLISHED)
                .map(productMapper::toListResponse)
                .collect(Collectors.toList());
        
        return new PageImpl<>(productResponses, pageable, wishListsPage.getTotalElements());
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
    
    @Override
    @Transactional(readOnly = true)
    public Map<Integer, Boolean> checkInWishlist(Integer userId, List<Integer> productIds) {
        if (productIds == null || productIds.isEmpty()) {
            return new HashMap<>();
        }
        
        // Get all wishlist items for the given user and product IDs
        List<WishList> wishListItems = wishListRepository.findByUserIdAndProductIdIn(userId, productIds);
        
        // Extract product IDs that are in wishlist
        List<Integer> wishedProductIds = wishListItems.stream()
                .map(WishList::getProductId)
                .collect(Collectors.toList());
        
        // Create result map: productId -> inWishlist (true/false)
        Map<Integer, Boolean> result = new HashMap<>();
        for (Integer productId : productIds) {
            result.put(productId, wishedProductIds.contains(productId));
        }
        
        return result;
    }
}
