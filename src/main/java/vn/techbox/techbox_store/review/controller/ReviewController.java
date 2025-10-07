package vn.techbox.techbox_store.review.controller;

import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import org.springframework.data.domain.Page;
import org.springframework.http.ResponseEntity;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.web.bind.annotation.*;
import vn.techbox.techbox_store.review.dto.*;
import vn.techbox.techbox_store.review.dto.ReviewCreateRequest;
import vn.techbox.techbox_store.review.dto.ReviewResponse;
import vn.techbox.techbox_store.review.dto.ReviewSummaryResponse;
import vn.techbox.techbox_store.review.dto.ReviewUpdateRequest;
import vn.techbox.techbox_store.review.service.ReviewService;

@RestController
@RequestMapping("/api/products/{productId}/reviews")
@RequiredArgsConstructor
public class ReviewController {

    private final ReviewService reviewService;

    private String currentUserEmail() {
        Authentication auth = SecurityContextHolder.getContext().getAuthentication();
        if (auth == null || !auth.isAuthenticated()) {
            throw new RuntimeException("Unauthenticated");
        }
        return auth.getName();
    }

    @PostMapping
    public ResponseEntity<ReviewResponse> create(@PathVariable Integer productId,
                                                 @Valid @RequestBody ReviewCreateRequest request) {
        return ResponseEntity.ok(reviewService.createReview(productId, request, currentUserEmail()));
    }

    @PutMapping("/{reviewId}")
    public ResponseEntity<ReviewResponse> update(@PathVariable Integer productId,
                                                 @PathVariable Integer reviewId,
                                                 @Valid @RequestBody ReviewUpdateRequest request) {
        return ResponseEntity.ok(reviewService.updateReview(productId, reviewId, request, currentUserEmail()));
    }

    @DeleteMapping("/{reviewId}")
    public ResponseEntity<Void> delete(@PathVariable Integer productId,
                                       @PathVariable Integer reviewId) {
        reviewService.deleteReview(productId, reviewId, currentUserEmail());
        return ResponseEntity.noContent().build();
    }

    @GetMapping
    public ResponseEntity<Page<ReviewResponse>> list(@PathVariable Integer productId,
                                                     @RequestParam(defaultValue = "0") int page,
                                                     @RequestParam(defaultValue = "10") int size) {
        return ResponseEntity.ok(reviewService.getReviews(productId, page, size));
    }

    @GetMapping("/summary")
    public ResponseEntity<ReviewSummaryResponse> summary(@PathVariable Integer productId) {
        return ResponseEntity.ok(reviewService.getSummary(productId));
    }

    @GetMapping("/me")
    public ResponseEntity<ReviewResponse> myReview(@PathVariable Integer productId) {
        return ResponseEntity.ok(reviewService.getUserReview(productId, currentUserEmail()));
    }
}

