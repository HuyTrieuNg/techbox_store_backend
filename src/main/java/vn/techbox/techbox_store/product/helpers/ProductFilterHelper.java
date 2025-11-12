package vn.techbox.techbox_store.product.helpers;

import java.util.List;

import org.springframework.stereotype.Component;

import vn.techbox.techbox_store.product.dto.productDto.ProductFilterRequest;
import vn.techbox.techbox_store.product.model.ProductStatus;
import vn.techbox.techbox_store.product.service.CategoryService;

@Component 
public class ProductFilterHelper {

    private final CategoryService categoryService;

    public ProductFilterHelper(CategoryService categoryService) {
        this.categoryService = categoryService;
    }

    
    public ProductFilterRequest prepareFilter(ProductFilterRequest filter) {
        ProductFilterRequest.ProductFilterRequestBuilder builder = filter.toBuilder();
        
        if (filter.getStatus() == null) {
            builder.status(ProductStatus.PUBLISHED);
        }
        
        if (filter.getCategoryId() != null) {
            List<Integer> allCategoryIds = categoryService.getAllChildCategoryIds(filter.getCategoryId());
            builder.categoryIds(allCategoryIds);
        }
        
        return builder.build();
    }

    public ProductFilterRequest prepareManagementFilter(ProductFilterRequest filter) {
        ProductFilterRequest.ProductFilterRequestBuilder builder = filter.toBuilder();
        
        if (filter.getCategoryId() != null) {
            List<Integer> allCategoryIds = categoryService.getAllChildCategoryIds(filter.getCategoryId());
            builder.categoryIds(allCategoryIds);
        }
        
        return builder.build();
    }
}