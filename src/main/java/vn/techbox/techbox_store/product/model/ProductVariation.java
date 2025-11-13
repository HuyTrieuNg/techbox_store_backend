package vn.techbox.techbox_store.product.model;

import jakarta.persistence.*;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.math.BigDecimal;
import java.time.LocalDateTime;
import java.util.List;

@Entity
@Table(name = "product_variations")
@Data
@NoArgsConstructor
@AllArgsConstructor
@Builder
public class ProductVariation {
    
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Integer id;
    
    @Column(name = "variation_name")
    private String variationName;
    
    @Column(name = "product_id", nullable = false)
    private Integer productId;
    
    @Column(name = "price", nullable = false, precision = 10, scale = 2)
    private BigDecimal price;
    
    @Column(name = "sku", unique = true)
    private String sku;
    
    @Column(name = "stock_quantity", nullable = false)
    @Builder.Default
    private Integer stockQuantity = 0;

    @Column(name = "reserved_quantity", nullable = false)
    @Builder.Default
    private Integer reservedQuantity = 0;

    @Version
    @Column(name = "version")
    private Integer version;

    @Column(name = "avg_cost_price", precision = 10, scale = 2)
        private BigDecimal avgCostPrice;

    @Column(name = "created_at")
    private LocalDateTime createdAt;
    
    @Column(name = "updated_at")
    private LocalDateTime updatedAt;
    
    @Column(name = "deleted_at")
    private LocalDateTime deletedAt;
    
    // Relationships
    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "product_id", insertable = false, updatable = false)
    private Product product;
    
    @OneToMany(mappedBy = "productVariation", fetch = FetchType.LAZY, cascade = CascadeType.ALL, orphanRemoval = true)
    @Builder.Default
    private List<VariationAttribute> variationAttributes = new java.util.ArrayList<>();
    
    @OneToMany(mappedBy = "productVariation", fetch = FetchType.LAZY, cascade = CascadeType.ALL, orphanRemoval = true)
    private List<ProductVariationImage> images;
    
    // Relationship with Promotions (one variation can have multiple promotions through campaigns)
    @OneToMany(fetch = FetchType.LAZY)
    @JoinColumn(name = "product_variation_id", insertable = false, updatable = false)
    private List<vn.techbox.techbox_store.promotion.model.Promotion> promotions;
    
    @PrePersist
    protected void onCreate() {
        createdAt = LocalDateTime.now();
        updatedAt = LocalDateTime.now();
    }
    
    @PreUpdate
    protected void onUpdate() {
        updatedAt = LocalDateTime.now();
    }

    // Helper methods for managing the bidirectional relationship with VariationAttribute
    public void addVariationAttribute(VariationAttribute attribute) {
        variationAttributes.add(attribute);
        attribute.setProductVariation(this);
    }

    public void removeVariationAttribute(VariationAttribute attribute) {
        variationAttributes.remove(attribute);
        attribute.setProductVariation(null);
    }
    
    // Helper method to check if product variation is deleted (soft delete)
    public boolean isDeleted() {
        return deletedAt != null;
    }
    
    // Helper method to soft delete
    public void delete() {
        this.deletedAt = LocalDateTime.now();
    }
    
    // Helper method to restore
    public void restore() {
        this.deletedAt = null;
    }
    
    // Helper method to get available quantity (stock - reserved)
    public Integer getAvailableQuantity() {
        int stock = (stockQuantity != null ? stockQuantity : 0);
        int reserved = (reservedQuantity != null ? reservedQuantity : 0);
        int available = stock - reserved;
        return Math.max(available, 0);
    }
    
    // Helper method to decrease quantity
    public void decreaseQuantity(int amount) {
        int available = (this.stockQuantity != null ? this.stockQuantity : 0) - (this.reservedQuantity != null ? this.reservedQuantity : 0);
        if (available >= amount) {
            this.stockQuantity = (this.stockQuantity != null ? this.stockQuantity : 0) - amount;
        } else {
            throw new IllegalArgumentException("Insufficient stock. Available: " + available + ", Requested: " + amount);
        }
    }
    
    // Helper method to increase quantity
    public void increaseQuantity(int amount) {
        this.stockQuantity = (this.stockQuantity != null ? this.stockQuantity : 0) + amount;
    }
}