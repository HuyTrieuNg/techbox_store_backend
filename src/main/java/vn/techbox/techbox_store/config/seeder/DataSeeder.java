package vn.techbox.techbox_store.config.seeder;

/**
 * Interface for all data seeders
 * Each seeder should implement this interface and define its execution order
 */
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
