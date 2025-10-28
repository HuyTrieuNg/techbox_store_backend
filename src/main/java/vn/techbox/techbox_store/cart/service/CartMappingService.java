package vn.techbox.techbox_store.cart.service;

import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import vn.techbox.techbox_store.cart.dto.CartItemResponse;
import vn.techbox.techbox_store.cart.dto.CartResponse;
import vn.techbox.techbox_store.cart.model.Cart;
import vn.techbox.techbox_store.cart.model.CartItem;
import vn.techbox.techbox_store.product.model.ProductVariation;

import java.math.BigDecimal;
import java.util.Collections;
import java.util.List;
import java.util.stream.Collectors;

@Service
@RequiredArgsConstructor
public class CartMappingService {

    public CartResponse toCartResponse(Cart cart) {
        if (cart == null) {
            return null;
        }

        List<CartItemResponse> itemResponses = cart.getCartItems() != null
            ? cart.getCartItems().stream()
                .map(this::toCartItemResponse)
                .collect(Collectors.toList())
            : Collections.emptyList();

        CartResponse.CartSummary summary = calculateCartSummary(itemResponses);

        return CartResponse.builder()
                .id(cart.getId())
                .userId(cart.getUserId())
                .items(itemResponses)
                .totalItems(cart.getTotalItems())
                .subtotal(calculateSubtotal(itemResponses))
                .createdAt(cart.getCreatedAt())
                .updatedAt(cart.getUpdatedAt())
                .isEmpty(cart.isEmpty())
                .summary(summary)
                .build();
    }

    public CartItemResponse toCartItemResponse(CartItem cartItem) {
        if (cartItem == null) {
            return null;
        }

        ProductVariation pv = cartItem.getProductVariation();

        return CartItemResponse.builder()
                .id(cartItem.getId())
                .productVariationId(pv.getId())
                .productName(pv.getProduct().getName())
                .productImage(getProductImage(pv))
                .variantName(buildVariantName(pv))
                .quantity(cartItem.getQuantity())
                .unitPrice(cartItem.getUnitPrice())
                .totalPrice(cartItem.getTotalPrice())
                .addedAt(cartItem.getAddedAt())
                .updatedAt(cartItem.getUpdatedAt())
                .sku(pv.getSku())
                .stockQuantity(pv.getStockQuantity())
                .isAvailable(pv.getStockQuantity() > 0)
                .build();
    }

    private BigDecimal calculateSubtotal(List<CartItemResponse> items) {
        return items.stream()
                .map(CartItemResponse::getTotalPrice)
                .reduce(BigDecimal.ZERO, BigDecimal::add);
    }

    private CartResponse.CartSummary calculateCartSummary(List<CartItemResponse> items) {
        int totalQuantity = items.stream()
                .mapToInt(CartItemResponse::getQuantity)
                .sum();

        BigDecimal totalAmount = calculateSubtotal(items);

        int uniqueItems = items.size();

        boolean hasUnavailableItems = items.stream()
                .anyMatch(item -> !item.isAvailable() || item.getStockQuantity() < item.getQuantity());

        return CartResponse.CartSummary.builder()
                .totalQuantity(totalQuantity)
                .totalAmount(totalAmount)
                .uniqueItems(uniqueItems)
                .hasUnavailableItems(hasUnavailableItems)
                .build();
    }

    private String getProductImage(ProductVariation pv) {
        if (pv.getImages() != null && !pv.getImages().isEmpty()) {
            return pv.getImages().getFirst().getImageUrl();
        }
        if (pv.getProduct() != null && pv.getProduct().getImageUrl() != null) {
            return pv.getProduct().getImageUrl();
        }

        return null;
    }

    private String buildVariantName(ProductVariation pv) {
        if (pv.getVariationAttributes() != null && !pv.getVariationAttributes().isEmpty()) {
            return pv.getVariationAttributes().stream()
                    .map(va -> va.getAttribute().getName() + ": " + va.getValue())
                    .collect(Collectors.joining(", "));
        }

        return pv.getVariationName() != null && !pv.getVariationName().trim().isEmpty()
                ? pv.getVariationName()
                : "Default";
    }
}
