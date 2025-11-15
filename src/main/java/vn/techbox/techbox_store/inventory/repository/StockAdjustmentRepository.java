package vn.techbox.techbox_store.inventory.repository;

import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import org.springframework.stereotype.Repository;
import vn.techbox.techbox_store.inventory.model.StockAdjustment;

import java.time.LocalDateTime;
import java.util.List;

@Repository
public interface StockAdjustmentRepository extends JpaRepository<StockAdjustment, Integer> {

    /**
     * Find all stock adjustments with filters
     */
    @Query("SELECT sa FROM StockAdjustment sa WHERE " +
           "(:fromDate IS NULL OR sa.adjustmentDate >= :fromDate) AND " +
           "(:toDate IS NULL OR sa.adjustmentDate <= :toDate) AND " +
           "(:userId IS NULL OR sa.userId = :userId) AND " +
           "(:checkName IS NULL OR :checkName = '' OR LOWER(sa.checkName) LIKE LOWER(CONCAT('%', :checkName, '%')))")
    Page<StockAdjustment> findAllWithFilters(
            @Param("fromDate") LocalDateTime fromDate,
            @Param("toDate") LocalDateTime toDate,
            @Param("userId") Integer userId,
            @Param("checkName") String checkName,
            Pageable pageable);

    /**
     * Find stock adjustments for report (without pagination)
     */
    @Query("SELECT sa FROM StockAdjustment sa WHERE " +
           "(:fromDate IS NULL OR sa.adjustmentDate >= :fromDate) AND " +
           "(:toDate IS NULL OR sa.adjustmentDate <= :toDate) " +
           "ORDER BY sa.adjustmentDate")
    List<StockAdjustment> findForReport(
            @Param("fromDate") LocalDateTime fromDate,
            @Param("toDate") LocalDateTime toDate);
}
