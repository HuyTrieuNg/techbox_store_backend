package vn.techbox.techbox_store.product.dto.productDto;

import jakarta.validation.constraints.NotNull;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@NoArgsConstructor
@AllArgsConstructor
public class AttributeRequest {
    @NotNull(message = "Attribute ID cannot be null")
    private Integer attributeId;

    private String value;
}
