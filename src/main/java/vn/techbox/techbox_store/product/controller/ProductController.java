package vn.techbox.techbox_store.product.controller;

import com.fasterxml.jackson.core.type.TypeReference;
import com.fasterxml.jackson.databind.ObjectMapper;
import lombok.RequiredArgsConstructor;
import org.springframework.data.domain.Page;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.multipart.MultipartFile;

import vn.techbox.techbox_store.cloudinary.service.CloudinaryService;
import vn.techbox.techbox_store.product.dto.productDto.ProductCreateRequest;
import vn.techbox.techbox_store.product.dto.productDto.ProductDetailResponse;
import vn.techbox.techbox_store.product.dto.productDto.ProductFilterRequest;
import vn.techbox.techbox_store.product.dto.productDto.ProductListResponse;
import vn.techbox.techbox_store.product.dto.productDto.ProductManagementDetailResponse;
import vn.techbox.techbox_store.product.dto.productDto.ProductManagementListResponse;
import vn.techbox.techbox_store.product.dto.productDto.ProductResponse;
import vn.techbox.techbox_store.product.dto.productDto.ProductUpdateRequest;
import vn.techbox.techbox_store.product.model.ProductStatus;
import vn.techbox.techbox_store.product.service.ProductService;

import java.io.IOException;
import java.util.HashMap;
import java.util.List;
import java.util.Map;


/**
 * Public Product Controller - Read-only operations
 * Handles product browsing, filtering, and details for public users
 */
@RestController
@RequestMapping("/products")
@RequiredArgsConstructor
public class ProductController {
    
    private final ProductService productService;
    private final CloudinaryService cloudinaryService;


    // ============================================
    // public endpoints for product browsing and details

    @GetMapping()
    public ResponseEntity<Page<ProductListResponse>> filterProducts(
            @RequestParam(required = false) String name,
            @RequestParam(required = false) Integer brandId,
            @RequestParam(required = false) Integer categoryId,
            @RequestParam(required = false) List<String> attributes,
            @RequestParam(required = false) java.math.BigDecimal minPrice,
            @RequestParam(required = false) java.math.BigDecimal maxPrice,
            @RequestParam(required = false) Double minRating,
            @RequestParam(required = false) Integer campaignId,
            @RequestParam(defaultValue = "id") String sortBy,
            @RequestParam(defaultValue = "ASC") String sortDirection,
            @RequestParam(defaultValue = "0") int page,
            @RequestParam(defaultValue = "20") int size) {
                
        ProductFilterRequest filterRequest = ProductFilterRequest.builder()
                .name(name)
                .brandId(brandId)
                .categoryId(categoryId)
                .attributes(attributes)
                .minPrice(minPrice)
                .maxPrice(maxPrice)
                .minRating(minRating)
                .campaignId(campaignId)
                .sortBy(sortBy)
                .sortDirection(sortDirection)
                .page(page)
                .size(size)
                .build();
        
        Page<ProductListResponse> products = productService.filterProducts(filterRequest);
        return ResponseEntity.ok(products);
    }

    @GetMapping("/{id}")
    public ResponseEntity<ProductDetailResponse> getProductDetail(
            @PathVariable Integer id) {

        return productService.getProductDetailById(id)
                .map(product -> ResponseEntity.ok(product))
                .orElse(ResponseEntity.notFound().build());
    }

    // end of public endpoints
    // ============================================
    




    // ============================================
    // management endpoints for products
    @PreAuthorize("hasAuthority('PRODUCT:READ')")
    @GetMapping("/management")
    public ResponseEntity<Page<ProductManagementListResponse>> getProductsForManagement(
            @RequestParam(required = false) String status,
            @RequestParam(required = false) String name,
            @RequestParam(required = false) String spu,
            @RequestParam(required = false) Integer brandId,
            @RequestParam(required = false) Integer categoryId,
            @RequestParam(required = false) List<String> attributes,
            @RequestParam(required = false) java.math.BigDecimal minPrice,
            @RequestParam(required = false) java.math.BigDecimal maxPrice,
            @RequestParam(required = false) Double minRating,
            @RequestParam(required = false) Integer campaignId,
            @RequestParam(defaultValue = "id") String sortBy,
            @RequestParam(defaultValue = "ASC") String sortDirection,
            @RequestParam(defaultValue = "0") int page,
            @RequestParam(defaultValue = "20") int size) {

        // Convert status string to enum
        ProductStatus productStatus = null;
        if (status != null && !status.trim().isEmpty()) {
            try {
                productStatus = ProductStatus.valueOf(status.toUpperCase());
            } catch (IllegalArgumentException e) {
                // Invalid status, leave as null (no filter)
            }
        }
                
        ProductFilterRequest filterRequest = ProductFilterRequest.builder()
                .status(productStatus)
                .name(name)
                .spu(spu)
                .brandId(brandId)
                .categoryId(categoryId)
                .attributes(attributes)
                .minPrice(minPrice)
                .maxPrice(maxPrice)
                .minRating(minRating)
                .campaignId(campaignId)
                .sortBy(sortBy)
                .sortDirection(sortDirection)
                .page(page)
                .size(size)
                .build();
        
        Page<ProductManagementListResponse> products = productService.filterProductsForManagement(filterRequest);
        return ResponseEntity.ok(products);
    }



