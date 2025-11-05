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
@Table(name = "products")
@Data
@NoArgsConstructor
@AllArgsConstructor
@Builder
public class Product {
    
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Integer id;
    
    @Column(name = "name", nullable = false)
    private String name;
    
    @Column(name = "description", columnDefinition = "TEXT")
    private String description;
    
    @Column(name = "category_id")
    private Integer categoryId;
    
    @Column(name = "brand_id")
    private Integer brandId;

    @Column(name = "SPU", nullable = false, unique = true)
    private String spu;
    
    @Column(name = "image_url")
    private String imageUrl;
    
    @Column(name = "image_public_id")
    private String imagePublicId;

    @Enumerated(EnumType.STRING)
    @Column(name = "status", nullable = false, length = 20)
    @Builder.Default
    private ProductStatus status = ProductStatus.DRAFT;

    @Column(name = "warranty_months")
    private Integer warrantyMonths;

    // Phi chuẩn hóa - thông tin đánh giá
    @Column(name = "average_rating")
    @Builder.Default
    private Double averageRating = 0.0;

    @Column(name = "total_ratings")
    @Builder.Default
    private Integer totalRatings = 0;

    // Phi chuẩn hóa - thông tin giá và giảm giá của biến thể có giá thấp nhất
    @Column(name = "display_original_price", precision = 10, scale = 2)
    private BigDecimal displayOriginalPrice;
    
    @Column(name = "display_sale_price", precision = 10, scale = 2)
    private BigDecimal displaySalePrice;
    
    @Column(name = "discount_type", length = 50)
    private String discountType; // PERCENTAGE or FIXED
    
    @Column(name = "discount_value", precision = 10, scale = 2)
    private BigDecimal discountValue;
    
    @Column(name = "created_at")
    private LocalDateTime createdAt;
    
    @Column(name = "updated_at")
    private LocalDateTime updatedAt;
    
    @Column(name = "deleted_at")
    private LocalDateTime deletedAt;
    
    // Relationships
    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "category_id", insertable = false, updatable = false)
    private Category category;
    
    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "brand_id", insertable = false, updatable = false)
    private Brand brand;


    
    @OneToMany(mappedBy = "product", fetch = FetchType.LAZY, cascade = CascadeType.ALL)
    private List<ProductVariation> productVariations;
    
    @OneToMany(mappedBy = "product", fetch = FetchType.LAZY, cascade = CascadeType.ALL)
    private List<ProductAttribute> productAttributes;
    
    @PrePersist
    protected void onCreate() {
        createdAt = LocalDateTime.now();
        updatedAt = LocalDateTime.now();

        if (this.spu == null && this.id != null) {
            this.spu = String.format("PRD-%05d", this.id);
        }
    }
    
    @PreUpdate
    protected void onUpdate() {
        updatedAt = LocalDateTime.now();
    }
    
    // Helper method to check if product is deleted (soft delete)
    public boolean isDeleted() {
        return deletedAt != null;
    }
    
    // Helper method to soft delete
    public void delete() {
        this.deletedAt = LocalDateTime.now();
        this.status = ProductStatus.DELETED;
    }
    
    // Helper method to restore
    public void restore() {
        this.deletedAt = null;
        this.status = ProductStatus.DRAFT;
    }
}