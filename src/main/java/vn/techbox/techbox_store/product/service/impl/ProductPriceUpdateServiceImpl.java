package vn.techbox.techbox_store.product.service.impl;

import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import vn.techbox.techbox_store.product.model.Product;
import vn.techbox.techbox_store.product.model.ProductVariation;
import vn.techbox.techbox_store.product.repository.ProductRepository;
import vn.techbox.techbox_store.product.repository.ProductVariationRepository;
import vn.techbox.techbox_store.product.service.ProductPriceUpdateService;
import vn.techbox.techbox_store.promotion.model.Promotion;
import vn.techbox.techbox_store.promotion.repository.PromotionRepository;

import java.math.BigDecimal;
import java.util.Comparator;
import java.util.List;
import java.util.Optional;

@Slf4j
@Service
@RequiredArgsConstructor
@Transactional
public class ProductPriceUpdateServiceImpl implements ProductPriceUpdateService {
    
    private final ProductRepository productRepository;
    private final ProductVariationRepository productVariationRepository;
    private final PromotionRepository promotionRepository;
    
    @Override
    public void updateProductPricing(Integer productId) {
        Product product = productRepository.findById(productId).orElse(null);
        if (product == null) {
            log.warn("Product not found with ID: {}", productId);
            return;
        }
        
        // Lấy tất cả biến thể của sản phẩm
        List<ProductVariation> variations = productVariationRepository.findByProductId(productId);
        if (variations.isEmpty()) {
            log.warn("No variations found for product ID: {}", productId);
            product.setDisplayOriginalPrice(null);
            product.setDisplaySalePrice(null);
            product.setDiscountType(null);
            product.setDiscountValue(null);
            productRepository.save(product);
            return;
        }
        
        // Tìm biến thể có giá sau giảm thấp nhất
        VariationWithDiscount lowestPriceVariation = findLowestPriceVariation(variations);
        
        if (lowestPriceVariation != null) {
            product.setDisplayOriginalPrice(lowestPriceVariation.originalPrice);
            product.setDisplaySalePrice(lowestPriceVariation.salePrice);
            product.setDiscountType(lowestPriceVariation.discountType);
            product.setDiscountValue(lowestPriceVariation.discountValue);
            
            productRepository.save(product);
            log.info("Updated pricing for product ID: {} - Original: {}, Sale: {}, Discount: {}{}",
                    productId, 
                    lowestPriceVariation.originalPrice,
                    lowestPriceVariation.salePrice,
                    lowestPriceVariation.discountValue,
                    lowestPriceVariation.discountType);
        }
    }
    
    @Override
    public void updateAllProductPricing() {
        log.info("Starting to update pricing for all products");
        List<Product> allProducts = productRepository.findAllActive();
        
        int count = 0;
        for (Product product : allProducts) {
            try {
                updateProductPricing(product.getId());
                count++;
            } catch (Exception e) {
                log.error("Error updating pricing for product ID: {}", product.getId(), e);
            }
        }
        
        log.info("Completed pricing update for {} products", count);
    }
    
    @Override
    public void updateProductPricingByCampaign(Integer campaignId) {
        log.info("Updating product pricing for campaign ID: {}", campaignId);
        
        // Lấy tất cả promotions trong campaign này
        List<Promotion> promotions = promotionRepository.findByCampaignId(campaignId);
        
        // Lấy danh sách product ID unique từ promotions
        List<Integer> productIds = promotions.stream()
                .map(Promotion::getProductVariationId)
                .distinct()
                .map(variationId -> {
                    ProductVariation variation = productVariationRepository.findById(variationId).orElse(null);
                    return variation != null ? variation.getProductId() : null;
                })
                .filter(java.util.Objects::nonNull)
                .distinct()
                .toList();
        
        log.info("Found {} products to update for campaign ID: {}", productIds.size(), campaignId);
        
        // Cập nhật giá cho từng sản phẩm
        for (Integer productId : productIds) {
            try {
                updateProductPricing(productId);
            } catch (Exception e) {
                log.error("Error updating pricing for product ID: {} in campaign: {}", productId, campaignId, e);
            }
        }
    }
    
    /**
     * Tìm biến thể có giá sau giảm thấp nhất
     */
    private VariationWithDiscount findLowestPriceVariation(List<ProductVariation> variations) {
        return variations.stream()
                .map(this::calculateVariationPricing)
                .min(Comparator.comparing(v -> v.salePrice))
                .orElse(null);
    }
    
    /**
     * Tính toán giá cho một biến thể (bao gồm promotion nếu có)
     */
    private VariationWithDiscount calculateVariationPricing(ProductVariation variation) {
        BigDecimal originalPrice = variation.getPrice();
        
        // Tìm promotion active cho biến thể này
        Optional<Promotion> activePromotion = promotionRepository
                .findByProductVariationId(variation.getId())
                .stream()
                .filter(Promotion::isActive)
                .findFirst();
        
        if (activePromotion.isPresent()) {
            Promotion promotion = activePromotion.get();
            BigDecimal discount = promotion.calculateDiscount(originalPrice, 1);
            BigDecimal salePrice = originalPrice.subtract(discount);
            
            return new VariationWithDiscount(
                    variation.getId(),
                    originalPrice,
                    salePrice,
                    promotion.getDiscountType().name(),
                    promotion.getDiscountValue()
            );
        } else {
            // Không có promotion
            return new VariationWithDiscount(
                    variation.getId(),
                    originalPrice,
                    originalPrice,
                    null,
                    null
            );
        }
    }
    
    /**
     * Helper class để lưu thông tin biến thể với giá giảm
     */
    private static class VariationWithDiscount {
        Integer variationId;
        BigDecimal originalPrice;
        BigDecimal salePrice;
        String discountType;
        BigDecimal discountValue;
        
        VariationWithDiscount(Integer variationId, BigDecimal originalPrice, BigDecimal salePrice,
                            String discountType, BigDecimal discountValue) {
            this.variationId = variationId;
            this.originalPrice = originalPrice;
            this.salePrice = salePrice;
            this.discountType = discountType;
            this.discountValue = discountValue;
        }
    }
}
