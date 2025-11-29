package vn.techbox.techbox_store.config.seeder;

import org.springframework.boot.ApplicationArguments;
import org.springframework.boot.ApplicationRunner;
import org.springframework.stereotype.Component;

import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import vn.techbox.techbox_store.product.service.ProductPriceUpdateService;

@Component
@RequiredArgsConstructor
@Slf4j
public class PriceStartupRunner implements ApplicationRunner {

    private final ProductPriceUpdateService productPriceUpdateService;

    @Override
    public void run(ApplicationArguments args) {
        log.info("Running initial product price update on startup...");

        try {
            productPriceUpdateService.updateAllProductPricing();
        } catch (Exception e) {
            log.error("Error running initial price update", e);
        }
    }
}
