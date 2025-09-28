package vn.techbox.techbox_store.product.model;

import jakarta.persistence.*;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

@Entity
@Table(name = "product_variation_images")
@Data
@NoArgsConstructor
@AllArgsConstructor
@Builder
public class ProductVariationImage {
    
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Integer id;
    
    @Column(name = "product_variation_id", nullable = false)
    private Integer productVariationId;
    
    @Column(name = "image_url", nullable = false)
    private String imageUrl;
    
    @Column(name = "image_public_id")
    private String imagePublicId;
    
    // Relationships
    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "product_variation_id", insertable = false, updatable = false)
    private ProductVariation productVariation;
}