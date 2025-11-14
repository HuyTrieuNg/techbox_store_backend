package vn.techbox.techbox_store.product.controller;

import com.fasterxml.jackson.databind.ObjectMapper;
import lombok.RequiredArgsConstructor;
import org.springframework.data.domain.Page;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;
import vn.techbox.techbox_store.product.dto.productDto.ProductDetailResponse;
import vn.techbox.techbox_store.product.dto.productDto.ProductFilterRequest;
import vn.techbox.techbox_store.product.dto.productDto.ProductListResponse;
import vn.techbox.techbox_store.product.service.ProductService;

import org.springframework.web.multipart.MultipartFile;

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
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestParam;


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
    private final ObjectMapper objectMapper;


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

    /**
     * Create a new product with attributes in a single transaction
     * POST /admin/products
     * This endpoint ensures atomicity: either product + attributes are created together, or nothing
     */
    // @PreAuthorize("hasAuthority('PRODUCT:WRITE')")
    // @PostMapping(consumes = {"multipart/form-data"})
    // public ResponseEntity<?> createProduct(
    //         @RequestParam("productData") String productDataJson,
    //         @RequestParam(value = "image", required = false) MultipartFile image) {

    //     try {
    //         // Parse product data JSON
    //         ProductWithAttributesRequest request = objectMapper.readValue(productDataJson, ProductWithAttributesRequest.class);

    //         // Handle image upload if a file is provided
    //         if (image != null && !image.isEmpty()) {
    //             @SuppressWarnings("unchecked")
    //             Map<String, Object> uploadResult = (Map<String, Object>) cloudinaryService.uploadFile(image, "product_images");
    //             request.setImageUrl((String) uploadResult.get("secure_url"));
    //             request.setImagePublicId((String) uploadResult.get("public_id"));
    //         }

    //         // Create product with attributes in single transaction
    //         ProductResponse createdProduct = productService.createProductWithAttributes(request);

    //         return ResponseEntity.status(HttpStatus.CREATED).body(createdProduct);

    //     } catch (IOException e) {
    //         return ResponseEntity.status(HttpStatus.BAD_REQUEST)
    //                 .body(Map.of("error", "Failed to process request data or upload image: " + e.getMessage()));
    //     } catch (IllegalArgumentException e) {
    //         return ResponseEntity.status(HttpStatus.BAD_REQUEST)
    //                 .body(Map.of("error", e.getMessage()));
    //     } catch (Exception e) {
    //         return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR)
    //                 .body(Map.of("error", "Failed to create product with attributes: " + e.getMessage()));
    //     }
    // }

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