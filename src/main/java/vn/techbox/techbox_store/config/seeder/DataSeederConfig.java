package vn.techbox.techbox_store.config.seeder;

import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.boot.CommandLineRunner;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.context.annotation.Profile;
import org.springframework.core.annotation.Order;

import java.util.List;

/**
 * Configuration for running data seeders
 * Only runs in development profile
 */
@Profile({"dev", "development"}) // Only run in development
@Configuration
@RequiredArgsConstructor
@Slf4j
public class DataSeederConfig {

    private final List<DataSeeder> seeders;

    @Bean
    @Order(Integer.MAX_VALUE) // Run after all other beans are initialized
    @Profile({"dev", "development"}) // Only run in development
    @org.springframework.context.annotation.DependsOn("entityManagerFactory") // Wait for JPA to create tables
    public CommandLineRunner runSeeders() {
        return args -> {
            log.info("=".repeat(80));
            log.info("Starting Data Seeding Process...");
            log.info("=".repeat(80));
            
            // Sort seeders by order
            seeders.stream()
                    .sorted((s1, s2) -> Integer.compare(s1.getOrder(), s2.getOrder()))
                    .forEach(seeder -> {
                        String seederName = seeder.getClass().getSimpleName();
                        
                        if (seeder.shouldSkip()) {
                            log.info("⏭️  Skipping {} - Data already exists", seederName);
                            return;
                        }
                        
                        try {
                            log.info("▶️  Running {} (Order: {})", seederName, seeder.getOrder());
                            long startTime = System.currentTimeMillis();
                            
                            seeder.seed();
                            
                            long duration = System.currentTimeMillis() - startTime;
                            log.info("✅ {} completed in {}ms", seederName, duration);
                        } catch (Exception e) {
                            log.error("❌ Error in {}: {}", seederName, e.getMessage(), e);
                        }
                    });
            
            log.info("=".repeat(80));
            log.info("Data Seeding Process Completed!");
            log.info("=".repeat(80));
        };
    }
}
