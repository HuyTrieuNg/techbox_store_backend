package vn.techbox.techbox_store.product.controller;

import lombok.RequiredArgsConstructor;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Pageable;
import org.springframework.data.domain.Sort;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.security.core.Authentication;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.multipart.MultipartFile;
import vn.techbox.techbox_store.cloudinary.service.CloudinaryService;
import vn.techbox.techbox_store.product.dto.*;
import vn.techbox.techbox_store.product.service.ProductService;
import vn.techbox.techbox_store.user.model.User;

import java.io.IOException;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

@RestController
@RequestMapping("/products")
@RequiredArgsConstructor
public class ProductController {
    
    private final ProductService productService;
    private final CloudinaryService cloudinaryService;
    
    /**
     * Search and filter products with multiple criteria
     * Supports: name, brand, category, attributes, price range, rating
     * With sorting and pagination
     */
    @GetMapping("/search")
    public ResponseEntity<Page<ProductListResponse>> searchAndFilterProducts(
            @RequestParam(required = false) String name,
            @RequestParam(required = false) Integer brandId,
            @RequestParam(required = false) Integer categoryId,
            @RequestParam(required = false) List<Integer> categoryIds,
            @RequestParam(required = false) List<String> attributes,
            @RequestParam(required = false) java.math.BigDecimal minPrice,
            @RequestParam(required = false) java.math.BigDecimal maxPrice,
            @RequestParam(required = false) Double minRating,
            @RequestParam(defaultValue = "id") String sortBy,
            @RequestParam(defaultValue = "ASC") String sortDirection,
            @RequestParam(defaultValue = "0") int page,
            @RequestParam(defaultValue = "20") int size,
            Authentication authentication) {

        Integer userId = null;
        if (authentication != null && authentication.getPrincipal() instanceof User) {
            userId = ((User) authentication.getPrincipal()).getId();
        }

        ProductFilterRequest filterRequest = ProductFilterRequest.builder()
                .name(name)
                .brandId(brandId)
                .categoryId(categoryId)
                .categoryIds(categoryIds)
                .attributes(attributes)
                .minPrice(minPrice)
                .maxPrice(maxPrice)
                .minRating(minRating)
                .sortBy(sortBy)
                .sortDirection(sortDirection)
                .page(page)
                .size(size)
                .build();

        Page<ProductListResponse> products = productService.filterProducts(filterRequest, userId);
        return ResponseEntity.ok(products);
    }

    /**
     * Public: Get all active products with pagination (no authentication required)
     */
    @GetMapping
    public ResponseEntity<Page<ProductListResponse>> getAllProducts(
            @RequestParam(defaultValue = "0") int page,
            @RequestParam(defaultValue = "20") int size,
            @RequestParam(defaultValue = "id") String sortBy,
            @RequestParam(defaultValue = "ASC") String sortDirection) {

        Sort.Direction direction = sortDirection.equalsIgnoreCase("DESC")
                ? Sort.Direction.DESC
                : Sort.Direction.ASC;
        Pageable pageable = PageRequest.of(page, size, Sort.by(direction, sortBy));

        Page<ProductListResponse> products = productService.getAllProducts(pageable);
        return ResponseEntity.ok(products);
    }
    
    /**
     * Public: Get products by campaign ID (no authentication required)
     * Returns products that have promotions in the specified campaign
     */
    @GetMapping("/campaign/{campaignId}")
    public ResponseEntity<Page<ProductListResponse>> getProductsByCampaign(
            @PathVariable Integer campaignId,
            @RequestParam(defaultValue = "0") int page,
            @RequestParam(defaultValue = "20") int size,
            @RequestParam(defaultValue = "id") String sortBy,
            @RequestParam(defaultValue = "ASC") String sortDirection,
            Authentication authentication) {

        Integer userId = null;
        if (authentication != null && authentication.getPrincipal() instanceof User) {
            userId = ((User) authentication.getPrincipal()).getId();
        }

        Sort.Direction direction = sortDirection.equalsIgnoreCase("DESC")
                ? Sort.Direction.DESC
                : Sort.Direction.ASC;
        Pageable pageable = PageRequest.of(page, size, Sort.by(direction, sortBy));

        Page<ProductListResponse> products = productService.getProductsByCampaign(campaignId, pageable, userId);
        return ResponseEntity.ok(products);
    }

    /**
     * Admin: Get only soft-deleted products with pagination
     */
    @PreAuthorize("hasAuthority('PRODUCT:READ')")
    @GetMapping("/admin/deleted")
    public ResponseEntity<Page<ProductListResponse>> getDeletedProductsForAdmin(
            @RequestParam(defaultValue = "0") int page,
            @RequestParam(defaultValue = "20") int size,
            @RequestParam(defaultValue = "deletedAt") String sortBy,
            @RequestParam(defaultValue = "DESC") String sortDirection) {

        Sort.Direction direction = sortDirection.equalsIgnoreCase("DESC")
                ? Sort.Direction.DESC
                : Sort.Direction.ASC;
        Pageable pageable = PageRequest.of(page, size, Sort.by(direction, sortBy));

        Page<ProductListResponse> products = productService.getDeletedProductsForAdmin(pageable);
        return ResponseEntity.ok(products);
    }

    /**
     * Get product detail with full information (variations, attributes, promotions)
     */
    @GetMapping("/{id}")
    public ResponseEntity<ProductDetailResponse> getProductDetail(
            @PathVariable Integer id,
            Authentication authentication) {

        Integer userId = null;
        if (authentication != null && authentication.getPrincipal() instanceof User) {
            userId = ((User) authentication.getPrincipal()).getId();
        }

        return productService.getProductDetailById(id, userId)
                .map(product -> ResponseEntity.ok(product))
                .orElse(ResponseEntity.notFound().build());
    }
    
    @PreAuthorize("hasAuthority('PRODUCT:WRITE')")
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
    
    @PreAuthorize("hasAuthority('PRODUCT:UPDATE')")
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
    
    @PreAuthorize("hasAuthority('PRODUCT:DELETE')")
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
    
    @PreAuthorize("hasAuthority('PRODUCT:UPDATE')")
    @PatchMapping("/{id}/restore")
    public ResponseEntity<Void> restoreProduct(@PathVariable Integer id) {
        productService.restoreProduct(id);
        return ResponseEntity.ok().build();
    }
    
    @PreAuthorize("hasAuthority('PRODUCT:READ')")
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