package vn.techbox.techbox_store.voucher.service;

import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.dao.OptimisticLockingFailureException;
import org.springframework.retry.annotation.Backoff;
import org.springframework.retry.annotation.Retryable;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import vn.techbox.techbox_store.inventory.model.ReservationStatus;
import vn.techbox.techbox_store.voucher.model.UserVoucher;
import vn.techbox.techbox_store.voucher.model.Voucher;
import vn.techbox.techbox_store.voucher.model.VoucherReservation;
import vn.techbox.techbox_store.voucher.repository.UserVoucherRepository;
import vn.techbox.techbox_store.voucher.repository.VoucherRepository;
import vn.techbox.techbox_store.voucher.repository.VoucherReservationRepository;

import java.time.LocalDateTime;
import java.util.List;

@Service
@RequiredArgsConstructor
@Slf4j
public class VoucherReservationService {

    private final VoucherReservationRepository voucherReservationRepository;
    private final VoucherRepository voucherRepository;
    private final UserVoucherRepository userVoucherRepository;

    @Transactional
    @Retryable(retryFor = {OptimisticLockingFailureException.class}, maxAttempts = 3, backoff = @Backoff(delay = 100))
    public VoucherReservation reserveVoucher(Integer orderId, Integer voucherId, Integer userId) {
        log.info("Reserving voucher for order: {}, voucher: {}, user: {}", orderId, voucherId, userId);

        Voucher voucher = voucherRepository.findById(voucherId)
                .orElseThrow(() -> new IllegalArgumentException("Voucher not found: " + voucherId));

        // Check if voucher is valid and has usage left
        if (!voucher.isValid()) {
            throw new IllegalArgumentException("Voucher is not valid or has expired");
        }

        if (voucher.getAvailableQuantity() <= 0) {
            throw new IllegalArgumentException("Voucher has no usage left");
        }

        // Update reserved quantity in voucher with optimistic locking
        voucher.setReservedQuantity(voucher.getReservedQuantity() + 1);
        voucherRepository.save(voucher);

        // Create voucher reservation record
        VoucherReservation reservation = VoucherReservation.builder()
                .orderId(orderId)
                .voucherId(voucherId)
                .userId(userId)
                .quantity(1)
                .status(ReservationStatus.RESERVED)
                .reservedAt(LocalDateTime.now())
                .expiresAt(LocalDateTime.now().plusMinutes(15))
                .build();

        return voucherReservationRepository.save(reservation);
    }

    @Transactional
    @Retryable(retryFor = {OptimisticLockingFailureException.class}, maxAttempts = 3, backoff = @Backoff(delay = 100))
    public VoucherReservation reserveVoucherByCode(Integer orderId, String voucherCode, Integer userId) {
        return reserveVoucherByCodeInternal(orderId, voucherCode, userId, LocalDateTime.now().plusMinutes(15));
    }

    @Transactional
    @Retryable(retryFor = {OptimisticLockingFailureException.class}, maxAttempts = 3, backoff = @Backoff(delay = 100))
    public VoucherReservation reserveVoucherPermanent(Integer orderId, String voucherCode, Integer userId) {
        return reserveVoucherByCodeInternal(orderId, voucherCode, userId, null);
    }

    @Transactional
    @Retryable(retryFor = {OptimisticLockingFailureException.class}, maxAttempts = 3, backoff = @Backoff(delay = 100))
    public VoucherReservation reserveVoucherByCodeInternal(Integer orderId, String voucherCode, Integer userId, LocalDateTime expiresAt) {
        log.info("Reserving voucher by code for order: {}, voucher code: {}, user: {}, permanent: {}",
                orderId, voucherCode, userId, expiresAt == null);

        Voucher voucher = voucherRepository.findByCodeAndNotDeleted(voucherCode)
                .orElseThrow(() -> new IllegalArgumentException("Voucher not found: " + voucherCode));

        // Check if voucher is valid and has usage left
        if (!voucher.isValid()) {
            throw new IllegalArgumentException("Voucher is not valid or has expired");
        }

        if (voucher.getAvailableQuantity() <= 0) {
            throw new IllegalArgumentException("Voucher has no usage left");
        }

        // Update reserved quantity in voucher with optimistic locking
        voucher.setReservedQuantity(voucher.getReservedQuantity() + 1);
        voucherRepository.save(voucher);

        // Create voucher reservation record
        VoucherReservation reservation = VoucherReservation.builder()
                .orderId(orderId)
                .voucherId(voucher.getId())
                .userId(userId)
                .quantity(1)
                .status(ReservationStatus.RESERVED)
                .reservedAt(LocalDateTime.now())
                .expiresAt(expiresAt)
                .build();

        return voucherReservationRepository.save(reservation);
    }

