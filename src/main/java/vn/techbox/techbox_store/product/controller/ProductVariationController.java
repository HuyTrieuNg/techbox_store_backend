package vn.techbox.techbox_store.product.controller;

import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;
import vn.techbox.techbox_store.product.dto.ProductVariationCreateRequest;
import vn.techbox.techbox_store.product.dto.ProductVariationResponse;
import vn.techbox.techbox_store.product.dto.ProductVariationUpdateRequest;
import vn.techbox.techbox_store.product.service.ProductVariationService;

import java.util.List;

@RestController
@RequestMapping("/api/product-variations")
@RequiredArgsConstructor
public class ProductVariationController {
    
    private final ProductVariationService productVariationService;
    
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
    
    @PostMapping
    public ResponseEntity<ProductVariationResponse> createProductVariation(
            @Valid @RequestBody ProductVariationCreateRequest request) {
        ProductVariationResponse createdVariation = productVariationService.createProductVariation(request);
        return ResponseEntity.status(HttpStatus.CREATED).body(createdVariation);
    }
    
    @PutMapping("/{id}")
    public ResponseEntity<ProductVariationResponse> updateProductVariation(
            @PathVariable Integer id, 
            @Valid @RequestBody ProductVariationUpdateRequest request) {
        ProductVariationResponse updatedVariation = productVariationService.updateProductVariation(id, request);
        return ResponseEntity.ok(updatedVariation);
    }
    
    @DeleteMapping("/{id}")
    public ResponseEntity<Void> deleteProductVariation(@PathVariable Integer id) {
        productVariationService.deleteProductVariation(id);
        return ResponseEntity.noContent().build();
    }
    
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
    
    @PatchMapping("/{id}/stock")
    public ResponseEntity<ProductVariationResponse> updateStock(
            @PathVariable Integer id, 
            @RequestParam Integer quantity) {
        ProductVariationResponse updatedVariation = productVariationService.updateStock(id, quantity);
        return ResponseEntity.ok(updatedVariation);
    }
    
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