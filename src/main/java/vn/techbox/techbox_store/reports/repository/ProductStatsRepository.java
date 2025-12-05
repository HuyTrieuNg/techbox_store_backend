package vn.techbox.techbox_store.reports.repository;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import org.springframework.stereotype.Repository;
import vn.techbox.techbox_store.product.model.Product;
import vn.techbox.techbox_store.product.model.ProductStatus;
import vn.techbox.techbox_store.reports.dto.LowStockProductDTO;
import vn.techbox.techbox_store.reports.dto.ProductByCategoryDTO;
import vn.techbox.techbox_store.reports.dto.TopSellingProductDTO;

import java.util.List;

@Repository
public interface ProductStatsRepository extends JpaRepository<Product, Integer> {

    /**
     * Count total products (excluding soft-deleted)
     */
    @Query("SELECT COUNT(p) FROM Product p WHERE p.deletedAt IS NULL")
    Long countTotalProducts();

    /**
     * Count products by status
     */
    @Query("SELECT COUNT(p) FROM Product p WHERE p.status = :status AND p.deletedAt IS NULL")
    Long countProductsByStatus(@Param("status") ProductStatus status);

    /**
     * Get products grouped by root category (handling subcategories)
     */
    @Query("""
        SELECT new vn.techbox.techbox_store.reports.dto.ProductByCategoryDTO(
            COALESCE(parent.id, c.id),
            COALESCE(parent.name, c.name),
            COUNT(p.id)
        )
        FROM Product p
        JOIN p.category c
        LEFT JOIN c.parentCategory parent
        WHERE p.deletedAt IS NULL
        GROUP BY COALESCE(parent.id, c.id), COALESCE(parent.name, c.name)
        ORDER BY COUNT(p.id) DESC
    """)
    List<ProductByCategoryDTO> findProductsByCategory();

    /**
     * Get top selling products based on order items
     */
    @Query("""
        SELECT new vn.techbox.techbox_store.reports.dto.TopSellingProductDTO(
            p.id,
            p.name,
            p.spu,
            p.imageUrl,
            COALESCE(SUM(oi.quantity), 0),
            COALESCE(SUM(oi.quantity * oi.unitPrice), 0),
            p.averageRating
        )
        FROM Product p
        LEFT JOIN ProductVariation pv ON pv.product.id = p.id
        LEFT JOIN OrderItem oi ON oi.productVariation.id = pv.id
        WHERE p.deletedAt IS NULL
        GROUP BY p.id, p.name, p.spu, p.imageUrl, p.averageRating
        HAVING SUM(oi.quantity) > 0
        ORDER BY SUM(oi.quantity) DESC
        LIMIT :limit
    """)
    List<TopSellingProductDTO> findTopSellingProducts(@Param("limit") int limit);

    /**
     * Get low stock products (variations with stock below threshold)
     */
    @Query("""
        SELECT new vn.techbox.techbox_store.reports.dto.LowStockProductDTO(
            p.id,
            p.name,
            p.spu,
            pv.id,
            pv.sku,
            pv.variationName,
            pv.stockQuantity,
            :threshold
        )
        FROM Product p
        JOIN ProductVariation pv ON pv.product.id = p.id
        WHERE p.deletedAt IS NULL 
        AND pv.deletedAt IS NULL
        AND pv.stockQuantity <= :threshold
        ORDER BY pv.stockQuantity ASC
    """)
    List<LowStockProductDTO> findLowStockProducts(@Param("threshold") int threshold);

    /**
     * Get average product rating across all products
     */
    @Query("SELECT AVG(p.averageRating) FROM Product p WHERE p.deletedAt IS NULL AND p.totalRatings > 0")
    Double getAverageProductRating();
}
