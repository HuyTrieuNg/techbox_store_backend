package vn.techbox.techbox_store.product.model;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.io.Serializable;
import java.util.Objects;

@Data
@NoArgsConstructor
@AllArgsConstructor
public class VariationAttributeId implements Serializable {
    
    
    private Integer productVariationId;
    private Integer attributeId;
    
    @Override
    public boolean equals(Object o) {
        if (this == o) return true;
        if (o == null || getClass() != o.getClass()) return false;
        VariationAttributeId that = (VariationAttributeId) o;
        return Objects.equals(productVariationId, that.productVariationId) &&
               Objects.equals(attributeId, that.attributeId);
    }
    
    @Override
    public int hashCode() {
        return Objects.hash(productVariationId, attributeId);
    }
}
