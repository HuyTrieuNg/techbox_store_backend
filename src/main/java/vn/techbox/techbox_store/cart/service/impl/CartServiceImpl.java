package vn.techbox.techbox_store.cart.service.impl;

import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import vn.techbox.techbox_store.cart.dto.AddToCartRequest;
import vn.techbox.techbox_store.cart.dto.CartResponse;
import vn.techbox.techbox_store.cart.dto.UpdateCartItemRequest;
import vn.techbox.techbox_store.cart.exception.CartException;
import vn.techbox.techbox_store.cart.model.Cart;
import vn.techbox.techbox_store.cart.model.CartItem;
import vn.techbox.techbox_store.cart.repository.CartItemRepository;
import vn.techbox.techbox_store.cart.repository.CartRepository;
import vn.techbox.techbox_store.cart.service.CartMappingService;
import vn.techbox.techbox_store.cart.service.CartService;
import vn.techbox.techbox_store.product.model.ProductVariation;
import vn.techbox.techbox_store.product.repository.ProductVariationRepository;
import vn.techbox.techbox_store.promotion.dto.PromotionCalculationRequest;
import vn.techbox.techbox_store.promotion.dto.PromotionCalculationResponse;
import vn.techbox.techbox_store.promotion.service.PromotionService;

import java.time.LocalDateTime;
import java.util.Map;
import java.util.Optional;
import java.util.stream.Collectors;

@Service
@RequiredArgsConstructor
@Slf4j
@Transactional
public class CartServiceImpl implements CartService {

    private final CartRepository cartRepository;
    private final CartItemRepository cartItemRepository;
    private final ProductVariationRepository productVariationRepository;
    private final CartMappingService cartMappingService;
    private final PromotionService promotionService;

    @Override
    @Transactional
    public CartResponse getUserCart(Integer userId) {
        log.info("Getting cart for user: {}", userId);

        Optional<Cart> cartOpt = cartRepository.findByUserIdWithItems(userId);

        if (cartOpt.isPresent()) {
            Cart cart = cartOpt.get();
            // Lấy thông tin promotion cho tất cả items trong giỏ hàng
            Map<Integer, PromotionCalculationResponse> promotionMap = getPromotionsForCart(cart);
            return cartMappingService.toCartResponse(cart, promotionMap);
        }

        Cart newCart = Cart.builder()
                .userId(userId)
                .build();
        Cart savedCart = cartRepository.save(newCart);
        return cartMappingService.toCartResponse(savedCart, Map.of());
    }

    @Override
    public CartResponse addToCart(Integer userId, AddToCartRequest request) {
        log.info("Adding product {} to cart for user {}", request.getProductVariationId(), userId);

        Cart cart = getOrCreateUserCart(userId);
        addItemToCart(cart, request.getProductVariationId(), request.getQuantity());

        Cart updatedCart = cartRepository.findByUserIdWithItems(userId).orElse(cart);
        Map<Integer, PromotionCalculationResponse> promotionMap = getPromotionsForCart(updatedCart);
        return cartMappingService.toCartResponse(updatedCart, promotionMap);
    }

    @Override
    public CartResponse updateCartItem(Integer userId, Integer productVariationId, UpdateCartItemRequest request) {
        log.info("Updating cart item for user {}, product {}, quantity {}",
                userId, productVariationId, request.getQuantity());

        Cart cart = getOrCreateUserCart(userId);

        Optional<CartItem> cartItemOpt = cartItemRepository
                .findByCartIdAndProductVariationId(cart.getId(), productVariationId);

        if (cartItemOpt.isPresent()) {
            CartItem cartItem = cartItemOpt.get();

            if (request.getQuantity() == 0) {
                cartItemRepository.delete(cartItem);
                log.info("Removed cart item for product {}", productVariationId);
            } else {
                cartItem.updateQuantity(request.getQuantity());
                cartItemRepository.save(cartItem);
                log.info("Updated cart item quantity to {}", request.getQuantity());
            }
            cart.setUpdatedAt(LocalDateTime.now());
            cartRepository.save(cart);
        } else {
            // Nếu item không tồn tại và quantity > 0, có thể thông báo lỗi hoặc bỏ qua
            if (request.getQuantity() > 0) {
                log.warn("Trying to update non-existent cart item: user={}, product={}", userId, productVariationId);
            }
        }

        Cart updatedCart = cartRepository.findByUserIdWithItems(userId).orElse(cart);
        Map<Integer, PromotionCalculationResponse> promotionMap = getPromotionsForCart(updatedCart);
        return cartMappingService.toCartResponse(updatedCart, promotionMap);
    }

