package vn.techbox.techbox_store.review.service.impl;

import lombok.RequiredArgsConstructor;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageImpl;
import org.springframework.data.domain.PageRequest;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import vn.techbox.techbox_store.product.repository.ProductRepository;
import vn.techbox.techbox_store.product.service.ProductService;
import vn.techbox.techbox_store.review.dto.ReviewCreateRequest;
import vn.techbox.techbox_store.review.dto.ReviewResponse;
import vn.techbox.techbox_store.review.dto.ReviewSummaryResponse;
import vn.techbox.techbox_store.review.dto.ReviewUpdateRequest;
import vn.techbox.techbox_store.review.model.Review;
import vn.techbox.techbox_store.review.repository.ReviewRepository;
import vn.techbox.techbox_store.review.service.ReviewService;
import vn.techbox.techbox_store.user.model.User;
import vn.techbox.techbox_store.user.repository.UserRepository;

import java.time.LocalDateTime;
import java.util.HashMap;
import java.util.Map;
import java.util.Optional;
import java.util.stream.Collectors;

@Service("reviewService")
@RequiredArgsConstructor
@Transactional
public class ReviewServiceImpl implements ReviewService {

    private final ReviewRepository reviewRepository;
    private final ProductRepository productRepository;
    private final UserRepository userRepository;
    private final ProductService productService;

    @Override
    public ReviewResponse createReview(Integer productId, ReviewCreateRequest request, String currentUserEmail) {
        // Ensure product exists and is active
        productRepository.findActiveById(productId)
                .orElseThrow(() -> new IllegalArgumentException("Product not found or inactive with id: " + productId));

        User user = userRepository.findByAccountEmail(currentUserEmail)
                .orElseThrow(() -> new IllegalArgumentException("User not found for email: " + currentUserEmail));

        Optional<Review> existing = reviewRepository.findByProductIdAndUserId(productId, user.getId());
        Review review;
        if (existing.isPresent()) {
            review = existing.get();
            if (!review.isDeleted()) {
                throw new IllegalArgumentException("You have already reviewed this product");
            }

            review.restore();
            review.setRating(request.getRating());
            review.setTitle(request.getTitle());
            review.setContent(request.getContent());
        } else {
            review = Review.builder()
                    .productId(productId)
                    .userId(user.getId())
                    .rating(request.getRating())
                    .title(request.getTitle())
                    .content(request.getContent())
                    .build();
        }
        Review saved = reviewRepository.save(review);
        
        // Cập nhật rating cho product
        productService.updateProductRating(productId);
        
        return mapToResponse(saved, user);
    }

    @Override
    public ReviewResponse updateReview(Integer productId, Integer reviewId, ReviewUpdateRequest request, String currentUserEmail) {
        productRepository.findActiveById(productId)
                .orElseThrow(() -> new IllegalArgumentException("Product not found or inactive with id: " + productId));

        User user = userRepository.findByAccountEmail(currentUserEmail)
                .orElseThrow(() -> new IllegalArgumentException("User not found for email: " + currentUserEmail));

        Review review = reviewRepository.findByIdAndProductIdAndDeletedAtIsNull(reviewId, productId)
                .orElseThrow(() -> new IllegalArgumentException("Review not found with id: " + reviewId));

        if (!review.getUserId().equals(user.getId())) {
            throw new IllegalArgumentException("You can only update your own review");
        }

        if (request.getRating() != null) {
            if (request.getRating() < 1 || request.getRating() > 5) {
                throw new IllegalArgumentException("Rating must be between 1 and 5");
            }
            review.setRating(request.getRating());
        }
        if (request.getTitle() != null) {
            review.setTitle(request.getTitle());
        }
        if (request.getContent() != null) {
            review.setContent(request.getContent());
        }
        review.setUpdatedAt(LocalDateTime.now());
        Review saved = reviewRepository.save(review);
        
        // Cập nhật rating cho product
        productService.updateProductRating(productId);
        
        return mapToResponse(saved, user);
    }

