package vn.techbox.techbox_store.config.seeder;

import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.boot.CommandLineRunner;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.core.annotation.Order;
import vn.techbox.techbox_store.product.service.ProductPriceUpdateService;
import vn.techbox.techbox_store.review.scheduler.ReviewRatingScheduler;

import java.util.List;

/**
 * Configuration for running data seeders
 * Only runs in development profile
 */
@Configuration
@RequiredArgsConstructor
@Slf4j
public class DataSeederConfig {

    private final List<DataSeeder> seeders;
    private final ProductPriceUpdateService productPriceUpdateService;
    private final ReviewRatingScheduler reviewRatingScheduler;

    @Value("${SEEDERS_ENABLED:true}")
    private boolean seedersEnabled;

    @Bean
    @Order(Integer.MAX_VALUE) // Run after all other beans are initialized
    @org.springframework.context.annotation.DependsOn("entityManagerFactory") // Wait for JPA to create tables
    public CommandLineRunner runSeeders() {
        return args -> {
            if (!seedersEnabled) {
                log.info("Data seeding is disabled by environment variable 'SEEDERS_ENABLED' (current value: {}) - skipping all seeders", seedersEnabled);
                return;
            }

            log.info("=".repeat(80));
            log.info("Starting Data Seeding Process...");
            log.info("=".repeat(80));
            
            // Sort seeders by order
            seeders.stream()
                    .sorted((s1, s2) -> Integer.compare(s1.getOrder(), s2.getOrder()))
                    .forEach(seeder -> {
                        String seederName = seeder.getClass().getSimpleName();
                        
                        if (seeder.shouldSkip()) {
                            log.info("⏭  Skipping {} - Data already exists", seederName);
                            return;
                        }
                        
                        try {
                            log.info("  Running {} (Order: {})", seederName, seeder.getOrder());
                            long startTime = System.currentTimeMillis();
                            
                            seeder.seed();
                            
                            long duration = System.currentTimeMillis() - startTime;
                            log.info(" {} completed in {}ms", seederName, duration);
                        } catch (Exception e) {
                            log.error(" Error in {}: {}", seederName, e.getMessage(), e);
                        }
                    });
            
            log.info("=".repeat(80));
            log.info("Data Seeding Process Completed!");
            log.info("=".repeat(80));
            
            // Post-seeding: Update product ratings and prices
            log.info("");
            log.info("=".repeat(80));
            log.info("Starting Post-Seeding: Update Product Ratings & Prices");
            log.info("=".repeat(80));
            
            try {
                log.info(" Updating average ratings for all products...");
                long startTime = System.currentTimeMillis();
                reviewRatingScheduler.updateAverageRatingsForAllProducts();
                long duration = System.currentTimeMillis() - startTime;
                log.info(" Rating update completed in {}ms", duration);
            } catch (Exception e) {
                log.error(" Error updating ratings: {}", e.getMessage(), e);
            }
            
            try {
                log.info(" Updating product prices...");
                long startTime = System.currentTimeMillis();
                productPriceUpdateService.updateAllProductPricing();
                long duration = System.currentTimeMillis() - startTime;
                log.info(" Price update completed in {}ms", duration);
            } catch (Exception e) {
                log.error(" Error updating prices: {}", e.getMessage(), e);
            }
            
            log.info("=".repeat(80));
            log.info("Post-Seeding Process Completed!");
            log.info("=".repeat(80));
        };
    }
}
