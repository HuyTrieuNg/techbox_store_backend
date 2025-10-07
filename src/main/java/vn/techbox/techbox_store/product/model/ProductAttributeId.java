package vn.techbox.techbox_store.product.model;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.io.Serializable;
import java.util.Objects;

@Data
@NoArgsConstructor
@AllArgsConstructor
public class ProductAttributeId implements Serializable {
    
    
    private Integer productId;
    private Integer attributeId;
    
    @Override
    public boolean equals(Object o) {
        if (this == o) return true;
        if (o == null || getClass() != o.getClass()) return false;
        ProductAttributeId that = (ProductAttributeId) o;
        return Objects.equals(productId, that.productId) &&
               Objects.equals(attributeId, that.attributeId);
    }
    
    @Override
    public int hashCode() {
        return Objects.hash(productId, attributeId);
    }
}
