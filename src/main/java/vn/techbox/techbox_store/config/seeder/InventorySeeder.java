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
import vn.techbox.techbox_store.product.model.ProductVariation;
import vn.techbox.techbox_store.product.repository.ProductVariationRepository;

import java.math.BigDecimal;
import java.math.RoundingMode;
import java.time.LocalDateTime;
import java.util.ArrayList;
import java.util.List;

@Component
@RequiredArgsConstructor
@Slf4j
public class InventorySeeder implements DataSeeder {

    private final SupplierRepository supplierRepository;
    private final StockImportRepository stockImportRepository;
    private final StockImportItemRepository stockImportItemRepository;
    private final ProductVariationRepository productVariationRepository;

    @Override
    @Transactional
    public void seed() {
        // Create suppliers
        List<Supplier> suppliers = createSuppliers();
        suppliers = supplierRepository.saveAll(suppliers);
        log.info("✓ Created {} suppliers", suppliers.size());

        // Create stock imports
        List<ProductVariation> allVariations = productVariationRepository.findAll();
        if (allVariations.isEmpty()) {
            log.warn("⚠️  No product variations found. Skipping inventory seeding.");
            return;
        }

        List<StockImport> imports = new ArrayList<>();
        List<StockImportItem> items = new ArrayList<>();

        // Import 1: Large initial stock for iPhones and Samsungs
        StockImport import1 = createStockImport(1, suppliers.get(0).getSupplierId(), 
                LocalDateTime.now().minusDays(30), "Nhập hàng iPhone và Samsung tháng 9");
        imports.add(import1);

        // Import 2: Xiaomi and accessories
        StockImport import2 = createStockImport(1, suppliers.get(1).getSupplierId(), 
                LocalDateTime.now().minusDays(25), "Nhập hàng Xiaomi và phụ kiện");
        imports.add(import2);

        // Import 3: Laptops
        StockImport import3 = createStockImport(1, suppliers.get(2).getSupplierId(), 
                LocalDateTime.now().minusDays(20), "Nhập hàng laptop");
        imports.add(import3);

        // Import 4: Audio devices
        StockImport import4 = createStockImport(1, suppliers.get(3).getSupplierId(), 
                LocalDateTime.now().minusDays(15), "Nhập hàng tai nghe và loa");
        imports.add(import4);

        // Save imports
        imports = stockImportRepository.saveAll(imports);

        // Create import items and update stock
        for (ProductVariation variation : allVariations) {
            StockImport targetImport;
            int quantity;
            BigDecimal costPrice;

            // Determine which import and quantities based on product category
            String sku = variation.getSku();
            if (sku.startsWith("IP15") || sku.startsWith("S24")) {
                // Premium phones - lower quantity, higher cost
                targetImport = imports.get(0);
                quantity = 30;
                costPrice = variation.getPrice().multiply(new BigDecimal("0.75")); // 75% of retail price
            } else if (sku.startsWith("IP14")) {
                // Mid-range phones
                targetImport = imports.get(0);
                quantity = 50;
                costPrice = variation.getPrice().multiply(new BigDecimal("0.70"));
            } else if (sku.startsWith("X14P")) {
                // Xiaomi
                targetImport = imports.get(1);
                quantity = 40;
                costPrice = variation.getPrice().multiply(new BigDecimal("0.65"));
            } else if (sku.startsWith("MBP") || sku.startsWith("XPS")) {
                // Laptops - lower quantity, premium pricing
                targetImport = imports.get(2);
                quantity = 15;
                costPrice = variation.getPrice().multiply(new BigDecimal("0.80"));
            } else if (sku.startsWith("APP") || sku.startsWith("WH")) {
                // Audio devices
                targetImport = imports.get(3);
                quantity = 60;
                costPrice = variation.getPrice().multiply(new BigDecimal("0.60"));
            } else {
                // Default
                targetImport = imports.get(0);
                quantity = 25;
                costPrice = variation.getPrice().multiply(new BigDecimal("0.70"));
            }

            // Create import item
            StockImportItem item = StockImportItem.builder()
                    .stockImport(targetImport)
                    .productVariation(variation)
                    .quantity(quantity)
                    .costPrice(costPrice.setScale(0, RoundingMode.HALF_UP))
                    .build();
            items.add(item);

            // Update product variation stock using weighted average
            variation.setStockQuantity(variation.getStockQuantity() + quantity);
            
            // Calculate weighted average cost price
            BigDecimal currentAvg = variation.getAvgCostPrice() != null ? 
                    variation.getAvgCostPrice() : BigDecimal.ZERO;
            int currentQty = variation.getStockQuantity() - quantity; // Old quantity
            
            if (currentQty == 0) {
                variation.setAvgCostPrice(costPrice);
            } else {
                BigDecimal totalOldValue = currentAvg.multiply(BigDecimal.valueOf(currentQty));
                BigDecimal newValue = costPrice.multiply(BigDecimal.valueOf(quantity));
                BigDecimal newAvg = totalOldValue.add(newValue)
                        .divide(BigDecimal.valueOf(variation.getStockQuantity()), 2, RoundingMode.HALF_UP);
                variation.setAvgCostPrice(newAvg);
            }
        }

        // Update import totals
        for (StockImport imp : imports) {
            BigDecimal total = items.stream()
                    .filter(item -> item.getStockImport().getId().equals(imp.getId()))
                    .map(StockImportItem::getTotalValue)
                    .reduce(BigDecimal.ZERO, BigDecimal::add);
            imp.setTotalCostValue(total);
        }

        stockImportItemRepository.saveAll(items);
        productVariationRepository.saveAll(allVariations);
        stockImportRepository.saveAll(imports);
        
        log.info("✓ Created {} stock imports with {} items", imports.size(), items.size());
        log.info("✓ Updated stock quantities for {} product variations", allVariations.size());
    }

    private List<Supplier> createSuppliers() {
        return List.of(
                Supplier.builder()
                        .name("Công ty TNHH Apple Việt Nam")
                        .phone("0901234567")
                        .email("apple.vn@supplier.com")
                        .address("123 Lê Lợi, Q.1, TP.HCM")
                        .build(),
                
                Supplier.builder()
                        .name("Nhà phân phối Xiaomi chính hãng")
                        .phone("0902345678")
                        .email("xiaomi.vn@supplier.com")
                        .address("456 Nguyễn Huệ, Q.1, TP.HCM")
                        .build(),
                
                Supplier.builder()
                        .name("Dell Technologies Vietnam")
                        .phone("0903456789")
                        .email("dell.vn@supplier.com")
                        .address("789 Hai Bà Trưng, Q.1, TP.HCM")
                        .build(),
                
                Supplier.builder()
                        .name("Sony Electronics Vietnam")
                        .phone("0904567890")
                        .email("sony.vn@supplier.com")
                        .address("321 Võ Văn Tần, Q.3, TP.HCM")
                        .build(),
                
                Supplier.builder()
                        .name("Samsung Vietnam Mobile")
                        .phone("0905678901")
                        .email("samsung.vn@supplier.com")
                        .address("654 Trần Hưng Đạo, Q.5, TP.HCM")
                        .build()
        );
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
}