    @Override
    public void deleteReview(Integer productId, Integer reviewId, String currentUserEmail) {
        productRepository.findActiveById(productId)
                .orElseThrow(() -> new IllegalArgumentException("Product not found or inactive with id: " + productId));

        User user = userRepository.findByAccountEmail(currentUserEmail)
                .orElseThrow(() -> new IllegalArgumentException("User not found for email: " + currentUserEmail));

        Review review = reviewRepository.findByIdAndProductIdAndDeletedAtIsNull(reviewId, productId)
                .orElseThrow(() -> new IllegalArgumentException("Review not found with id: " + reviewId));

        if (!review.getUserId().equals(user.getId())) {
            throw new IllegalArgumentException("You can only delete your own review");
        }

        review.softDelete();
        reviewRepository.save(review);
        
        // Cập nhật rating cho product
        productService.updateProductRating(productId);
    }

    @Override
    @Transactional(readOnly = true)
    public Page<ReviewResponse> getReviews(Integer productId, int page, int size) {
        productRepository.findActiveById(productId)
                .orElseThrow(() -> new IllegalArgumentException("Product not found or inactive with id: " + productId));

        PageRequest pageable = PageRequest.of(page, size);
        Page<Review> reviewPage = reviewRepository.findByProductIdAndDeletedAtIsNullOrderByCreatedAtDesc(productId, pageable);

        return new PageImpl<>(
                reviewPage.getContent().stream().map(r -> {
                    User user = userRepository.findById(r.getUserId()).orElse(null);
                    return mapToResponse(r, user);
                }).collect(Collectors.toList()),
                pageable,
                reviewPage.getTotalElements()
        );
    }

    @Override
    @Transactional(readOnly = true)
    public ReviewSummaryResponse getSummary(Integer productId) {
        productRepository.findActiveById(productId)
                .orElseThrow(() -> new IllegalArgumentException("Product not found or inactive with id: " + productId));

        long total = reviewRepository.countActiveByProductId(productId);
        double avg = total == 0 ? 0.0 : reviewRepository.findAverageRating(productId);

        Map<Integer, Long> counts = new HashMap<>();
        reviewRepository.countByRating(productId).forEach(arr -> {
            Integer rating = (Integer) arr[0];
            Long c = (Long) arr[1];
            counts.put(rating, c);
        });

        return ReviewSummaryResponse.builder()
                .productId(productId)
                .totalReviews(total)
                .averageRating(round(avg))
                .rating1Count(counts.getOrDefault(1, 0L))
                .rating2Count(counts.getOrDefault(2, 0L))
                .rating3Count(counts.getOrDefault(3, 0L))
                .rating4Count(counts.getOrDefault(4, 0L))
                .rating5Count(counts.getOrDefault(5, 0L))
                .build();
    }

    @Override
    @Transactional(readOnly = true)
    public ReviewResponse getUserReview(Integer productId, String currentUserEmail) {
        productRepository.findActiveById(productId)
                .orElseThrow(() -> new IllegalArgumentException("Product not found or inactive with id: " + productId));

        User user = userRepository.findByAccountEmail(currentUserEmail)
                .orElseThrow(() -> new IllegalArgumentException("User not found for email: " + currentUserEmail));

        Review review = reviewRepository.findByProductIdAndUserIdAndDeletedAtIsNull(productId, user.getId())
                .orElseThrow(() -> new IllegalArgumentException("You haven't reviewed this product yet"));

        return mapToResponse(review, user);
    }

    private ReviewResponse mapToResponse(Review review, User user) {
        String fullName = null;
        if (user != null) {
            StringBuilder sb = new StringBuilder();
            if (user.getFirstName() != null && !user.getFirstName().isBlank()) sb.append(user.getFirstName());
            if (user.getLastName() != null && !user.getLastName().isBlank()) {
                if (!sb.isEmpty()) sb.append(" ");
                sb.append(user.getLastName());
            }
            fullName = sb.isEmpty() ? null : sb.toString();
        }
        return ReviewResponse.builder()
                .id(review.getId())
                .productId(review.getProductId())
                .userId(review.getUserId())
                .userFullName(fullName)
                .rating(review.getRating())
                .title(review.getTitle())
                .content(review.getContent())
                .createdAt(review.getCreatedAt())
                .updatedAt(review.getUpdatedAt())
                .build();
    }

    @Override
    public boolean isReviewOwner(Integer reviewId, String email) {
        User user = userRepository.findByAccountEmail(email)
                .orElse(null);
        if (user == null) {
            return false;
        }
        
        Review review = reviewRepository.findById(reviewId)
                .orElse(null);
        if (review == null) {
            return false;
        }
        
        return review.getUserId().equals(user.getId());
    }

    private double round(double value) {
        double factor = Math.pow(10, 2);
        return Math.round(value * factor) / factor;
    }
}