    /**
     * Get full product details for management
     * Includes all information for admin to view and edit
     * Does NOT filter soft deleted products
     * Does NOT include variations (use separate variation API)
     */
    // @PreAuthorize("hasAuthority('PRODUCT:READ')")
    @GetMapping("/management/{id}")
    public ResponseEntity<?> getProductForManagement(@PathVariable Integer id) {
        try {
            ProductManagementDetailResponse response = productService.getProductForManagement(id);
            return ResponseEntity.ok(response);
        } catch (IllegalArgumentException e) {
            return ResponseEntity.status(HttpStatus.NOT_FOUND)
                    .body(Map.of("error", e.getMessage()));
        }
    }
    
    /**
     * Publish product - Change status to PUBLISHED
     * Requirements:
     * - Product must have at least 1 variation
     * - Updates product's display prices
     */
    @PreAuthorize("hasAuthority('PRODUCT:UPDATE')")
    @PutMapping("/{id}/publish")
    public ResponseEntity<?> publishProduct(@PathVariable Integer id) {
        try {
            ProductResponse response = productService.publishProduct(id);
            return ResponseEntity.ok(response);
        } catch (IllegalArgumentException e) {
            return ResponseEntity.status(HttpStatus.NOT_FOUND)
                    .body(Map.of("error", e.getMessage()));
        } catch (IllegalStateException e) {
            return ResponseEntity.status(HttpStatus.BAD_REQUEST)
                    .body(Map.of("error", e.getMessage()));
        }
    }
    
    /**
     * Draft product - Change status to DRAFT
     * Can restore deleted product or hide published product
     */
    @PreAuthorize("hasAuthority('PRODUCT:UPDATE')")
    @PutMapping("/{id}/draft")
    public ResponseEntity<?> draftProduct(@PathVariable Integer id) {
        try {
            ProductResponse response = productService.draftProduct(id);
            return ResponseEntity.ok(response);
        } catch (IllegalArgumentException e) {
            return ResponseEntity.status(HttpStatus.NOT_FOUND)
                    .body(Map.of("error", e.getMessage()));
        }
    }
    
    /**
     * Soft delete product - Change status to DELETED
     * Requirements:
     * - Product must be in DRAFT or PUBLISHED status
     */
    @PreAuthorize("hasAuthority('PRODUCT:DELETE')")
    @DeleteMapping("/{id}")
    public ResponseEntity<?> deleteProduct(@PathVariable Integer id) {
        try {
            ProductResponse response = productService.deleteProductSoft(id);
            return ResponseEntity.ok(response);
        } catch (IllegalArgumentException e) {
            return ResponseEntity.status(HttpStatus.NOT_FOUND)
                    .body(Map.of("error", e.getMessage()));
        } catch (IllegalStateException e) {
            return ResponseEntity.status(HttpStatus.BAD_REQUEST)
                    .body(Map.of("error", e.getMessage()));
        }
    }

    /**
     * Hard delete product - Permanently remove a product by ID
     * Requirements:
     * - Product must be soft deleted (status = DELETED)
     */
    @PreAuthorize("hasAuthority('PRODUCT:DELETE')")
    @DeleteMapping("/hard/{id}")
    public ResponseEntity<?> deleteProductHard(@PathVariable Integer id) {
        try {
            productService.deleteProductHard(id);
            return ResponseEntity.ok(Map.of("message", "Product deleted successfully"));
        } catch (IllegalArgumentException e) {
            return ResponseEntity.status(HttpStatus.NOT_FOUND).body(Map.of("error", e.getMessage()));
        }
    }

