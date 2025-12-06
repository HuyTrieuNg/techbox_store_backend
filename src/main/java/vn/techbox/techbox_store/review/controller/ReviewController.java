package vn.techbox.techbox_store.review.controller;

import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import org.springframework.data.domain.Page;
import org.springframework.http.ResponseEntity;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.security.core.annotation.AuthenticationPrincipal;
import org.springframework.web.bind.annotation.*;
import vn.techbox.techbox_store.review.dto.*;
import vn.techbox.techbox_store.review.service.ReviewService;
import vn.techbox.techbox_store.user.security.UserPrincipal;

@RestController
@RequestMapping("/products/{productId}/reviews")
@RequiredArgsConstructor
public class ReviewController {

    private final ReviewService reviewService;

    @PreAuthorize("hasAuthority('REVIEW:WRITE')")
    @PostMapping
    public ResponseEntity<ReviewResponse> create(
            @AuthenticationPrincipal UserPrincipal userPrincipal,
            @PathVariable Integer productId,
            @Valid @RequestBody ReviewCreateRequest request) {
        return ResponseEntity.ok(reviewService.createReview(productId, request, userPrincipal.getUsername()));
    }

    @PreAuthorize("hasAuthority('REVIEW:UPDATE') and (@reviewService.isReviewOwner(#reviewId, principal.username))")
    @PutMapping("/{reviewId}")
    public ResponseEntity<ReviewResponse> update(
            @AuthenticationPrincipal UserPrincipal userPrincipal,
            @PathVariable Integer productId,
            @PathVariable Integer reviewId,
            @Valid @RequestBody ReviewUpdateRequest request) {
        return ResponseEntity.ok(reviewService.updateReview(productId, reviewId, request, userPrincipal.getUsername()));
    }

    @PreAuthorize("hasAuthority('REVIEW:DELETE') and (@reviewService.isReviewOwner(#reviewId, principal.username))")
    @DeleteMapping("/{reviewId}")
    public ResponseEntity<Void> delete(
            @AuthenticationPrincipal UserPrincipal userPrincipal,
            @PathVariable Integer productId,
            @PathVariable Integer reviewId) {
        reviewService.deleteReview(productId, reviewId, userPrincipal.getUsername());
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

    @PreAuthorize("hasAuthority('REVIEW:READ')")
    @GetMapping("/me")
    public ResponseEntity<ReviewResponse> myReview(
            @AuthenticationPrincipal UserPrincipal userPrincipal,
            @PathVariable Integer productId) {
        return ResponseEntity.ok(reviewService.getUserReview(productId, userPrincipal.getUsername()));
    }
}
