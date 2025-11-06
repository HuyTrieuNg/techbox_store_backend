package vn.techbox.techbox_store.voucher.model;

import jakarta.persistence.*;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;
import vn.techbox.techbox_store.inventory.model.ReservationStatus;

import java.time.LocalDateTime;

@Entity
@Table(name = "voucher_reservations")
@Data
@NoArgsConstructor
@AllArgsConstructor
@Builder
public class VoucherReservation {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @Column(name = "order_id", nullable = false)
    private Integer orderId;

    @Column(name = "voucher_id", nullable = false)
    private Integer voucherId;

    @Column(name = "user_id", nullable = false)
    private Integer userId;

    @Column(name = "quantity", nullable = false)
    @Builder.Default
    private Integer quantity = 1;

    @Enumerated(EnumType.STRING)
    @Column(name = "status", nullable = false)
    @Builder.Default
    private ReservationStatus status = ReservationStatus.RESERVED;

    @Column(name = "reserved_at", nullable = false)
    @Builder.Default
    private LocalDateTime reservedAt = LocalDateTime.now();

    @Column(name = "expires_at", nullable = false)
    private LocalDateTime expiresAt;

    @Column(name = "released_at")
    private LocalDateTime releasedAt;

    @Column(name = "created_at")
    private LocalDateTime createdAt;

    @Column(name = "updated_at")
    private LocalDateTime updatedAt;

    @PrePersist
    protected void onCreate() {
        createdAt = LocalDateTime.now();
        updatedAt = LocalDateTime.now();
        if (reservedAt == null) {
            reservedAt = LocalDateTime.now();
        }
        if (expiresAt == null) {
            expiresAt = reservedAt.plusMinutes(15);
        }
    }

    @PreUpdate
    protected void onUpdate() {
        updatedAt = LocalDateTime.now();
    }

    public boolean isExpired() {
        return LocalDateTime.now().isAfter(expiresAt);
    }

    public boolean isActive() {
        return status == ReservationStatus.RESERVED && !isExpired();
    }

    public void release() {
        this.status = ReservationStatus.RELEASED;
        this.releasedAt = LocalDateTime.now();
    }

    public void confirm() {
        this.status = ReservationStatus.CONFIRMED;
    }
}
