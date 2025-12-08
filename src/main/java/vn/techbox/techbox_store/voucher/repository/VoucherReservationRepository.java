package vn.techbox.techbox_store.voucher.repository;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Modifying;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import org.springframework.stereotype.Repository;
import vn.techbox.techbox_store.inventory.model.ReservationStatus;
import vn.techbox.techbox_store.voucher.model.VoucherReservation;

import java.time.LocalDateTime;
import java.util.List;
import java.util.Optional;

@Repository
public interface VoucherReservationRepository extends JpaRepository<VoucherReservation, Long> {

    List<VoucherReservation> findByOrderIdAndStatus(Integer orderId, ReservationStatus status);

    List<VoucherReservation> findByVoucherIdAndStatus(Integer voucherId, ReservationStatus status);

    @Query("SELECT vr FROM VoucherReservation vr WHERE vr.status = :status AND vr.expiresAt < :expiredBefore")
    List<VoucherReservation> findExpiredReservations(@Param("status") ReservationStatus status,
                                                    @Param("expiredBefore") LocalDateTime expiredBefore);

    @Query("SELECT COALESCE(SUM(vr.quantity), 0) FROM VoucherReservation vr WHERE vr.voucherId = :voucherId AND vr.status = 'RESERVED'")
    Integer getTotalReservedQuantity(@Param("voucherId") Integer voucherId);

    // Check if user has voucher reservation
    @Query("SELECT vr FROM VoucherReservation vr " +
           "WHERE vr.userId = :userId " +
           "AND vr.voucherId = (SELECT v.id FROM Voucher v WHERE v.code = :voucherCode) " +
           "AND vr.status = 'RESERVED'")
    Optional<VoucherReservation> findByUserIdAndVoucherCodeAndReserved(@Param("userId") Integer userId,
                                                                       @Param("voucherCode") String voucherCode);

    // Delete RELEASED and EXPIRED reservations
    @Modifying(clearAutomatically = true, flushAutomatically = true)
    @Query("DELETE FROM VoucherReservation vr WHERE (vr.status = 'RELEASED' AND vr.releasedAt < :cutoff) OR (vr.status = 'EXPIRED' AND vr.expiresAt < :cutoff)")
    int deleteReleasedOrExpiredBefore(@Param("cutoff") LocalDateTime cutoff);
}
