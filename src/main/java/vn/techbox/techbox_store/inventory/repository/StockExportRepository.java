package vn.techbox.techbox_store.inventory.repository;

import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import org.springframework.stereotype.Repository;
import vn.techbox.techbox_store.inventory.model.StockExport;

import java.time.LocalDateTime;
import java.util.List;
import java.util.Optional;

@Repository
public interface StockExportRepository extends JpaRepository<StockExport, Integer> {
    
    /**
     * Find stock export by document code
     */
    Optional<StockExport> findByDocumentCode(String documentCode);
    
    /**
     * Find stock export by order ID
     */
    Optional<StockExport> findByOrderId(Integer orderId);
    
    /**
     * Find all stock exports with filters
     */
    @Query("SELECT se FROM StockExport se WHERE " +
           "(:fromDate IS NULL OR se.exportDate >= :fromDate) AND " +
           "(:toDate IS NULL OR se.exportDate <= :toDate) AND " +
           "(:userId IS NULL OR se.userId = :userId) AND " +
           "(:orderId IS NULL OR se.orderId = :orderId) AND " +
           "(:documentCode IS NULL OR :documentCode = '' OR LOWER(se.documentCode) LIKE LOWER(CONCAT('%', :documentCode, '%')))")
    Page<StockExport> findAllWithFilters(
            @Param("fromDate") LocalDateTime fromDate,
            @Param("toDate") LocalDateTime toDate,
            @Param("userId") Integer userId,
            @Param("orderId") Integer orderId,
            @Param("documentCode") String documentCode,
            Pageable pageable);
    
    /**
     * Find stock exports for report (without pagination)
     */
    @Query("SELECT se FROM StockExport se WHERE " +
           "(:fromDate IS NULL OR se.exportDate >= :fromDate) AND " +
           "(:toDate IS NULL OR se.exportDate <= :toDate) " +
           "ORDER BY se.exportDate")
    List<StockExport> findForReport(
            @Param("fromDate") LocalDateTime fromDate,
            @Param("toDate") LocalDateTime toDate);
}
