package vn.techbox.techbox_store.product.dto;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;
import vn.techbox.techbox_store.product.model.AttributeDataType;

@Data
@NoArgsConstructor
@AllArgsConstructor
@Builder
public class AttributeResponse {
    
    private Integer id;
    private String name;
    private AttributeDataType dataType;
}