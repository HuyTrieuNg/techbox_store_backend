package vn.techbox.techbox_store.config.seeder;

import com.fasterxml.jackson.core.type.TypeReference;
import com.fasterxml.jackson.databind.ObjectMapper;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.core.io.ClassPathResource;
import org.springframework.stereotype.Component;
import org.springframework.transaction.annotation.Transactional;
import vn.techbox.techbox_store.config.seeder.dto.ProductSeedDto;
import vn.techbox.techbox_store.product.model.*;
import vn.techbox.techbox_store.product.repository.BrandRepository;
import vn.techbox.techbox_store.product.repository.CategoryRepository;
import vn.techbox.techbox_store.product.repository.ProductRepository;
import vn.techbox.techbox_store.product.repository.ProductVariationRepository;
import vn.techbox.techbox_store.product.repository.AttributeRepository;
import vn.techbox.techbox_store.product.repository.ProductAttributeRepository;
import vn.techbox.techbox_store.product.repository.VariationAttributeRepository;

import java.io.IOException;
import java.io.InputStream;
import java.math.BigDecimal;
import java.util.*;

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

    // Cache for attributes to avoid duplicate creation
    private final Map<String, Attribute> attributeCache = new HashMap<>();

    @Override
    @Transactional
    public void seed() {
        try {
            log.info("Starting Product seeding from JSON...");

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

            int productCount = 0;
            int variationCount = 0;
            int productAttributeCount = 0;
            int variationAttributeCount = 0;

            // Process each product
            for (Map.Entry<String, ProductSeedDto> entry : productsMap.entrySet()) {
                ProductSeedDto dto = entry.getValue();

                try {
                    // Find or create brand
                    Brand brand = findOrCreateBrand(dto.getBrand());

                    // Find category by parsing the category string (e.g., "Lenovo - Yoga")
                    Category category = findCategoryByPath(dto.getCategory());

                    if (category == null) {
                        log.warn("Category not found for path: {}, skipping product: {}", dto.getCategory(), dto.getName());
                        continue;
                    }

                    // Create product
                    Product product = Product.builder()
                            .name(dto.getName())
                            .description(dto.getDescriptionMd())
                            .categoryId(category.getId())
                            .brandId(brand.getId())
                            .imageUrl(dto.getImage())
                            .status(ProductStatus.PUBLISHED)
                            .build();

                    product = productRepository.save(product);
                    productCount++;

                    // Process common_specs (product-level attributes)
                    if (dto.getCommonSpecs() != null && !dto.getCommonSpecs().isEmpty()) {
                        for (Map.Entry<String, String> specEntry : dto.getCommonSpecs().entrySet()) {
                            String attributeName = specEntry.getKey();
                            String attributeValue = specEntry.getValue();

                            if (attributeValue == null || attributeValue.trim().isEmpty()) {
                                continue;
                            }

                            try {
                                // Find or create attribute
                                Attribute attribute = findOrCreateAttribute(attributeName);

                                // Create product attribute
                                ProductAttribute productAttribute = ProductAttribute.builder()
                                        .productId(product.getId())
                                        .attributeId(attribute.getId())
                                        .value(attributeValue)
                                        .build();

                                productAttributeRepository.save(productAttribute);
                                productAttributeCount++;
                            } catch (Exception e) {
                                log.warn("Error creating product attribute {} for product {}: {}",
                                    attributeName, dto.getName(), e.getMessage());
                            }
                        }
                    }

                    // Create product variations
                    if (dto.getVariants() != null && !dto.getVariants().isEmpty()) {
                        for (ProductSeedDto.VariantSeedDto variantDto : dto.getVariants()) {
                            try {
                                BigDecimal price = new BigDecimal(variantDto.getPrice());

                                // Skip variants with price = 0
                                if (price.compareTo(BigDecimal.ZERO) <= 0) {
                                    continue;
                                }

                                ProductVariation variation = ProductVariation.builder()
                                        .productId(product.getId())
                                        .variationName(variantDto.getName())
                                        .price(price)
                                        .stockQuantity(0)
                                        .reservedQuantity(0)
                                        .avgCostPrice(BigDecimal.ZERO)
                                        .build();

                                variation = productVariationRepository.save(variation);
                                variationCount++;

                                // Add images for variation
                                if (variantDto.getImages() != null && !variantDto.getImages().isEmpty()) {
                                    for (String imageUrl : variantDto.getImages()) {
                                        ProductVariationImage image = ProductVariationImage.builder()
                                                .productVariationId(variation.getId())
                                                .imageUrl(imageUrl)
                                                .build();
                                        variation.addImage(image);
                                    }
                                }

                                // Process variation attributes
                                if (variantDto.getAttributes() != null && !variantDto.getAttributes().isEmpty()) {
                                    for (Map.Entry<String, String> attrEntry : variantDto.getAttributes().entrySet()) {
                                        String attributeName = attrEntry.getKey();
                                        String attributeValue = attrEntry.getValue();

                                        if (attributeValue == null || attributeValue.trim().isEmpty()) {
                                            continue;
                                        }

                                        try {
                                            // Find or create attribute
                                            Attribute attribute = findOrCreateAttribute(attributeName);

                                            // Create variation attribute
                                            VariationAttribute variationAttribute = VariationAttribute.builder()
                                                    .productVariationId(variation.getId())
                                                    .attributeId(attribute.getId())
                                                    .value(attributeValue)
                                                    .build();

                                            variationAttributeRepository.save(variationAttribute);
                                            variationAttributeCount++;
                                        } catch (Exception e) {
                                            log.warn("Error creating variation attribute {} for variant {}: {}",
                                                attributeName, variantDto.getName(), e.getMessage());
                                        }
                                    }
                                }

                            } catch (NumberFormatException e) {
                                log.warn("Invalid price for variant: {} - {}", dto.getName(), variantDto.getName());
                            }
                        }
                    }

                } catch (Exception e) {
                    log.error("Error processing product: {}", dto.getName(), e);
                }
            }

            log.info("✓ Successfully seeded {} products with {} variations", productCount, variationCount);
            log.info("✓ Created {} product attributes (common specs)", productAttributeCount);
            log.info("✓ Created {} variation attributes", variationAttributeCount);

        } catch (IOException e) {
            log.error("Failed to load products from JSON file", e);
            throw new RuntimeException("Failed to seed products", e);
        }
    }

    private Brand findOrCreateBrand(String brandName) {
        final String finalBrandName = (brandName == null || brandName.trim().isEmpty()) ? "Unknown" : brandName;

        return brandRepository.findByName(finalBrandName)
                .orElseGet(() -> {
                    Brand newBrand = Brand.builder()
                            .name(finalBrandName)
                            .build();
                    return brandRepository.save(newBrand);
                });
    }

    private synchronized Attribute findOrCreateAttribute(String attributeName) {
        if (attributeName == null || attributeName.trim().isEmpty()) {
            throw new IllegalArgumentException("Attribute name cannot be null or empty");
        }

        String normalizedName = attributeName.trim();
        String cacheKey = normalizedName.toLowerCase();

        // Check cache first
        if (attributeCache.containsKey(cacheKey)) {
            return attributeCache.get(cacheKey);
        }

        // Try to find in database
        try {
            Optional<Attribute> existingAttr = attributeRepository.findByName(normalizedName);
            if (existingAttr.isPresent()) {
                attributeCache.put(cacheKey, existingAttr.get());
                return existingAttr.get();
            }
        } catch (Exception e) {
            // If multiple results found, get the first one
            log.warn("Multiple attributes found for name: {}, using first one", normalizedName);
            List<Attribute> attrs = attributeRepository.searchByName(normalizedName);
            if (!attrs.isEmpty()) {
                Attribute attr = attrs.get(0);
                attributeCache.put(cacheKey, attr);
                return attr;
            }
        }

        // Create new attribute
        Attribute newAttribute = Attribute.builder()
                .name(normalizedName)
                .build();
        newAttribute = attributeRepository.save(newAttribute);
        attributeCache.put(cacheKey, newAttribute);

        log.debug("Created new attribute: {}", normalizedName);
        return newAttribute;
    }

    private Category findCategoryByPath(String categoryPath) {
        if (categoryPath == null || categoryPath.trim().isEmpty()) {
            return null;
        }

        // Parse category path (e.g., "Lenovo - Yoga" or "Laptop")
        String[] parts = categoryPath.split(" - ");

        if (parts.length == 1) {
            // Single level category
            return categoryRepository.findByName(parts[0].trim()).orElse(null);
        } else if (parts.length == 2) {
            // Two level category - find the child
            String parentName = parts[0].trim();
            String childName = parts[1].trim();

            // Find parent first
            Optional<Category> parent = categoryRepository.findByName(parentName);
            if (parent.isEmpty()) {
                return null;
            }

            // Find child with this parent
            List<Category> children = categoryRepository.findByParentCategoryId(parent.get().getId());
            return children.stream()
                    .filter(c -> c.getName().equals(childName))
                    .findFirst()
                    .orElse(null);
        }

        return null;
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
