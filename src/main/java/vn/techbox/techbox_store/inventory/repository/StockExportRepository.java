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
           "(COALESCE(:fromDate, se.exportDate) = se.exportDate OR se.exportDate >= :fromDate) AND " +
           "(COALESCE(:toDate, se.exportDate) = se.exportDate OR se.exportDate <= :toDate) AND " +
           "(COALESCE(:userId, se.userId) = se.userId OR se.userId = :userId) AND " +
           "(COALESCE(:orderId, se.orderId) = se.orderId OR se.orderId = :orderId) AND " +
           "(COALESCE(:documentCode, '') = '' OR LOWER(se.documentCode) LIKE LOWER(CONCAT('%', :documentCode, '%')))")
    Page<StockExport> findAllWithFilters(
            @Param("fromDate") LocalDateTime fromDate,
            @Param("toDate") LocalDateTime toDate,
            @Param("userId") Integer userId,
            @Param("orderId") Integer orderId,
            @Param("documentCode") String documentCode,
            Pageable pageable);
    
}
