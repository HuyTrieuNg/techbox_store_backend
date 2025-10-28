package vn.techbox.techbox_store.config.seeder;

import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Component;
import org.springframework.transaction.annotation.Transactional;
import vn.techbox.techbox_store.product.model.ProductVariation;
import vn.techbox.techbox_store.product.repository.ProductVariationRepository;
import vn.techbox.techbox_store.promotion.model.Campaign;
import vn.techbox.techbox_store.promotion.model.Promotion;
import vn.techbox.techbox_store.promotion.model.PromotionType;
import vn.techbox.techbox_store.promotion.repository.CampaignRepository;
import vn.techbox.techbox_store.promotion.repository.PromotionRepository;

import java.math.BigDecimal;
import java.util.ArrayList;
import java.util.List;

@Component
@RequiredArgsConstructor
@Slf4j
public class PromotionSeeder implements DataSeeder {

    private final PromotionRepository promotionRepository;
    private final CampaignRepository campaignRepository;
    private final ProductVariationRepository productVariationRepository;

    @Override
    public int getOrder() {
        return 8; // After Campaign
    }

    @Override
    public boolean shouldSkip() {
        long count = promotionRepository.count();
        if (count > 0) {
            log.info("Promotions already exist ({} found), skipping seeder", count);
            return true;
        }
        return false;
    }

    @Override
    @Transactional
    public void seed() {
        log.info("Starting Promotion seeding...");
        
        List<Campaign> campaigns = campaignRepository.findAll();
        List<ProductVariation> variations = productVariationRepository.findAll();
        
        if (campaigns.isEmpty()) {
            log.warn("No campaigns found, skipping promotion seeding");
            return;
        }
        
        if (variations.isEmpty()) {
            log.warn("No product variations found, skipping promotion seeding");
            return;
        }
        
        List<Promotion> promotions = new ArrayList<>();
        
        // Get campaigns
        Campaign megaSale = campaigns.get(0); // Mega Sale 12.12
        Campaign blackFriday = campaigns.size() > 2 ? campaigns.get(2) : campaigns.get(0); // Black Friday
        Campaign audioWeek = campaigns.size() > 4 ? campaigns.get(4) : campaigns.get(0); // Audio Week
        
        // Promotion cho iPhone 15 Pro Max - Giảm 10%
        if (variations.size() > 0) {
            promotions.add(createPromotion(
                megaSale,
                variations.get(0).getId(),
                PromotionType.PERCENTAGE,
                new BigDecimal("10.00")
            ));
        }
        
        // Promotion cho Samsung S24 Ultra - Giảm 15%
        if (variations.size() > 9) {
            promotions.add(createPromotion(
                megaSale,
                variations.get(9).getId(),
                PromotionType.PERCENTAGE,
                new BigDecimal("15.00") // Max giảm 5 triệu
            ));
        }
        
        // Promotion cho MacBook - Giảm cố định 5 triệu
        if (variations.size() > 16) {
            promotions.add(createPromotion(
                blackFriday,
                
                variations.get(16).getId(),
                PromotionType.FIXED,
                new BigDecimal("5000000")
            ));
        }
        
        // Promotion cho tai nghe AirPods - Giảm 20%
        if (variations.size() > 22) {
            promotions.add(createPromotion(
                audioWeek,
                variations.get(22).getId(),
                PromotionType.PERCENTAGE,
                new BigDecimal("20.00")
            ));
        }
        
        // Promotion cho Sony headphone - Giảm 25%
        if (variations.size() > 24) {
            promotions.add(createPromotion(
                audioWeek,
                variations.get(24).getId(),
                PromotionType.PERCENTAGE,
                new BigDecimal("25.00") // Max giảm 2 triệu
            ));
        }
        
        // Promotion mua nhiều - Mua 2 giảm 30%
        if (variations.size() > 5) {
            promotions.add(createPromotion(
                blackFriday,
                variations.get(5).getId(),
                PromotionType.PERCENTAGE,
                new BigDecimal("30.00")
            ));
        }
        
        promotionRepository.saveAll(promotions);
        log.info("✓ Created {} promotions", promotions.size());
        log.info("Promotion seeding completed successfully");
    }
    
    private Promotion createPromotion(
            Campaign campaign,
            Integer productVariationId,
            PromotionType discountType,
            BigDecimal discountValue
           ) {
        
        return Promotion.builder()
                .campaign(campaign)
                .productVariationId(productVariationId)
                .discountType(discountType)
                .discountValue(discountValue)
                .build();
    }
}
