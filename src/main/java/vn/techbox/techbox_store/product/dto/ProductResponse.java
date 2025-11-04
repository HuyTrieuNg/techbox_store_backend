package vn.techbox.techbox_store.product.dto;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.time.LocalDateTime;

@Data
@NoArgsConstructor
@AllArgsConstructor
@Builder
public class ProductResponse {

    private Integer id;
    private String name;
    private String description;
    private Integer categoryId;
    private String categoryName;
    private Integer brandId;
    private String brandName;
    private String imageUrl;
    private String imagePublicId;
    private Integer warrantyMonths;
    private Double averageRating;
    private Integer totalRatings;
    private LocalDateTime createdAt;
    private LocalDateTime updatedAt;
    private LocalDateTime deletedAt;
    private Boolean inWishlist;

    // Helper method to check if deleted
    public boolean isDeleted() {
        return deletedAt != null;
    }
}