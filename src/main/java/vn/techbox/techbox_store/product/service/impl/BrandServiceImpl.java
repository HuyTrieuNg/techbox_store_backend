package vn.techbox.techbox_store.product.service.impl;

import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import vn.techbox.techbox_store.product.dto.brandDto.BrandCreateRequest;
import vn.techbox.techbox_store.product.dto.brandDto.BrandResponse;
import vn.techbox.techbox_store.product.dto.brandDto.BrandUpdateRequest;
import vn.techbox.techbox_store.product.model.Brand;
import vn.techbox.techbox_store.product.repository.BrandRepository;
import vn.techbox.techbox_store.product.service.BrandService;

import java.util.List;
import java.util.Optional;
import java.util.stream.Collectors;

@Service
@RequiredArgsConstructor
@Transactional
public class BrandServiceImpl implements BrandService {
    
    private final BrandRepository brandRepository;
    
    @Override
    @Transactional(readOnly = true)
    public List<BrandResponse> getAllBrands() {
        return brandRepository.findAll()
                .stream()
                .map(this::convertToResponse)
                .collect(Collectors.toList());
    }
    
    @Override
    @Transactional(readOnly = true)
    public Optional<BrandResponse> getBrandById(Integer id) {
        return brandRepository.findById(id)
                .map(this::convertToResponse);
    }
    
    @Override
    public BrandResponse createBrand(BrandCreateRequest request) {
        // Check if brand name already exists
        if (brandRepository.existsByName(request.getName())) {
            throw new RuntimeException("Brand with name '" + request.getName() + "' already exists");
        }
        
        Brand brand = Brand.builder()
                .name(request.getName())
                .build();
        
        Brand savedBrand = brandRepository.save(brand);
        return convertToResponse(savedBrand);
    }
    
    @Override
    public BrandResponse updateBrand(Integer id, BrandUpdateRequest request) {
        Brand brand = brandRepository.findById(id)
                .orElseThrow(() -> new RuntimeException("Brand not found with id: " + id));
        
        // Check if brand name already exists (excluding current brand)
        if (brandRepository.existsByNameAndIdNot(request.getName(), id)) {
            throw new RuntimeException("Brand with name '" + request.getName() + "' already exists");
        }
        
        brand.setName(request.getName());
        
        Brand updatedBrand = brandRepository.save(brand);
        return convertToResponse(updatedBrand);
    }
    
    @Override
    public void deleteBrand(Integer id) {
        Brand brand = brandRepository.findById(id)
                .orElseThrow(() -> new RuntimeException("Brand not found with id: " + id));
        
        brandRepository.delete(brand);
    }
    
    @Override
    @Transactional(readOnly = true)
    public boolean existsByName(String name) {
        return brandRepository.existsByName(name);
    }
    
    @Override
    @Transactional(readOnly = true)
    public boolean existsByNameAndIdNot(String name, Integer id) {
        return brandRepository.existsByNameAndIdNot(name, id);
    }
    
    private BrandResponse convertToResponse(Brand brand) {
        return BrandResponse.builder()
                .id(brand.getId())
                .name(brand.getName())
                .createdAt(brand.getCreatedAt())
                .updatedAt(brand.getUpdatedAt())
                .build();
    }
}