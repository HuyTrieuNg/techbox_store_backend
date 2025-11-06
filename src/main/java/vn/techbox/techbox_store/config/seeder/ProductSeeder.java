package vn.techbox.techbox_store.config.seeder;

import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Component;
import org.springframework.transaction.annotation.Transactional;
import vn.techbox.techbox_store.product.model.Product;
import vn.techbox.techbox_store.product.model.ProductStatus;
import vn.techbox.techbox_store.product.model.ProductVariation;
import vn.techbox.techbox_store.product.repository.ProductRepository;
import vn.techbox.techbox_store.product.repository.ProductVariationRepository;

import java.math.BigDecimal;
import java.util.ArrayList;
import java.util.List;

@Component
@RequiredArgsConstructor
@Slf4j
public class ProductSeeder implements DataSeeder {

    private final ProductRepository productRepository;
    private final ProductVariationRepository productVariationRepository;

    @Override
    @Transactional
    public void seed() {
        log.info("Starting Product seeding...");
        
        List<Product> products = new ArrayList<>();
        List<ProductVariation> variations = new ArrayList<>();

        // ===== ĐIỆN THOẠI =====
        // iPhone 15 Pro Max
        Product iphone15ProMax = createProduct("iPhone 15 Pro Max", 
                "iPhone 15 Pro Max với chip A17 Pro mạnh mẽ, camera 48MP, màn hình Super Retina XDR", 
                1, 1);
        products.add(iphone15ProMax);

        // iPhone 14
        Product iphone14 = createProduct("iPhone 14", 
                "iPhone 14 với chip A15 Bionic, camera kép 12MP, màn hình OLED 6.1 inch", 
                1, 1);
        products.add(iphone14);

        // Samsung Galaxy S24 Ultra
        Product s24Ultra = createProduct("Samsung Galaxy S24 Ultra", 
                "Galaxy S24 Ultra với Snapdragon 8 Gen 3, S Pen tích hợp, camera 200MP", 
                1, 2);
        products.add(s24Ultra);

        // Xiaomi 14 Pro
        Product xiaomi14Pro = createProduct("Xiaomi 14 Pro", 
                "Xiaomi 14 Pro với Snapdragon 8 Gen 3, camera Leica 50MP, sạc nhanh 120W", 
                1, 3);
        products.add(xiaomi14Pro);

        // ===== LAPTOP =====
        // MacBook Pro M3
        Product macbookM3 = createProduct("MacBook Pro 14-inch M3", 
                "MacBook Pro với chip M3, màn hình Liquid Retina XDR, pin 17 giờ", 
                2, 1);
        products.add(macbookM3);

        // Dell XPS 15
        Product dellXps15 = createProduct("Dell XPS 15", 
                "Dell XPS 15 với Intel Core i7, NVIDIA RTX 4060, màn hình 4K OLED", 
                2, 7);
        products.add(dellXps15);

        // ===== TAI NGHE =====
        // AirPods Pro Gen 2
        Product airpodsPro2 = createProduct("AirPods Pro Gen 2", 
                "AirPods Pro với chip H2, chống ồn chủ động, sạc MagSafe", 
                4, 1);
        products.add(airpodsPro2);

        // Sony WH-1000XM5
        Product sonyXm5 = createProduct("Sony WH-1000XM5", 
                "Sony WH-1000XM5 với chống ồn hàng đầu, âm thanh Hi-Res, pin 30 giờ", 
                4, 13);
        products.add(sonyXm5);

        // Save products first
        products = productRepository.saveAll(products);
        log.info("✓ Created {} products", products.size());

        // Create variations for each product
        // iPhone 15 Pro Max variations
        variations.addAll(createIPhone15ProMaxVariations(products.get(0).getId()));
        
        // iPhone 14 variations
        variations.addAll(createIPhone14Variations(products.get(1).getId()));
        
        // Samsung S24 Ultra variations
        variations.addAll(createS24UltraVariations(products.get(2).getId()));
        
        // Xiaomi 14 Pro variations
        variations.addAll(createXiaomi14ProVariations(products.get(3).getId()));
        
        // MacBook Pro M3 variations
        variations.addAll(createMacBookM3Variations(products.get(4).getId()));
        
        // Dell XPS 15 variations
        variations.addAll(createDellXps15Variations(products.get(5).getId()));
        
        // AirPods Pro Gen 2 variations
        variations.addAll(createAirProdsProGen2Variations(products.get(6).getId()));
        
        // Sony WH-1000XM5 variations
        variations.addAll(createSonyXM5Variations(products.get(7).getId()));

        productVariationRepository.saveAll(variations);
        log.info("✓ Created {} product variations", variations.size());
    }

    private Product createProduct(String name, String description, Integer categoryId, Integer brandId) {
        return Product.builder()
                .name(name)
                .description(description)
                .categoryId(categoryId)
                .brandId(brandId)
                .status(ProductStatus.PUBLISHED)
                .build();
    }

