package vn.techbox.techbox_store.config.seeder;

import com.fasterxml.jackson.core.type.TypeReference;
import com.fasterxml.jackson.databind.ObjectMapper;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.core.io.ClassPathResource;
import org.springframework.stereotype.Component;
import org.springframework.transaction.PlatformTransactionManager;
import org.springframework.transaction.TransactionDefinition;
import org.springframework.transaction.support.TransactionTemplate;
import vn.techbox.techbox_store.config.seeder.dto.ProductSeedDto;
import vn.techbox.techbox_store.config.seeder.dto.CategorySeedDto;
import vn.techbox.techbox_store.product.model.*;
import vn.techbox.techbox_store.product.repository.*;

import java.io.IOException;
import java.io.InputStream;
import java.math.BigDecimal;
import java.util.*;
import java.util.concurrent.ThreadLocalRandom;

@Component
@RequiredArgsConstructor
@Slf4j
public class ProductSeeder implements DataSeeder {

    private final ProductRepository productRepository;
    private final ProductVariationRepository productVariationRepository;
    private final CategoryRepository categoryRepository;
    private final BrandRepository brandRepository;
    private final AttributeRepository attributeRepository;
    private final ProductAttributeRepository productAttributeRepository;
    private final VariationAttributeRepository variationAttributeRepository;
    private final ObjectMapper objectMapper;
    
    // Inject TransactionManager để xử lý transaction cô lập
    private final PlatformTransactionManager transactionManager;

    // Cache for attributes to avoid duplicate creation
    private final Map<String, Attribute> attributeCache = new HashMap<>();

    @Override
    // LƯU Ý: Không dùng @Transactional ở đây nữa
    public void seed() {
        try {
            log.info("Starting Product seeding from JSON...");

            // Seed categories first if not already seeded
            if (categoryRepository.count() == 0) {
                seedCategories();
            }

            // Pre-load all existing attributes into cache
            List<Attribute> existingAttributes = attributeRepository.findAll();
            for (Attribute attr : existingAttributes) {
                attributeCache.put(attr.getName().toLowerCase().trim(), attr);
            }
            log.info("Loaded {} existing attributes into cache", attributeCache.size());

            // Read JSON file from resources
            ClassPathResource resource = new ClassPathResource("seed_data/products.json");
            InputStream inputStream = resource.getInputStream();

            // Parse JSON to Map<String, ProductSeedDto>
            Map<String, ProductSeedDto> productsMap = objectMapper.readValue(
                inputStream,
                new TypeReference<>() {}
            );

            log.info("Loaded {} products from JSON", productsMap.size());

            int successCount = 0;
            int errorCount = 0;

            // Cấu hình Transaction Template: Tạo Transaction mới cho mỗi lần thực thi
            TransactionTemplate transactionTemplate = new TransactionTemplate(transactionManager);
            transactionTemplate.setPropagationBehavior(TransactionDefinition.PROPAGATION_REQUIRES_NEW);

            // Process each product
            for (Map.Entry<String, ProductSeedDto> entry : productsMap.entrySet()) {
                ProductSeedDto dto = entry.getValue();

                try {
                    // Thực thi logic seed 1 sản phẩm trong 1 transaction riêng biệt
                    transactionTemplate.execute(status -> {
                        seedSingleProduct(dto);
                        return null;
                    });
                    
                    successCount++;
                    
                } catch (Exception e) {
                    errorCount++;
                    // Log lỗi nhưng KHÔNG throw để vòng lặp tiếp tục chạy sản phẩm tiếp theo
                    log.error("❌ FAILED to seed product: '{}'. Reason: {}", dto.getName(), e.getMessage());
                }
            }

            log.info("=================================================");
            log.info("Seeding Complete!");
            log.info("✓ Success: {}", successCount);
            log.info("❌ Failed:  {}", errorCount);
            log.info("=================================================");

        } catch (IOException e) {
            log.error("Failed to load products from JSON file", e);
            throw new RuntimeException("Failed to seed products", e);
        }
    }

