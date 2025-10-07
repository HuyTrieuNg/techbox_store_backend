package vn.techbox.techbox_store.product.model;

import jakarta.persistence.*;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

@Entity
@Table(name = "variation_attributes")
@Data
@NoArgsConstructor
@AllArgsConstructor
@Builder
@IdClass(VariationAttributeId.class)
public class VariationAttribute {
    
    @Id
    @Column(name = "product_variation_id")
    private Integer productVariationId;
    
    @Id
    @Column(name = "attribute_id")
    private Integer attributeId;
    
    @Column(name = "value", nullable = false, columnDefinition = "TEXT")
    private String value;
    
    // Relationships
    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "product_variation_id", insertable = false, updatable = false)
    private ProductVariation productVariation;
    
    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "attribute_id", insertable = false, updatable = false)
    private Attribute attribute;
}
