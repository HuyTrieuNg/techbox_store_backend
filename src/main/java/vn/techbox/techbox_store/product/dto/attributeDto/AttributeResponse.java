package vn.techbox.techbox_store.product.dto.attributeDto;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@NoArgsConstructor
@AllArgsConstructor
@Builder
public class AttributeResponse {
    
    private Integer id;
    private String name;
}