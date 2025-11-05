package vn.techbox.techbox_store.product.service.impl;

import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import vn.techbox.techbox_store.product.dto.attributeDto.AttributeCreateRequest;
import vn.techbox.techbox_store.product.dto.attributeDto.AttributeResponse;
import vn.techbox.techbox_store.product.dto.attributeDto.AttributeUpdateRequest;
import vn.techbox.techbox_store.product.model.Attribute;
import vn.techbox.techbox_store.product.repository.AttributeRepository;
import vn.techbox.techbox_store.product.service.AttributeService;

import java.util.List;
import java.util.Optional;
import java.util.stream.Collectors;

@Service
@RequiredArgsConstructor
@Transactional
public class AttributeServiceImpl implements AttributeService {
    
    private final AttributeRepository attributeRepository;
    
    @Override
    @Transactional(readOnly = true)
    public List<AttributeResponse> getAllAttributes() {
        return attributeRepository.findAll()
                .stream()
                .map(this::convertToResponse)
                .collect(Collectors.toList());
    }
    
    @Override
    @Transactional(readOnly = true)
    public Optional<AttributeResponse> getAttributeById(Integer id) {
        return attributeRepository.findById(id)
                .map(this::convertToResponse);
    }
    
    @Override
    public AttributeResponse createAttribute(AttributeCreateRequest request) {
        if (existsByName(request.getName())) {
            throw new IllegalArgumentException("Attribute name already exists: " + request.getName());
        }
        
        Attribute attribute = Attribute.builder()
                .name(request.getName())
                .build();
        
        Attribute savedAttribute = attributeRepository.save(attribute);
        return convertToResponse(savedAttribute);
    }
    
    @Override
    public AttributeResponse updateAttribute(Integer id, AttributeUpdateRequest request) {
        Attribute attribute = attributeRepository.findById(id)
                .orElseThrow(() -> new IllegalArgumentException("Attribute not found with id: " + id));
        
        if (request.getName() != null) {
            if (existsByNameAndIdNot(request.getName(), id)) {
                throw new IllegalArgumentException("Attribute name already exists: " + request.getName());
            }
            attribute.setName(request.getName());
        }
        
        Attribute updatedAttribute = attributeRepository.save(attribute);
        return convertToResponse(updatedAttribute);
    }
    
    @Override
    public void deleteAttribute(Integer id) {
        Attribute attribute = attributeRepository.findById(id)
                .orElseThrow(() -> new IllegalArgumentException("Attribute not found with id: " + id));
        
        // Hard delete for attributes (no soft delete needed)
        attributeRepository.delete(attribute);
    }
    
    @Override
    @Transactional(readOnly = true)
    public List<AttributeResponse> searchAttributesByName(String keyword) {
        return attributeRepository.searchByName(keyword)
                .stream()
                .map(this::convertToResponse)
                .collect(Collectors.toList());
    }
    
    @Override
    @Transactional(readOnly = true)
    public boolean existsByName(String name) {
        return attributeRepository.existsByName(name);
    }
    
    @Override
    @Transactional(readOnly = true)
    public boolean existsByNameAndIdNot(String name, Integer id) {
        return attributeRepository.existsByNameAndIdNot(name, id);
    }
    
    private AttributeResponse convertToResponse(Attribute attribute) {
        return AttributeResponse.builder()
                .id(attribute.getId())
                .name(attribute.getName())
                .build();
    }
}