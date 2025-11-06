package vn.techbox.techbox_store.inventory.repository;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Modifying;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import org.springframework.stereotype.Repository;
import vn.techbox.techbox_store.inventory.model.InventoryReservation;
import vn.techbox.techbox_store.inventory.model.ReservationStatus;

import java.time.LocalDateTime;
import java.util.List;

@Repository
public interface InventoryReservationRepository extends JpaRepository<InventoryReservation, Long> {

    List<InventoryReservation> findByOrderIdAndStatus(Integer orderId, ReservationStatus status);

    List<InventoryReservation> findByProductVariationIdAndStatus(Integer productVariationId, ReservationStatus status);

    @Query("SELECT ir FROM InventoryReservation ir WHERE ir.status = :status AND ir.expiresAt < :expiredBefore")
    List<InventoryReservation> findExpiredReservations(@Param("status") ReservationStatus status,
                                                      @Param("expiredBefore") LocalDateTime expiredBefore);

    @Query("SELECT COALESCE(SUM(ir.quantity), 0) FROM InventoryReservation ir WHERE ir.productVariationId = :productVariationId AND ir.status = 'RESERVED'")
    Integer getTotalReservedQuantity(@Param("productVariationId") Integer productVariationId);

    // Delete RELEASED and EXPIRED reservations
    @Modifying(clearAutomatically = true, flushAutomatically = true)
    @Query("DELETE FROM InventoryReservation ir WHERE (ir.status = 'RELEASED' AND ir.releasedAt < :cutoff) OR (ir.status = 'EXPIRED' AND ir.expiresAt < :cutoff)")
    int deleteReleasedOrExpiredBefore(@Param("cutoff") LocalDateTime cutoff);
}
