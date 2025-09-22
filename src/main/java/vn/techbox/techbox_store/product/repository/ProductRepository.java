package vn.techbox.techbox_store.product.repository;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import org.springframework.stereotype.Repository;
import vn.techbox.techbox_store.product.model.Product;

import java.util.List;
import java.util.Optional;

@Repository
public interface ProductRepository extends JpaRepository<Product, Integer> {
    
    Optional<Product> findByName(String name);
    
    boolean existsByName(String name);
    
    boolean existsByNameAndIdNot(String name, Integer id);
    
    // Find all non-deleted products
    @Query("SELECT p FROM Product p WHERE p.deletedAt IS NULL")
    List<Product> findAllActive();
    
    // Find active product by id
    @Query("SELECT p FROM Product p WHERE p.id = :id AND p.deletedAt IS NULL")
    Optional<Product> findActiveById(@Param("id") Integer id);
    
    // Find products by category
    @Query("SELECT p FROM Product p WHERE p.categoryId = :categoryId AND p.deletedAt IS NULL")
    List<Product> findByCategoryId(@Param("categoryId") Integer categoryId);
    
    // Find products by brand
    @Query("SELECT p FROM Product p WHERE p.brandId = :brandId AND p.deletedAt IS NULL")
    List<Product> findByBrandId(@Param("brandId") Integer brandId);
    
    // Search products by name containing
    @Query("SELECT p FROM Product p WHERE LOWER(p.name) LIKE LOWER(CONCAT('%', :keyword, '%')) AND p.deletedAt IS NULL")
    List<Product> searchByName(@Param("keyword") String keyword);
}