package vn.techbox.techbox_store.product.model;

import jakarta.persistence.*;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.io.Serializable;

@Entity
@Table(name = "product_attributes")
@Data
@NoArgsConstructor
@AllArgsConstructor
@Builder
@IdClass(ProductAttributeId.class)
public class ProductAttribute {
    
    @Id
    @Column(name = "product_id")
    private Integer productId;
    
    @Id
    @Column(name = "attribute_id")
    private Integer attributeId;
    
    @Column(name = "value", nullable = false, columnDefinition = "TEXT")
    private String value;
    
    // Relationships
    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "product_id", insertable = false, updatable = false)
    private Product product;
    
    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "attribute_id", insertable = false, updatable = false)
    private Attribute attribute;
}

// Composite key class
@Data
@NoArgsConstructor
@AllArgsConstructor
class ProductAttributeId implements Serializable {
    private Integer productId;
    private Integer attributeId;
}