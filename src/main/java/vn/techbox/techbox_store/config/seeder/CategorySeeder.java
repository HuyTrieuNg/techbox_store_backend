package vn.techbox.techbox_store.config.seeder;

import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Component;
import vn.techbox.techbox_store.product.model.Category;
import vn.techbox.techbox_store.product.repository.CategoryRepository;

import java.util.Arrays;
import java.util.List;

@Component
@RequiredArgsConstructor
@Slf4j
public class CategorySeeder implements DataSeeder {

    private final CategoryRepository categoryRepository;

    @Override
    public void seed() {
        List<Category> categories = Arrays.asList(
                Category.builder()
                        .name("Điện thoại")
                        .build(),
                
                Category.builder()
                        .name("Laptop")
                        .build(),
                
                Category.builder()
                        .name("Tablet")
                        .build(),
                
                Category.builder()
                        .name("Tai nghe")
                        .build(),
                
                Category.builder()
                        .name("Đồng hồ thông minh")
                        .build(),
                
                Category.builder()
                        .name("Phụ kiện")
                        .build(),
                
                Category.builder()
                        .name("Loa")
                        .build(),
                
                Category.builder()
                        .name("PC & Màn hình")
                        .build()
        );

        categoryRepository.saveAll(categories);
        log.info("✓ Created {} categories", categories.size());
    }

    @Override
    public int getOrder() {
        return 2;
    }

    @Override
    public boolean shouldSkip() {
        return categoryRepository.count() > 0;
    }
}
