package vn.techbox.techbox_store.promotion.service.impl;

import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import vn.techbox.techbox_store.promotion.dto.*;
import vn.techbox.techbox_store.promotion.model.Promotion;
import vn.techbox.techbox_store.promotion.model.Campaign;
import vn.techbox.techbox_store.promotion.repository.CampaignRepository;
import vn.techbox.techbox_store.promotion.repository.PromotionRepository;
import vn.techbox.techbox_store.promotion.service.PromotionService;

import java.math.BigDecimal;
import java.util.ArrayList;
import java.util.List;
import java.util.stream.Collectors;

@Service
@RequiredArgsConstructor
@Slf4j
@Transactional
public class PromotionServiceImpl implements PromotionService {
    
    private final PromotionRepository promotionRepository;
    private final CampaignRepository campaignRepository;
    
    @Override
    public PromotionResponse createPromotion(PromotionCreateRequest request) {
        log.info("Creating new promotion for product variation: {}", request.getProductVariationId());
        
        // Validate campaign exists
        Campaign campaign = campaignRepository.findById(request.getCampaignId())
                .orElseThrow(() -> new IllegalArgumentException("Campaign not found with ID: " + request.getCampaignId()));
        
        // Validate percentage discount value
        if (request.getDiscountType().name().equals("PERCENTAGE") && 
            request.getDiscountValue().compareTo(BigDecimal.valueOf(100)) > 0) {
            throw new IllegalArgumentException("Percentage discount cannot exceed 100%");
        }
        
        Promotion promotion = Promotion.builder()
                .campaign(campaign)
                .productVariationId(request.getProductVariationId())
                .discountType(request.getDiscountType())
                .discountValue(request.getDiscountValue())
                .build();
        
        Promotion savedPromotion = promotionRepository.save(promotion);
        log.info("Promotion created successfully with ID: {}", savedPromotion.getId());
        
        return mapToResponse(savedPromotion);
    }
    
    @Override
    public PromotionResponse updatePromotion(Integer id, PromotionUpdateRequest request) {
        log.info("Updating promotion with ID: {}", id);
        
        Promotion promotion = promotionRepository.findById(id)
                .orElseThrow(() -> new IllegalArgumentException("Promotion not found with ID: " + id));
        
        if (request.getProductVariationId() != null) {
            promotion.setProductVariationId(request.getProductVariationId());
        }
        
        if (request.getDiscountType() != null) {
            promotion.setDiscountType(request.getDiscountType());
        }
        
        if (request.getDiscountValue() != null) {
            // Validate percentage discount value
            if (promotion.getDiscountType().name().equals("PERCENTAGE") && 
                request.getDiscountValue().compareTo(BigDecimal.valueOf(100)) > 0) {
                throw new IllegalArgumentException("Percentage discount cannot exceed 100%");
            }
            promotion.setDiscountValue(request.getDiscountValue());
        }
        
        Promotion savedPromotion = promotionRepository.save(promotion);
        log.info("Promotion updated successfully with ID: {}", savedPromotion.getId());
        
        return mapToResponse(savedPromotion);
    }
    
    @Override
    @Transactional(readOnly = true)
    public PromotionResponse getPromotionById(Integer id) {
        log.info("Retrieving promotion with ID: {}", id);
        
        Promotion promotion = promotionRepository.findById(id)
                .orElseThrow(() -> new IllegalArgumentException("Promotion not found with ID: " + id));
        
        return mapToResponse(promotion);
    }
    
    @Override
    @Transactional(readOnly = true)
    public Page<PromotionResponse> getAllPromotions(Pageable pageable) {
        log.info("Retrieving all promotions with pagination");
        
        return promotionRepository.findAll(pageable)
                .map(this::mapToResponse);
    }
    
    @Override
    @Transactional(readOnly = true)
    public List<PromotionResponse> getPromotionsByCampaign(Integer campaignId) {
        log.info("Retrieving promotions for campaign ID: {}", campaignId);
        
        return promotionRepository.findByCampaignId(campaignId).stream()
                .map(this::mapToResponse)
                .collect(Collectors.toList());
    }
    
    @Override
    @Transactional(readOnly = true)
    public List<PromotionResponse> getPromotionsByProductVariation(Integer productVariationId) {
        log.info("Retrieving promotions for product variation ID: {}", productVariationId);
        
        return promotionRepository.findByProductVariationId(productVariationId).stream()
                .filter(promotion -> promotion.isActive())
                .map(this::mapToResponse)
                .collect(Collectors.toList());
    }
    
    @Override
    @Transactional(readOnly = true)
    public PromotionCalculationResponse calculatePromotions(PromotionCalculationRequest request) {
        log.info("Calculating promotions for product variation ID: {}", request.getProductVariationId());
        
        List<Promotion> applicablePromotions = promotionRepository.findByProductVariationId(request.getProductVariationId())
                .stream()
                .filter(promotion -> promotion.isActive())
                .collect(Collectors.toList());
        
        BigDecimal originalTotal = request.getOriginalPrice().multiply(BigDecimal.valueOf(request.getQuantity()));
        BigDecimal totalDiscount = BigDecimal.ZERO;
        List<PromotionCalculationResponse.AppliedPromotion> appliedPromotions = new ArrayList<>();
        
        for (Promotion promotion : applicablePromotions) {
            BigDecimal discount = promotion.calculateDiscount(
                    request.getOriginalPrice(), 
                    request.getQuantity()
            );
            
            if (discount.compareTo(BigDecimal.ZERO) > 0) {
                totalDiscount = totalDiscount.add(discount);
                
                appliedPromotions.add(PromotionCalculationResponse.AppliedPromotion.builder()
                        .promotionId(promotion.getId())
                        .campaignName(promotion.getCampaign().getName())
                        .discountType(promotion.getDiscountType().name())
                        .discountAmount(discount)
                        .build());
            }
        }
        
        BigDecimal finalTotal = originalTotal.subtract(totalDiscount);
        BigDecimal finalPrice = finalTotal.divide(BigDecimal.valueOf(request.getQuantity()));
        
        return PromotionCalculationResponse.builder()
                .productVariationId(request.getProductVariationId())
                .originalPrice(request.getOriginalPrice())
                .originalTotal(originalTotal)
                .quantity(request.getQuantity())
                .orderAmount(request.getOrderAmount())
                .totalDiscount(totalDiscount)
                .finalPrice(finalPrice)
                .finalTotal(finalTotal)
                .appliedPromotions(appliedPromotions)
                .build();
    }
    
    @Override
    public void deletePromotion(Integer id) {
        log.info("Deleting promotion with ID: {}", id);
        
        if (!promotionRepository.existsById(id)) {
            throw new IllegalArgumentException("Promotion not found with ID: " + id);
        }
        
        promotionRepository.deleteById(id);
        log.info("Promotion deleted successfully with ID: {}", id);
    }
    
    private PromotionResponse mapToResponse(Promotion promotion) {
        return PromotionResponse.builder()
                .id(promotion.getId())
                .campaignId(promotion.getCampaign().getId())
                .campaignName(promotion.getCampaign().getName())
                .productVariationId(promotion.getProductVariationId())
                .discountType(promotion.getDiscountType())
                .discountValue(promotion.getDiscountValue())
                .createdAt(promotion.getCreatedAt())
                .updatedAt(promotion.getUpdatedAt())
                .isActive(promotion.isActive())
                .build();
    }
}
