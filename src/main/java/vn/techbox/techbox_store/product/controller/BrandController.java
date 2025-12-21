package vn.techbox.techbox_store.product.controller;

import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.web.bind.annotation.*;
import vn.techbox.techbox_store.product.dto.brandDto.BrandCreateRequest;
import vn.techbox.techbox_store.product.dto.brandDto.BrandResponse;
import vn.techbox.techbox_store.product.dto.brandDto.BrandUpdateRequest;
import vn.techbox.techbox_store.product.service.BrandService;
import vn.techbox.techbox_store.product.exception.BrandDeleteException;

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
    
    @PreAuthorize("hasAuthority('BRAND:WRITE')")
    @PostMapping
    public ResponseEntity<BrandResponse> createBrand(@Valid @RequestBody BrandCreateRequest request) {
        try {
            BrandResponse createdBrand = brandService.createBrand(request);
            return ResponseEntity.status(HttpStatus.CREATED).body(createdBrand);
        } catch (RuntimeException e) {
            return ResponseEntity.badRequest().build();
        }
    }
    
    @PreAuthorize("hasAuthority('BRAND:UPDATE')")
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
    
    @PreAuthorize("hasAuthority('BRAND:DELETE')")
    @DeleteMapping("/{id}")
    public ResponseEntity<Void> deleteBrand(@PathVariable Integer id) {
        try {
            brandService.deleteBrand(id);
            return ResponseEntity.noContent().build();
        } catch (BrandDeleteException e) {
            // Rethrow so GlobalExceptionHandler handles it and returns 409 + message
            throw e;
        } catch (RuntimeException e) {
            return ResponseEntity.badRequest().build();
        }
    }
    
    @PreAuthorize("hasAuthority('BRAND:READ')")
    @GetMapping("/exists")
    public ResponseEntity<Boolean> checkBrandNameExists(@RequestParam String name) {
        boolean exists = brandService.existsByName(name);
        return ResponseEntity.ok(exists);
    }
}