    private List<ProductVariation> createIPhone15ProMaxVariations(Integer productId) {
        return List.of(
                createVariation(productId, "256GB - Titan Tự Nhiên", "IP15PM-256-TN", new BigDecimal("29990000")),
                createVariation(productId, "256GB - Titan Đen", "IP15PM-256-TD", new BigDecimal("29990000")),
                createVariation(productId, "512GB - Titan Tự Nhiên", "IP15PM-512-TN", new BigDecimal("34990000")),
                createVariation(productId, "512GB - Titan Trắng", "IP15PM-512-TT", new BigDecimal("34990000")),
                createVariation(productId, "1TB - Titan Xanh", "IP15PM-1TB-TX", new BigDecimal("39990000"))
        );
    }

    private List<ProductVariation> createIPhone14Variations(Integer productId) {
        return List.of(
                createVariation(productId, "128GB - Xanh Dương", "IP14-128-XD", new BigDecimal("18990000")),
                createVariation(productId, "128GB - Đen", "IP14-128-D", new BigDecimal("18990000")),
                createVariation(productId, "256GB - Xanh Dương", "IP14-256-XD", new BigDecimal("21990000")),
                createVariation(productId, "256GB - Tím", "IP14-256-T", new BigDecimal("21990000"))
        );
    }

    private List<ProductVariation> createS24UltraVariations(Integer productId) {
        return List.of(
                createVariation(productId, "256GB - Titan Xám", "S24U-256-TX", new BigDecimal("27990000")),
                createVariation(productId, "512GB - Titan Đen", "S24U-512-TD", new BigDecimal("31990000")),
                createVariation(productId, "512GB - Titan Tím", "S24U-512-TT", new BigDecimal("31990000")),
                createVariation(productId, "1TB - Titan Xám", "S24U-1TB-TX", new BigDecimal("36990000"))
        );
    }

    private List<ProductVariation> createXiaomi14ProVariations(Integer productId) {
        return List.of(
                createVariation(productId, "256GB - Đen", "X14P-256-D", new BigDecimal("19990000")),
                createVariation(productId, "512GB - Đen", "X14P-512-D", new BigDecimal("22990000")),
                createVariation(productId, "512GB - Trắng", "X14P-512-T", new BigDecimal("22990000"))
        );
    }

    private List<ProductVariation> createMacBookM3Variations(Integer productId) {
        return List.of(
                createVariation(productId, "8GB RAM - 512GB SSD - Space Gray", "MBP-M3-8-512-SG", new BigDecimal("44990000")),
                createVariation(productId, "8GB RAM - 512GB SSD - Silver", "MBP-M3-8-512-SV", new BigDecimal("44990000")),
                createVariation(productId, "16GB RAM - 512GB SSD - Space Gray", "MBP-M3-16-512-SG", new BigDecimal("49990000")),
                createVariation(productId, "16GB RAM - 1TB SSD - Space Gray", "MBP-M3-16-1TB-SG", new BigDecimal("54990000"))
        );
    }

    private List<ProductVariation> createDellXps15Variations(Integer productId) {
        return List.of(
                createVariation(productId, "16GB RAM - 512GB SSD", "XPS15-16-512", new BigDecimal("42990000")),
                createVariation(productId, "32GB RAM - 1TB SSD", "XPS15-32-1TB", new BigDecimal("54990000"))
        );
    }

    private List<ProductVariation> createAirProdsProGen2Variations(Integer productId) {
        return List.of(
                createVariation(productId, "Lightning", "APP2-LIGHTNING", new BigDecimal("5990000")),
                createVariation(productId, "USB-C", "APP2-USBC", new BigDecimal("5990000"))
        );
    }

    private List<ProductVariation> createSonyXM5Variations(Integer productId) {
        return List.of(
                createVariation(productId, "Đen", "WH1000XM5-BLACK", new BigDecimal("8990000")),
                createVariation(productId, "Bạc", "WH1000XM5-SILVER", new BigDecimal("8990000"))
        );
    }

    private ProductVariation createVariation(Integer productId, String variationName, String sku, BigDecimal price) {
        return ProductVariation.builder()
                .productId(productId)
                .variationName(variationName)
                .sku(sku)
                .price(price)
                .stockQuantity(0) // Will be updated by inventory imports
                .reservedQuantity(0)
                .avgCostPrice(BigDecimal.ZERO)
                .build();
    }

    @Override
    public int getOrder() {
        return 5; // After Attribute
    }

    @Override
    public boolean shouldSkip() {
        long count = productRepository.count();
        if (count > 0) {
            log.info("Products already exist ({} found), skipping ProductSeeder", count);
            return true;
        }
        log.info("No products found, will run ProductSeeder");
        return false;
    }
}
