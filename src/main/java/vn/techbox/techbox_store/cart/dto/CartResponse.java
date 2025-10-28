package vn.techbox.techbox_store.cart.dto;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.math.BigDecimal;
import java.time.LocalDateTime;
import java.util.List;

@Data
@Builder
@NoArgsConstructor
@AllArgsConstructor
public class CartResponse {

    private Integer id;
    private Integer userId;
    private List<CartItemResponse> items;
    private int totalItems;
    private BigDecimal subtotal;
    private LocalDateTime createdAt;
    private LocalDateTime updatedAt;
    private boolean isEmpty;

    private CartSummary summary;

    @Data
    @Builder
    @NoArgsConstructor
    @AllArgsConstructor
    public static class CartSummary {
        private int totalQuantity;
        private BigDecimal totalAmount;
        private int uniqueItems;
        private boolean hasUnavailableItems;
    }
}