    /**
     * Create a new product
     * POST /admin/products
     */
    @PreAuthorize("hasAuthority('PRODUCT:WRITE')")
    @PostMapping(consumes = {"multipart/form-data"})
    public ResponseEntity<?> createProduct(
            @RequestParam("name") String name,
            @RequestParam(value = "description", required = false) String description,
            @RequestParam(value = "categoryId", required = false) Integer categoryId,
            @RequestParam(value = "brandId", required = false) Integer brandId,
            @RequestParam(value = "attributes", required = false) String attributesJson,
            @RequestParam(value = "warrantyMonths", required = false) Integer warrantyMonths,
            @RequestParam(value = "image", required = false) MultipartFile image) {

        try {
            // Parse attributes JSON
            Map<String, String> attributes = new HashMap<>();
            if (attributesJson != null && !attributesJson.isEmpty()) {
                try {
                    attributes = new ObjectMapper().readValue(attributesJson, new TypeReference<>() {});
                } catch (IOException e) {
                    return ResponseEntity.status(HttpStatus.BAD_REQUEST)
                            .body(Map.of("error", "Invalid attributes JSON: " + e.getMessage()));
                }
            }

            // Build product request
            ProductCreateRequest request = ProductCreateRequest.builder()
                    .name(name)
                    .description(description)
                    .categoryId(categoryId)
                    .brandId(brandId)
                    .warrantyMonths(warrantyMonths)
                    .status(ProductStatus.DRAFT) // Default status to DRAFT
                    .build();

            // Upload image to Cloudinary if provided
            if (image != null && !image.isEmpty()) {
                @SuppressWarnings("unchecked")
                Map<String, Object> uploadResult = (Map<String, Object>) cloudinaryService.uploadFile(image, "product_images");
                request.setImageUrl((String) uploadResult.get("secure_url"));
                request.setImagePublicId((String) uploadResult.get("public_id"));
            }

            // Save product first
            ProductResponse createdProduct = productService.createProduct(request);

            // Add attributes after product is created
            if (!attributes.isEmpty()) {
                productService.addAttributesToProduct(createdProduct.getId(), attributes);
            }

            return ResponseEntity.status(HttpStatus.CREATED).body(createdProduct);

        } catch (IOException e) {
            return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR)
                    .body(Map.of("error", "Failed to upload image: " + e.getMessage()));
        } catch (Exception e) {
            return ResponseEntity.status(HttpStatus.BAD_REQUEST)
                    .body(Map.of("error", "Failed to create product: " + e.getMessage()));
        }
    }
    
    /**
     * Update an existing product
     * PUT /admin/products/{id}
     */
    @PreAuthorize("hasAuthority('PRODUCT:UPDATE')")
    @PutMapping(value = "/{id}", consumes = {"multipart/form-data"})
    public ResponseEntity<?> updateProduct(
            @PathVariable Integer id,
            @RequestParam(value = "name", required = false) String name,
            @RequestParam(value = "description", required = false) String description,
            @RequestParam(value = "categoryId", required = false) Integer categoryId,
            @RequestParam(value = "brandId", required = false) Integer brandId,
            @RequestParam(value = "attributes", required = false) String attributesJson,
            @RequestParam(value = "warrantyMonths", required = false) Integer warrantyMonths,
            @RequestParam(value = "image", required = false) MultipartFile image,
            @RequestParam(value = "deleteImage", required = false, defaultValue = "false") boolean deleteImage) {
        
        try {
            // Parse attributes JSON
            Map<String, String> attributes = new HashMap<>();
            if (attributesJson != null && !attributesJson.isEmpty()) {
                attributes = new ObjectMapper().readValue(attributesJson, new TypeReference<>() {});
            }

            // Get current product to check existing image
            ProductResponse currentProduct = productService.getProductById(id)
                    .orElseThrow(() -> new RuntimeException("Product not found"));

            ProductUpdateRequest request = ProductUpdateRequest.builder()
                    .name(name)
                    .description(description)
                    .categoryId(categoryId)
                    .brandId(brandId)
                    .attributes(attributes)
                    .warrantyMonths(warrantyMonths)
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

    /**
     * Add attributes to an existing product
     * POST /admin/products/{id}/attributes
     */
    @PreAuthorize("hasAuthority('PRODUCT:UPDATE')")
    @PostMapping("/{id}/attributes")
    public ResponseEntity<?> addAttributesToProduct(
            @PathVariable Integer id,
            @RequestBody Map<String, String> attributes) {
        try {
            productService.addAttributesToProduct(id, attributes);
            return ResponseEntity.ok(Map.of("message", "Attributes added successfully"));
        } catch (IllegalArgumentException e) {
            return ResponseEntity.status(HttpStatus.BAD_REQUEST).body(Map.of("error", e.getMessage()));
        }
    }

    // end of management endpoints
    // ============================================

}