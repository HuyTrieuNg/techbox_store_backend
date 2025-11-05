package vn.techbox.techbox_store.product.mapper;

import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Component;
import vn.techbox.techbox_store.product.dto.CategoryDto.CategoryResponse;
import vn.techbox.techbox_store.product.model.Category;

import java.util.List;
import java.util.stream.Collectors;

@Component
@RequiredArgsConstructor
public class CategoryMapper {
    
    /**
     * Convert Category entity to CategoryResponse DTO
     */
    public CategoryResponse toResponse(Category category) {
        if (category == null) {
            return null;
        }
        
        CategoryResponse.CategoryResponseBuilder builder = CategoryResponse.builder()
                .id(category.getId())
                .name(category.getName())
                .parentCategoryId(category.getParentCategoryId())
                .createdAt(category.getCreatedAt())
                .updatedAt(category.getUpdatedAt());
        
        // Set parent category name if exists
        if (category.getParentCategory() != null) {
            builder.parentCategoryName(category.getParentCategory().getName());
        }
        
        // Set child categories if exists
        if (category.getChildCategories() != null && !category.getChildCategories().isEmpty()) {
            List<CategoryResponse> childResponses = category.getChildCategories()
                    .stream()
                    .map(this::toResponse)
                    .collect(Collectors.toList());
            builder.childCategories(childResponses);
        }
        
        return builder.build();
    }
}
