package vn.techbox.techbox_store.product.service;

import vn.techbox.techbox_store.product.dto.CategoryCreateRequest;
import vn.techbox.techbox_store.product.dto.CategoryResponse;
import vn.techbox.techbox_store.product.dto.CategoryUpdateRequest;

import java.util.List;
import java.util.Optional;

public interface CategoryService {
    
    List<CategoryResponse> getAllCategories();
    
    Optional<CategoryResponse> getCategoryById(Integer id);
    
    List<CategoryResponse> getRootCategories();
    
    List<CategoryResponse> getChildCategories(Integer parentId);
    
    CategoryResponse createCategory(CategoryCreateRequest request);
    
    CategoryResponse updateCategory(Integer id, CategoryUpdateRequest request);
    
    void deleteCategory(Integer id);
    
    boolean existsByName(String name);
    
    boolean existsByNameAndIdNot(String name, Integer id);
}