package vn.techbox.techbox_store.product.dto;

import jakarta.validation.constraints.NotEmpty;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.util.List;

/**
 * Request DTO for checking multiple products in wishlist
 */
@Data
@NoArgsConstructor
@AllArgsConstructor
@Builder
public class CheckWishlistRequest {
    
    @NotEmpty(message = "Product IDs list cannot be empty")
    private List<Integer> productIds;
}
