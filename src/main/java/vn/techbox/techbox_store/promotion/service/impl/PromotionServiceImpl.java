package vn.techbox.techbox_store.promotion.service.impl;

import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import vn.techbox.techbox_store.promotion.dto.*;
import vn.techbox.techbox_store.promotion.dto.PromotionVariantResponse;
import vn.techbox.techbox_store.product.service.ProductVariationService;
import vn.techbox.techbox_store.product.dto.productDto.ProductVariationResponse;
import vn.techbox.techbox_store.promotion.model.Promotion;
import vn.techbox.techbox_store.promotion.model.Campaign;
import vn.techbox.techbox_store.promotion.repository.CampaignRepository;
import vn.techbox.techbox_store.promotion.repository.PromotionRepository;
import vn.techbox.techbox_store.promotion.service.PromotionService;

import java.math.BigDecimal;
import java.time.LocalDateTime;
import java.util.List;
import java.util.Objects;
import java.util.Optional;
import java.util.stream.Collectors;

@Service
@RequiredArgsConstructor
@Slf4j
@Transactional
public class PromotionServiceImpl implements PromotionService {
    
    private final PromotionRepository promotionRepository;
    private final CampaignRepository campaignRepository;
    private final ProductVariationService productVariationService;
    
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
    public List<PromotionVariantResponse> getPromotionVariantsByCampaign(Integer campaignId) {
        log.info("Retrieving promotion variants for campaign ID: {}", campaignId);

        List<Promotion> promotions = promotionRepository.findByCampaignId(campaignId);

        return promotions.stream()
            .filter(Promotion::isValid) // include only valid/active promotions
                .map(promotion -> {
                    PromotionVariantResponse.PromotionVariantResponseBuilder builder = PromotionVariantResponse.builder()
                            .promotionId(promotion.getId())
                            .campaignId(promotion.getCampaign().getId())
                            .campaignName(promotion.getCampaign().getName())
                            .discountType(promotion.getDiscountType())
                            .discountValue(promotion.getDiscountValue());

                    // Load variation via service
                    Optional<ProductVariationResponse> maybeVariationDTO = productVariationService.getActiveProductVariationById(promotion.getProductVariationId());
                    if (maybeVariationDTO.isEmpty()) return null; // skip promotions without variation
                    ProductVariationResponse variationDto = maybeVariationDTO.get();

                        // Fill variation fields
                            builder.variationId(variationDto.getId())
                                .variationName(variationDto.getVariationName())
                                .originalPrice(variationDto.getPrice());

                        // productName, productSpu and sku are available on variationDto via mapper
                        builder.productId(variationDto.getProductId())
                            .productName(variationDto.getProductName())
                            .productSpu(variationDto.getProductSpu())
                            .sku(variationDto.getSku());

                        // Compute discounted price for quantity = 1
                        BigDecimal original = variationDto.getPrice();
                        BigDecimal discount = promotion.calculateDiscount(original, 1);
                        BigDecimal discountedPrice = original.subtract(discount);
                        if (discountedPrice.compareTo(BigDecimal.ZERO) < 0) {
                            discountedPrice = BigDecimal.ZERO;
                        }
                        builder.discountedPrice(discountedPrice);
                        return builder.build();
                })
                .filter(Objects::nonNull)
                .collect(Collectors.toList());
    }
    
    @Override
    @Transactional(readOnly = true)
    public List<PromotionVariantResponse> getPromotionVariantsByCampaignForAdmin(Integer campaignId) {
        log.info("Retrieving all promotion variants for campaign ID: {} (admin access)", campaignId);

        List<Promotion> promotions = promotionRepository.findByCampaignId(campaignId);

        return promotions.stream()
                // No filter for isValid() - admin can see all promotions regardless of campaign status
                .map(promotion -> {
                    PromotionVariantResponse.PromotionVariantResponseBuilder builder = PromotionVariantResponse.builder()
                            .promotionId(promotion.getId())
                            .campaignId(promotion.getCampaign().getId())
                            .campaignName(promotion.getCampaign().getName())
                            .discountType(promotion.getDiscountType())
                            .discountValue(promotion.getDiscountValue());

                    // Load variation via service (use regular method instead of active-only)
                    Optional<ProductVariationResponse> maybeVariationDto = productVariationService.getProductVariationById(promotion.getProductVariationId());

                    if (maybeVariationDto.isPresent()) {
                        ProductVariationResponse variationDto = maybeVariationDto.get();

                        // Fill variation fields
                        builder.variationId(variationDto.getId())
                                .variationName(variationDto.getVariationName())
                                .originalPrice(variationDto.getPrice());

                        // productName, productSpu and sku are available on variationDto via mapper
                        builder.productId(variationDto.getProductId())
                                .productName(variationDto.getProductName())
                                .productSpu(variationDto.getProductSpu())
                                .sku(variationDto.getSku());

                        // Compute discounted price for quantity = 1
                        BigDecimal original = variationDto.getPrice();
                        BigDecimal discount = promotion.calculateDiscount(original, 1);
                        BigDecimal discountedPrice = original.subtract(discount);
                        if (discountedPrice.compareTo(BigDecimal.ZERO) < 0) {
                            discountedPrice = BigDecimal.ZERO;
                        }
                        builder.discountedPrice(discountedPrice);
                    } else {
                        // If product variation not found, still return promotion info with placeholder data
                        log.warn("Product variation {} not found for promotion {}, returning partial data",
                                promotion.getProductVariationId(), promotion.getId());
                        builder.variationId(promotion.getProductVariationId())
                                .variationName("Product not found")
                                .originalPrice(BigDecimal.ZERO)
                                .discountedPrice(BigDecimal.ZERO);
                    }

                    return builder.build();
                })
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
        
        // Lấy promotion active cho product variation
        List<Promotion> activePromotions = promotionRepository
                .findActivePromotionsByProductVariationId(
                    request.getProductVariationId(), 
                    LocalDateTime.now()
                );
        
        // Nếu không có promotion active, trả về response với giá gốc
        if (activePromotions.isEmpty()) {
            log.debug("No active promotion found for product variation ID: {}", request.getProductVariationId());
            return PromotionCalculationResponse.builder()
                    .productVariationId(request.getProductVariationId())
                    .salePrice(request.getOriginalPrice())
                    .discountType(null)
                    .discountValue(null)
                    .promotionId(null)
                    .campaignId(null)
                    .build();
        }
        Promotion promotion = activePromotions.get(0);
        BigDecimal salePrice = request.getOriginalPrice();
        
        if ("PERCENTAGE".equals(promotion.getDiscountType().name())) {
            // Giảm theo phần trăm
            BigDecimal discount = request.getOriginalPrice()
                .multiply(promotion.getDiscountValue())
                .divide(BigDecimal.valueOf(100), 2, java.math.RoundingMode.HALF_UP);
            salePrice = request.getOriginalPrice().subtract(discount);
        } else if ("FIXED".equals(promotion.getDiscountType().name())) {
            // Giảm theo số tiền cố định
            salePrice = request.getOriginalPrice().subtract(promotion.getDiscountValue());
            // Đảm bảo giá không âm
            if (salePrice.compareTo(BigDecimal.ZERO) < 0) {
                salePrice = BigDecimal.ZERO;
            }
        }
        
        return PromotionCalculationResponse.builder()
                .productVariationId(request.getProductVariationId())
                .salePrice(salePrice)
                .discountType(promotion.getDiscountType().name())
                .discountValue(promotion.getDiscountValue())
                .promotionId(promotion.getId())
                .campaignId(promotion.getCampaign().getId())
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
