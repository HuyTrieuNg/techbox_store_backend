package vn.techbox.techbox_store.product.controller;

import lombok.RequiredArgsConstructor;
import org.springframework.data.domain.Page;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;
import vn.techbox.techbox_store.product.dto.productDto.ProductDetailResponse;
import vn.techbox.techbox_store.product.dto.productDto.ProductFilterRequest;
import vn.techbox.techbox_store.product.dto.productDto.ProductListResponse;
import vn.techbox.techbox_store.product.service.ProductService;

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

    @GetMapping("/manage/{id}")
    public String getMethodName(@PathVariable Integer id) {
        return new String();
    }
    

}