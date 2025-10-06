package vn.techbox.techbox_store.product.controller;

import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.multipart.MultipartFile;
import vn.techbox.techbox_store.cloudinary.service.CloudinaryService;
import vn.techbox.techbox_store.product.dto.ProductCreateRequest;
import vn.techbox.techbox_store.product.dto.ProductResponse;
import vn.techbox.techbox_store.product.dto.ProductUpdateRequest;
import vn.techbox.techbox_store.product.service.ProductService;

import java.io.IOException;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

@RestController
@RequestMapping("/api/products")
@RequiredArgsConstructor
public class ProductController {
    
    private final ProductService productService;
    private final CloudinaryService cloudinaryService;
    
    @GetMapping
    public ResponseEntity<List<ProductResponse>> getAllProducts(
            @RequestParam(defaultValue = "false") boolean includeDeleted) {
        List<ProductResponse> products = includeDeleted 
                ? productService.getAllProducts() 
                : productService.getAllActiveProducts();
        return ResponseEntity.ok(products);
    }
    
    @GetMapping("/active")
    public ResponseEntity<List<ProductResponse>> getAllActiveProducts() {
        List<ProductResponse> products = productService.getAllActiveProducts();
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
    
    @PostMapping(consumes = {"multipart/form-data"})
    public ResponseEntity<?> createProduct(
            @RequestParam("name") String name,
            @RequestParam(value = "description", required = false) String description,
            @RequestParam(value = "categoryId", required = false) Integer categoryId,
            @RequestParam(value = "brandId", required = false) Integer brandId,
            @RequestParam(value = "image", required = false) MultipartFile image) {
        
        try {
            ProductCreateRequest request = ProductCreateRequest.builder()
                    .name(name)
                    .description(description)
                    .categoryId(categoryId)
                    .brandId(brandId)
                    .build();
            
            // Upload image to Cloudinary if provided
            if (image != null && !image.isEmpty()) {
                @SuppressWarnings("unchecked")
                Map<String, Object> uploadResult = (Map<String, Object>) cloudinaryService.uploadFile(image, "product_images");
                request.setImageUrl((String) uploadResult.get("secure_url"));
                request.setImagePublicId((String) uploadResult.get("public_id"));
            }
            
            ProductResponse createdProduct = productService.createProduct(request);
            return ResponseEntity.status(HttpStatus.CREATED).body(createdProduct);
            
        } catch (IOException e) {
            Map<String, String> error = new HashMap<>();
            error.put("error", "Failed to upload image: " + e.getMessage());
            return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR).body(error);
        } catch (Exception e) {
            Map<String, String> error = new HashMap<>();  
            error.put("error", "Failed to create product: " + e.getMessage());
            return ResponseEntity.status(HttpStatus.BAD_REQUEST).body(error);
        }
    }
    
    @PutMapping(value = "/{id}", consumes = {"multipart/form-data"})
    public ResponseEntity<?> updateProduct(
            @PathVariable Integer id,
            @RequestParam(value = "name", required = false) String name,
            @RequestParam(value = "description", required = false) String description,
            @RequestParam(value = "categoryId", required = false) Integer categoryId,
            @RequestParam(value = "brandId", required = false) Integer brandId,
            @RequestParam(value = "image", required = false) MultipartFile image,
            @RequestParam(value = "deleteImage", required = false, defaultValue = "false") boolean deleteImage) {
        
        try {
            // Get current product to check existing image
            ProductResponse currentProduct = productService.getProductById(id)
                    .orElseThrow(() -> new RuntimeException("Product not found"));
            
            ProductUpdateRequest request = ProductUpdateRequest.builder()
                    .name(name)
                    .description(description)
                    .categoryId(categoryId)
                    .brandId(brandId)
                    .build();
            
            // Handle image operations
            if (image != null && !image.isEmpty()) {
                // Always delete old image when uploading new one
                if (currentProduct.getImagePublicId() != null) {
                    cloudinaryService.deleteFile(currentProduct.getImagePublicId());
                }
                
                // Upload new image
                @SuppressWarnings("unchecked")
                Map<String, Object> uploadResult = (Map<String, Object>) cloudinaryService.uploadFile(image, "product_images");
                request.setImageUrl((String) uploadResult.get("secure_url"));
                request.setImagePublicId((String) uploadResult.get("public_id"));
            } else if (deleteImage && currentProduct.getImagePublicId() != null) {
                // Only delete image if explicitly requested
                cloudinaryService.deleteFile(currentProduct.getImagePublicId());
                request.setImageUrl(null);
                request.setImagePublicId(null);
            }
            
            ProductResponse updatedProduct = productService.updateProduct(id, request);
            return ResponseEntity.ok(updatedProduct);
            
        } catch (IOException e) {
            Map<String, String> error = new HashMap<>();
            error.put("error", "Failed to process image: " + e.getMessage());
            return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR).body(error);
        } catch (Exception e) {
            Map<String, String> error = new HashMap<>();
            error.put("error", "Failed to update product: " + e.getMessage());
            return ResponseEntity.status(HttpStatus.BAD_REQUEST).body(error);
        }
    }
    
    @DeleteMapping("/{id}")
    public ResponseEntity<?> deleteProduct(@PathVariable Integer id) {
        try {
            // Soft delete - don't delete image from Cloudinary
            productService.deleteProduct(id);
            return ResponseEntity.noContent().build();
            
        } catch (Exception e) {
            Map<String, String> error = new HashMap<>();
            error.put("error", "Failed to delete product: " + e.getMessage());
            return ResponseEntity.status(HttpStatus.BAD_REQUEST).body(error);
        }
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