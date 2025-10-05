package vn.techbox.techbox_store.voucher.repository;

import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import org.springframework.stereotype.Repository;
import vn.techbox.techbox_store.voucher.model.Voucher;
import vn.techbox.techbox_store.voucher.model.VoucherType;

import java.time.LocalDateTime;
import java.util.List;
import java.util.Optional;

@Repository
public interface VoucherRepository extends JpaRepository<Voucher, Integer> {
    
    // Find active (non-deleted) vouchers
    @Query("SELECT v FROM Voucher v WHERE v.deletedAt IS NULL")
    List<Voucher> findAllActive();
    
    @Query("SELECT v FROM Voucher v WHERE v.deletedAt IS NULL")
    Page<Voucher> findAllActive(Pageable pageable);
    
    // Find by code (active only)
    @Query("SELECT v FROM Voucher v WHERE v.code = :code AND v.deletedAt IS NULL")
    Optional<Voucher> findByCodeAndNotDeleted(@Param("code") String code);

       // Find by code (include deleted) used for restore operation
       @Query("SELECT v FROM Voucher v WHERE v.code = :code")
       Optional<Voucher> findByCode(@Param("code") String code);
    
    // Find by ID (active only)
    @Query("SELECT v FROM Voucher v WHERE v.id = :id AND v.deletedAt IS NULL")
    Optional<Voucher> findByIdAndNotDeleted(@Param("id") Integer id);
    
    // Find valid vouchers (active and within validity period)
    @Query("SELECT v FROM Voucher v WHERE v.deletedAt IS NULL " +
           "AND v.validFrom <= :now AND v.validUntil >= :now")
    List<Voucher> findValidVouchers(@Param("now") LocalDateTime now);
    
    @Query("SELECT v FROM Voucher v WHERE v.deletedAt IS NULL " +
           "AND v.validFrom <= :now AND v.validUntil >= :now")
    Page<Voucher> findValidVouchers(@Param("now") LocalDateTime now, Pageable pageable);
    
    // Find by voucher type
    @Query("SELECT v FROM Voucher v WHERE v.voucherType = :type AND v.deletedAt IS NULL")
    List<Voucher> findByVoucherTypeAndNotDeleted(@Param("type") VoucherType type);
    
    // Find expired vouchers
    @Query("SELECT v FROM Voucher v WHERE v.deletedAt IS NULL AND v.validUntil < :now")
    List<Voucher> findExpiredVouchers(@Param("now") LocalDateTime now);
    
    // Find vouchers expiring soon
    @Query("SELECT v FROM Voucher v WHERE v.deletedAt IS NULL " +
           "AND v.validUntil BETWEEN :now AND :expirationDate")
    List<Voucher> findVouchersExpiringSoon(@Param("now") LocalDateTime now, 
                                           @Param("expirationDate") LocalDateTime expirationDate);
    
    // Find vouchers with usage left
    @Query("SELECT v FROM Voucher v LEFT JOIN v.userVouchers uv " +
           "WHERE v.deletedAt IS NULL " +
           "GROUP BY v.id " +
           "HAVING COUNT(uv) < v.usageLimit")
    List<Voucher> findVouchersWithUsageLeft();
    
    // Search vouchers by code pattern
    @Query("SELECT v FROM Voucher v WHERE v.deletedAt IS NULL " +
           "AND UPPER(v.code) LIKE UPPER(CONCAT('%', :searchTerm, '%'))")
    Page<Voucher> searchByCode(@Param("searchTerm") String searchTerm, Pageable pageable);
    
    // Check if voucher code exists (excluding current voucher for updates)
    @Query("SELECT COUNT(v) > 0 FROM Voucher v WHERE v.code = :code " +
           "AND v.deletedAt IS NULL AND (:currentId IS NULL OR v.id != :currentId)")
    boolean existsByCodeAndNotDeleted(@Param("code") String code, @Param("currentId") Integer currentId);
    
    // Find vouchers created within date range
    @Query("SELECT v FROM Voucher v WHERE v.deletedAt IS NULL " +
           "AND v.createdAt BETWEEN :startDate AND :endDate")
    List<Voucher> findByCreatedAtBetween(@Param("startDate") LocalDateTime startDate, 
                                        @Param("endDate") LocalDateTime endDate);
}