package vn.techbox.techbox_store.voucher.model;

import com.fasterxml.jackson.annotation.JsonIgnoreProperties;
import jakarta.persistence.*;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.time.LocalDateTime;

@Entity
@Table(name = "user_vouchers")
@JsonIgnoreProperties({"voucher"})
@Data
@NoArgsConstructor
@AllArgsConstructor
@Builder
@IdClass(UserVoucherId.class)
public class UserVoucher {
    
    @Id
    @Column(name = "user_id")
    private Integer userId;
    
    @Id
    @Column(name = "voucher_id")
    private Integer voucherId;
    
    @Column(name = "used_at", nullable = false)
    private LocalDateTime usedAt;
    
    @Column(name = "order_id")
    private Integer orderId;
    
    // Relationships
    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "voucher_id", insertable = false, updatable = false)
    private Voucher voucher;
    
    @PrePersist
    protected void onCreate() {
        if (usedAt == null) {
            usedAt = LocalDateTime.now();
        }
    }
}