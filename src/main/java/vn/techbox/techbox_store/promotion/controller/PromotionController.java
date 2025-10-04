package vn.techbox.techbox_store.promotion.controller;

import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Pageable;
import org.springframework.data.domain.Sort;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;
import vn.techbox.techbox_store.promotion.dto.*;
import vn.techbox.techbox_store.promotion.service.PromotionService;

import java.math.BigDecimal;
import java.util.List;

@RestController
@RequestMapping("/api/promotions")
@RequiredArgsConstructor
@Slf4j
public class PromotionController {
    
    private final PromotionService promotionService;
    
    @PostMapping
    public ResponseEntity<PromotionResponse> createPromotion(@Valid @RequestBody PromotionCreateRequest request) {
        log.info("REST request to create promotion: {}", request.getRuleName());
        
        try {
            PromotionResponse response = promotionService.createPromotion(request);
            return ResponseEntity.status(HttpStatus.CREATED).body(response);
        } catch (IllegalArgumentException e) {
            log.error("Error creating promotion: {}", e.getMessage());
            return ResponseEntity.badRequest().build();
        }
    }
    
    @PutMapping("/{id}")
    public ResponseEntity<PromotionResponse> updatePromotion(
            @PathVariable Integer id,
            @Valid @RequestBody PromotionUpdateRequest request) {
        log.info("REST request to update promotion with ID: {}", id);
        
        try {
            PromotionResponse response = promotionService.updatePromotion(id, request);
            return ResponseEntity.ok(response);
        } catch (IllegalArgumentException e) {
            log.error("Error updating promotion: {}", e.getMessage());
            return ResponseEntity.badRequest().build();
        }
    }
    
    
    @GetMapping
    public ResponseEntity<Page<PromotionResponse>> getAllPromotions(
            @RequestParam(defaultValue = "0") int page,
            @RequestParam(defaultValue = "10") int size,
            @RequestParam(defaultValue = "createdAt") String sortBy,
            @RequestParam(defaultValue = "DESC") String sortDir) {
        log.info("REST request to get all promotions with pagination");
        
        Sort sort = Sort.by(Sort.Direction.fromString(sortDir), sortBy);
        Pageable pageable = PageRequest.of(page, size, sort);
        
        Page<PromotionResponse> promotions = promotionService.getAllPromotions(pageable);
        return ResponseEntity.ok(promotions);
    }
    
    @GetMapping("/campaign/{campaignId}")
    public ResponseEntity<List<PromotionResponse>> getPromotionsByCampaign(@PathVariable Integer campaignId) {
        log.info("REST request to get promotions for campaign ID: {}", campaignId);
        
        List<PromotionResponse> promotions = promotionService.getPromotionsByCampaign(campaignId);
        return ResponseEntity.ok(promotions);
    }
    
    @GetMapping("/product-variation/{productVariationId}")
    public ResponseEntity<List<PromotionResponse>> getPromotionsByProductVariation(@PathVariable Integer productVariationId) {
        log.info("REST request to get promotions for product variation ID: {}", productVariationId);
        
        List<PromotionResponse> promotions = promotionService.getPromotionsByProductVariation(productVariationId);
        return ResponseEntity.ok(promotions);
    }
    
    @PostMapping("/calculate")
    public ResponseEntity<PromotionCalculationResponse> calculatePromotions(@Valid @RequestBody PromotionCalculationRequest request) {
        log.info("REST request to calculate promotions for product variation ID: {}", request.getProductVariationId());
        
        PromotionCalculationResponse response = promotionService.calculatePromotions(request);
        return ResponseEntity.ok(response);
    }
    
    @GetMapping("/product-variation/{productVariationId}/calculate")
    public ResponseEntity<PromotionCalculationResponse> calculatePromotionsForProduct(
            @PathVariable Integer productVariationId,
            @RequestParam BigDecimal originalPrice,
            @RequestParam(defaultValue = "1") Integer quantity,
            @RequestParam BigDecimal orderAmount) {
        log.info("REST request to calculate promotions for product variation ID: {} via GET", productVariationId);
        
        PromotionCalculationRequest request = PromotionCalculationRequest.builder()
                .productVariationId(productVariationId)
                .originalPrice(originalPrice)
                .quantity(quantity)
                .orderAmount(orderAmount)
                .build();
        
        PromotionCalculationResponse response = promotionService.calculatePromotions(request);
        return ResponseEntity.ok(response);
    }
    
    @DeleteMapping("/{id}")
    public ResponseEntity<Void> deletePromotion(@PathVariable Integer id) {
        log.info("REST request to delete promotion with ID: {}", id);
        
        try {
            promotionService.deletePromotion(id);
            return ResponseEntity.noContent().build();
        } catch (IllegalArgumentException e) {
            log.error("Promotion not found: {}", e.getMessage());
            return ResponseEntity.notFound().build();
        }
    }
    
    @GetMapping("/{id}")
    public ResponseEntity<PromotionResponse> getPromotionById(@PathVariable Integer id) {
        log.info("REST request to get promotion with ID: {}", id);
        
        try {
            PromotionResponse response = promotionService.getPromotionById(id);
            return ResponseEntity.ok(response);
        } catch (IllegalArgumentException e) {
            log.error("Promotion not found: {}", e.getMessage());
            return ResponseEntity.notFound().build();
        }
    }
    
}