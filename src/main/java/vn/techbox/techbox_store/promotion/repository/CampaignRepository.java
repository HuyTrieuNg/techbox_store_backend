package vn.techbox.techbox_store.promotion.repository;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import org.springframework.stereotype.Repository;
import vn.techbox.techbox_store.promotion.model.Campaign;

import java.time.LocalDateTime;
import java.util.List;

@Repository
public interface CampaignRepository extends JpaRepository<Campaign, Integer> {
    boolean existsByName(String name);
    
    /**
     * Tìm các campaigns có startDate hoặc endDate trong khoảng thời gian
     */
    @Query("SELECT c FROM Campaign c WHERE " +
           "(c.startDate BETWEEN :startTime AND :endTime) OR " +
           "(c.endDate BETWEEN :startTime AND :endTime)")
    List<Campaign> findCampaignsWithRecentStartOrEnd(
            @Param("startTime") LocalDateTime startTime,
            @Param("endTime") LocalDateTime endTime
    );
}