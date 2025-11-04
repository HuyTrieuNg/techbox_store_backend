package vn.techbox.techbox_store.product.service.impl;

import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import vn.techbox.techbox_store.product.dto.CategoryCreateRequest;
import vn.techbox.techbox_store.product.dto.CategoryResponse;
import vn.techbox.techbox_store.product.dto.CategoryUpdateRequest;
import vn.techbox.techbox_store.product.model.Category;
import vn.techbox.techbox_store.product.repository.CategoryRepository;
import vn.techbox.techbox_store.product.service.CategoryService;

import java.util.ArrayList;
import java.util.List;
import java.util.Optional;
import java.util.stream.Collectors;

@Service
@RequiredArgsConstructor
@Transactional
public class CategoryServiceImpl implements CategoryService {
    
    private final CategoryRepository categoryRepository;
    
    @Override
    @Transactional(readOnly = true)
    public List<CategoryResponse> getAllCategories() {
        return categoryRepository.findAll()
                .stream()
                .map(this::convertToResponse)
                .collect(Collectors.toList());
    }
    
    @Override
    @Transactional(readOnly = true)
    public Optional<CategoryResponse> getCategoryById(Integer id) {
        return categoryRepository.findById(id)
                .map(this::convertToResponse);
    }
    
    @Override
    @Transactional(readOnly = true)
    public List<CategoryResponse> getRootCategories() {
        return categoryRepository.findRootCategories()
                .stream()
                .map(this::convertToResponse)
                .collect(Collectors.toList());
    }
    
    @Override
    @Transactional(readOnly = true)
    public List<CategoryResponse> getChildCategories(Integer parentId) {
        return categoryRepository.findChildCategories(parentId)
                .stream()
                .map(this::convertToResponse)
                .collect(Collectors.toList());
    }
    
    @Override
    @Transactional(readOnly = true)
    public List<Integer> getAllChildCategoryIds(Integer parentCategoryId) {
        List<Integer> result = new ArrayList<>();
        
        // Add the parent category itself
        result.add(parentCategoryId);
        
        // Recursively get all child category IDs
        List<Category> childCategories = categoryRepository.findByParentCategoryId(parentCategoryId);
        for (Category child : childCategories) {
            // Recursive call to get all descendants
            result.addAll(getAllChildCategoryIds(child.getId()));
        }
        
        return result;
    }
    
    @Override
    public CategoryResponse createCategory(CategoryCreateRequest request) {
        // Validate parent category exists if provided
        if (request.getParentCategoryId() != null) {
            if (!categoryRepository.existsById(request.getParentCategoryId())) {
                throw new RuntimeException("Parent category not found with id: " + request.getParentCategoryId());
            }
        }
        
        // Check if category name already exists
        if (categoryRepository.existsByName(request.getName())) {
            throw new RuntimeException("Category with name '" + request.getName() + "' already exists");
        }
        
        Category category = Category.builder()
                .name(request.getName())
                .parentCategoryId(request.getParentCategoryId())
                .build();
        
        Category savedCategory = categoryRepository.save(category);
        return convertToResponse(savedCategory);
    }
    
    @Override
    public CategoryResponse updateCategory(Integer id, CategoryUpdateRequest request) {
        Category category = categoryRepository.findById(id)
                .orElseThrow(() -> new RuntimeException("Category not found with id: " + id));
        
        // Validate parent category exists if provided
        if (request.getParentCategoryId() != null) {
            if (!categoryRepository.existsById(request.getParentCategoryId())) {
                throw new RuntimeException("Parent category not found with id: " + request.getParentCategoryId());
            }
            
            // Prevent circular reference (category cannot be its own parent)
            if (request.getParentCategoryId().equals(id)) {
                throw new RuntimeException("Category cannot be its own parent");
            }
        }
        
        // Check if category name already exists (excluding current category)
        if (categoryRepository.existsByNameAndIdNot(request.getName(), id)) {
            throw new RuntimeException("Category with name '" + request.getName() + "' already exists");
        }
        
        category.setName(request.getName());
        category.setParentCategoryId(request.getParentCategoryId());
        
        Category updatedCategory = categoryRepository.save(category);
        return convertToResponse(updatedCategory);
    }
    
    @Override
    public void deleteCategory(Integer id) {
        Category category = categoryRepository.findById(id)
                .orElseThrow(() -> new RuntimeException("Category not found with id: " + id));
        
        // Check if category has child categories
        List<Category> childCategories = categoryRepository.findByParentCategoryId(id);
        if (!childCategories.isEmpty()) {
            throw new RuntimeException("Cannot delete category with child categories. Delete child categories first.");
        }
        
        categoryRepository.delete(category);
    }
    
    @Override
    @Transactional(readOnly = true)
    public boolean existsByName(String name) {
        return categoryRepository.existsByName(name);
    }
    
    @Override
    @Transactional(readOnly = true)
    public boolean existsByNameAndIdNot(String name, Integer id) {
        return categoryRepository.existsByNameAndIdNot(name, id);
    }
    
    private CategoryResponse convertToResponse(Category category) {
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
                    .map(this::convertToResponse)
                    .collect(Collectors.toList());
            builder.childCategories(childResponses);
        }
        
        return builder.build();
    }
}