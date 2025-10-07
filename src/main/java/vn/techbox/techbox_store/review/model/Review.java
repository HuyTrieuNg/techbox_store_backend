package vn.techbox.techbox_store.review.model;

import jakarta.persistence.*;
import lombok.*;
import org.hibernate.annotations.CreationTimestamp;
import org.hibernate.annotations.UpdateTimestamp;
import vn.techbox.techbox_store.product.model.Product;
import vn.techbox.techbox_store.user.model.User;

import java.time.LocalDateTime;

@Getter
@Setter
@Builder
@NoArgsConstructor
@AllArgsConstructor
@Entity
@Table(name = "product_reviews",
        indexes = {
                @Index(name = "idx_review_product", columnList = "product_id"),
                @Index(name = "idx_review_user", columnList = "user_id")
        },
        uniqueConstraints = {
                @UniqueConstraint(name = "uk_review_product_user", columnNames = {"product_id", "user_id"})
        }
)
public class Review {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Integer id;

    @Column(name = "product_id", nullable = false)
    private Integer productId;

    @Column(name = "user_id", nullable = false)
    private Integer userId;

    @Column(name = "rating", nullable = false)
    private Integer rating; // 1..5

    @Column(name = "title", length = 150)
    private String title;

    @Column(name = "content", columnDefinition = "TEXT")
    private String content;

    @CreationTimestamp
    @Column(name = "created_at", updatable = false)
    private LocalDateTime createdAt;

    @UpdateTimestamp
    @Column(name = "updated_at")
    private LocalDateTime updatedAt;

    @Column(name = "deleted_at")
    private LocalDateTime deletedAt;

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "product_id", insertable = false, updatable = false)
    private Product product;

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "user_id", insertable = false, updatable = false)
    private User user;

    @PrePersist
    public void prePersist() {
        validateRating();
    }

    @PreUpdate
    public void preUpdate() {
        validateRating();
    }

    private void validateRating() {
        if (rating == null || rating < 1 || rating > 5) {
            throw new IllegalArgumentException("Rating must be between 1 and 5");
        }
    }

    public boolean isDeleted() { return deletedAt != null; }
    public void softDelete() { this.deletedAt = LocalDateTime.now(); }
    public void restore() { this.deletedAt = null; }
}

