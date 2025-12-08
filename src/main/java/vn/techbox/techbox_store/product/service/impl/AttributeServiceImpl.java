package vn.techbox.techbox_store.product.service.impl;

import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import vn.techbox.techbox_store.product.dto.attributeDto.AttributeCreateRequest;
import vn.techbox.techbox_store.product.dto.attributeDto.AttributeResponse;
import vn.techbox.techbox_store.product.dto.attributeDto.AttributeUpdateRequest;
import vn.techbox.techbox_store.product.mapper.AttributeMapper;
import vn.techbox.techbox_store.product.model.Attribute;
import vn.techbox.techbox_store.product.repository.AttributeRepository;
import vn.techbox.techbox_store.product.repository.ProductAttributeRepository;
import vn.techbox.techbox_store.product.repository.VariationAttributeRepository;
import vn.techbox.techbox_store.product.service.AttributeService;

import java.util.List;
import java.util.Objects;
import java.util.Optional;
import java.util.TreeSet;
import java.util.stream.Collectors;
import java.util.stream.Stream;

@Service
@RequiredArgsConstructor
@Transactional
public class AttributeServiceImpl implements AttributeService {
    
    private final AttributeRepository attributeRepository;
    private final ProductAttributeRepository productAttributeRepository;
    private final VariationAttributeRepository variationAttributeRepository;
    private final AttributeMapper attributeMapper;
    
    @Override
    @Transactional(readOnly = true)
    public List<AttributeResponse> getAllAttributes() {
        return attributeRepository.findAll()
                .stream()
                .map(attributeMapper::toResponse)
                .collect(Collectors.toList());
    }
    
    @Override
    @Transactional(readOnly = true)
    public Optional<AttributeResponse> getAttributeById(Integer id) {
        return attributeRepository.findById(id)
                .map(attributeMapper::toResponse);
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
        return attributeMapper.toResponse(savedAttribute);
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
        return attributeMapper.toResponse(updatedAttribute);
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
                .map(attributeMapper::toResponse)
                .collect(Collectors.toList());
    }


    
    @Override
    @Transactional(readOnly = true)
    public List<String> searchValueById(Integer id, String value) {
        // Normalize value for case-insensitive LIKE match
        String q = value != null ? value.trim() : "";
        // Fetch distinct values from both product attributes and variation attributes
        List<String> productValues = productAttributeRepository.findDistinctValuesByAttributeIdAndValueContaining(id, q);
        List<String> variationValues = variationAttributeRepository.findDistinctValuesByAttributeIdAndValueContaining(id, q);

        // Combine, deduplicate and sort
        return Stream.concat(
                        productValues == null ? Stream.empty() : productValues.stream(),
                        variationValues == null ? Stream.empty() : variationValues.stream())
                .filter(Objects::nonNull)
                .map(String::trim)
                .filter(s -> !s.isEmpty())
                .map(s -> s) // keep original case
                .collect(Collectors.toCollection(() -> new TreeSet<>(String.CASE_INSENSITIVE_ORDER)))
                .stream()
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
    
    

}