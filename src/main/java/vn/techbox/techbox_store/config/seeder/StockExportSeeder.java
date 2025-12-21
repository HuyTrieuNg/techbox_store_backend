package vn.techbox.techbox_store.config.seeder;

import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Component;
import org.springframework.transaction.annotation.Transactional;
import vn.techbox.techbox_store.inventory.model.StockExport;
import vn.techbox.techbox_store.inventory.model.StockExportItem;
import vn.techbox.techbox_store.inventory.repository.StockExportRepository;
import vn.techbox.techbox_store.product.model.ProductVariation;
import vn.techbox.techbox_store.product.repository.ProductVariationRepository;
import vn.techbox.techbox_store.user.model.User;
import vn.techbox.techbox_store.user.repository.UserRepository;

import java.math.BigDecimal;
import java.time.LocalDateTime;
import java.util.ArrayList;
import java.util.Collections;
import java.util.List;
import java.util.Random;

@Component
@RequiredArgsConstructor
@Slf4j
public class StockExportSeeder implements DataSeeder {

    private final StockExportRepository stockExportRepository;
    private final ProductVariationRepository productVariationRepository;
    private final UserRepository userRepository;

    @Override
    public int getOrder() {
        return 12; // Run after StockAdjustmentSeeder
    }

    @Override
    public boolean shouldSkip() {
        long count = stockExportRepository.count();
        if (count > 0) {
            log.info("Stock exports already exist ({}), skipping seeder", count);
            return true;
        }
        return false;
    }

    @Override
    @Transactional
    public void seed() {
        log.info("Starting Stock Exports seeding...");
        
        List<ProductVariation> variations = productVariationRepository.findAll();
        List<User> users = userRepository.findAll();
        
        if (variations.isEmpty() || users.isEmpty()) {
            log.warn("Missing base data (variations or users), skipping seeding");
            return;
        }
        
        User staffUser = users.stream()
                .filter(u -> u.getAccount() != null && u.getAccount().getEmail() != null
                        && (u.getAccount().getEmail().contains("staff")
                        || u.getAccount().getEmail().contains("admin")))
                .findFirst()
                .orElse(users.get(0));
        
        // Generate 15 random stock exports
        Random rand = new Random();
        List<StockExport> exports = new ArrayList<>();
        int exportsToCreate = 15;

        for (int i = 0; i < exportsToCreate; i++) {
            LocalDateTime exportDate = LocalDateTime.now().minusDays(rand.nextInt(90) + 1)
                    .withHour(8 + rand.nextInt(9))
                    .withMinute(rand.nextInt(60));
            String docCode = String.format("EXP-%s-%04d", exportDate.toLocalDate().toString().replace("-", ""), i + 1);

            // Occasionally have null order (demo/other) or random order id
            Integer orderId = rand.nextDouble() < 0.2 ? null : 1000 + rand.nextInt(5000);

            StockExport exp = StockExport.builder()
                    .documentCode(docCode)
                    .userId(staffUser.getId())
                    .orderId(orderId)
                    .exportDate(exportDate)
                    .totalCogsValue(BigDecimal.ZERO)
                    .note("Auto-generated export #" + (i + 1))
                    .build();

            // pick 1..4 items
            int itemsCount = 1 + rand.nextInt(Math.min(4, variations.size()));
            List<ProductVariation> shuffled = new ArrayList<>(variations);
            Collections.shuffle(shuffled, rand);

            for (int j = 0; j < itemsCount; j++) {
                ProductVariation v = shuffled.get(j);
                int qty = 1 + rand.nextInt(5); // 1..5
                BigDecimal cost = v.getAvgCostPrice() != null ? v.getAvgCostPrice() : v.getPrice().multiply(new BigDecimal("0.7"));

                StockExportItem it = StockExportItem.builder()
                        .productVariation(v)
                        .quantity(qty)
                        .costPrice(cost)
                        .build();
                exp.addItem(it);

                // Decrease variation stock if available
                int curQty = v.getStockQuantity() != null ? v.getStockQuantity() : 0;
                v.setStockQuantity(Math.max(0, curQty - qty));
            }

            exp.calculateTotalCogsValue();
            exports.add(exp);
        }

        stockExportRepository.saveAll(exports);
        // Persist variation stock changes
        productVariationRepository.saveAll(variations);

        int totalExportItems = exports.stream().mapToInt(e -> e.getItems().size()).sum();
        log.info("✓ Created {} stock exports with {} items (auto-generated)", exports.size(), totalExportItems);
    }
}
