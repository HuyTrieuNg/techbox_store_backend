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
    
    @Column(name = "image_url", columnDefinition = "JSON")
    private String imageUrl; // JSON stored as String, can be parsed to List<String> in service layer
    
    @Column(name = "quantity", nullable = false)
    private Integer quantity;
    
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
    
    @OneToMany(mappedBy = "productVariation", fetch = FetchType.LAZY, cascade = CascadeType.ALL)
    private List<VariationAttribute> variationAttributes;
    
    @PrePersist
    protected void onCreate() {
        createdAt = LocalDateTime.now();
        updatedAt = LocalDateTime.now();
    }
    
    @PreUpdate
    protected void onUpdate() {
        updatedAt = LocalDateTime.now();
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
    
    // Helper method to check if in stock
    public boolean isInStock() {
        return quantity != null && quantity > 0;
    }
    
    // Helper method to decrease quantity
    public void decreaseQuantity(int amount) {
        if (this.quantity >= amount) {
            this.quantity -= amount;
        } else {
            throw new IllegalArgumentException("Insufficient stock. Available: " + this.quantity + ", Requested: " + amount);
        }
    }
    
    // Helper method to increase quantity
    public void increaseQuantity(int amount) {
        this.quantity += amount;
    }
}