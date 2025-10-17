package vn.techbox.techbox_store.review.service;

import org.springframework.data.domain.Page;
import vn.techbox.techbox_store.review.dto.ReviewCreateRequest;
import vn.techbox.techbox_store.review.dto.ReviewResponse;
import vn.techbox.techbox_store.review.dto.ReviewSummaryResponse;
import vn.techbox.techbox_store.review.dto.ReviewUpdateRequest;

public interface ReviewService {
    ReviewResponse createReview(Integer productId, ReviewCreateRequest request, String currentUserEmail);
    ReviewResponse updateReview(Integer productId, Integer reviewId, ReviewUpdateRequest request, String currentUserEmail);
    void deleteReview(Integer productId, Integer reviewId, String currentUserEmail);
    Page<ReviewResponse> getReviews(Integer productId, int page, int size);
    ReviewSummaryResponse getSummary(Integer productId);
    ReviewResponse getUserReview(Integer productId, String currentUserEmail);
    boolean isReviewOwner(Integer reviewId, String email);
}

