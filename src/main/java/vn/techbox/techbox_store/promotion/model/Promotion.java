package vn.techbox.techbox_store.promotion.model;

import jakarta.persistence.*;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.math.BigDecimal;
import java.time.LocalDateTime;

@Entity
@Table(name = "promotions")
@Data
@Builder
@NoArgsConstructor
@AllArgsConstructor
public class Promotion {
    
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Integer id;
    
    // Khóa ngoại liên kết với Chiến dịch
    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "campaign_id", nullable = false)
    private Campaign campaign;
    
    // Mục tiêu: Áp dụng cho Biến thể sản phẩm cụ thể
    @Column(name = "product_variation_id", nullable = false)
    private Integer productVariationId;
    
    // Chi tiết giảm giá
    @Enumerated(EnumType.STRING)
    @Column(name = "promotion_type", nullable = false) 
    private PromotionType discountType;

    
    @Column(name = "discount_value", nullable = false, precision = 10, scale = 2)
    private BigDecimal discountValue;

    // Quản lý thời gian
    @Column(name = "created_at", updatable = false)
    private LocalDateTime createdAt;    @Column(name = "updated_at")
    private LocalDateTime updatedAt;
    
    @PrePersist
    public void prePersist() {
        LocalDateTime now = LocalDateTime.now();
        this.createdAt = now;
        this.updatedAt = now;
    }
    
    @PreUpdate
    public void preUpdate() {
        this.updatedAt = LocalDateTime.now();
    }
    
    // Utility methods
    public BigDecimal calculateDiscount(BigDecimal originalPrice, Integer quantity) {
        BigDecimal discount;
        
        switch (discountType) {
            case PERCENTAGE:
                discount = originalPrice.multiply(BigDecimal.valueOf(quantity))
                    .multiply(discountValue)
                    .divide(BigDecimal.valueOf(100));
                break;
            case FIXED:
                discount = discountValue.multiply(BigDecimal.valueOf(quantity));
                break;
            default:
                discount = BigDecimal.ZERO;
        }
        
        return discount;
    }
    
    @Transient
    public boolean isActive() {
        return campaign != null && campaign.isActive();
    }
    
    // Utility methods để lấy thời gian từ campaign
    @Transient
    public LocalDateTime getStartDate() {
        return campaign != null ? campaign.getStartDate() : null;
    }
    
    @Transient
    public LocalDateTime getEndDate() {
        return campaign != null ? campaign.getEndDate() : null;
    }
    
    @Transient
    public boolean isValid() {
        if (campaign == null) return false;
        LocalDateTime now = LocalDateTime.now();
        return now.isAfter(campaign.getStartDate()) && now.isBefore(campaign.getEndDate());
    }
}