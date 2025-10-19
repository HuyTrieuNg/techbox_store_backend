package vn.techbox.techbox_store.product.controller;

import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.web.bind.annotation.*;
import vn.techbox.techbox_store.product.dto.BrandCreateRequest;
import vn.techbox.techbox_store.product.dto.BrandResponse;
import vn.techbox.techbox_store.product.dto.BrandUpdateRequest;
import vn.techbox.techbox_store.product.service.BrandService;

import java.util.List;

@RestController
@RequestMapping("/brands")
@RequiredArgsConstructor
public class BrandController {
    
    private final BrandService brandService;
    
    @GetMapping
    public ResponseEntity<List<BrandResponse>> getAllBrands() {
        List<BrandResponse> brands = brandService.getAllBrands();
        return ResponseEntity.ok(brands);
    }
    
    @GetMapping("/{id}")
    public ResponseEntity<BrandResponse> getBrandById(@PathVariable Integer id) {
        return brandService.getBrandById(id)
                .map(brand -> ResponseEntity.ok(brand))
                .orElse(ResponseEntity.notFound().build());
    }
    
    @PreAuthorize("hasAuthority('PRODUCT:WRITE')")
    @PostMapping
    public ResponseEntity<BrandResponse> createBrand(@Valid @RequestBody BrandCreateRequest request) {
        try {
            BrandResponse createdBrand = brandService.createBrand(request);
            return ResponseEntity.status(HttpStatus.CREATED).body(createdBrand);
        } catch (RuntimeException e) {
            return ResponseEntity.badRequest().build();
        }
    }
    
    @PreAuthorize("hasAuthority('PRODUCT:UPDATE')")
    @PutMapping("/{id}")
    public ResponseEntity<BrandResponse> updateBrand(
            @PathVariable Integer id,
            @Valid @RequestBody BrandUpdateRequest request) {
        try {
            BrandResponse updatedBrand = brandService.updateBrand(id, request);
            return ResponseEntity.ok(updatedBrand);
        } catch (RuntimeException e) {
            return ResponseEntity.badRequest().build();
        }
    }
    
    @PreAuthorize("hasAuthority('PRODUCT:DELETE')")
    @DeleteMapping("/{id}")
    public ResponseEntity<Void> deleteBrand(@PathVariable Integer id) {
        try {
            brandService.deleteBrand(id);
            return ResponseEntity.noContent().build();
        } catch (RuntimeException e) {
            return ResponseEntity.badRequest().build();
        }
    }
    
    @PreAuthorize("hasAuthority('PRODUCT:READ')")
    @GetMapping("/exists")
    public ResponseEntity<Boolean> checkBrandNameExists(@RequestParam String name) {
        boolean exists = brandService.existsByName(name);
        return ResponseEntity.ok(exists);
    }
}