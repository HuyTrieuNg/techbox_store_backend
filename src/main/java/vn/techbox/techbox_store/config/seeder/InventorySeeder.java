package vn.techbox.techbox_store.config.seeder;

import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Component;
import org.springframework.transaction.annotation.Transactional;
import vn.techbox.techbox_store.inventory.model.StockImport;
import vn.techbox.techbox_store.inventory.model.StockImportItem;
import vn.techbox.techbox_store.inventory.model.Supplier;
import vn.techbox.techbox_store.inventory.repository.StockImportItemRepository;
import vn.techbox.techbox_store.inventory.repository.StockImportRepository;
import vn.techbox.techbox_store.inventory.repository.SupplierRepository;
import vn.techbox.techbox_store.product.model.Brand;
import vn.techbox.techbox_store.product.model.Category;
import vn.techbox.techbox_store.product.model.Product;
import vn.techbox.techbox_store.product.model.ProductVariation;
import vn.techbox.techbox_store.product.repository.BrandRepository;
import vn.techbox.techbox_store.product.repository.CategoryRepository;
import vn.techbox.techbox_store.product.repository.ProductRepository;
import vn.techbox.techbox_store.product.repository.ProductVariationRepository;

import java.math.BigDecimal;
import java.math.RoundingMode;
import java.time.LocalDateTime;
import java.util.ArrayList;
import java.util.List;
import java.util.Map;
import java.util.Random;
import java.util.function.Function;
import java.util.stream.Collectors;

@Component
@RequiredArgsConstructor
@Slf4j
public class InventorySeeder implements DataSeeder {

    private final SupplierRepository supplierRepository;
    private final StockImportRepository stockImportRepository;
    private final StockImportItemRepository stockImportItemRepository;
    private final ProductVariationRepository productVariationRepository;
    private final ProductRepository productRepository;
    private final BrandRepository brandRepository;
    private final CategoryRepository categoryRepository;
    private final Random random = new Random();

    @Override
    @Transactional
    public void seed() {
        // Create suppliers
        Map<String, Supplier> suppliers = createSuppliers().stream()
                .collect(Collectors.toMap(Supplier::getName, Function.identity()));
        supplierRepository.saveAll(suppliers.values());
        log.info("✓ Created {} suppliers", suppliers.size());

        // Pre-fetch all necessary data
        List<ProductVariation> allVariations = productVariationRepository.findAll();
        if (allVariations.isEmpty()) {
            log.warn("⚠️ No product variations found. Skipping inventory seeding.");
            return;
        }
        Map<Integer, Product> productMap = productRepository.findAllById(allVariations.stream()
                        .map(ProductVariation::getProductId).collect(Collectors.toSet()))
                .stream().collect(Collectors.toMap(Product::getId, Function.identity()));
        Map<Integer, Brand> brandMap = brandRepository.findAll().stream()
                .collect(Collectors.toMap(Brand::getId, Function.identity()));
        Map<Integer, Category> categoryMap = categoryRepository.findAll().stream()
                .collect(Collectors.toMap(Category::getId, Function.identity()));

        // Create stock imports for each major supplier
        Map<String, StockImport> imports = createStockImports(suppliers);
        stockImportRepository.saveAll(imports.values());

        List<StockImportItem> allItems = new ArrayList<>();
        
        for (ProductVariation variation : allVariations) {
            Product product = productMap.get(variation.getProductId());
            if (product == null) continue;

            Brand brand = brandMap.get(product.getBrandId());
            Category category = categoryMap.get(product.getCategoryId());
            if (brand == null || category == null) continue;

            // Determine supplier, quantity, and cost based on rules
            StockingRule rule = determineStockingRule(brand, category);
            Supplier supplier = suppliers.get(rule.supplierName);
            StockImport stockImport = imports.get(rule.supplierName);

            if (supplier == null || stockImport == null) {
                log.warn("Could not find supplier or import for brand '{}'. Skipping variation '{}'.", brand.getName(), variation.getSku());
                continue;
            }

            int quantity = random.nextInt(rule.maxQuantity - rule.minQuantity + 1) + rule.minQuantity;
            BigDecimal costPrice = variation.getPrice().multiply(rule.costMultiplier)
                    .setScale(0, RoundingMode.HALF_UP);

            // Create import item
            StockImportItem item = StockImportItem.builder()
                    .stockImport(stockImport)
                    .productVariation(variation)
                    .quantity(quantity)
                    .costPrice(costPrice)
                    .build();
            allItems.add(item);

            // Update product variation stock
            updateVariationStock(variation, quantity, costPrice);
        }

        // Update import total cost values
        imports.values().forEach(imp -> {
            BigDecimal total = allItems.stream()
                    .filter(item -> item.getStockImport().getId().equals(imp.getId()))
                    .map(StockImportItem::getTotalValue)
                    .reduce(BigDecimal.ZERO, BigDecimal::add);
            imp.setTotalCostValue(total);
        });

        stockImportItemRepository.saveAll(allItems);
        productVariationRepository.saveAll(allVariations);
        stockImportRepository.saveAll(imports.values());

        log.info("✓ Created {} stock imports with {} items", imports.size(), allItems.size());
        log.info("✓ Updated stock quantities for {} product variations", allVariations.size());
    }

