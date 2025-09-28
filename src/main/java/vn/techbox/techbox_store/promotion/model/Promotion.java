package vn.techbox.techbox_store.promotion.model;

import jakarta.persistence.*;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;
import org.hibernate.annotations.SQLDelete;

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
    
    // Tên quy tắc (ví dụ: Giảm 30% Laptop Dell)
    @Column(name = "rule_name", nullable = false, length = 255)
    private String ruleName;
    
    // Mục tiêu: Áp dụng cho Biến thể sản phẩm cụ thể
    @Column(name = "product_variation_id", nullable = false)
    private Integer productVariationId;
    
    // Chi tiết giảm giá
    @Enumerated(EnumType.STRING)
    @Column(name = "discount_type", nullable = false)
    private PromotionType discountType;
    
    @Column(name = "discount_value", nullable = false, precision = 10, scale = 2)
    private BigDecimal discountValue;
    
    // Điều kiện áp dụng (Tùy chọn)
    @Column(name = "min_quantity")
    @Builder.Default
    private Integer minQuantity = 1;
    
    @Column(name = "min_order_amount", precision = 10, scale = 2)
    @Builder.Default
    private BigDecimal minOrderAmount = BigDecimal.ZERO;
    
    @Column(name = "max_discount_amount", precision = 10, scale = 2)
    private BigDecimal maxDiscountAmount;
    
    // Quản lý thời gian
    @Column(name = "created_at", updatable = false)
    private LocalDateTime createdAt;
    
    @Column(name = "updated_at")
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
    public BigDecimal calculateDiscount(BigDecimal originalPrice, Integer quantity, BigDecimal orderAmount) {
        // Kiểm tra điều kiện tối thiểu
        if (quantity < minQuantity || orderAmount.compareTo(minOrderAmount) < 0) {
            return BigDecimal.ZERO;
        }
        
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
        
        // Áp dụng giới hạn giảm giá tối đa nếu có
        if (maxDiscountAmount != null && discount.compareTo(maxDiscountAmount) > 0) {
            discount = maxDiscountAmount;
        }
        
        return discount;
    }
    
    public String getDiscountDisplay() {
        return discountValue + discountType.getSymbol();
    }
    
    public boolean isActive() {
        return campaign != null && campaign.isActive();
    }
}