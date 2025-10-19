package vn.techbox.techbox_store.product.service;

import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import vn.techbox.techbox_store.product.dto.ProductListResponse;
import vn.techbox.techbox_store.product.dto.WishListRequest;

public interface WishListService {
    ProductListResponse addToWishList(Integer userId, WishListRequest wishListRequest);

    Page<ProductListResponse> getWishListByUserId(Integer userId, Pageable pageable);

    void removeFromWishList(Integer userId, WishListRequest wishListRequest);

}
