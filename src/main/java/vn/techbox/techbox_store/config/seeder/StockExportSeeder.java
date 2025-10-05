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
import java.util.List;

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
                .filter(u -> u.getEmail() != null && (u.getEmail().contains("staff") || u.getEmail().contains("admin")))
                .findFirst()
                .orElse(users.get(0));
        
        // Stock Export 1: Xuất kho cho đơn hàng #1001
        StockExport export1 = StockExport.builder()
                .documentCode("EXP-20240920-0001")
                .userId(staffUser.getId())
                .orderId(1001)
                .exportDate(LocalDateTime.of(2024, 9, 20, 14, 30))
                .totalCogsValue(BigDecimal.ZERO)
                .note("Xuất kho cho đơn hàng khách hàng #1001")
                .build();
        
        // Add items for export 1
        if (variations.size() > 0) {
            ProductVariation v1 = variations.get(0);
            StockExportItem item1 = StockExportItem.builder()
                    .productVariation(v1)
                    .quantity(2)
                    .costPrice(v1.getAvgCostPrice() != null ? v1.getAvgCostPrice() : v1.getPrice().multiply(new BigDecimal("0.7")))
                    .build();
            export1.addItem(item1);
        }
        
        if (variations.size() > 1) {
            ProductVariation v2 = variations.get(1);
            StockExportItem item2 = StockExportItem.builder()
                    .productVariation(v2)
                    .quantity(1)
                    .costPrice(v2.getAvgCostPrice() != null ? v2.getAvgCostPrice() : v2.getPrice().multiply(new BigDecimal("0.7")))
                    .build();
            export1.addItem(item2);
        }
        
        export1.calculateTotalCogsValue();
        stockExportRepository.save(export1);
        log.info("✓ Created stock export: {} with total COGS: {}", export1.getDocumentCode(), export1.getTotalCogsValue());
        
        // Stock Export 2: Xuất kho cho đơn hàng #1002
        StockExport export2 = StockExport.builder()
                .documentCode("EXP-20240925-0001")
                .userId(staffUser.getId())
                .orderId(1002)
                .exportDate(LocalDateTime.of(2024, 9, 25, 10, 15))
                .totalCogsValue(BigDecimal.ZERO)
                .note("Xuất kho cho đơn hàng khách hàng #1002")
                .build();
        
        // Add items for export 2
        if (variations.size() > 2) {
            ProductVariation v3 = variations.get(2);
            StockExportItem item3 = StockExportItem.builder()
                    .productVariation(v3)
                    .quantity(3)
                    .costPrice(v3.getAvgCostPrice() != null ? v3.getAvgCostPrice() : v3.getPrice().multiply(new BigDecimal("0.7")))
                    .build();
            export2.addItem(item3);
        }
        
        if (variations.size() > 3) {
            ProductVariation v4 = variations.get(3);
            StockExportItem item4 = StockExportItem.builder()
                    .productVariation(v4)
                    .quantity(1)
                    .costPrice(v4.getAvgCostPrice() != null ? v4.getAvgCostPrice() : v4.getPrice().multiply(new BigDecimal("0.7")))
                    .build();
            export2.addItem(item4);
        }
        
        export2.calculateTotalCogsValue();
        stockExportRepository.save(export2);
        log.info("✓ Created stock export: {} with total COGS: {}", export2.getDocumentCode(), export2.getTotalCogsValue());
        
        // Stock Export 3: Xuất kho cho đơn hàng #1003
        StockExport export3 = StockExport.builder()
                .documentCode("EXP-20241002-0001")
                .userId(staffUser.getId())
                .orderId(1003)
                .exportDate(LocalDateTime.of(2024, 10, 2, 16, 45))
                .totalCogsValue(BigDecimal.ZERO)
                .note("Xuất kho cho đơn hàng khách hàng #1003 - Đơn lớn")
                .build();
        
        // Add items for export 3
        if (variations.size() > 4) {
            ProductVariation v5 = variations.get(4);
            StockExportItem item5 = StockExportItem.builder()
                    .productVariation(v5)
                    .quantity(5)
                    .costPrice(v5.getAvgCostPrice() != null ? v5.getAvgCostPrice() : v5.getPrice().multiply(new BigDecimal("0.7")))
                    .build();
            export3.addItem(item5);
        }
        
        if (variations.size() > 5) {
            ProductVariation v6 = variations.get(5);
            StockExportItem item6 = StockExportItem.builder()
                    .productVariation(v6)
                    .quantity(2)
                    .costPrice(v6.getAvgCostPrice() != null ? v6.getAvgCostPrice() : v6.getPrice().multiply(new BigDecimal("0.7")))
                    .build();
            export3.addItem(item6);
        }
        
        if (variations.size() > 6) {
            ProductVariation v7 = variations.get(6);
            StockExportItem item7 = StockExportItem.builder()
                    .productVariation(v7)
                    .quantity(1)
                    .costPrice(v7.getAvgCostPrice() != null ? v7.getAvgCostPrice() : v7.getPrice().multiply(new BigDecimal("0.7")))
                    .build();
            export3.addItem(item7);
        }
        
        export3.calculateTotalCogsValue();
        stockExportRepository.save(export3);
        log.info("✓ Created stock export: {} with total COGS: {}", export3.getDocumentCode(), export3.getTotalCogsValue());
        
        // Stock Export 4: Xuất kho không có order (xuất hủy/demo)
        StockExport export4 = StockExport.builder()
                .documentCode("EXP-20241004-0001")
                .userId(staffUser.getId())
                .orderId(null)
                .exportDate(LocalDateTime.of(2024, 10, 4, 11, 20))
                .totalCogsValue(BigDecimal.ZERO)
                .note("Xuất kho để demo sản phẩm cho khách hàng tiềm năng")
                .build();
        
        // Add items for export 4
        if (variations.size() > 7) {
            ProductVariation v8 = variations.get(7);
            StockExportItem item8 = StockExportItem.builder()
                    .productVariation(v8)
                    .quantity(1)
                    .costPrice(v8.getAvgCostPrice() != null ? v8.getAvgCostPrice() : v8.getPrice().multiply(new BigDecimal("0.7")))
                    .build();
            export4.addItem(item8);
        }
        
        export4.calculateTotalCogsValue();
        stockExportRepository.save(export4);
        log.info("✓ Created stock export: {} with total COGS: {}", export4.getDocumentCode(), export4.getTotalCogsValue());
        
        log.info("Stock Exports seeding completed successfully. Created 4 exports with total {} items", 
                export1.getItems().size() + export2.getItems().size() + export3.getItems().size() + export4.getItems().size());
    }
}
