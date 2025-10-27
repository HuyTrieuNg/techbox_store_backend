package vn.techbox.techbox_store.cart.controller;

import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.http.ResponseEntity;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.security.core.annotation.AuthenticationPrincipal;
import org.springframework.web.bind.annotation.*;
import vn.techbox.techbox_store.cart.dto.AddToCartRequest;
import vn.techbox.techbox_store.cart.dto.CartResponse;
import vn.techbox.techbox_store.cart.dto.UpdateCartItemRequest;
import vn.techbox.techbox_store.cart.service.CartService;
import vn.techbox.techbox_store.user.security.UserPrincipal;

import jakarta.validation.Valid;

@RestController
@RequestMapping("/cart")
@RequiredArgsConstructor
@Slf4j
@PreAuthorize("hasRole('USER')")
public class CartController {

    private final CartService cartService;

    @GetMapping
    @PreAuthorize("hasRole('CUSTOMER')")
    public ResponseEntity<CartResponse> getUserCart(@AuthenticationPrincipal UserPrincipal userPrincipal) {
        Integer userId = userPrincipal.getId();
        log.info("Getting cart for user: {}", userId);

        CartResponse cart = cartService.getUserCart(userId);
        return ResponseEntity.ok(cart);
    }

    @PostMapping("/add")
    @PreAuthorize("hasRole('CUSTOMER')")
    public ResponseEntity<CartResponse> addToCart(
            @AuthenticationPrincipal UserPrincipal userPrincipal,
            @Valid @RequestBody AddToCartRequest request) {

        Integer userId = userPrincipal.getId();
        log.info("Adding product {} to cart for user {}",
                request.getProductVariationId(), userId);

        CartResponse cart = cartService.addToCart(userId, request);
        return ResponseEntity.ok(cart);
    }

    @PutMapping("/items/{productVariationId}")
    @PreAuthorize("hasRole('CUSTOMER')")
    public ResponseEntity<CartResponse> updateCartItem(
            @AuthenticationPrincipal UserPrincipal userPrincipal,
            @PathVariable Integer productVariationId,
            @Valid @RequestBody UpdateCartItemRequest request) {

        Integer userId = userPrincipal.getId();
        log.info("Updating cart item for user {}, product {}, quantity {}",
                userId, productVariationId, request.getQuantity());

        CartResponse cart = cartService.updateCartItem(userId, productVariationId, request);
        return ResponseEntity.ok(cart);
    }

    @DeleteMapping("/items/{productVariationId}")
    @PreAuthorize("hasRole('CUSTOMER')")
    public ResponseEntity<CartResponse> removeFromCart(
            @AuthenticationPrincipal UserPrincipal userPrincipal,
            @PathVariable Integer productVariationId) {

        Integer userId = userPrincipal.getId();
        log.info("Removing product {} from cart for user {}", productVariationId, userId);

        CartResponse cart = cartService.removeFromCart(userId, productVariationId);
        return ResponseEntity.ok(cart);
    }

    @DeleteMapping("/clear")
    @PreAuthorize("hasRole('CUSTOMER')")
    public ResponseEntity<Void> clearCart(@AuthenticationPrincipal UserPrincipal userPrincipal) {
        Integer userId = userPrincipal.getId();
        log.info("Clearing cart for user: {}", userId);

        cartService.clearCart(userId);
        return ResponseEntity.ok().build();
    }

    @GetMapping("/count")
    @PreAuthorize("hasRole('CUSTOMER')")
    public ResponseEntity<CartCountResponse> getCartItemCount(@AuthenticationPrincipal UserPrincipal userPrincipal) {
        Integer userId = userPrincipal.getId();
        CartResponse cart = cartService.getUserCart(userId);

        CartCountResponse response = CartCountResponse.builder()
                .totalItems(cart.getTotalItems())
                .uniqueItems(cart.getSummary().getUniqueItems())
                .build();

        return ResponseEntity.ok(response);
    }

    @lombok.Data
    @lombok.Builder
    @lombok.NoArgsConstructor
    @lombok.AllArgsConstructor
    public static class CartCountResponse {
        private int totalItems;
        private int uniqueItems;
    }
}
