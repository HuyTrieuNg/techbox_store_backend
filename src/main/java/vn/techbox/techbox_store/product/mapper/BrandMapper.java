package vn.techbox.techbox_store.product.mapper;

import org.springframework.stereotype.Component;
import vn.techbox.techbox_store.product.dto.brandDto.BrandResponse;
import vn.techbox.techbox_store.product.model.Brand;

@Component
public class BrandMapper {
    
    /**
     * Convert Brand entity to BrandResponse DTO
     */
    public BrandResponse toResponse(Brand brand) {
        if (brand == null) {
            return null;
        }
        
        return BrandResponse.builder()
                .id(brand.getId())
                .name(brand.getName())
                .createdAt(brand.getCreatedAt())
                .updatedAt(brand.getUpdatedAt())
                .build();
    }
}
