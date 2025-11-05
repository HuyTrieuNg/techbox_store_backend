package vn.techbox.techbox_store.product.service;

import vn.techbox.techbox_store.product.dto.brandDto.BrandCreateRequest;
import vn.techbox.techbox_store.product.dto.brandDto.BrandResponse;
import vn.techbox.techbox_store.product.dto.brandDto.BrandUpdateRequest;

import java.util.List;
import java.util.Optional;

public interface BrandService {
    
    List<BrandResponse> getAllBrands();
    
    Optional<BrandResponse> getBrandById(Integer id);
    
    BrandResponse createBrand(BrandCreateRequest request);
    
    BrandResponse updateBrand(Integer id, BrandUpdateRequest request);
    
    void deleteBrand(Integer id);
    
    boolean existsByName(String name);
    
    boolean existsByNameAndIdNot(String name, Integer id);
}