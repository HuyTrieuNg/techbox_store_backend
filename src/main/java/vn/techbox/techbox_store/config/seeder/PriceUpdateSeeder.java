package vn.techbox.techbox_store.config.seeder;

import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;

import org.springframework.context.annotation.Profile;
import org.springframework.stereotype.Component;
import org.springframework.transaction.annotation.Transactional;
import vn.techbox.techbox_store.product.service.ProductPriceUpdateService;

@Component
@RequiredArgsConstructor
@Slf4j
@Profile({"dev", "development"}) //
public class PriceUpdateSeeder implements DataSeeder {

    private final ProductPriceUpdateService productPriceUpdateService;

    @Override
    public int getOrder() {
        // Run this seeder after all other data seeders have completed.
        return Integer.MAX_VALUE;
    }

    @Override
    public boolean shouldSkip() {
        // We always want to run this after seeding to ensure prices are correct.
        return false;
    }

    @Override
    @Transactional
    public void seed() {
        log.info("Starting initial product price update after seeding...");
        try {
            // This method will recalculate and update prices for all products based on active promotions.
            productPriceUpdateService.updateAllProductPricing();
            log.info("✓ Initial product price update completed successfully.");
        } catch (Exception e) {
            log.error("❌ Error occurred during initial product price update after seeding.", e);
        }
    }
}
