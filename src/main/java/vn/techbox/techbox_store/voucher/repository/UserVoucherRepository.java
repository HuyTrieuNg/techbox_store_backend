package vn.techbox.techbox_store.voucher.repository;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import org.springframework.stereotype.Repository;
import vn.techbox.techbox_store.voucher.model.UserVoucher;
import vn.techbox.techbox_store.voucher.model.UserVoucherId;

import java.time.LocalDateTime;
import java.util.List;
import java.util.Optional;

@Repository
public interface UserVoucherRepository extends JpaRepository<UserVoucher, UserVoucherId> {
    
    // Find vouchers used by a specific user
    @Query("SELECT uv FROM UserVoucher uv JOIN FETCH uv.voucher v " +
           "WHERE uv.userId = :userId AND v.deletedAt IS NULL")
    List<UserVoucher> findByUserId(@Param("userId") Integer userId);
    
    // Find users who used a specific voucher
    @Query("SELECT uv FROM UserVoucher uv JOIN FETCH uv.voucher v " +
           "WHERE uv.voucherId = :voucherId AND v.deletedAt IS NULL")
    List<UserVoucher> findByVoucherId(@Param("voucherId") Integer voucherId);
    
    // Check if user has already used a specific voucher
    @Query("SELECT uv FROM UserVoucher uv WHERE uv.userId = :userId AND uv.voucherId = :voucherId")
    Optional<UserVoucher> findByUserIdAndVoucherId(@Param("userId") Integer userId, 
                                                   @Param("voucherId") Integer voucherId);
    
    // Count usage for a specific voucher
    @Query("SELECT COUNT(uv) FROM UserVoucher uv WHERE uv.voucherId = :voucherId")
    Long countByVoucherId(@Param("voucherId") Integer voucherId);
    
    // Count vouchers used by a specific user
    @Query("SELECT COUNT(uv) FROM UserVoucher uv WHERE uv.userId = :userId")
    Long countByUserId(@Param("userId") Integer userId);
    
    // Find voucher usage within date range
    @Query("SELECT uv FROM UserVoucher uv JOIN FETCH uv.voucher v " +
           "WHERE uv.usedAt BETWEEN :startDate AND :endDate AND v.deletedAt IS NULL")
    List<UserVoucher> findByUsedAtBetween(@Param("startDate") LocalDateTime startDate, 
                                         @Param("endDate") LocalDateTime endDate);
    
    // Find voucher usage by order
    @Query("SELECT uv FROM UserVoucher uv JOIN FETCH uv.voucher v " +
           "WHERE uv.orderId = :orderId AND v.deletedAt IS NULL")
    Optional<UserVoucher> findByOrderId(@Param("orderId") Integer orderId);
    
    // Find all voucher usage for reporting
    @Query("SELECT uv FROM UserVoucher uv JOIN FETCH uv.voucher v " +
           "WHERE v.deletedAt IS NULL ORDER BY uv.usedAt DESC")
    List<UserVoucher> findAllWithVoucher();
}