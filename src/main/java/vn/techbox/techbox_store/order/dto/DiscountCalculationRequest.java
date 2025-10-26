package vn.techbox.techbox_store.order.dto;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.math.BigDecimal;
import java.util.List;

@Data
@Builder
@NoArgsConstructor
@AllArgsConstructor
public class DiscountCalculationRequest {

    private List<OrderItemRequest> orderItems;
    private String voucherCode;

    @Data
    @Builder
    @NoArgsConstructor
    @AllArgsConstructor
    public static class OrderItemRequest {
        private Integer productVariationId;
        private Integer quantity;
        private BigDecimal unitPrice;
    }
}
