package vn.techbox.techbox_store.product.controller;

import lombok.RequiredArgsConstructor;
import org.springframework.data.domain.Page;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.web.bind.annotation.*;

import vn.techbox.techbox_store.cloudinary.service.CloudinaryService;
import vn.techbox.techbox_store.product.dto.productDto.ProductDetailResponse;
import vn.techbox.techbox_store.product.dto.productDto.ProductFilterRequest;
import vn.techbox.techbox_store.product.dto.productDto.ProductListResponse;
import vn.techbox.techbox_store.product.dto.productDto.ProductManagementDetailResponse;
import vn.techbox.techbox_store.product.dto.productDto.ProductManagementListResponse;
import vn.techbox.techbox_store.product.dto.productDto.ProductResponse;
import vn.techbox.techbox_store.product.dto.productDto.ProductUpdateRequest;
import vn.techbox.techbox_store.product.dto.productDto.ProductWithAttributesRequest;
import vn.techbox.techbox_store.product.model.ProductStatus;
import vn.techbox.techbox_store.product.service.ProductService;

import java.io.IOException;
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

    @GetMapping("/by-spus")
    public ResponseEntity<Page<ProductListResponse>> getProductsBySpus(
            @RequestParam List<String> spus,
            @RequestParam(defaultValue = "0") int page,
            @RequestParam(defaultValue = "20") int size) {

        Page<ProductListResponse> products = productService.getProductsBySpus(spus, page, size);
        return ResponseEntity.ok(products);
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
     * Create a new product with attributes using JSON (alternative to multipart endpoint)
     * POST /admin/products/json
     * Image URLs and public IDs must be pre-uploaded to Cloudinary
     */
    @PreAuthorize("hasAuthority('PRODUCT:WRITE')")
    @PostMapping(consumes = "application/json")
    public ResponseEntity<?> createProductJson(@RequestBody ProductWithAttributesRequest request) {
        try {
            // Create product with attributes in single transaction
            ProductResponse createdProduct = productService.createProductWithAttributes(request);

            return ResponseEntity.status(HttpStatus.CREATED).body(createdProduct);

        } catch (IllegalArgumentException e) {
            return ResponseEntity.status(HttpStatus.BAD_REQUEST)
                    .body(Map.of("error", e.getMessage(), "type", "VALIDATION_ERROR"));
        } catch (Exception e) {
            return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR)
                    .body(Map.of("error", "Failed to create product with attributes: " + e.getMessage(), "type", "INTERNAL_ERROR"));
        }
    }

    /**
     * Update an existing product
     * PUT /api/products/{id}
     * Supports partial updates - only provided fields will be updated
     */
    @PreAuthorize("hasAuthority('PRODUCT:UPDATE')")
    @PutMapping("/{id}")
    public ResponseEntity<?> updateProduct(
            @PathVariable Integer id,
            @RequestBody ProductUpdateRequest request) {

        try {
            // Get current product to check existing image
            ProductResponse currentProduct = productService.getProductById(id)
                    .orElseThrow(() -> new IllegalArgumentException("Product not found with id: " + id));

            // Handle image operations
            if (request.getImageUrl() != null && request.getImagePublicId() != null) {
                // Delete old image if exists
                if (currentProduct.getImagePublicId() != null) {
                    try {
                        cloudinaryService.deleteFile(currentProduct.getImagePublicId());
                    } catch (IOException e) {
                        // Log error but continue
                        System.err.println("Failed to delete old image from Cloudinary: " + currentProduct.getImagePublicId());
                    }
                }
                // New image is already provided in request
            } else if (request.getImageUrl() == null && request.getImagePublicId() == null) {
                // Keep existing image
                request.setImageUrl(currentProduct.getImageUrl());
                request.setImagePublicId(currentProduct.getImagePublicId());
            }

            ProductResponse updatedProduct = productService.updateProduct(id, request);
            return ResponseEntity.ok(updatedProduct);

        } catch (IllegalArgumentException e) {
            return ResponseEntity.status(HttpStatus.BAD_REQUEST)
                    .body(Map.of("error", e.getMessage(), "type", "VALIDATION_ERROR"));
        } catch (Exception e) {
            return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR)
                    .body(Map.of("error", "Failed to update product: " + e.getMessage(), "type", "INTERNAL_ERROR"));
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
            @RequestBody Map<Integer, String> attributes) {
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