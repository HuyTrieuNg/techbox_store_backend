package vn.techbox.techbox_store.voucher.model;

import jakarta.persistence.*;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.math.BigDecimal;
import java.time.LocalDateTime;
import java.util.List;

@Entity
@Table(name = "vouchers")
@Data
@NoArgsConstructor
@AllArgsConstructor
@Builder
public class Voucher {
    
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Integer id;
    
    @Column(name = "code", nullable = false, unique = true, length = 50)
    private String code;
    
    @Enumerated(EnumType.STRING)
    @Column(name = "voucher_type", nullable = false)
    private VoucherType voucherType;
    
    @Column(name = "value", nullable = false, precision = 10, scale = 2)
    private BigDecimal value;
    
    @Column(name = "min_order_amount", nullable = false, precision = 10, scale = 2)
    @Builder.Default
    private BigDecimal minOrderAmount = BigDecimal.ZERO;
    
    @Column(name = "usage_limit", nullable = false)
    private Integer usageLimit;
    
    @Column(name = "used_count", nullable = false)
    @Builder.Default
    private Integer usedCount = 0;
    
    @Column(name = "reserved_quantity", nullable = false)
    @Builder.Default
    private Integer reservedQuantity = 0;

    @Version
    @Column(name = "version")
    private Integer version;

    @Column(name = "valid_from", nullable = false)
    private LocalDateTime validFrom;
    
    @Column(name = "valid_until", nullable = false)
    private LocalDateTime validUntil;
    
    @Column(name = "created_at")
    private LocalDateTime createdAt;
    
    @Column(name = "updated_at")
    private LocalDateTime updatedAt;
    
    @Column(name = "deleted_at")
    private LocalDateTime deletedAt;
    
    // Relationships
    @OneToMany(mappedBy = "voucher", fetch = FetchType.LAZY, cascade = CascadeType.ALL)
    private List<UserVoucher> userVouchers;
    
    @PrePersist
    protected void onCreate() {
        createdAt = LocalDateTime.now();
        updatedAt = LocalDateTime.now();
    }
    
    @PreUpdate
    protected void onUpdate() {
        updatedAt = LocalDateTime.now();
    }
    
    // Helper methods
    public boolean isDeleted() {
        return deletedAt != null;
    }
    
    public void delete() {
        this.deletedAt = LocalDateTime.now();
    }
    
    public void restore() {
        this.deletedAt = null;
    }
    
    public boolean isValid() {
        LocalDateTime now = LocalDateTime.now();
        return !isDeleted() && 
               (validFrom.isBefore(now) || validFrom.isEqual(now)) &&
               (validUntil.isAfter(now) || validUntil.isEqual(now));
    }
    
    public boolean hasUsageLeft() {
        return (usedCount + reservedQuantity) < usageLimit;
    }
    
    public Integer getAvailableQuantity() {
        int limit = (usageLimit != null ? usageLimit : 0);
        int used = (usedCount != null ? usedCount : 0);
        int reserved = (reservedQuantity != null ? reservedQuantity : 0);
        int available = limit - used - reserved;
        return Math.max(0, available);
    }

    public BigDecimal calculateDiscount(BigDecimal orderAmount) {
        if (orderAmount.compareTo(minOrderAmount) < 0) {
            return BigDecimal.ZERO;
        }
        
        if (voucherType == VoucherType.FIXED_AMOUNT) {
            return value;
        } else if (voucherType == VoucherType.PERCENTAGE) {
            return orderAmount.multiply(value).divide(BigDecimal.valueOf(100));
        }
        
        return BigDecimal.ZERO;
    }
}