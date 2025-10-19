package vn.techbox.techbox_store.product.controller;

import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.web.bind.annotation.*;
import vn.techbox.techbox_store.product.dto.CategoryCreateRequest;
import vn.techbox.techbox_store.product.dto.CategoryResponse;
import vn.techbox.techbox_store.product.dto.CategoryUpdateRequest;
import vn.techbox.techbox_store.product.service.CategoryService;

import java.util.List;

@RestController
@RequestMapping("/categories")
@RequiredArgsConstructor
public class CategoryController {
    
    private final CategoryService categoryService;
    
    @GetMapping
    public ResponseEntity<List<CategoryResponse>> getAllCategories() {
        List<CategoryResponse> categories = categoryService.getAllCategories();
        return ResponseEntity.ok(categories);
    }
    
    @GetMapping("/{id}")
    public ResponseEntity<CategoryResponse> getCategoryById(@PathVariable Integer id) {
        return categoryService.getCategoryById(id)
                .map(ResponseEntity::ok)
                .orElse(ResponseEntity.notFound().build());
    }
    
    @GetMapping("/root")
    public ResponseEntity<List<CategoryResponse>> getRootCategories() {
        List<CategoryResponse> rootCategories = categoryService.getRootCategories();
        return ResponseEntity.ok(rootCategories);
    }
    
    @GetMapping("/{parentId}/children")
    public ResponseEntity<List<CategoryResponse>> getChildCategories(@PathVariable Integer parentId) {
        List<CategoryResponse> childCategories = categoryService.getChildCategories(parentId);
        return ResponseEntity.ok(childCategories);
    }
    
    @PreAuthorize("hasAuthority('PRODUCT:WRITE')")
    @PostMapping
    public ResponseEntity<CategoryResponse> createCategory(@Valid @RequestBody CategoryCreateRequest request) {
        try {
            CategoryResponse createdCategory = categoryService.createCategory(request);
            return ResponseEntity.status(HttpStatus.CREATED).body(createdCategory);
        } catch (RuntimeException e) {
            return ResponseEntity.badRequest().build();
        }
    }
    
    @PreAuthorize("hasAuthority('PRODUCT:UPDATE')")
    @PutMapping("/{id}")
    public ResponseEntity<CategoryResponse> updateCategory(
            @PathVariable Integer id,
            @Valid @RequestBody CategoryUpdateRequest request) {
        try {
            CategoryResponse updatedCategory = categoryService.updateCategory(id, request);
            return ResponseEntity.ok(updatedCategory);
        } catch (RuntimeException e) {
            return ResponseEntity.badRequest().build();
        }
    }
    
    @PreAuthorize("hasAuthority('PRODUCT:DELETE')")
    @DeleteMapping("/{id}")
    public ResponseEntity<Void> deleteCategory(@PathVariable Integer id) {
        try {
            categoryService.deleteCategory(id);
            return ResponseEntity.noContent().build();
        } catch (RuntimeException e) {
            return ResponseEntity.badRequest().build();
        }
    }
    
    @PreAuthorize("hasAuthority('PRODUCT:READ')")
    @GetMapping("/exists")
    public ResponseEntity<Boolean> checkCategoryNameExists(@RequestParam String name) {
        boolean exists = categoryService.existsByName(name);
        return ResponseEntity.ok(exists);
    }
}