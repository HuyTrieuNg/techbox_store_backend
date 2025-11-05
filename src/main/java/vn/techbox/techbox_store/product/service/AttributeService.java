package vn.techbox.techbox_store.product.service;

import vn.techbox.techbox_store.product.dto.attributeDto.AttributeCreateRequest;
import vn.techbox.techbox_store.product.dto.attributeDto.AttributeResponse;
import vn.techbox.techbox_store.product.dto.attributeDto.AttributeUpdateRequest;

import java.util.List;
import java.util.Optional;

public interface AttributeService {
    
    List<AttributeResponse> getAllAttributes();
    
    Optional<AttributeResponse> getAttributeById(Integer id);
    
    AttributeResponse createAttribute(AttributeCreateRequest request);
    
    AttributeResponse updateAttribute(Integer id, AttributeUpdateRequest request);
    
    void deleteAttribute(Integer id);
    
    List<AttributeResponse> searchAttributesByName(String keyword);
    
    boolean existsByName(String name);
    
    boolean existsByNameAndIdNot(String name, Integer id);
}