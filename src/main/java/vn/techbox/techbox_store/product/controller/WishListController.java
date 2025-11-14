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
import vn.techbox.techbox_store.product.dto.wishListDto.CheckWishlistRequest;
import vn.techbox.techbox_store.product.dto.productDto.ProductListResponse;
import vn.techbox.techbox_store.product.dto.wishListDto.WishListRequest;
import vn.techbox.techbox_store.product.service.WishListService;
import vn.techbox.techbox_store.user.security.UserPrincipal;

import java.util.Map;

@RestController
@RequestMapping("/wishlists")
@RequiredArgsConstructor
public class WishListController {

    private final WishListService wishListService;

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
        
        UserPrincipal userPrincipal = (UserPrincipal) authentication.getPrincipal();
        Integer userId = userPrincipal.id();
        
        Sort.Direction direction = sortDirection.equalsIgnoreCase("DESC") 
                ? Sort.Direction.DESC 
                : Sort.Direction.ASC;
        Pageable pageable = PageRequest.of(page, size, Sort.by(direction, sortBy));
        
        Page<ProductListResponse> wishlist = wishListService.getWishListByUserId(userId, pageable);
        return ResponseEntity.ok(wishlist);
    }

    /**
     * Add product to wishlist
     * POST /wishlists
     */
    @PostMapping
    public ResponseEntity<ProductListResponse> addToWishList(
            @Valid @RequestBody WishListRequest request,
            Authentication authentication) {
        
        UserPrincipal userPrincipal = (UserPrincipal) authentication.getPrincipal();
        Integer userId = userPrincipal.id();
        
        ProductListResponse response = wishListService.addToWishList(userId, request);
        return ResponseEntity.status(HttpStatus.CREATED).body(response);
    }

    /**
     * Remove product from wishlist
     * DELETE /wishlists/{productId}
     */
    @DeleteMapping("/{productId}")
    public ResponseEntity<Void> removeFromWishList(
            @PathVariable Integer productId,
            Authentication authentication) {
        
        UserPrincipal userPrincipal = (UserPrincipal) authentication.getPrincipal();
        Integer userId = userPrincipal.id();
        
        WishListRequest request = new WishListRequest();
        request.setProductId(productId);
        
        wishListService.removeFromWishList(userId, request);
        return ResponseEntity.noContent().build();
    }
    
    /**
     * Check if multiple products are in wishlist
     * POST /wishlists/check
     * @return Map of productId -> inWishlist (true/false)
     */
    @PostMapping("/check")
    public ResponseEntity<Map<Integer, Boolean>> checkInWishlist(
            @Valid @RequestBody CheckWishlistRequest request,
            Authentication authentication) {
        
        UserPrincipal userPrincipal = (UserPrincipal) authentication.getPrincipal();
        Integer userId = userPrincipal.id();
        
        Map<Integer, Boolean> result = wishListService.checkInWishlist(userId, request.getProductIds());
        return ResponseEntity.ok(result);
    }
}
