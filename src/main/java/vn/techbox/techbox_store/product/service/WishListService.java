package vn.techbox.techbox_store.product.service;

import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import vn.techbox.techbox_store.product.dto.productDto.ProductListResponse;
import vn.techbox.techbox_store.product.dto.wishListDto.WishListRequest;

import java.util.List;
import java.util.Map;

public interface WishListService {
    ProductListResponse addToWishList(Integer userId, WishListRequest wishListRequest);

    Page<ProductListResponse> getWishListByUserId(Integer userId, Pageable pageable);

    void removeFromWishList(Integer userId, WishListRequest wishListRequest);
    
    Map<Integer, Boolean> checkInWishlist(Integer userId, List<Integer> productIds);

}
