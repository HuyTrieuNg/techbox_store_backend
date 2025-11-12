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
        // Always run this seeder to ensure data is fresh
        return false;
    }

    @Override
    @Transactional
    public void seed() {
        log.info("Starting Promotion seeding...");
        
        // Clean up existing data to ensure idempotency
        promotionRepository.deleteAllInBatch();
        
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
        Campaign megaSale = campaigns.stream().filter(c -> c.getName().equals("Mega Sale 12.12")).findFirst().orElse(null);
        Campaign blackFriday = campaigns.stream().filter(c -> c.getName().equals("Black Friday Tech Sale")).findFirst().orElse(null);
        Campaign audioWeek = campaigns.stream().filter(c -> c.getName().equals("Audio Week")).findFirst().orElse(null);

        if (megaSale == null || blackFriday == null || audioWeek == null) {
            log.warn("Could not find all required campaigns, promotion seeding might be incomplete.");
            return;
        }
        
        // Promotion cho iPhone 15 Pro Max - Giảm 10%
        productVariationRepository.findBySku("IP15PM-256-TN").ifPresent(v -> promotions.add(createPromotion(
            megaSale,
            v.getId(),
            PromotionType.PERCENTAGE,
            new BigDecimal("10.00")
        )));
        
        // Promotion cho Samsung S24 Ultra - Giảm 15%
        productVariationRepository.findBySku("S24U-256-TX").ifPresent(v -> promotions.add(createPromotion(
            megaSale,
            v.getId(),
            PromotionType.PERCENTAGE,
            new BigDecimal("15.00")
        )));
        
        // Promotion cho MacBook - Giảm cố định 5 triệu
        productVariationRepository.findBySku("MBP-M3-16-512-SG").ifPresent(v -> promotions.add(createPromotion(
            blackFriday,
            v.getId(),
            PromotionType.FIXED,
            new BigDecimal("5000000")
        )));
        
        // Promotion cho tai nghe AirPods - Giảm 20%
        productVariationRepository.findBySku("APP2-USBC").ifPresent(v -> promotions.add(createPromotion(
            audioWeek,
            v.getId(),
            PromotionType.PERCENTAGE,
            new BigDecimal("20.00")
        )));
        
        // Promotion cho Sony headphone - Giảm 25%
        productVariationRepository.findBySku("WH1000XM5-BLACK").ifPresent(v -> promotions.add(createPromotion(
            audioWeek,
            v.getId(),
            PromotionType.PERCENTAGE,
            new BigDecimal("25.00")
        )));
        
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
