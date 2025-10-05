package vn.techbox.techbox_store.product.review.service;

import org.springframework.data.domain.Page;
import vn.techbox.techbox_store.product.review.dto.*;

public interface ReviewService {
    ReviewResponse createReview(Integer productId, ReviewCreateRequest request, String currentUserEmail);
    ReviewResponse updateReview(Integer productId, Integer reviewId, ReviewUpdateRequest request, String currentUserEmail);
    void deleteReview(Integer productId, Integer reviewId, String currentUserEmail);
    Page<ReviewResponse> getReviews(Integer productId, int page, int size);
    ReviewSummaryResponse getSummary(Integer productId);
    ReviewResponse getUserReview(Integer productId, String currentUserEmail);
}

