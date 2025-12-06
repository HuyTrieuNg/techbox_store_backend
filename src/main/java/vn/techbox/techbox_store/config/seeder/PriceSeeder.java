package vn.techbox.techbox_store.config.seeder;

import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Component;
import vn.techbox.techbox_store.product.service.ProductPriceUpdateService;

@Component
@RequiredArgsConstructor
@Slf4j
public class PriceSeeder implements DataSeeder {
    private final ProductPriceUpdateService productPriceUpdateService;

    @Override
    public int getOrder() {
        return 9999; // Đảm bảo chạy cuối cùng
    }

    @Override
    public boolean shouldSkip() {
        // Luôn chạy, không skip
        return false;
    }

    @Override
    public void seed() {
        log.info("Running initial product price update at the end of seeding...");
        try {
            productPriceUpdateService.updateAllProductPricing();
            log.info("✓ Product price update completed");
        } catch (Exception e) {
            log.error("Error running initial price update", e);
        }
    }
}
