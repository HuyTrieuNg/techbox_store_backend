package vn.techbox.techbox_store.inventory.scheduler;

import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.scheduling.annotation.Scheduled;
import org.springframework.stereotype.Service;
import vn.techbox.techbox_store.inventory.service.InventoryReservationService;
import vn.techbox.techbox_store.voucher.service.VoucherReservationService;

import java.time.LocalDateTime;

@Service
@RequiredArgsConstructor
@Slf4j
public class ReservationCleanupScheduler {

    private final InventoryReservationService inventoryReservationService;
    private final VoucherReservationService voucherReservationService;

    @Scheduled(fixedRate = 300000)
    public void cleanUpExpiredReservations() {
        log.info("Starting scheduled cleanup of expired reservations");

        try {
            inventoryReservationService.cleanUpExpiredReservations();
            voucherReservationService.cleanUpExpiredReservations();

            log.info("Successfully completed scheduled cleanup of expired reservations");
        } catch (Exception e) {
            log.error("Error during scheduled cleanup of expired reservations", e);
        }
    }

    @Scheduled(cron = "0 30 2 * * *")
    public void purgeOldReleasedAndExpiredReservations() {
        LocalDateTime cutoff = LocalDateTime.now().minusDays(7);
        log.info("Starting daily purge of RELEASED/EXPIRED reservations older than {}", cutoff);
        try {
            int invDeleted = inventoryReservationService.purgeOldReleasedAndExpiredReservations(cutoff);
            int vouDeleted = voucherReservationService.purgeOldReleasedAndExpiredReservations(cutoff);
            log.info("Purged old reservations - inventory: {}, vouchers: {}", invDeleted, vouDeleted);
        } catch (Exception e) {
            log.error("Error during daily purge of old reservations", e);
        }
    }
}
