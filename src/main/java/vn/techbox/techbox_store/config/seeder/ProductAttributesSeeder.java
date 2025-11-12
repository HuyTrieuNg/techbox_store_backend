package vn.techbox.techbox_store.config.seeder;

import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;

import org.springframework.context.annotation.Profile;
import org.springframework.stereotype.Component;
import org.springframework.transaction.annotation.Transactional;
import vn.techbox.techbox_store.product.model.*;
import vn.techbox.techbox_store.product.repository.*;

import java.util.ArrayList;
import java.util.List;

@Component
@RequiredArgsConstructor
@Slf4j
@Profile({"dev", "development"}) //
public class ProductAttributesSeeder implements DataSeeder {

    private final ProductRepository productRepository;
    private final ProductVariationRepository productVariationRepository;
    private final AttributeRepository attributeRepository;
    private final ProductAttributeRepository productAttributeRepository;
    private final VariationAttributeRepository variationAttributeRepository;
    private final ProductVariationImageRepository productVariationImageRepository;

    @Override
    public int getOrder() {
        return 10; // Run after all other seeders
    }

    @Override
    public boolean shouldSkip() {
        long paCount = productAttributeRepository.count();
        long vaCount = variationAttributeRepository.count();
        long imgCount = productVariationImageRepository.count();
        
        if (paCount > 0 || vaCount > 0 || imgCount > 0) {
            log.info("Product attributes/variation attributes/images already exist (PA: {}, VA: {}, IMG: {}), skipping seeder", 
                    paCount, vaCount, imgCount);
            return true;
        }
        return false;
    }

