package vn.techbox.techbox_store.config.seeder;

import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;

import org.springframework.context.annotation.Profile;
import org.springframework.stereotype.Component;
import org.springframework.transaction.annotation.Transactional;
import vn.techbox.techbox_store.product.model.*;
import vn.techbox.techbox_store.product.repository.BrandRepository;
import vn.techbox.techbox_store.product.repository.CategoryRepository;
import vn.techbox.techbox_store.product.repository.ProductRepository;
import vn.techbox.techbox_store.product.repository.ProductVariationRepository;

import java.math.BigDecimal;
import java.util.ArrayList;
import java.util.List;
import java.util.Map;
import java.util.function.Function;
import java.util.stream.Collectors;

@Component
@RequiredArgsConstructor
@Slf4j
@Profile({"dev", "development"}) //
public class ProductSeeder implements DataSeeder {

    private final ProductRepository productRepository;
    private final ProductVariationRepository productVariationRepository;
    private final BrandRepository brandRepository;
    private final CategoryRepository categoryRepository;

    private Map<String, Brand> brandMap;
    private Map<String, Category> categoryMap;

    @Override
    @Transactional
    public void seed() {
        log.info("Starting Product seeding...");
        
        // Clean up existing data to ensure idempotency
        log.info("Deleting existing product data...");
        productVariationRepository.deleteAllInBatch();
        productRepository.deleteAllInBatch();
        log.info("✓ Existing product data deleted.");

        // Pre-load all brands and categories for efficient lookup
        brandMap = brandRepository.findAll().stream()
                .collect(Collectors.toMap(Brand::getName, Function.identity()));
        categoryMap = categoryRepository.findAll().stream()
                .collect(Collectors.toMap(Category::getName, Function.identity()));

        List<Product> products = new ArrayList<>();
        List<ProductVariation> variations = new ArrayList<>();

        // ===== ĐIỆN THOẠI =====
        createAndAddProduct(products, variations, "iPhone 15 Pro Max", "Apple", "iPhone 15 Series",
                "https://fdn2.gsmarena.com/vv/pics/apple/apple-iphone-15-pro-max-1.jpg",
                "iPhone 15 Pro Max với chip A17 Pro mạnh mẽ, camera 48MP, màn hình Super Retina XDR.",
                this::createIPhone15ProMaxVariations);

        createAndAddProduct(products, variations, "iPhone 14", "Apple", "iPhone 14 Series",
                "https://fdn2.gsmarena.com/vv/pics/apple/apple-iphone-14-1.jpg",
                "iPhone 14 với chip A15 Bionic, camera kép 12MP, màn hình OLED 6.1 inch.",
                this::createIPhone14Variations);

        createAndAddProduct(products, variations, "Samsung Galaxy S24 Ultra", "Samsung", "Galaxy S Series",
                "https://fdn2.gsmarena.com/vv/pics/samsung/samsung-galaxy-s24-ultra-5g-sm-s928-1.jpg",
                "Galaxy S24 Ultra với Snapdragon 8 Gen 3, S Pen tích hợp, camera 200MP.",
                this::createS24UltraVariations);
        
        createAndAddProduct(products, variations, "Samsung Galaxy Z Fold 5", "Samsung", "Galaxy Z Series",
                "https://fdn2.gsmarena.com/vv/pics/samsung/samsung-galaxy-z-fold5-5g-1.jpg",
                "Galaxy Z Fold5 - Màn hình gập thế hệ mới, Snapdragon 8 Gen 2 for Galaxy, camera 50MP.",
                this::createZFold5Variations);

        createAndAddProduct(products, variations, "Xiaomi 14 Pro", "Xiaomi", "Xiaomi Series",
                "https://fdn2.gsmarena.com/vv/pics/xiaomi/xiaomi-14-pro-1.jpg",
                "Xiaomi 14 Pro với Snapdragon 8 Gen 3, camera Leica 50MP, sạc nhanh 120W.",
                this::createXiaomi14ProVariations);

        // ===== LAPTOP =====
        createAndAddProduct(products, variations, "MacBook Pro 14-inch M3", "Apple", "MacBook Pro M3",
                "https://fdn2.gsmarena.com/vv/pics/apple/apple-macbook-pro-14-2023-1.jpg",
                "MacBook Pro với chip M3, màn hình Liquid Retina XDR, pin 17 giờ.",
                this::createMacBookM3Variations);
        
        createAndAddProduct(products, variations, "MacBook Air M2", "Apple", "MacBook Air M2",
                "https://www.apple.com/v/macbook-air-13-and-15-m2/b/images/overview/hero/hero_13__d1tfa5zby7e6_large.jpg",
                "MacBook Air M2 - Thiết kế mỏng nhẹ, hiệu năng vượt trội, màn hình Liquid Retina 13.6 inch.",
                this::createMacBookAirM2Variations);

        createAndAddProduct(products, variations, "Dell XPS 15", "Dell", "Laptop Cao cấp",
                "https://i.dell.com/is/image/DellContent/content/dam/ss2/product-images/dell-client-products/notebooks/xps-notebooks/xps-15-9530/media-gallery/touch-black/notebook-xps-15-9530-t-black-gallery-1.psd?fmt=pjpg&pscan=auto&scl=1&wid=3491&hei=2077&qlt=100,1&resMode=sharp2&size=3491,2077&chrss=full",
                "Dell XPS 15 với Intel Core i7, NVIDIA RTX 4060, màn hình 4K OLED.",
                this::createDellXps15Variations);
        
        createAndAddProduct(products, variations, "ASUS ROG Zephyrus G14", "Asus", "Gaming RTX 4060",
                "https://dlcdnwebimgs.asus.com/gain/cf69b78c-24a4-4763-9e9a-f23a3650a953/w1000/h732",
                "Laptop gaming ROG Zephyrus G14 - AMD Ryzen 9, RTX 4060, màn hình Nebula Display.",
                this::createAsusROGVariations);

        // ===== TABLET =====
        createAndAddProduct(products, variations, "iPad Pro M2 11-inch", "Apple", "iPad Pro",
                "https://fdn2.gsmarena.com/vv/pics/apple/apple-ipad-pro-11-2022-1.jpg",
                "iPad Pro M2 11-inch - Sức mạnh siêu việt từ chip M2, màn hình Liquid Retina, hỗ trợ Apple Pencil 2.",
                this::createIpadProM2Variations);
        
        createAndAddProduct(products, variations, "Samsung Galaxy Tab S9", "Samsung", "Samsung Galaxy Tab",
                "https://fdn2.gsmarena.com/vv/pics/samsung/samsung-galaxy-tab-s9-fe-plus-1.jpg",
                "Galaxy Tab S9 - Màn hình Dynamic AMOLED 2X, Snapdragon 8 Gen 2, kháng nước IP68.",
                this::createTabS9Variations);

        // ===== TAI NGHE =====
        createAndAddProduct(products, variations, "AirPods Pro Gen 2", "Apple", "AirPods",
                "https://www.apple.com/v/airpods-pro/g/images/overview/case_front__r6scgy1ibx2q_large.jpg",
                "AirPods Pro với chip H2, chống ồn chủ động, sạc MagSafe.",
                this::createAirPodsProGen2Variations);

        createAndAddProduct(products, variations, "Sony WH-1000XM5", "Sony", "Tai nghe chụp tai",
                "https://www.sony.com.vn/image/5d02da5df552836db894cead8a68f5f3?fmt=pjpeg&wid=330&bgcolor=FFFFFF&bgc=FFFFFF",
                "Sony WH-1000XM5 với chống ồn hàng đầu, âm thanh Hi-Res, pin 30 giờ.",
                this::createSonyXM5Variations);
        
        createAndAddProduct(products, variations, "Samsung Galaxy Buds 3", "Samsung", "Galaxy Buds",
                "https://fdn2.gsmarena.com/vv/pics/samsung/samsung-galaxy-buds3-pro-1.jpg",
                "Galaxy Buds 3 - Thiết kế mới, chống ồn chủ động thông minh, chất âm Hi-Fi.",
                this::createGalaxyBuds3Variations);

        // ===== ĐỒNG HỒ THÔNG MINH =====
        createAndAddProduct(products, variations, "Apple Watch Ultra 2", "Apple", "Apple Watch Ultra",
                "https://www.apple.com/v/apple-watch-ultra-2/c/images/overview/design/design_display_endframe__ctln416a4z2a_large.jpg",
                "Apple Watch Ultra 2 - Vỏ titan, GPS tần số kép, pin 36 giờ, dành cho thể thao mạo hiểm.",
                this::createAppleWatchUltra2Variations);
        
        createAndAddProduct(products, variations, "Samsung Galaxy Watch 6", "Samsung", "Samsung Galaxy Watch",
                "https://images.samsung.com/vn/smartphones/galaxy-watch6/buy/product_color_watch6_graphite_s.png",
                "Galaxy Watch 6 - Viền bezel siêu mỏng, theo dõi sức khỏe toàn diện, hệ điều hành Wear OS.",
                this::createGalaxyWatch6Variations);

        // ===== PHỤ KIỆN =====
        createAndAddProduct(products, variations, "Sạc nhanh Anker 30W", "Anker", "Củ sạc nhanh",
                "https://m.media-amazon.com/images/I/51m3oKvY-sL._AC_SL1500_.jpg",
                "Củ sạc Anker PowerPort III 30W, công nghệ GaN, sạc nhanh cho iPhone, MacBook Air.",
                this::createAnkerChargerVariations);
        
        createAndAddProduct(products, variations, "Bàn phím cơ Keychron K2", "Keychron", "Bàn phím cơ",
                "https://m.media-amazon.com/images/I/61-rA1HefBL._AC_SL1500_.jpg",
                "Bàn phím cơ không dây Keychron K2 (V2), layout 84 phím, hot-swap, LED RGB.",
                this::createKeychronK2Variations);

        // Save all products and variations
        productRepository.saveAll(products);
        log.info("✓ Created {} products", products.size());
        productVariationRepository.saveAll(variations);
        log.info("✓ Created {} product variations", variations.size());
    }

