package vn.techbox.techbox_store.product.repository;

import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.JpaSpecificationExecutor;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import org.springframework.stereotype.Repository;
import vn.techbox.techbox_store.product.model.Product;
import vn.techbox.techbox_store.product.model.ProductVariation;

import java.util.List;
import java.util.Optional;

@Repository
public interface ProductRepository extends JpaRepository<Product, Integer>, JpaSpecificationExecutor<Product> {

    boolean existsByName(String name);
    
    boolean existsByNameAndIdNot(String name, Integer id);

    // Find all non-deleted products
    @Query("SELECT p FROM Product p WHERE p.deletedAt IS NULL")
    List<Product> findAllActive();
    
    // Find all active products with pagination
    @Query("SELECT p FROM Product p WHERE p.deletedAt IS NULL")
    Page<Product> findAllActive(Pageable pageable);
    
    // Find active product by id
    @Query("SELECT p FROM Product p WHERE p.id = :id AND p.deletedAt IS NULL")
    Optional<Product> findActiveById(@Param("id") Integer id);
    
    // Find active product with full details (JOIN FETCH all relationships to avoid N+1)
    // Use DISTINCT to avoid duplicate rows from multiple JOINs
    @Query("SELECT DISTINCT p FROM Product p " +
           "LEFT JOIN FETCH p.category " +
           "LEFT JOIN FETCH p.brand " +
           "WHERE p.id = :id AND p.deletedAt IS NULL")
    Optional<Product> findFullDetailById(@Param("id") Integer id);
    
    // Fetch product attributes separately
    @Query("SELECT DISTINCT p FROM Product p " +
           "LEFT JOIN FETCH p.productAttributes pa " +
           "LEFT JOIN FETCH pa.attribute " +
           "WHERE p.id = :id AND p.deletedAt IS NULL")
    Optional<Product> findWithAttributes(@Param("id") Integer id);
    
    // Fetch variations separately to avoid MultipleBagFetchException
    @Query("SELECT DISTINCT p FROM Product p " +
           "LEFT JOIN FETCH p.productVariations pv " +
           "WHERE p.id = :id AND p.deletedAt IS NULL")
    Optional<Product> findWithVariations(@Param("id") Integer id);
    
    // Fetch variation images separately
    @Query("SELECT DISTINCT pv FROM ProductVariation pv " +
           "LEFT JOIN FETCH pv.images " +
           "WHERE pv.product.id = :productId")
    List<ProductVariation> findVariationImagesById(@Param("productId") Integer productId);
    
    // Fetch variation attributes separately
    @Query("SELECT DISTINCT pv FROM ProductVariation pv " +
           "LEFT JOIN FETCH pv.variationAttributes va " +
           "LEFT JOIN FETCH va.attribute " +
           "WHERE pv.product.id = :productId")
    List<ProductVariation> findVariationAttributesById(@Param("productId") Integer productId);
    
    // Fetch variation promotions separately
    @Query("SELECT DISTINCT pv FROM ProductVariation pv " +
           "LEFT JOIN FETCH pv.promotions pr " +
           "LEFT JOIN FETCH pr.campaign " +
           "WHERE pv.product.id = :productId")
    List<ProductVariation> findVariationPromotionsById(@Param("productId") Integer productId);
    
    // Find only deleted products (for admin)
    @Query("SELECT p FROM Product p WHERE p.deletedAt IS NOT NULL")
    Page<Product> findAllDeleted(Pageable pageable);
    
    // Find products by list of IDs and not deleted
    @Query("SELECT p FROM Product p WHERE p.id IN :ids AND p.deletedAt IS NULL")
    Page<Product> findByIdInAndDeletedAtIsNull(@Param("ids") List<Integer> ids, Pageable pageable);
}