    @Override
    @Transactional
    public void seed() {
        log.info("Starting Product Attributes/Variation Attributes/Images seeding...");
        
        List<Product> products = productRepository.findAll();
        List<ProductVariation> variations = productVariationRepository.findAll();
        List<Attribute> attributes = attributeRepository.findAll();
        
        if (products.isEmpty() || variations.isEmpty() || attributes.isEmpty()) {
            log.warn("Missing base data (products, variations, or attributes), skipping seeding");
            return;
        }
        
        // Create mappings for quick lookup
        Attribute storageAttr = findAttributeByName(attributes, "Storage");
        Attribute colorAttr = findAttributeByName(attributes, "Color");
        Attribute ramAttr = findAttributeByName(attributes, "RAM");
        Attribute processorAttr = findAttributeByName(attributes, "Processor");
        Attribute screenAttr = findAttributeByName(attributes, "Screen Size");
        Attribute osAttr = findAttributeByName(attributes, "Operating System");
        Attribute batteryAttr = findAttributeByName(attributes, "Battery");
        Attribute warrantyAttr = findAttributeByName(attributes, "Warranty");
        
        List<ProductAttribute> productAttributes = new ArrayList<>();
        List<VariationAttribute> variationAttributes = new ArrayList<>();
        List<ProductVariationImage> variationImages = new ArrayList<>();
        
        // Seed data for each product
        for (Product product : products) {
            String productName = product.getName().toLowerCase();
            
            // iPhone 15 Pro Max
            if (productName.contains("iphone 15 pro max")) {
                if (processorAttr != null) productAttributes.add(createProductAttribute(product.getId(), processorAttr.getId(), "Apple A17 Pro"));
                if (osAttr != null) productAttributes.add(createProductAttribute(product.getId(), osAttr.getId(), "iOS 17"));
                if (screenAttr != null) productAttributes.add(createProductAttribute(product.getId(), screenAttr.getId(), "6.7 inch Super Retina XDR"));
                if (warrantyAttr != null) productAttributes.add(createProductAttribute(product.getId(), warrantyAttr.getId(), "12 months"));
                
                // Variations for iPhone 15 Pro Max
                for (ProductVariation v : variations) {
                    if (v.getProductId().equals(product.getId())) {
                        String vName = v.getVariationName() != null ? v.getVariationName().toLowerCase() : "";
                        
                        // Storage
                        if (storageAttr != null) {
                            if (vName.contains("256gb")) variationAttributes.add(createVariationAttribute(v.getId(), storageAttr.getId(), "256GB"));
                            else if (vName.contains("512gb")) variationAttributes.add(createVariationAttribute(v.getId(), storageAttr.getId(), "512GB"));
                            else if (vName.contains("1tb")) variationAttributes.add(createVariationAttribute(v.getId(), storageAttr.getId(), "1TB"));
                        }
                        
                        // Color
                        if (colorAttr != null) {
                            if (vName.contains("tự nhiên") || vName.contains("natural")) variationAttributes.add(createVariationAttribute(v.getId(), colorAttr.getId(), "Titan Tự Nhiên"));
                            else if (vName.contains("đen") || vName.contains("black")) variationAttributes.add(createVariationAttribute(v.getId(), colorAttr.getId(), "Titan Đen"));
                            else if (vName.contains("trắng") || vName.contains("white")) variationAttributes.add(createVariationAttribute(v.getId(), colorAttr.getId(), "Titan Trắng"));
                            else if (vName.contains("xanh") || vName.contains("blue")) variationAttributes.add(createVariationAttribute(v.getId(), colorAttr.getId(), "Titan Xanh"));
                        }
                        
                        // Images
                        variationImages.add(createVariationImage(v.getId(), "https://cdn2.cellphones.com.vn/insecure/rs:fill:358:358/q:90/plain/https://cellphones.com.vn/media/catalog/product/i/p/iphone-15-pro-max_3.png"));
                    }
                }
            }
            
            // iPhone 14
            else if (productName.contains("iphone 14") && !productName.contains("pro")) {
                if (processorAttr != null) productAttributes.add(createProductAttribute(product.getId(), processorAttr.getId(), "Apple A15 Bionic"));
                if (osAttr != null) productAttributes.add(createProductAttribute(product.getId(), osAttr.getId(), "iOS 16"));
                if (screenAttr != null) productAttributes.add(createProductAttribute(product.getId(), screenAttr.getId(), "6.1 inch OLED"));
                if (warrantyAttr != null) productAttributes.add(createProductAttribute(product.getId(), warrantyAttr.getId(), "12 months"));
                
                for (ProductVariation v : variations) {
                    if (v.getProductId().equals(product.getId())) {
                        String vName = v.getVariationName() != null ? v.getVariationName().toLowerCase() : "";
                        
                        if (storageAttr != null) {
                            if (vName.contains("128gb")) variationAttributes.add(createVariationAttribute(v.getId(), storageAttr.getId(), "128GB"));
                            else if (vName.contains("256gb")) variationAttributes.add(createVariationAttribute(v.getId(), storageAttr.getId(), "256GB"));
                        }
                        
                        if (colorAttr != null) {
                            if (vName.contains("xanh") || vName.contains("blue")) variationAttributes.add(createVariationAttribute(v.getId(), colorAttr.getId(), "Xanh Dương"));
                            else if (vName.contains("đen") || vName.contains("black")) variationAttributes.add(createVariationAttribute(v.getId(), colorAttr.getId(), "Đen"));
                            else if (vName.contains("tím") || vName.contains("purple")) variationAttributes.add(createVariationAttribute(v.getId(), colorAttr.getId(), "Tím"));
                        }
                        
                        variationImages.add(createVariationImage(v.getId(), "https://cdn2.cellphones.com.vn/insecure/rs:fill:358:358/q:90/plain/https://cellphones.com.vn/media/catalog/product/i/p/iphone-14_2_.png"));
                    }
                }
            }
            
            // Samsung Galaxy S24 Ultra
            else if (productName.contains("s24 ultra") || productName.contains("galaxy s24")) {
                if (processorAttr != null) productAttributes.add(createProductAttribute(product.getId(), processorAttr.getId(), "Snapdragon 8 Gen 3"));
                if (osAttr != null) productAttributes.add(createProductAttribute(product.getId(), osAttr.getId(), "Android 14"));
                if (screenAttr != null) productAttributes.add(createProductAttribute(product.getId(), screenAttr.getId(), "6.8 inch Dynamic AMOLED 2X"));
                if (warrantyAttr != null) productAttributes.add(createProductAttribute(product.getId(), warrantyAttr.getId(), "12 months"));
                
                for (ProductVariation v : variations) {
                    if (v.getProductId().equals(product.getId())) {
                        String vName = v.getVariationName() != null ? v.getVariationName().toLowerCase() : "";
                        
                        if (storageAttr != null) {
                            if (vName.contains("256gb")) variationAttributes.add(createVariationAttribute(v.getId(), storageAttr.getId(), "256GB"));
                            else if (vName.contains("512gb")) variationAttributes.add(createVariationAttribute(v.getId(), storageAttr.getId(), "512GB"));
                            else if (vName.contains("1tb")) variationAttributes.add(createVariationAttribute(v.getId(), storageAttr.getId(), "1TB"));
                        }
                        
                        if (colorAttr != null) {
                            if (vName.contains("xám") || vName.contains("gray")) variationAttributes.add(createVariationAttribute(v.getId(), colorAttr.getId(), "Titan Xám"));
                            else if (vName.contains("đen") || vName.contains("black")) variationAttributes.add(createVariationAttribute(v.getId(), colorAttr.getId(), "Titan Đen"));
                            else if (vName.contains("tím") || vName.contains("purple")) variationAttributes.add(createVariationAttribute(v.getId(), colorAttr.getId(), "Titan Tím"));
                        }
                        
                        variationImages.add(createVariationImage(v.getId(), "https://cdn2.cellphones.com.vn/insecure/rs:fill:358:358/q:90/plain/https://cellphones.com.vn/media/catalog/product/s/a/samsung-galaxy-s24-ultra.png"));
                    }
                }
            }
            
            // MacBook Pro M3
            else if (productName.contains("macbook pro") && productName.contains("m3")) {
                if (processorAttr != null) productAttributes.add(createProductAttribute(product.getId(), processorAttr.getId(), "Apple M3"));
                if (osAttr != null) productAttributes.add(createProductAttribute(product.getId(), osAttr.getId(), "macOS Sonoma"));
                if (screenAttr != null) productAttributes.add(createProductAttribute(product.getId(), screenAttr.getId(), "14 inch Liquid Retina XDR"));
                if (batteryAttr != null) productAttributes.add(createProductAttribute(product.getId(), batteryAttr.getId(), "70Wh, up to 17 hours"));
                if (warrantyAttr != null) productAttributes.add(createProductAttribute(product.getId(), warrantyAttr.getId(), "12 months"));
                
                for (ProductVariation v : variations) {
                    if (v.getProductId().equals(product.getId())) {
                        String vName = v.getVariationName() != null ? v.getVariationName().toLowerCase() : "";
                        
                        if (ramAttr != null) {
                            if (vName.contains("8gb")) variationAttributes.add(createVariationAttribute(v.getId(), ramAttr.getId(), "8GB"));
                            else if (vName.contains("16gb")) variationAttributes.add(createVariationAttribute(v.getId(), ramAttr.getId(), "16GB"));
                            else if (vName.contains("32gb")) variationAttributes.add(createVariationAttribute(v.getId(), ramAttr.getId(), "32GB"));
                        }
                        
                        if (storageAttr != null) {
                            if (vName.contains("512gb")) variationAttributes.add(createVariationAttribute(v.getId(), storageAttr.getId(), "512GB SSD"));
                            else if (vName.contains("1tb")) variationAttributes.add(createVariationAttribute(v.getId(), storageAttr.getId(), "1TB SSD"));
                        }
                        
                        if (colorAttr != null) {
                            if (vName.contains("space gray") || vName.contains("xám")) variationAttributes.add(createVariationAttribute(v.getId(), colorAttr.getId(), "Space Gray"));
                            else if (vName.contains("silver") || vName.contains("bạc")) variationAttributes.add(createVariationAttribute(v.getId(), colorAttr.getId(), "Silver"));
                        }
                        
                        variationImages.add(createVariationImage(v.getId(), "https://cdn2.cellphones.com.vn/insecure/rs:fill:358:358/q:90/plain/https://cellphones.com.vn/media/catalog/product/m/a/macbook-pro-14-m3.png"));
                    }
                }
            }
            
            // AirPods Pro Gen 2
            else if (productName.contains("airpods pro") && productName.contains("gen 2")) {
                if (osAttr != null) productAttributes.add(createProductAttribute(product.getId(), osAttr.getId(), "Compatible with iOS"));
                if (batteryAttr != null) productAttributes.add(createProductAttribute(product.getId(), batteryAttr.getId(), "Up to 6 hours listening time"));
                if (warrantyAttr != null) productAttributes.add(createProductAttribute(product.getId(), warrantyAttr.getId(), "12 months"));
                
                for (ProductVariation v : variations) {
                    if (v.getProductId().equals(product.getId())) {
                        String vName = v.getVariationName() != null ? v.getVariationName().toLowerCase() : "";
                        
                        if (vName.contains("lightning")) variationAttributes.add(createVariationAttribute(v.getId(), colorAttr.getId(), "Lightning Charging Case"));
                        else if (vName.contains("usb-c")) variationAttributes.add(createVariationAttribute(v.getId(), colorAttr.getId(), "USB-C Charging Case"));
                        
                        variationImages.add(createVariationImage(v.getId(), "https://cdn2.cellphones.com.vn/insecure/rs:fill:358:358/q:90/plain/https://cellphones.com.vn/media/catalog/product/a/i/airpods-pro-2.png"));
                    }
                }
            }
        }
        
        // Save all data
        if (!productAttributes.isEmpty()) {
            productAttributeRepository.saveAll(productAttributes);
            log.info("✓ Created {} product attributes", productAttributes.size());
        }
        
        if (!variationAttributes.isEmpty()) {
            variationAttributeRepository.saveAll(variationAttributes);
            log.info("✓ Created {} variation attributes", variationAttributes.size());
        }
        
        if (!variationImages.isEmpty()) {
            productVariationImageRepository.saveAll(variationImages);
            log.info("✓ Created {} product variation images", variationImages.size());
        }
        
        log.info("Product Attributes/Variation Attributes/Images seeding completed successfully");
    }
    
    private Attribute findAttributeByName(List<Attribute> attributes, String name) {
        return attributes.stream()
                .filter(a -> a.getName().equalsIgnoreCase(name))
                .findFirst()
                .orElse(null);
    }
    
    private ProductAttribute createProductAttribute(Integer productId, Integer attributeId, String value) {
        return ProductAttribute.builder()
                .productId(productId)
                .attributeId(attributeId)
                .value(value)
                .build();
    }
    
    private VariationAttribute createVariationAttribute(Integer variationId, Integer attributeId, String value) {
        return VariationAttribute.builder()
                .productVariationId(variationId)
                .attributeId(attributeId)
                .value(value)
                .build();
    }
    
    private ProductVariationImage createVariationImage(Integer variationId, String imageUrl) {
        return ProductVariationImage.builder()
                .productVariationId(variationId)
                .imageUrl(imageUrl)
                .imagePublicId("sample_" + variationId) // Sample public ID
                .build();
    }
}