    /**
     * Logic để seed MỘT sản phẩm duy nhất.
     * Hàm này được gọi bên trong TransactionTemplate.
     * Nếu có lỗi xảy ra ở đây, Transaction sẽ rollback và ném Exception ra ngoài.
     */
    private void seedSingleProduct(ProductSeedDto dto) {
        // Find or create brand
        Brand brand = findOrCreateBrand(dto.getBrand());

        // Find category by name (unique)
        Category category = findCategoryByName(dto.getCategory());

        if (category == null) {
            // Throw exception để trigger rollback
            throw new RuntimeException("Category not found for path: " + dto.getCategory());
        }

        // Create product
        Product product = Product.builder()
                .name(dto.getName())
                .description(dto.getDescriptionMd())
                .warrantyMonths(ThreadLocalRandom.current().nextInt(3, 25))
                .categoryId(category.getId())
                .brandId(brand.getId())
                .imageUrl(dto.getImage())
                .spu(dto.getSpu())
                .status(ProductStatus.PUBLISHED)
                .build();

        product = productRepository.save(product);

        // Process common_specs (product-level attributes)
        if (dto.getCommonSpecs() != null && !dto.getCommonSpecs().isEmpty()) {
            for (Map.Entry<String, String> specEntry : dto.getCommonSpecs().entrySet()) {
                String attributeName = specEntry.getKey();
                String attributeValue = specEntry.getValue();

                if (attributeValue == null || attributeValue.trim().isEmpty()) {
                    continue;
                }

                // Find or create attribute
                Attribute attribute = findOrCreateAttribute(attributeName);

                // Create product attribute
                ProductAttribute productAttribute = ProductAttribute.builder()
                        .productId(product.getId())
                        .attributeId(attribute.getId())
                        .value(attributeValue)
                        .build();

                productAttributeRepository.save(productAttribute);
            }
        }

        // Create product variations
        if (dto.getVariants() != null && !dto.getVariants().isEmpty()) {
            for (ProductSeedDto.VariantSeedDto variantDto : dto.getVariants()) {
                BigDecimal price;
                try {
                    price = new BigDecimal(variantDto.getPrice());
                } catch (NumberFormatException e) {
                    price =  new BigDecimal(19999000);
                   
                }

                // Skip variants with price = 0
                if (price.compareTo(BigDecimal.ZERO) <= 0) {
                    price =  new BigDecimal(19999000);
                }

                BigDecimal costPrice = price.multiply(new BigDecimal("0.9"));


                ProductVariation variation = ProductVariation.builder()
                        .productId(product.getId())
                        .variationName(variantDto.getName())
                        .price(price)
                        .sku(variantDto.getSku())
                        .stockQuantity(ThreadLocalRandom.current().nextInt(10, 101))
                        .reservedQuantity(0)
                        .avgCostPrice(costPrice)
                        .build();

                variation = productVariationRepository.save(variation);

                // Add images for variation
                if (variantDto.getImages() != null && !variantDto.getImages().isEmpty()) {
                    for (String imageUrl : variantDto.getImages()) {
                        ProductVariationImage image = ProductVariationImage.builder()
                                .productVariationId(variation.getId())
                                .imageUrl(imageUrl)
                                .build();
                        
                        variation.addImage(image); 
                    }
                    // Save lại variation nếu cần update list images (tùy JPA config)
                    productVariationRepository.save(variation);
                }

                // Process variation attributes
                if (variantDto.getAttributes() != null && !variantDto.getAttributes().isEmpty()) {
                    for (Map.Entry<String, String> attrEntry : variantDto.getAttributes().entrySet()) {
                        String attributeName = attrEntry.getKey();
                        String attributeValue = attrEntry.getValue();

                        if (attributeValue == null || attributeValue.trim().isEmpty()) {
                            continue;
                        }

                        // Find or create attribute
                        Attribute attribute = findOrCreateAttribute(attributeName);

                        // Create variation attribute
                        VariationAttribute variationAttribute = VariationAttribute.builder()
                                .productVariationId(variation.getId())
                                .attributeId(attribute.getId())
                                .value(attributeValue)
                                .build();

                        variationAttributeRepository.save(variationAttribute);
                    }
                }
            }
        }
    }

    // --- Các hàm phụ trợ giữ nguyên logic nhưng có thể được gọi trong Transaction mới ---

    private Brand findOrCreateBrand(String brandName) {
        final String finalBrandName = (brandName == null || brandName.trim().isEmpty()) ? "Unknown" : brandName;
        // Logic này an toàn vì chạy trong Transaction
        return brandRepository.findByName(finalBrandName)
                .orElseGet(() -> brandRepository.save(Brand.builder().name(finalBrandName).build()));
    }

    private synchronized Attribute findOrCreateAttribute(String attributeName) {
        if (attributeName == null || attributeName.trim().isEmpty()) {
            throw new IllegalArgumentException("Attribute name cannot be null or empty");
        }

        String normalizedName = attributeName.trim();
        String cacheKey = normalizedName.toLowerCase();

        if (attributeCache.containsKey(cacheKey)) {
            return attributeCache.get(cacheKey);
        }

        try {
            Optional<Attribute> existingAttr = attributeRepository.findByName(normalizedName);
            if (existingAttr.isPresent()) {
                attributeCache.put(cacheKey, existingAttr.get());
                return existingAttr.get();
            }
        } catch (Exception e) {
            List<Attribute> attrs = attributeRepository.searchByName(normalizedName);
            if (!attrs.isEmpty()) {
                Attribute attr = attrs.get(0);
                attributeCache.put(cacheKey, attr);
                return attr;
            }
        }

        Attribute newAttribute = Attribute.builder().name(normalizedName).build();
        newAttribute = attributeRepository.save(newAttribute);
        attributeCache.put(cacheKey, newAttribute);

        return newAttribute;
    }

    private Category findCategoryByName(String categoryName) {
        if (categoryName == null || categoryName.trim().isEmpty()) {
            return null;
        }
        return categoryRepository.findByName(categoryName.trim()).orElse(null);
    }

    private void seedCategories() {
        try {
            log.info("Starting Category seeding from JSON...");
            ClassPathResource resource = new ClassPathResource("seed_data/categories.json");
            InputStream inputStream = resource.getInputStream();

            List<CategorySeedDto> categories = objectMapper.readValue(
                inputStream,
                new TypeReference<List<CategorySeedDto>>() {}
            );

            for (CategorySeedDto dto : categories) {
                seedCategory(null, dto);
            }
            log.info("✓ Successfully seeded categories");
        } catch (IOException e) {
            log.error("Failed to load categories from JSON file", e);
            throw new RuntimeException("Failed to seed categories", e);
        }
    }

    private void seedCategory(Category parent, CategorySeedDto dto) {
        Category category = Category.builder()
            .name(dto.getName())
            .parentCategoryId(parent != null ? parent.getId() : null)
            .build();
        category = categoryRepository.save(category);

        if (dto.getChildren() != null && !dto.getChildren().isEmpty()) {
            for (CategorySeedDto child : dto.getChildren()) {
                seedCategory(category, child);
            }
        }
    }

    @Override
    public int getOrder() {
        return 5;
    }

    @Override
    public boolean shouldSkip() {
        return productRepository.count() > 0;
    }
}