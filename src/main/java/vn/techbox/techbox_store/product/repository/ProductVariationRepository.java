package vn.techbox.techbox_store.product.repository;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import org.springframework.stereotype.Repository;
import vn.techbox.techbox_store.product.model.ProductVariation;

import java.util.List;
import java.util.Optional;

@Repository
public interface ProductVariationRepository extends JpaRepository<ProductVariation, Integer> {
    
    Optional<ProductVariation> findBySku(String sku);
    
    boolean existsBySku(String sku);
    
    boolean existsBySkuAndIdNot(String sku, Integer id);
    
    // Find all non-deleted product variations
    @Query("SELECT pv FROM ProductVariation pv WHERE pv.deletedAt IS NULL")
    List<ProductVariation> findAllActive();
    
    // Find active product variation by id
    @Query("SELECT pv FROM ProductVariation pv WHERE pv.id = :id AND pv.deletedAt IS NULL")
    Optional<ProductVariation> findActiveById(@Param("id") Integer id);
    
    // Find variations by product id
    @Query("SELECT pv FROM ProductVariation pv WHERE pv.productId = :productId AND pv.deletedAt IS NULL")
    List<ProductVariation> findByProductId(@Param("productId") Integer productId);
    
    // Find in stock variations
    @Query("SELECT pv FROM ProductVariation pv WHERE pv.stockQuantity - COALESCE(pv.reservedQuantity, 0) > 0 AND pv.deletedAt IS NULL")
    List<ProductVariation> findInStockVariations();
    
    // Find variations by product id and in stock
    @Query("SELECT pv FROM ProductVariation pv WHERE pv.productId = :productId AND pv.stockQuantity - COALESCE(pv.reservedQuantity, 0) > 0 AND pv.deletedAt IS NULL")
    List<ProductVariation> findInStockByProductId(@Param("productId") Integer productId);
    
    // Find variations with low stock (quantity <= threshold)
    @Query("SELECT pv FROM ProductVariation pv WHERE pv.stockQuantity - COALESCE(pv.reservedQuantity, 0) <= :threshold AND pv.deletedAt IS NULL")
    List<ProductVariation> findLowStockVariations(@Param("threshold") Integer threshold);
}