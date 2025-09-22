package vn.techbox.techbox_store.product.controller;

import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;
import vn.techbox.techbox_store.product.dto.ProductCreateRequest;
import vn.techbox.techbox_store.product.dto.ProductResponse;
import vn.techbox.techbox_store.product.dto.ProductUpdateRequest;
import vn.techbox.techbox_store.product.service.ProductService;

import java.util.List;

@RestController
@RequestMapping("/api/products")
@RequiredArgsConstructor
public class ProductController {
    
    private final ProductService productService;
    
    @GetMapping
    public ResponseEntity<List<ProductResponse>> getAllProducts(
            @RequestParam(defaultValue = "false") boolean includeDeleted) {
        List<ProductResponse> products = includeDeleted 
                ? productService.getAllProducts() 
                : productService.getAllActiveProducts();
        return ResponseEntity.ok(products);
    }
    
    @GetMapping("/{id}")
    public ResponseEntity<ProductResponse> getProductById(
            @PathVariable Integer id,
            @RequestParam(defaultValue = "false") boolean includeDeleted) {
        return (includeDeleted 
                ? productService.getProductById(id) 
                : productService.getActiveProductById(id))
                .map(product -> ResponseEntity.ok(product))
                .orElse(ResponseEntity.notFound().build());
    }
    
    @PostMapping
    public ResponseEntity<ProductResponse> createProduct(@Valid @RequestBody ProductCreateRequest request) {
        ProductResponse createdProduct = productService.createProduct(request);
        return ResponseEntity.status(HttpStatus.CREATED).body(createdProduct);
    }
    
    @PutMapping("/{id}")
    public ResponseEntity<ProductResponse> updateProduct(
            @PathVariable Integer id, 
            @Valid @RequestBody ProductUpdateRequest request) {
        ProductResponse updatedProduct = productService.updateProduct(id, request);
        return ResponseEntity.ok(updatedProduct);
    }
    
    @DeleteMapping("/{id}")
    public ResponseEntity<Void> deleteProduct(@PathVariable Integer id) {
        productService.deleteProduct(id);
        return ResponseEntity.noContent().build();
    }
    
    @PatchMapping("/{id}/restore")
    public ResponseEntity<Void> restoreProduct(@PathVariable Integer id) {
        productService.restoreProduct(id);
        return ResponseEntity.ok().build();
    }
    
    @GetMapping("/category/{categoryId}")
    public ResponseEntity<List<ProductResponse>> getProductsByCategory(@PathVariable Integer categoryId) {
        List<ProductResponse> products = productService.getProductsByCategory(categoryId);
        return ResponseEntity.ok(products);
    }
    
    @GetMapping("/brand/{brandId}")
    public ResponseEntity<List<ProductResponse>> getProductsByBrand(@PathVariable Integer brandId) {
        List<ProductResponse> products = productService.getProductsByBrand(brandId);
        return ResponseEntity.ok(products);
    }
    
    @GetMapping("/search")
    public ResponseEntity<List<ProductResponse>> searchProducts(@RequestParam String keyword) {
        List<ProductResponse> products = productService.searchProductsByName(keyword);
        return ResponseEntity.ok(products);
    }
    
    @GetMapping("/exists")
    public ResponseEntity<Boolean> checkProductExists(
            @RequestParam String name,
            @RequestParam(required = false) Integer excludeId) {
        boolean exists = excludeId != null 
                ? productService.existsByNameAndIdNot(name, excludeId)
                : productService.existsByName(name);
        return ResponseEntity.ok(exists);
    }
}