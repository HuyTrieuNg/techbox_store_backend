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
import java.util.List;

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
                .filter(u -> u.getEmail() != null && u.getEmail().contains("admin"))
                .findFirst()
                .orElse(users.get(0));
        
        // Stock Adjustment 1: Kiểm kê định kỳ tháng 9
        StockAdjustment adjustment1 = StockAdjustment.builder()
                .documentCode("ADJ-20240915-0001")
                .userId(adminUser.getId())
                .checkName("Kiểm kê định kỳ tháng 9/2024")
                .adjustmentDate(LocalDateTime.of(2024, 9, 15, 10, 0))
                .note("Kiểm kê định kỳ cuối quý 3")
                .build();
        
        // Add items for adjustment 1 (một số sản phẩm có chênh lệch nhỏ)
        if (variations.size() > 0) {
            ProductVariation v1 = variations.get(0);
            StockAdjustmentItem item1 = StockAdjustmentItem.builder()
                    .productVariation(v1)
                    .systemQty(v1.getStockQuantity() != null ? v1.getStockQuantity() : 100)
                    .realQty((v1.getStockQuantity() != null ? v1.getStockQuantity() : 100) - 2)
                    .costPrice(v1.getAvgCostPrice() != null ? v1.getAvgCostPrice() : v1.getPrice().multiply(new BigDecimal("0.7")))
                    .note("Thiếu 2 sản phẩm do hỏng hóc")
                    .build();
            adjustment1.addItem(item1);
        }
        
        if (variations.size() > 1) {
            ProductVariation v2 = variations.get(1);
            StockAdjustmentItem item2 = StockAdjustmentItem.builder()
                    .productVariation(v2)
                    .systemQty(v2.getStockQuantity() != null ? v2.getStockQuantity() : 50)
                    .realQty((v2.getStockQuantity() != null ? v2.getStockQuantity() : 50) + 1)
                    .costPrice(v2.getAvgCostPrice() != null ? v2.getAvgCostPrice() : v2.getPrice().multiply(new BigDecimal("0.7")))
                    .note("Thừa 1 sản phẩm - lỗi nhập liệu trước đó")
                    .build();
            adjustment1.addItem(item2);
        }
        
        if (variations.size() > 2) {
            ProductVariation v3 = variations.get(2);
            StockAdjustmentItem item3 = StockAdjustmentItem.builder()
                    .productVariation(v3)
                    .systemQty(v3.getStockQuantity() != null ? v3.getStockQuantity() : 30)
                    .realQty(v3.getStockQuantity() != null ? v3.getStockQuantity() : 30)
                    .costPrice(v3.getAvgCostPrice() != null ? v3.getAvgCostPrice() : v3.getPrice().multiply(new BigDecimal("0.7")))
                    .note("Khớp với hệ thống")
                    .build();
            adjustment1.addItem(item3);
        }
        
        stockAdjustmentRepository.save(adjustment1);
        log.info("✓ Created stock adjustment: {}", adjustment1.getDocumentCode());
        
        // Stock Adjustment 2: Kiểm kê sau nhập hàng
        StockAdjustment adjustment2 = StockAdjustment.builder()
                .documentCode("ADJ-20241001-0001")
                .userId(adminUser.getId())
                .checkName("Kiểm kê sau nhập hàng đầu tháng 10")
                .adjustmentDate(LocalDateTime.of(2024, 10, 1, 14, 30))
                .note("Kiểm tra số lượng sau khi nhập hàng lớn")
                .build();
        
        // Add items for adjustment 2
        if (variations.size() > 3) {
            ProductVariation v4 = variations.get(3);
            StockAdjustmentItem item4 = StockAdjustmentItem.builder()
                    .productVariation(v4)
                    .systemQty(v4.getStockQuantity() != null ? v4.getStockQuantity() : 80)
                    .realQty((v4.getStockQuantity() != null ? v4.getStockQuantity() : 80) - 3)
                    .costPrice(v4.getAvgCostPrice() != null ? v4.getAvgCostPrice() : v4.getPrice().multiply(new BigDecimal("0.7")))
                    .note("Thiếu 3 sản phẩm - đang điều tra")
                    .build();
            adjustment2.addItem(item4);
        }
        
        if (variations.size() > 4) {
            ProductVariation v5 = variations.get(4);
            StockAdjustmentItem item5 = StockAdjustmentItem.builder()
                    .productVariation(v5)
                    .systemQty(v5.getStockQuantity() != null ? v5.getStockQuantity() : 60)
                    .realQty(v5.getStockQuantity() != null ? v5.getStockQuantity() : 60)
                    .costPrice(v5.getAvgCostPrice() != null ? v5.getAvgCostPrice() : v5.getPrice().multiply(new BigDecimal("0.7")))
                    .note("Số lượng chính xác")
                    .build();
            adjustment2.addItem(item5);
        }
        
        stockAdjustmentRepository.save(adjustment2);
        log.info("✓ Created stock adjustment: {}", adjustment2.getDocumentCode());
        
        // Stock Adjustment 3: Kiểm kê đột xuất
        StockAdjustment adjustment3 = StockAdjustment.builder()
                .documentCode("ADJ-20241005-0001")
                .userId(adminUser.getId())
                .checkName("Kiểm kê đột xuất - Kiểm tra hàng tồn kho")
                .adjustmentDate(LocalDateTime.of(2024, 10, 5, 9, 15))
                .note("Kiểm tra do phát hiện bất thường trong báo cáo")
                .build();
        
        // Add items for adjustment 3
        if (variations.size() > 5) {
            ProductVariation v6 = variations.get(5);
            StockAdjustmentItem item6 = StockAdjustmentItem.builder()
                    .productVariation(v6)
                    .systemQty(v6.getStockQuantity() != null ? v6.getStockQuantity() : 40)
                    .realQty((v6.getStockQuantity() != null ? v6.getStockQuantity() : 40) + 2)
                    .costPrice(v6.getAvgCostPrice() != null ? v6.getAvgCostPrice() : v6.getPrice().multiply(new BigDecimal("0.7")))
                    .note("Thừa 2 sản phẩm - chưa cập nhật vào hệ thống")
                    .build();
            adjustment3.addItem(item6);
        }
        
        if (variations.size() > 6) {
            ProductVariation v7 = variations.get(6);
            StockAdjustmentItem item7 = StockAdjustmentItem.builder()
                    .productVariation(v7)
                    .systemQty(v7.getStockQuantity() != null ? v7.getStockQuantity() : 25)
                    .realQty((v7.getStockQuantity() != null ? v7.getStockQuantity() : 25) - 1)
                    .costPrice(v7.getAvgCostPrice() != null ? v7.getAvgCostPrice() : v7.getPrice().multiply(new BigDecimal("0.7")))
                    .note("Thiếu 1 sản phẩm - đã báo cáo")
                    .build();
            adjustment3.addItem(item7);
        }
        
        stockAdjustmentRepository.save(adjustment3);
        log.info("✓ Created stock adjustment: {}", adjustment3.getDocumentCode());
        
        log.info("Stock Adjustments seeding completed successfully. Created 3 adjustments with {} items", 
                adjustment1.getItems().size() + adjustment2.getItems().size() + adjustment3.getItems().size());
    }
}
