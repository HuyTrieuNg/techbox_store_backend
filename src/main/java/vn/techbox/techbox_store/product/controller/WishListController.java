package vn.techbox.techbox_store.product.controller;

import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Pageable;
import org.springframework.data.domain.Sort;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.security.core.Authentication;
import org.springframework.web.bind.annotation.*;
import vn.techbox.techbox_store.product.dto.ProductListResponse;
import vn.techbox.techbox_store.product.dto.WishListRequest;
import vn.techbox.techbox_store.product.service.WishListService;
import vn.techbox.techbox_store.user.model.User;

@RestController
@RequestMapping("/wishlists")
@RequiredArgsConstructor
public class WishListController {

    private final WishListService wishListService;

    /**
     * Add product to wishlist
     * POST /wishlists
     */
    @PostMapping
    public ResponseEntity<ProductListResponse> addToWishList(
            @Valid @RequestBody WishListRequest request,
            Authentication authentication) {
        
        User user = (User) authentication.getPrincipal();
        Integer userId = user.getId();
        
        ProductListResponse response = wishListService.addToWishList(userId, request);
        return ResponseEntity.status(HttpStatus.CREATED).body(response);
    }

    /**
     * Get user's wishlist with pagination
     * GET /wishlists
     */
    @GetMapping
    public ResponseEntity<Page<ProductListResponse>> getWishList(
            Authentication authentication,
            @RequestParam(defaultValue = "0") int page,
            @RequestParam(defaultValue = "20") int size,
            @RequestParam(defaultValue = "id") String sortBy,
            @RequestParam(defaultValue = "DESC") String sortDirection) {
        
        User user = (User) authentication.getPrincipal();
        Integer userId = user.getId();
        
        Sort.Direction direction = sortDirection.equalsIgnoreCase("DESC") 
                ? Sort.Direction.DESC 
                : Sort.Direction.ASC;
        Pageable pageable = PageRequest.of(page, size, Sort.by(direction, sortBy));
        
        Page<ProductListResponse> wishlist = wishListService.getWishListByUserId(userId, pageable);
        return ResponseEntity.ok(wishlist);
    }

    /**
     * Remove product from wishlist
     * DELETE /wishlists/{productId}
     */
    @DeleteMapping("/{productId}")
    public ResponseEntity<Void> removeFromWishList(
            @PathVariable Integer productId,
            Authentication authentication) {
        
        User user = (User) authentication.getPrincipal();
        Integer userId = user.getId();
        
        WishListRequest request = new WishListRequest();
        request.setProductId(productId);
        
        wishListService.removeFromWishList(userId, request);
        return ResponseEntity.noContent().build();
    }
}
