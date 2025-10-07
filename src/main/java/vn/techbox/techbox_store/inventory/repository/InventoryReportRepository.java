package vn.techbox.techbox_store.inventory.repository;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import org.springframework.stereotype.Repository;
import vn.techbox.techbox_store.product.model.ProductVariation;

import java.util.List;

@Repository
public interface InventoryReportRepository extends JpaRepository<ProductVariation, Integer> {

    /**
     * Get stock balance with filters
     */
    @Query("SELECT pv FROM ProductVariation pv " +
           "JOIN pv.product p " +
           "WHERE pv.deletedAt IS NULL " +
           "AND (:categoryId IS NULL OR p.categoryId = :categoryId) " +
           "AND (:brandId IS NULL OR p.brandId = :brandId) " +
           "AND (:keyword IS NULL OR :keyword = '' OR " +
           "     LOWER(p.name) LIKE LOWER(CONCAT('%', :keyword, '%')) OR " +
           "     LOWER(pv.variationName) LIKE LOWER(CONCAT('%', :keyword, '%')) OR " +
           "     LOWER(pv.sku) LIKE LOWER(CONCAT('%', :keyword, '%'))) " +
           "AND (:lowStock = false OR pv.stockQuantity <= 10) " +
           "AND (:outOfStock = false OR pv.stockQuantity = 0)")
    List<ProductVariation> findStockBalance(
            @Param("categoryId") Integer categoryId,
            @Param("brandId") Integer brandId,
            @Param("keyword") String keyword,
            @Param("lowStock") boolean lowStock,
            @Param("outOfStock") boolean outOfStock
    );

    /**
     * Get out of stock products (quantity = 0)
     */
    @Query("SELECT pv FROM ProductVariation pv " +
           "WHERE pv.deletedAt IS NULL AND pv.stockQuantity = 0")
    List<ProductVariation> findOutOfStockProducts();

    /**
     * Get low stock products (quantity > 0 and <= threshold)
     */
    @Query("SELECT pv FROM ProductVariation pv " +
           "WHERE pv.deletedAt IS NULL " +
           "AND pv.stockQuantity > 0 " +
           "AND pv.stockQuantity <= 10")
    List<ProductVariation> findLowStockProducts();

    /**
     * Get overstock products (quantity > threshold * 5)
     * Assuming overstock is 5x the low stock threshold
     */
    @Query("SELECT pv FROM ProductVariation pv " +
           "WHERE pv.deletedAt IS NULL " +
           "AND pv.stockQuantity > 50")
    List<ProductVariation> findOverstockProducts();
}
