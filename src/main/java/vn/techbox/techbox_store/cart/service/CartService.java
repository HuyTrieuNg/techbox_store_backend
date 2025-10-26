package vn.techbox.techbox_store.cart.service;

import vn.techbox.techbox_store.cart.dto.AddToCartRequest;
import vn.techbox.techbox_store.cart.dto.CartResponse;
import vn.techbox.techbox_store.cart.dto.UpdateCartItemRequest;

public interface CartService {

    CartResponse getUserCart(Integer userId);

    CartResponse addToCart(Integer userId, AddToCartRequest request);

    CartResponse updateCartItem(Integer userId, Integer productVariationId, UpdateCartItemRequest request);

    CartResponse removeFromCart(Integer userId, Integer productVariationId);

    void clearCart(Integer userId);

    int deleteOldCarts(int daysOld);
}
