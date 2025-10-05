package vn.techbox.techbox_store.product.review.repository;

import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import org.springframework.stereotype.Repository;
import vn.techbox.techbox_store.product.review.model.Review;

import java.util.List;
import java.util.Optional;

@Repository
public interface ReviewRepository extends JpaRepository<Review, Integer> {

    Page<Review> findByProductIdAndDeletedAtIsNullOrderByCreatedAtDesc(Integer productId, Pageable pageable);

    Optional<Review> findByProductIdAndUserIdAndDeletedAtIsNull(Integer productId, Integer userId);

    Optional<Review> findByProductIdAndUserId(Integer productId, Integer userId);

    Optional<Review> findByIdAndProductIdAndDeletedAtIsNull(Integer id, Integer productId);

    @Query("SELECT COALESCE(AVG(r.rating),0) FROM Review r WHERE r.productId = :productId AND r.deletedAt IS NULL")
    double findAverageRating(@Param("productId") Integer productId);

    @Query("SELECT r.rating, COUNT(r) FROM Review r WHERE r.productId = :productId AND r.deletedAt IS NULL GROUP BY r.rating")
    List<Object[]> countByRating(@Param("productId") Integer productId);

    @Query("SELECT COUNT(r) FROM Review r WHERE r.productId = :productId AND r.deletedAt IS NULL")
    long countActiveByProductId(@Param("productId") Integer productId);
}
