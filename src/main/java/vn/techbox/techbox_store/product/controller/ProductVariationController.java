package vn.techbox.techbox_store.product.controller;

import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.multipart.MultipartFile;
import vn.techbox.techbox_store.cloudinary.service.CloudinaryService;
import vn.techbox.techbox_store.product.dto.ProductVariationCreateRequest;
import vn.techbox.techbox_store.product.dto.ProductVariationResponse;
import vn.techbox.techbox_store.product.dto.ProductVariationUpdateRequest;
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
    
    @GetMapping
    public ResponseEntity<List<ProductVariationResponse>> getAllProductVariations(
            @RequestParam(defaultValue = "false") boolean includeDeleted) {
        List<ProductVariationResponse> variations = includeDeleted 
                ? productVariationService.getAllProductVariations() 
                : productVariationService.getAllActiveProductVariations();
        return ResponseEntity.ok(variations);
    }
    
    @GetMapping("/{id}")
    public ResponseEntity<ProductVariationResponse> getProductVariationById(
            @PathVariable Integer id,
            @RequestParam(defaultValue = "false") boolean includeDeleted) {
        return (includeDeleted 
                ? productVariationService.getProductVariationById(id) 
                : productVariationService.getActiveProductVariationById(id))
                .map(variation -> ResponseEntity.ok(variation))
                .orElse(ResponseEntity.notFound().build());
    }
    
    @PreAuthorize("hasAuthority('PRODUCT_WRITE')")
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
    
    @PreAuthorize("hasAuthority('PRODUCT_UPDATE')")
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
    
    @PreAuthorize("hasAuthority('PRODUCT_DELETE')")
    @DeleteMapping("/{id}")
    public ResponseEntity<Void> deleteProductVariation(@PathVariable Integer id) {
        productVariationService.deleteProductVariation(id);
        return ResponseEntity.noContent().build();
    }
    
    @PreAuthorize("hasAuthority('PRODUCT_UPDATE')")
    @PatchMapping("/{id}/restore")
    public ResponseEntity<Void> restoreProductVariation(@PathVariable Integer id) {
        productVariationService.restoreProductVariation(id);
        return ResponseEntity.ok().build();
    }
    
    @GetMapping("/product/{productId}")
    public ResponseEntity<List<ProductVariationResponse>> getVariationsByProduct(@PathVariable Integer productId) {
        List<ProductVariationResponse> variations = productVariationService.getVariationsByProductId(productId);
        return ResponseEntity.ok(variations);
    }
    
    @GetMapping("/in-stock")
    public ResponseEntity<List<ProductVariationResponse>> getInStockVariations(
            @RequestParam(required = false) Integer productId) {
        List<ProductVariationResponse> variations = productId != null
                ? productVariationService.getInStockVariationsByProductId(productId)
                : productVariationService.getInStockVariations();
        return ResponseEntity.ok(variations);
    }
    

    @PreAuthorize("hasAuthority('PRODUCT_REPORT')")
    @GetMapping("/low-stock")
    public ResponseEntity<List<ProductVariationResponse>> getLowStockVariations(
            @RequestParam(defaultValue = "10") Integer threshold) {
        List<ProductVariationResponse> variations = productVariationService.getLowStockVariations(threshold);
        return ResponseEntity.ok(variations);
    }
    
    @GetMapping("/sku/{sku}")
    public ResponseEntity<ProductVariationResponse> getVariationBySku(@PathVariable String sku) {
        return productVariationService.getVariationBySku(sku)
                .map(variation -> ResponseEntity.ok(variation))
                .orElse(ResponseEntity.notFound().build());
    }
    
    @PreAuthorize("hasAuthority('PRODUCT_UPDATE')")
    @PatchMapping("/{id}/stock")
    public ResponseEntity<ProductVariationResponse> updateStock(
            @PathVariable Integer id, 
            @RequestParam Integer stockQuantity) {
        ProductVariationResponse updatedVariation = productVariationService.updateStock(id, stockQuantity);
        return ResponseEntity.ok(updatedVariation);
    }
    
    @PreAuthorize("hasAuthority('PRODUCT_READ')")
    @GetMapping("/exists")
    public ResponseEntity<Boolean> checkSkuExists(
            @RequestParam String sku,
            @RequestParam(required = false) Integer excludeId) {
        boolean exists = excludeId != null 
                ? productVariationService.existsBySkuAndIdNot(sku, excludeId)
                : productVariationService.existsBySku(sku);
        return ResponseEntity.ok(exists);
    }
}