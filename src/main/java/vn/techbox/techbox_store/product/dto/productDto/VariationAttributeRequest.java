package vn.techbox.techbox_store.product.dto.productDto;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@NoArgsConstructor
@AllArgsConstructor
public class VariationAttributeRequest {
    private String attributeKey;
    private String attributeValue;
}