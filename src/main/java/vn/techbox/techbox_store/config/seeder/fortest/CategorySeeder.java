package vn.techbox.techbox_store.config.seeder.fortest;

import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.boot.CommandLineRunner;
import org.springframework.core.annotation.Order;
import org.springframework.stereotype.Component;
import org.springframework.transaction.annotation.Transactional;
import vn.techbox.techbox_store.product.model.Category;
import vn.techbox.techbox_store.product.repository.CategoryRepository;

import java.time.LocalDateTime;
import java.util.ArrayList;
import java.util.List;

@Component
@RequiredArgsConstructor
@Slf4j
@Order(4)
public class CategorySeeder implements CommandLineRunner {
    private final CategoryRepository categoryRepository;

    @Override
    public void run(String... args) {
        if (shouldSkip()) {
            return;
        }
        try {
            seed();
        } catch (Exception e) {
            log.error("Failed to seed categories: {}", e.getMessage(), e);
        }
    }

    public boolean shouldSkip() {
        if (categoryRepository.existsByName("Electronics")) {
            log.info("Categories already exist, skipping seeder");
            return true;
        }
        return false;
    }

    @Transactional
    public void seed() {
        log.info("Starting Category seeding...");

        LocalDateTime now = LocalDateTime.now();
        List<Category> categories = new ArrayList<>();

        // Root category
        Category electronics = Category.builder()
                .name("Electronics")
                .parentCategoryId(null)
                .createdAt(now)
                .updatedAt(now)
                .build();
        categories.add(electronics);

        // Save root first to get ID
        categoryRepository.save(electronics);

        // Child category
        Category smartphones = Category.builder()
                .name("Smartphones")
                .parentCategoryId(electronics.getId())
                .createdAt(now)
                .updatedAt(now)
                .build();
        categories.add(smartphones);

        categoryRepository.saveAll(categories);
        log.info("Category seeding completed successfully");
    }
}