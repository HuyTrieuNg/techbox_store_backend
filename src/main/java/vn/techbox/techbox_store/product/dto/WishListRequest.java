package vn.techbox.techbox_store.product.dto;

import jakarta.validation.constraints.NotNull;
import lombok.Data;

@Data
public class WishListRequest {
    @NotNull(message = "Product ID is required")
    private Integer productId;
}
