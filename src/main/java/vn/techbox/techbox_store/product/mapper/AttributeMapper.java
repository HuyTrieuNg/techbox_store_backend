package vn.techbox.techbox_store.product.mapper;

import org.springframework.stereotype.Component;
import vn.techbox.techbox_store.product.dto.attributeDto.AttributeResponse;
import vn.techbox.techbox_store.product.model.Attribute;

@Component
public class AttributeMapper {
    
    /**
     * Convert Attribute entity to AttributeResponse DTO
     */
    public AttributeResponse toResponse(Attribute attribute) {
        if (attribute == null) {
            return null;
        }
        
        return AttributeResponse.builder()
                .id(attribute.getId())
                .name(attribute.getName())
                .build();
    }
}
