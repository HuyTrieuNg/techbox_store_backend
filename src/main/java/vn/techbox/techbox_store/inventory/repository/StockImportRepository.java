package vn.techbox.techbox_store.inventory.repository;

import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import org.springframework.stereotype.Repository;
import vn.techbox.techbox_store.inventory.model.StockImport;

import java.time.LocalDateTime;
import java.util.List;
import java.util.Optional;

@Repository
public interface StockImportRepository extends JpaRepository<StockImport, Integer> {
    
    /**
     * Find stock import by document code
     */
    Optional<StockImport> findByDocumentCode(String documentCode);
    
    /**
     * Find all stock imports with filters
     */
    @Query("SELECT si FROM StockImport si WHERE " +
           "(:fromDate IS NULL OR si.importDate >= :fromDate) AND " +
           "(:toDate IS NULL OR si.importDate <= :toDate) AND " +
           "(:supplierId IS NULL OR si.supplierId = :supplierId) AND " +
           "(:userId IS NULL OR si.userId = :userId) AND " +
           "(:documentCode IS NULL OR :documentCode = '' OR LOWER(si.documentCode) LIKE LOWER(CONCAT('%', :documentCode, '%')))")
    Page<StockImport> findAllWithFilters(
            @Param("fromDate") LocalDateTime fromDate,
            @Param("toDate") LocalDateTime toDate,
            @Param("supplierId") Integer supplierId,
            @Param("userId") Integer userId,
            @Param("documentCode") String documentCode,
            Pageable pageable);
    
    /**
     * Find stock imports for report (without pagination)
     */
    @Query("SELECT si FROM StockImport si WHERE " +
           "(:fromDate IS NULL OR si.importDate >= :fromDate) AND " +
           "(:toDate IS NULL OR si.importDate <= :toDate) AND " +
           "(:supplierId IS NULL OR si.supplierId = :supplierId) " +
           "ORDER BY si.importDate")
    List<StockImport> findForReport(
            @Param("fromDate") LocalDateTime fromDate,
            @Param("toDate") LocalDateTime toDate,
            @Param("supplierId") Integer supplierId);
}
