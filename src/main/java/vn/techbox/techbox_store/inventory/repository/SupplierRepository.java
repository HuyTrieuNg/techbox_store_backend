package vn.techbox.techbox_store.inventory.repository;

import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import org.springframework.stereotype.Repository;
import vn.techbox.techbox_store.inventory.model.Supplier;

import java.util.Optional;

@Repository
public interface SupplierRepository extends JpaRepository<Supplier, Integer> {
    
    /**
     * Find supplier by ID (only non-deleted)
     */
    @Query("SELECT s FROM Supplier s WHERE s.supplierId = :supplierId AND s.deletedAt IS NULL")
    Optional<Supplier> findByIdAndNotDeleted(@Param("supplierId") Integer supplierId);
    
    /**
     * Find all suppliers with optional keyword search (only non-deleted)
     */
    @Query("SELECT s FROM Supplier s WHERE s.deletedAt IS NULL " +
           "AND (:keyword IS NULL OR :keyword = '' OR " +
           "LOWER(s.name) LIKE LOWER(CONCAT('%', :keyword, '%')) OR " +
           "LOWER(s.phone) LIKE LOWER(CONCAT('%', :keyword, '%')) OR " +
           "LOWER(s.email) LIKE LOWER(CONCAT('%', :keyword, '%')))")
    Page<Supplier> findAllNotDeleted(@Param("keyword") String keyword, Pageable pageable);
    
    /**
     * Find all suppliers with optional keyword search (including deleted)
     */
    @Query("SELECT s FROM Supplier s WHERE " +
           ":keyword IS NULL OR :keyword = '' OR " +
           "LOWER(s.name) LIKE LOWER(CONCAT('%', :keyword, '%')) OR " +
           "LOWER(s.phone) LIKE LOWER(CONCAT('%', :keyword, '%')) OR " +
           "LOWER(s.email) LIKE LOWER(CONCAT('%', :keyword, '%'))")
    Page<Supplier> findAllIncludingDeleted(@Param("keyword") String keyword, Pageable pageable);
    
    /**
     * Check if supplier exists by email (excluding specific supplier ID)
     */
    @Query("SELECT COUNT(s) > 0 FROM Supplier s WHERE s.email = :email " +
           "AND s.deletedAt IS NULL " +
           "AND (:excludeId IS NULL OR s.supplierId != :excludeId)")
    boolean existsByEmail(@Param("email") String email, @Param("excludeId") Integer excludeId);
    
    /**
     * Check if supplier exists by tax code (excluding specific supplier ID)
     */
    @Query("SELECT COUNT(s) > 0 FROM Supplier s WHERE s.taxCode = :taxCode " +
           "AND s.deletedAt IS NULL " +
           "AND (:excludeId IS NULL OR s.supplierId != :excludeId)")
    boolean existsByTaxCode(@Param("taxCode") String taxCode, @Param("excludeId") Integer excludeId);
}