    private void createAndAddProduct(List<Product> products, List<ProductVariation> variations,
                                     String name, String brandName, String categoryName, String imageUrl, String description,
                                     Function<Integer, List<ProductVariation>> variationCreator) {
        Brand brand = brandMap.get(brandName);
        Category category = categoryMap.get(categoryName);

        if (brand == null) {
            log.warn("Brand '{}' not found for product '{}'. Skipping.", brandName, name);
            return;
        }
        if (category == null) {
            log.warn("Category '{}' not found for product '{}'. Skipping.", categoryName, name);
            return;
        }

        Product product = Product.builder()
                .name(name)
                .description(description)
                .categoryId(category.getId())
                .brandId(brand.getId())
                .imageUrl(imageUrl)
                .warrantyMonths(12) // Default 12 months warranty
                .status(ProductStatus.PUBLISHED)
                .build();
        
        // Save product to get ID before creating variations
        product = productRepository.save(product);
        
        List<ProductVariation> createdVariations = variationCreator.apply(product.getId());
        variations.addAll(createdVariations);

        // Find the minimum price from the variations and update the product
        BigDecimal minPrice = createdVariations.stream()
                .map(ProductVariation::getPrice)
                .min(BigDecimal::compareTo)
                .orElse(BigDecimal.ZERO);

        product.setDisplayOriginalPrice(minPrice);
        product.setDisplaySalePrice(minPrice); // Initially, sale price is the same as original

        // Add the fully constructed product to the list
        products.add(product);
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
    
    private List<ProductVariation> createZFold5Variations(Integer productId) {
        return List.of(
                createVariation(productId, "256GB - Xanh Icy", "ZF5-256-XI", new BigDecimal("38990000")),
                createVariation(productId, "512GB - Đen", "ZF5-512-D", new BigDecimal("42990000"))
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
    
    private List<ProductVariation> createMacBookAirM2Variations(Integer productId) {
        return List.of(
                createVariation(productId, "8GB RAM - 256GB SSD - Starlight", "MBA-M2-8-256-SL", new BigDecimal("26990000")),
                createVariation(productId, "8GB RAM - 256GB SSD - Midnight", "MBA-M2-8-256-MN", new BigDecimal("26990000")),
                createVariation(productId, "16GB RAM - 512GB SSD - Starlight", "MBA-M2-16-512-SL", new BigDecimal("32990000"))
        );
    }

    private List<ProductVariation> createDellXps15Variations(Integer productId) {
        return List.of(
                createVariation(productId, "16GB RAM - 512GB SSD", "XPS15-16-512", new BigDecimal("42990000")),
                createVariation(productId, "32GB RAM - 1TB SSD", "XPS15-32-1TB", new BigDecimal("54990000"))
        );
    }
    
    private List<ProductVariation> createAsusROGVariations(Integer productId) {
        return List.of(
                createVariation(productId, "16GB RAM - 1TB SSD - Eclipse Gray", "ROG-G14-16-1TB-EG", new BigDecimal("45990000"))
        );
    }

    private List<ProductVariation> createIpadProM2Variations(Integer productId) {
        return List.of(
                createVariation(productId, "128GB - Wifi - Space Gray", "IPPM2-11-128-W-SG", new BigDecimal("21990000")),
                createVariation(productId, "256GB - Wifi - Silver", "IPPM2-11-256-W-SV", new BigDecimal("24990000")),
                createVariation(productId, "256GB - Wifi + 5G - Space Gray", "IPPM2-11-256-5G-SG", new BigDecimal("28990000"))
        );
    }
    
    private List<ProductVariation> createTabS9Variations(Integer productId) {
        return List.of(
                createVariation(productId, "128GB - Wifi - Graphite", "TS9-128-W-GR", new BigDecimal("19990000")),
                createVariation(productId, "256GB - 5G - Beige", "TS9-256-5G-BG", new BigDecimal("23990000"))
        );
    }

    private List<ProductVariation> createAirPodsProGen2Variations(Integer productId) {
        return List.of(
                createVariation(productId, "USB-C", "APP2-USBC", new BigDecimal("5990000"))
        );
    }

    private List<ProductVariation> createSonyXM5Variations(Integer productId) {
        return List.of(
                createVariation(productId, "Đen", "WH1000XM5-BLACK", new BigDecimal("8990000")),
                createVariation(productId, "Bạc", "WH1000XM5-SILVER", new BigDecimal("8990000"))
        );
    }
    
    private List<ProductVariation> createGalaxyBuds3Variations(Integer productId) {
        return List.of(
                createVariation(productId, "Bạc", "GB3-SILVER", new BigDecimal("4990000")),
                createVariation(productId, "Trắng", "GB3-WHITE", new BigDecimal("4990000"))
        );
    }
    
    private List<ProductVariation> createAppleWatchUltra2Variations(Integer productId) {
        return List.of(
                createVariation(productId, "49mm - Dây Alpine", "AWU2-49-ALP", new BigDecimal("21990000"))
        );
    }
    
    private List<ProductVariation> createGalaxyWatch6Variations(Integer productId) {
        return List.of(
                createVariation(productId, "40mm - Bluetooth - Vàng", "GW6-40-BT-GD", new BigDecimal("6990000")),
                createVariation(productId, "44mm - LTE - Đen", "GW6-44-LTE-BK", new BigDecimal("8490000"))
        );
    }
    
    private List<ProductVariation> createAnkerChargerVariations(Integer productId) {
        return List.of(
                createVariation(productId, "Trắng", "ANK-C30-W", new BigDecimal("550000"))
        );
    }
    
    private List<ProductVariation> createKeychronK2Variations(Integer productId) {
        return List.of(
                createVariation(productId, "Gateron Brown Switch", "KEY-K2-BR", new BigDecimal("2190000")),
                createVariation(productId, "Gateron Blue Switch", "KEY-K2-BL", new BigDecimal("2190000"))
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
        // Always run this seeder to ensure data is fresh
        return false;
    }
}

