package vn.techbox.techbox_store.product.controller;

import lombok.RequiredArgsConstructor;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.multipart.MultipartFile;
import vn.techbox.techbox_store.cloudinary.service.CloudinaryService;
import vn.techbox.techbox_store.product.dto.productDto.ProductVariationCreateRequest;
import vn.techbox.techbox_store.product.dto.productDto.ProductVariationManagementResponse;
import vn.techbox.techbox_store.product.dto.productDto.ProductVariationResponse;
import vn.techbox.techbox_store.product.dto.productDto.ProductVariationUpdateRequest;
import vn.techbox.techbox_store.product.service.ProductVariationService;

import java.io.IOException;
import java.math.BigDecimal;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

@RestController
@RequestMapping("/product-variations")
@RequiredArgsConstructor
public class ProductVariationController {
    
    private final ProductVariationService productVariationService;
    private final CloudinaryService cloudinaryService;
    
    /**
     * Get all variations by product ID
     * Main API for fetching variations of a specific product
     */
    @GetMapping("/product/{productId}")
    public ResponseEntity<List<ProductVariationResponse>> getVariationsByProduct(@PathVariable Integer productId) {
        List<ProductVariationResponse> variations = productVariationService.getVariationsByProductId(productId);
        return ResponseEntity.ok(variations);
    }
    
    /**
     * Get all variations for management by product ID with optional deleted filter
     * Used for admin/management view and edit
     * 
     * @param productId The ID of the product
     * @param deleted Optional filter:
     *                - Not provided (null): return all variations
     *                - ?deleted=false: return only active variations
     *                - ?deleted=true: return only soft-deleted variations
     * @return List of variations matching the filter criteria
     */

    // @PreAuthorize("hasAuthority('PRODUCT:READ')")
    @GetMapping("/management/product/{productId}")
    public ResponseEntity<List<ProductVariationManagementResponse>> getVariationsForManagement(
            @PathVariable Integer productId,
            @RequestParam(value = "deleted", required = false) Boolean deleted) {
        List<ProductVariationManagementResponse> variations = productVariationService.getVariationsForManagement(productId, deleted);
        return ResponseEntity.ok(variations);
    }
    
    /**
     * Get single variation by ID
     */
    @GetMapping("/{id}")
    public ResponseEntity<ProductVariationResponse> getProductVariationById(@PathVariable Integer id){
        return  productVariationService.getActiveProductVariationById(id)
                .map(variation -> ResponseEntity.ok(variation))
                .orElse(ResponseEntity.notFound().build());
    }
    
