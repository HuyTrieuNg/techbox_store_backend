package vn.techbox.techbox_store.promotion.repository;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;
import vn.techbox.techbox_store.promotion.model.Campaign;

@Repository
public interface CampaignRepository extends JpaRepository<Campaign, Integer> {
    boolean existsByName(String name);
}