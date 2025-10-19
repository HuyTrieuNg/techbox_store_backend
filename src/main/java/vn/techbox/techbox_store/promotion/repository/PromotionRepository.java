package vn.techbox.techbox_store.promotion.repository;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;
import vn.techbox.techbox_store.promotion.model.Promotion;

import java.util.List;

@Repository
public interface PromotionRepository extends JpaRepository<Promotion, Integer> {
    List<Promotion> findByCampaignId(Integer campaignId);
    List<Promotion> findByProductVariationId(Integer productVariationId);
    List<Promotion> findByProductVariationIdIn(List<Integer> productVariationIds);
}