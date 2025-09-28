package vn.techbox.techbox_store.product.controller;

import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;
import vn.techbox.techbox_store.product.dto.AttributeCreateRequest;
import vn.techbox.techbox_store.product.dto.AttributeResponse;
import vn.techbox.techbox_store.product.dto.AttributeUpdateRequest;
import vn.techbox.techbox_store.product.service.AttributeService;

import java.util.List;

@RestController
@RequestMapping("/api/attributes")
@RequiredArgsConstructor
public class AttributeController {
    
    private final AttributeService attributeService;
    
    @GetMapping
    public ResponseEntity<List<AttributeResponse>> getAllAttributes() {
        List<AttributeResponse> attributes = attributeService.getAllAttributes();
        return ResponseEntity.ok(attributes);
    }
    
    @GetMapping("/{id}")
    public ResponseEntity<AttributeResponse> getAttributeById(@PathVariable Integer id) {
        return attributeService.getAttributeById(id)
                .map(attribute -> ResponseEntity.ok(attribute))
                .orElse(ResponseEntity.notFound().build());
    }
    
    @PostMapping
    public ResponseEntity<AttributeResponse> createAttribute(@Valid @RequestBody AttributeCreateRequest request) {
        AttributeResponse createdAttribute = attributeService.createAttribute(request);
        return ResponseEntity.status(HttpStatus.CREATED).body(createdAttribute);
    }
    
    @PutMapping("/{id}")
    public ResponseEntity<AttributeResponse> updateAttribute(
            @PathVariable Integer id, 
            @Valid @RequestBody AttributeUpdateRequest request) {
        AttributeResponse updatedAttribute = attributeService.updateAttribute(id, request);
        return ResponseEntity.ok(updatedAttribute);
    }
    
    @DeleteMapping("/{id}")
    public ResponseEntity<Void> deleteAttribute(@PathVariable Integer id) {
        attributeService.deleteAttribute(id);
        return ResponseEntity.noContent().build();
    }
    
    @GetMapping("/search")
    public ResponseEntity<List<AttributeResponse>> searchAttributes(@RequestParam String keyword) {
        List<AttributeResponse> attributes = attributeService.searchAttributesByName(keyword);
        return ResponseEntity.ok(attributes);
    }
    
    @GetMapping("/exists")
    public ResponseEntity<Boolean> checkAttributeExists(
            @RequestParam String name,
            @RequestParam(required = false) Integer excludeId) {
        boolean exists = excludeId != null 
                ? attributeService.existsByNameAndIdNot(name, excludeId)
                : attributeService.existsByName(name);
        return ResponseEntity.ok(exists);
    }
}