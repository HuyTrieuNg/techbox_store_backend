package vn.techbox.techbox_store.config.seeder;

import org.springframework.context.annotation.Profile;

/**
 * Interface for all data seeders
 * Each seeder should implement this interface and define its execution order
 */
@Profile({"dev", "development"}) // Only run in development
public interface DataSeeder {
    
    /**
     * Execute the seeding logic
     */
    void seed();
    
    /**
     * Get the execution order (lower numbers execute first)
     * @return order priority
     */
    int getOrder();
    
    /**
     * Check if data already exists to avoid duplicate seeding
     * @return true if should skip seeding
     */
    boolean shouldSkip();
}
