package vn.techbox.techbox_store.product.review.dto;

import lombok.Builder;
import lombok.Data;

@Data
@Builder
public class ReviewSummaryResponse {
    private Integer productId;
    private long totalReviews;
    private double averageRating;
    private long rating1Count;
    private long rating2Count;
    private long rating3Count;
    private long rating4Count;
    private long rating5Count;
}