    @Transactional
    public void confirmReservations(Integer orderId) {
        log.info("Confirming voucher reservations for order: {}", orderId);

        List<VoucherReservation> reservations = voucherReservationRepository
                .findByOrderIdAndStatus(orderId, ReservationStatus.RESERVED);

        for (VoucherReservation reservation : reservations) {
            // Confirm the reservation
            reservation.confirm();

            // Get voucher to find the voucher code
            Voucher voucher = voucherRepository.findById(reservation.getVoucherId())
                    .orElseThrow(() -> new IllegalStateException("Voucher not found: " + reservation.getVoucherId()));

            // Create UserVoucher record to mark voucher as used
            UserVoucher userVoucher = UserVoucher.builder()
                    .userId(reservation.getUserId())
                    .voucherCode(voucher.getCode())
                    .usedAt(LocalDateTime.now())
                    .orderId(orderId)
                    .build();

            // Update voucher quantities
            voucher.setReservedQuantity(voucher.getReservedQuantity() - reservation.getQuantity());
            voucher.setUsedCount(voucher.getUsedCount() + reservation.getQuantity());

            userVoucherRepository.save(userVoucher);
            voucherRepository.save(voucher);
            voucherReservationRepository.save(reservation);

            log.info("Confirmed voucher reservation for voucher: {}. Used count: {}/{}",
                    voucher.getCode(), voucher.getUsedCount(), voucher.getUsageLimit());
        }
    }

    @Transactional
    public void     releaseReservations(Integer orderId) {
        log.info("Releasing voucher reservations for order: {}", orderId);

        List<VoucherReservation> reservations = voucherReservationRepository
                .findByOrderIdAndStatus(orderId, ReservationStatus.RESERVED);

        for (VoucherReservation reservation : reservations) {
            // Release the reservation
            reservation.release();

            // Remove from reserved quantity
            Voucher voucher = voucherRepository.findById(reservation.getVoucherId())
                    .orElseThrow(() -> new IllegalStateException("Voucher not found: " + reservation.getVoucherId()));

            voucher.setReservedQuantity(voucher.getReservedQuantity() - reservation.getQuantity());

            voucherRepository.save(voucher);
            voucherReservationRepository.save(reservation);

            log.info("Released voucher reservation for voucher: {}", voucher.getCode());
        }
    }

    @Transactional
    public void setReservationsExpiryNull(Integer orderId) {
        log.info("Setting voucher reservations expiry to null for order: {}", orderId);

        List<VoucherReservation> reservations = voucherReservationRepository
                .findByOrderIdAndStatus(orderId, ReservationStatus.RESERVED);

        for (VoucherReservation reservation : reservations) {
            // Set expiry to null (permanent reservation)
            reservation.setExpiresAt(null);
            voucherReservationRepository.save(reservation);

            log.info("Set voucher reservation expiry to null for voucher ID: {}", reservation.getVoucherId());
        }
    }

    @Transactional
    public void cleanUpExpiredReservations() {
        log.info("Cleaning up expired voucher reservations");

        List<VoucherReservation> expiredReservations = voucherReservationRepository
                .findExpiredReservations(ReservationStatus.RESERVED, LocalDateTime.now());

        for (VoucherReservation reservation : expiredReservations) {
            reservation.setStatus(ReservationStatus.EXPIRED);

            // Remove from reserved quantity
            Voucher voucher = voucherRepository.findById(reservation.getVoucherId())
                    .orElse(null);

            if (voucher != null) {
                voucher.setReservedQuantity(voucher.getReservedQuantity() - reservation.getQuantity());
                voucherRepository.save(voucher);
            }

            voucherReservationRepository.save(reservation);
        }

        log.info("Cleaned up {} expired voucher reservations", expiredReservations.size());
    }

    @Transactional
    public int purgeOldReleasedAndExpiredReservations(LocalDateTime cutoff) {
        log.info("Purging old voucher reservations before {}", cutoff);
        int deleted = voucherReservationRepository.deleteReleasedOrExpiredBefore(cutoff);
        log.info("Purged {} old voucher reservations", deleted);
        return deleted;
    }
}
