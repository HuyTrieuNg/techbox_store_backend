package vn.techbox.techbox_store.config.seeder;

import com.fasterxml.jackson.core.type.TypeReference;
import com.fasterxml.jackson.databind.ObjectMapper;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.core.io.ClassPathResource;
import org.springframework.stereotype.Component;
import vn.techbox.techbox_store.config.seeder.dto.CategorySeedDto;
import vn.techbox.techbox_store.product.model.Category;
import vn.techbox.techbox_store.product.repository.CategoryRepository;

import java.io.IOException;
import java.io.InputStream;
import java.util.ArrayList;
import java.util.List;

@Component
@RequiredArgsConstructor
@Slf4j
public class CategorySeeder implements DataSeeder {

    private final CategoryRepository categoryRepository;
    private final ObjectMapper objectMapper;

    @Override
    public void seed() {
        try {
            // Read JSON file from resources
            ClassPathResource resource = new ClassPathResource("seed_data/categories.json");
            InputStream inputStream = resource.getInputStream();
            
            // Parse JSON to DTO list
            List<CategorySeedDto> categorySeedList = objectMapper.readValue(
                inputStream, 
                new TypeReference<>() {}
            );
            
            log.info("Loaded {} top-level categories from JSON", categorySeedList.size());
            
            // Process and save categories
            List<Category> allCategories = new ArrayList<>();
            
            for (CategorySeedDto dto : categorySeedList) {
                processCategory(dto, null, allCategories);
            }
            
            categoryRepository.saveAll(allCategories);
            log.info("✓ Successfully seeded {} categories", allCategories.size());
            
        } catch (IOException e) {
            log.error("Failed to load categories from JSON file", e);
            throw new RuntimeException("Failed to seed categories", e);
        }
    }
    
    private void processCategory(CategorySeedDto dto, Integer parentId, List<Category> allCategories) {
        // Create current category
        Category category = Category.builder()
                .name(dto.getName())
                .parentCategoryId(parentId)
                .build();
        
        allCategories.add(category);
        
        // Save immediately to get ID for children
        category = categoryRepository.save(category);
        
        // Process children recursively
        if (dto.getChildren() != null && !dto.getChildren().isEmpty()) {
            for (CategorySeedDto childDto : dto.getChildren()) {
                processCategory(childDto, category.getId(), allCategories);
            }
        }
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
