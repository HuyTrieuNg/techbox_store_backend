package vn.techbox.techbox_store.promotion.repository;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import org.springframework.stereotype.Repository;
import vn.techbox.techbox_store.promotion.model.Promotion;

import java.time.LocalDateTime;
import java.util.List;

@Repository
public interface PromotionRepository extends JpaRepository<Promotion, Integer> {
    List<Promotion> findByCampaignId(Integer campaignId);
    List<Promotion> findByProductVariationId(Integer productVariationId);

    @Query("SELECT p FROM Promotion p JOIN p.campaign c " +
           "WHERE p.productVariationId = :productVariationId " +
           "AND c.startDate <= :currentTime " +
           "AND c.endDate >= :currentTime " +
           "AND c.deletedAt IS NULL")
    List<Promotion> findActivePromotionsByProductVariationId(
        @Param("productVariationId") Integer productVariationId,
        @Param("currentTime") LocalDateTime currentTime
    );
}