    @PreAuthorize("hasAuthority('PRODUCT:WRITE')")
    @PostMapping(consumes = {"multipart/form-data"})
    public ResponseEntity<?> createProductVariation(
        @RequestParam(value = "variationName", required = false) String variationName,
        @RequestParam("productId") Integer productId,
        @RequestParam("price") BigDecimal price,
        @RequestParam(value = "sku", required = false) String sku,
        @RequestParam(value = "images", required = false) MultipartFile[] images) {
        
        try {
        ProductVariationCreateRequest request = ProductVariationCreateRequest.builder()
            .variationName(variationName)
            .productId(productId)
            .price(price)
            .sku(sku)
            .build();
            
            // Upload images to Cloudinary if provided
            List<String> imageUrls = new ArrayList<>();
            List<String> imagePublicIds = new ArrayList<>();
            
            if (images != null && images.length > 0) {
                for (MultipartFile image : images) {
                    if (!image.isEmpty()) {
                        @SuppressWarnings("unchecked")
                        Map<String, Object> uploadResult = (Map<String, Object>) cloudinaryService.uploadFile(image, "product_variation_images");
                        imageUrls.add((String) uploadResult.get("secure_url"));
                        imagePublicIds.add((String) uploadResult.get("public_id"));
                    }
                }
            }
            
            request.setImageUrls(imageUrls);
            request.setImagePublicIds(imagePublicIds);
            
            ProductVariationResponse createdVariation = productVariationService.createProductVariation(request);
            return ResponseEntity.status(HttpStatus.CREATED).body(createdVariation);
            
        } catch (IOException e) {
            Map<String, String> error = new HashMap<>();
            error.put("error", "Failed to upload images: " + e.getMessage());
            return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR).body(error);
        } catch (Exception e) {
            Map<String, String> error = new HashMap<>();
            error.put("error", "Failed to create product variation: " + e.getMessage());
            return ResponseEntity.status(HttpStatus.BAD_REQUEST).body(error);
        }
    }
    
    @PreAuthorize("hasAuthority('PRODUCT:UPDATE')")
    @PutMapping(value = "/{id}", consumes = {"multipart/form-data"})
    public ResponseEntity<?> updateProductVariation(
            @PathVariable Integer id,
            @RequestParam(value = "variationName", required = false) String variationName,
            @RequestParam(value = "price", required = false) BigDecimal price,
            @RequestParam(value = "sku", required = false) String sku,
            @RequestParam(value = "stockQuantity", required = false) Integer stockQuantity,
            @RequestParam(value = "newImages", required = false) MultipartFile[] newImages,
            @RequestParam(value = "deleteImageIds", required = false) List<String> deleteImageIds) {
        
        try {
        ProductVariationUpdateRequest request = ProductVariationUpdateRequest.builder()
                    .variationName(variationName)
                    .price(price)
                    .sku(sku)
                    .deleteImageIds(deleteImageIds)
                    .build();
            
            // Upload new images to Cloudinary if provided
            List<String> newImageUrls = new ArrayList<>();
            List<String> newImagePublicIds = new ArrayList<>();
            
            if (newImages != null && newImages.length > 0) {
                for (MultipartFile image : newImages) {
                    if (!image.isEmpty()) {
                        @SuppressWarnings("unchecked")
                        Map<String, Object> uploadResult = (Map<String, Object>) cloudinaryService.uploadFile(image, "product_variation_images");
                        newImageUrls.add((String) uploadResult.get("secure_url"));
                        newImagePublicIds.add((String) uploadResult.get("public_id"));
                    }
                }
            }
            
            request.setImageUrls(newImageUrls);
            request.setImagePublicIds(newImagePublicIds);
            
            // Delete specified images from Cloudinary
            if (deleteImageIds != null && !deleteImageIds.isEmpty()) {
                for (String publicId : deleteImageIds) {
                    try {
                        cloudinaryService.deleteFile(publicId);
                    } catch (IOException e) {
                        // Log error but continue with update
                        System.err.println("Failed to delete image from Cloudinary: " + publicId);
                    }
                }
            }
            
            ProductVariationResponse updatedVariation = productVariationService.updateProductVariation(id, request);
            return ResponseEntity.ok(updatedVariation);
            
        } catch (IOException e) {
            Map<String, String> error = new HashMap<>();
            error.put("error", "Failed to process images: " + e.getMessage());
            return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR).body(error);
        } catch (Exception e) {
            Map<String, String> error = new HashMap<>();
            error.put("error", "Failed to update product variation: " + e.getMessage());
            return ResponseEntity.status(HttpStatus.BAD_REQUEST).body(error);
        }
    }
    
    @PreAuthorize("hasAuthority('PRODUCT:DELETE')")
    @DeleteMapping("/{id}")
    public ResponseEntity<Void> deleteProductVariation(@PathVariable Integer id) {
        productVariationService.deleteProductVariation(id);
        return ResponseEntity.noContent().build();
    }


    @PreAuthorize("hasAuthority('PRODUCT:DELETE')")
    @DeleteMapping("/hard/{id}")
    public ResponseEntity<Void> deleteProductVariationHard(@PathVariable Integer id) {
        productVariationService.deleteProductVariationHard(id);
        return ResponseEntity.noContent().build();
    }

    
    
    @PreAuthorize("hasAuthority('PRODUCT:UPDATE')")
    @PatchMapping("/{id}/restore")
    public ResponseEntity<Void> restoreProductVariation(@PathVariable Integer id) {
        productVariationService.restoreProductVariation(id);
        return ResponseEntity.ok().build();
    }
    
    /**
     * Get variation by SKU
     * Useful for inventory management
     */
    @GetMapping("/sku/{sku}")
    public ResponseEntity<ProductVariationResponse> getVariationBySku(@PathVariable String sku) {
        return productVariationService.getVariationBySku(sku)
                .map(variation -> ResponseEntity.ok(variation))
                .orElse(ResponseEntity.notFound().build());
    }
    
    /**
     * Update stock quantity
     */
    @PreAuthorize("hasAuthority('PRODUCT:UPDATE')")
    @PatchMapping("/{id}/stock")
    public ResponseEntity<ProductVariationResponse> updateStock(
            @PathVariable Integer id, 
            @RequestParam Integer stockQuantity) {
        ProductVariationResponse updatedVariation = productVariationService.updateStock(id, stockQuantity);
        return ResponseEntity.ok(updatedVariation);
    }
}