    @Override
    public CartResponse removeFromCart(Integer userId, Integer productVariationId) {
        log.info("Removing product {} from cart for user {}", productVariationId, userId);

        Cart cart = getOrCreateUserCart(userId);

        cartItemRepository.deleteByCartIdAndProductVariationId(cart.getId(), productVariationId);

        cart.setUpdatedAt(LocalDateTime.now());
        cartRepository.save(cart);

        Cart updatedCart = cartRepository.findByUserIdWithItems(userId).orElse(cart);
        Map<Integer, PromotionCalculationResponse> promotionMap = getPromotionsForCart(updatedCart);
        return cartMappingService.toCartResponse(updatedCart, promotionMap);
    }

    @Override
    public void clearCart(Integer userId) {
        log.info("Clearing cart for user: {}", userId);

        Cart cart = getOrCreateUserCart(userId);
        cartItemRepository.deleteByCartId(cart.getId());

        cart.setUpdatedAt(LocalDateTime.now());
        cartRepository.save(cart);
    }

    @Override
    public int deleteOldCarts(int daysOld) {
        LocalDateTime cutoffTime = LocalDateTime.now().minusDays(daysOld);
        log.info("Deleting carts older than {} days (before {})", daysOld, cutoffTime);

        int deletedCount = cartRepository.deleteCartsOlderThan(cutoffTime);
        log.info("Deleted {} old carts", deletedCount);

        return deletedCount;
    }

    private Cart getOrCreateUserCart(Integer userId) {
        return cartRepository.findByUserId(userId)
                .orElseGet(() -> {
                    Cart newCart = Cart.builder()
                            .userId(userId)
                            .build();
                    return cartRepository.save(newCart);
                });
    }

    private void addItemToCart(Cart cart, Integer productVariationId, Integer quantity) {
        ProductVariation productVariation = productVariationRepository.findById(productVariationId)
                .orElseThrow(() -> new CartException.CartItemNotFoundException("Product variation not found: " + productVariationId));

        if (productVariation.getStockQuantity() < quantity) {
            throw new CartException.InsufficientStockException("Insufficient stock. Available: " + productVariation.getStockQuantity());
        }
        Optional<CartItem> existingItemOpt = cartItemRepository
                .findByCartIdAndProductVariationId(cart.getId(), productVariationId);

        if (existingItemOpt.isPresent()) {
            // Cập nhật quantity nếu item đã tồn tại
            CartItem existingItem = existingItemOpt.get();
            int newQuantity = existingItem.getQuantity() + quantity;

            if (productVariation.getStockQuantity() < newQuantity) {
                throw new CartException.InsufficientStockException("Insufficient stock. Available: " + productVariation.getStockQuantity() +
                        ", Requested total: " + newQuantity);
            }

            existingItem.updateQuantity(newQuantity);
            cartItemRepository.save(existingItem);
        } else {
            // Tạo item mới
            CartItem newItem = CartItem.builder()
                    .cart(cart)
                    .productVariation(productVariation)
                    .quantity(quantity)
                    .unitPrice(productVariation.getPrice())
                    .build();
            cartItemRepository.save(newItem);
        }

        cart.setUpdatedAt(LocalDateTime.now());
        cartRepository.save(cart);
    }
    
    /**
     * Lấy thông tin promotion cho tất cả items trong giỏ hàng
     * @param cart Cart entity
     * @return Map với key là productVariationId và value là PromotionCalculationResponse
     */
    private Map<Integer, PromotionCalculationResponse> getPromotionsForCart(Cart cart) {
        if (cart.getCartItems() == null || cart.getCartItems().isEmpty()) {
            return Map.of();
        }
        
        return cart.getCartItems().stream()
                .collect(Collectors.toMap(
                    cartItem -> cartItem.getProductVariation().getId(),
                    cartItem -> {
                        PromotionCalculationRequest request = PromotionCalculationRequest.builder()
                                .productVariationId(cartItem.getProductVariation().getId())
                                .originalPrice(cartItem.getUnitPrice())
                                .build();
                        return promotionService.calculatePromotions(request);
                    }
                ));
    }
}
