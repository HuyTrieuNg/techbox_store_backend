package vn.techbox.techbox_store.reports.repository;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import org.springframework.stereotype.Repository;
import vn.techbox.techbox_store.inventory.model.StockImport;
import vn.techbox.techbox_store.reports.dto.StockMovementDTO;

import java.math.BigDecimal;
import java.time.LocalDateTime;
import java.util.List;

@Repository
public interface InventoryStatsRepository extends JpaRepository<StockImport, Long> {

    /**
     * Calculate total inventory value (sum of all product variations' stock * cost price)
     */
    @Query("""
        SELECT COALESCE(SUM(pv.stockQuantity * pv.avgCostPrice), 0)
        FROM ProductVariation pv
        WHERE pv.deletedAt IS NULL
        AND pv.avgCostPrice IS NOT NULL
    """)
    BigDecimal calculateTotalInventoryValue();

    /**
     * Count total stock imports
     */
    @Query("SELECT COUNT(si) FROM StockImport si")
    Long countTotalStockImports();

    /**
     * Count total stock exports
     */
    @Query("SELECT COUNT(se) FROM StockExport se")
    Long countTotalStockExports();

    /**
     * Count total product variations (excluding soft-deleted)
     */
    @Query("SELECT COUNT(pv) FROM ProductVariation pv WHERE pv.deletedAt IS NULL")
    Integer countTotalProductVariations();

    /**
     * Count low stock variations (stock below threshold)
     */
    @Query("""
        SELECT COUNT(pv) 
        FROM ProductVariation pv 
        WHERE pv.deletedAt IS NULL 
        AND pv.stockQuantity <= :threshold
    """)
    Integer countLowStockVariations(@Param("threshold") int threshold);

    /**
     * Get recent stock imports
     */
    @Query("""
        SELECT new vn.techbox.techbox_store.reports.dto.StockMovementDTO(
            'IMPORT',
            si.id,
            si.importDate,
            SIZE(si.items),
            si.totalCostValue,
            COALESCE(s.name, ''),
            si.note
        )
        FROM StockImport si
        LEFT JOIN Supplier s ON s.id = si.supplierId
        WHERE si.importDate BETWEEN :startDate AND :endDate
        ORDER BY si.importDate DESC
    """)
    List<StockMovementDTO> findRecentStockImports(
        @Param("startDate") LocalDateTime startDate,
        @Param("endDate") LocalDateTime endDate
    );

    /**
     * Get recent stock exports
     */
    @Query("""
        SELECT new vn.techbox.techbox_store.reports.dto.StockMovementDTO(
            'EXPORT',
            se.id,
            se.exportDate,
            SIZE(se.items),
            se.totalCogsValue,
            '',
            se.note
        )
        FROM StockExport se
        WHERE se.exportDate BETWEEN :startDate AND :endDate
        ORDER BY se.exportDate DESC
    """)
    List<StockMovementDTO> findRecentStockExports(
        @Param("startDate") LocalDateTime startDate,
        @Param("endDate") LocalDateTime endDate
    );
}
