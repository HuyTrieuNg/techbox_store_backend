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
import java.util.Collections;
import java.util.List;
import java.util.Random;

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
        Random rand = new Random();

        // Shuffle variations and partition into chunks of max 10 to ensure every variation is imported at least once
        List<ProductVariation> variationsShuffled = new ArrayList<>(allVariations);
        Collections.shuffle(variationsShuffled, rand);

        int maxPerImport = 10;
        int totalVariations = variationsShuffled.size();
        int importsNeeded = (int) Math.ceil((double) totalVariations / maxPerImport);

        // Create imports (one per chunk) with random supplier and dates
        for (int i = 0; i < importsNeeded; i++) {
            int supplierIdx = rand.nextInt(suppliers.size());
            Supplier chosen = suppliers.get(supplierIdx);
            LocalDateTime importDate = LocalDateTime.now().minusDays(rand.nextInt(60) + 1); // within last 60 days
            String note = "Seeded import batch " + (i + 1);
            StockImport imp = createStockImport(1, chosen.getSupplierId(), importDate, note);
            imports.add(imp);
        }

        // Save imports to obtain IDs
        imports = stockImportRepository.saveAll(imports);

        // For each chunk, create up to 10 items with random qty 20..50 and random cost multiplier
        for (int i = 0; i < imports.size(); i++) {
            int from = i * maxPerImport;
            int to = Math.min(from + maxPerImport, variationsShuffled.size());
            StockImport imp = imports.get(i);

            for (int idx = from; idx < to; idx++) {
                ProductVariation variation = variationsShuffled.get(idx);

                try {
                    if (variation == null) {
                        log.warn("Skipping null ProductVariation at index {}", idx);
                        continue;
                    }

                    if (variation.getPrice() == null) {
                        log.warn("Skipping variation id={} sku={} because price is null", variation.getId(), variation.getSku());
                        continue;
                    }

                    int quantity = 15 + rand.nextInt(30); // 15..44
                    // cost factor between 0.55 and 0.85
                    double factor = 0.55 + (rand.nextDouble() * 0.30);
                    BigDecimal costPrice = variation.getPrice().multiply(BigDecimal.valueOf(factor)).setScale(0, RoundingMode.HALF_UP);

                    StockImportItem item = StockImportItem.builder()
                            .stockImport(imp)
                            .productVariation(variation)
                            .quantity(quantity)
                            .costPrice(costPrice)
                            .build();
                    items.add(item);

                    // Update variation stock safely (handle nulls)
                    int currentQty = variation.getStockQuantity() != null ? variation.getStockQuantity() : 0;
                    BigDecimal currentAvg = variation.getAvgCostPrice() != null ? variation.getAvgCostPrice() : BigDecimal.ZERO;

                    // New totals
                    int newQty = currentQty + quantity;
                    variation.setStockQuantity(newQty);

                    if (currentQty == 0) {
                        variation.setAvgCostPrice(costPrice);
                    } else {
                        BigDecimal totalOldValue = currentAvg.multiply(BigDecimal.valueOf(currentQty));
                        BigDecimal newValue = costPrice.multiply(BigDecimal.valueOf(quantity));
                        BigDecimal newAvg = totalOldValue.add(newValue)
                                .divide(BigDecimal.valueOf(newQty), 2, RoundingMode.HALF_UP);
                        variation.setAvgCostPrice(newAvg);
                    }
                } catch (Exception e) {
                    log.warn("Failed to process variation id={} sku={} for import {}: {}", variation != null ? variation.getId() : null,
                            variation != null ? variation.getSku() : null, imp.getId(), e.getMessage(), e);
                    // continue processing remaining variations
                }
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