    private void updateVariationStock(ProductVariation variation, int newQuantity, BigDecimal newCostPrice) {
        BigDecimal currentAvgCost = variation.getAvgCostPrice() != null ? variation.getAvgCostPrice() : BigDecimal.ZERO;
        int currentStock = variation.getStockQuantity() != null ? variation.getStockQuantity() : 0;

        if (currentStock + newQuantity > 0) {
            BigDecimal totalOldValue = currentAvgCost.multiply(BigDecimal.valueOf(currentStock));
            BigDecimal totalNewValue = newCostPrice.multiply(BigDecimal.valueOf(newQuantity));
            BigDecimal newAvgCost = totalOldValue.add(totalNewValue)
                    .divide(BigDecimal.valueOf(currentStock + newQuantity), 2, RoundingMode.HALF_UP);
            variation.setAvgCostPrice(newAvgCost);
        } else {
            variation.setAvgCostPrice(BigDecimal.ZERO);
        }
        
        variation.setStockQuantity(currentStock + newQuantity);
    }

    private StockingRule determineStockingRule(Brand brand, Category category) {
        String brandName = brand.getName();
        String categoryName = category.getName();

        return switch (brandName) {
            case "Apple" -> new StockingRule("FPT Trading (Apple)", 10, 40, new BigDecimal("0.80"));
            case "Samsung" -> new StockingRule("Digiworld (Samsung)", 15, 50, new BigDecimal("0.75"));
            case "Xiaomi" -> new StockingRule("Digiworld (Xiaomi)", 30, 80, new BigDecimal("0.65"));
            case "Dell", "Asus", "HP", "Lenovo", "Acer", "MSI" -> new StockingRule("FPT Trading (Laptops)", 5, 25, new BigDecimal("0.78"));
            case "Sony", "JBL" -> new StockingRule("Phuc Giang (PGI)", 20, 60, new BigDecimal("0.60"));
            case "Anker", "Belkin", "Keychron" -> new StockingRule("Petrosetco (PSD)", 25, 100, new BigDecimal("0.55"));
            default -> new StockingRule("Digiworld (Xiaomi)", 10, 30, new BigDecimal("0.70")); // Default rule
        };
    }

    private List<Supplier> createSuppliers() {
        return List.of(
                createSupplier("FPT Trading (Apple)", "0901111111", "contact@fpt-trading.com.vn", "Khu chế xuất Tân Thuận, Q.7, TP.HCM"),
                createSupplier("Digiworld (Samsung)", "0902222222", "samsung.dist@digiworld.com.vn", "195-197 Nguyễn Thái Bình, Q.1, TP.HCM"),
                createSupplier("Digiworld (Xiaomi)", "0903333333", "xiaomi.dist@digiworld.com.vn", "195-197 Nguyễn Thái Bình, Q.1, TP.HCM"),
                createSupplier("FPT Trading (Laptops)", "0904444444", "laptops.dist@fpt-trading.com.vn", "Khu chế xuất Tân Thuận, Q.7, TP.HCM"),
                createSupplier("Phuc Giang (PGI)", "0905555555", "info@pgi.com.vn", "10 Trịnh Văn Cấn, Q.1, TP.HCM"),
                createSupplier("Petrosetco (PSD)", "0906666666", "accessories@psd.com.vn", "Tòa nhà PetroVietnam, 1-5 Lê Duẩn, Q.1, TP.HCM")
        );
    }

    private Supplier createSupplier(String name, String phone, String email, String address) {
        return Supplier.builder().name(name).phone(phone).email(email).address(address).build();
    }

    private Map<String, StockImport> createStockImports(Map<String, Supplier> suppliers) {
        return suppliers.entrySet().stream()
                .collect(Collectors.toMap(
                        Map.Entry::getKey,
                        entry -> createStockImport(1, entry.getValue().getSupplierId(),
                                LocalDateTime.now().minusDays(random.nextInt(10) + 5),
                                "Initial stock import for " + entry.getKey())
                ));
    }



    private StockImport createStockImport(Integer userId, Integer supplierId,
                                         LocalDateTime importDate, String note) {
        return StockImport.builder()
                .userId(userId)
                .supplierId(supplierId)
                .importDate(importDate)
                .note(note)
                .totalCostValue(BigDecimal.ZERO) // Will be calculated
                .build();
    }

    @Override
    public int getOrder() {
        return 6; // After Product
    }

    @Override
    public boolean shouldSkip() {
        return supplierRepository.count() > 0 || stockImportRepository.count() > 0;
    }
    
    // Helper record for stocking rules
    private record StockingRule(String supplierName, int minQuantity, int maxQuantity, BigDecimal costMultiplier) {}
}
