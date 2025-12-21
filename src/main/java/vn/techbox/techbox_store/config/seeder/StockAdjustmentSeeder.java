package vn.techbox.techbox_store.config.seeder;

import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Component;
import org.springframework.transaction.annotation.Transactional;
import vn.techbox.techbox_store.inventory.model.StockAdjustment;
import vn.techbox.techbox_store.inventory.model.StockAdjustmentItem;
import vn.techbox.techbox_store.inventory.repository.StockAdjustmentRepository;
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
public class StockAdjustmentSeeder implements DataSeeder {

    private final StockAdjustmentRepository stockAdjustmentRepository;
    private final ProductVariationRepository productVariationRepository;
    private final UserRepository userRepository;

    @Override
    public int getOrder() {
        return 11; // Run after ProductAttributesSeeder
    }

    @Override
    public boolean shouldSkip() {
        long count = stockAdjustmentRepository.count();
        if (count > 0) {
            log.info("Stock adjustments already exist ({}), skipping seeder", count);
            return true;
        }
        return false;
    }

    @Override
    @Transactional
    public void seed() {
        log.info("Starting Stock Adjustments seeding...");
        
        List<ProductVariation> variations = productVariationRepository.findAll();
        List<User> users = userRepository.findAll();
        
        if (variations.isEmpty() || users.isEmpty()) {
            log.warn("Missing base data (variations or users), skipping seeding");
            return;
        }
        
        User adminUser = users.stream()
                .filter(u -> u.getAccount() != null && u.getAccount().getEmail() != null && u.getAccount().getEmail().contains("admin"))
                .findFirst()
                .orElse(users.get(0));
        
        // Create multiple random stock adjustments (15) each with a few random items
        Random rand = new Random();
        List<StockAdjustment> adjustments = new ArrayList<>();
        int adjustmentsToCreate = 40;

        for (int i = 0; i < adjustmentsToCreate; i++) {
            LocalDateTime adjDate = LocalDateTime.now().minusDays(rand.nextInt(120) + 1)
                    .withHour(8 + rand.nextInt(9))
                    .withMinute(rand.nextInt(60));
            String docCode = String.format("ADJ-%s-%04d", adjDate.toLocalDate().toString().replace("-", ""), i + 1);
            StockAdjustment adj = StockAdjustment.builder()
                    .documentCode(docCode)
                    .userId(adminUser.getId())
                    .checkName("Kiểm kê " + (i + 1))
                    .adjustmentDate(adjDate)
                    .note("Auto-generated adjustment #" + (i + 1))
                    .build();

            // pick 1..min(5, variations.size()) distinct variations
            int itemCount = Math.min(5, Math.max(1, 1 + rand.nextInt(5)));
            List<ProductVariation> shuffled = new ArrayList<>(variations);
            Collections.shuffle(shuffled, rand);
            for (int j = 0; j < itemCount && j < shuffled.size(); j++) {
                ProductVariation v = shuffled.get(j);
                int systemQty = v.getStockQuantity() != null ? v.getStockQuantity() : (20 + rand.nextInt(100));
                int realQty = systemQty + (rand.nextInt(11) - 5); // -5..+5
                if (realQty < 0) realQty = 0;
                BigDecimal cost = v.getAvgCostPrice() != null ? v.getAvgCostPrice() : v.getPrice().multiply(new BigDecimal("0.7"));

                StockAdjustmentItem it = StockAdjustmentItem.builder()
                        .productVariation(v)
                        .systemQty(systemQty)
                        .realQty(realQty)
                        .costPrice(cost)
                        .note(realQty == systemQty ? "Khớp" : (realQty > systemQty ? "Thừa" : "Thiếu"))
                        .build();
                adj.addItem(it);
            }

            adjustments.add(adj);
        }

        stockAdjustmentRepository.saveAll(adjustments);
        log.info("✓ Created {} stock adjustments (auto-generated)", adjustments.size());
        int totalItems = adjustments.stream().mapToInt(a -> a.getItems().size()).sum();
        log.info("Stock Adjustments seeding completed successfully. Created {} adjustments with {} items", adjustments.size(), totalItems);
    }
}
