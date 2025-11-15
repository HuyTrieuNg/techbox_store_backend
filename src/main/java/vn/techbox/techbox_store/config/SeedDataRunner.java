package vn.techbox.techbox_store.config;

import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.boot.CommandLineRunner;
import org.springframework.stereotype.Component;
import vn.techbox.techbox_store.inventory.model.*;
import vn.techbox.techbox_store.inventory.repository.*;
import vn.techbox.techbox_store.order.model.*;
import vn.techbox.techbox_store.order.repository.OrderRepository;
import vn.techbox.techbox_store.product.model.ProductVariation;
import vn.techbox.techbox_store.product.repository.ProductVariationRepository;
import vn.techbox.techbox_store.user.model.User;
import vn.techbox.techbox_store.user.model.Account;
import vn.techbox.techbox_store.user.repository.UserRepository;
import vn.techbox.techbox_store.payment.model.PaymentMethod;
import vn.techbox.techbox_store.payment.model.PaymentStatus;
import vn.techbox.techbox_store.payment.model.CodPayment;

import java.math.BigDecimal;
import java.time.LocalDateTime;
import java.util.Arrays;

@Component
@RequiredArgsConstructor
@Slf4j
public class SeedDataRunner implements CommandLineRunner {

    private final OrderRepository orderRepository;
    private final StockImportRepository stockImportRepository;
    private final StockExportRepository stockExportRepository;
    private final StockAdjustmentRepository stockAdjustmentRepository;
    private final ProductVariationRepository productVariationRepository;
    private final UserRepository userRepository;
    private final SupplierRepository supplierRepository;

    @Override
    public void run(String... args) throws Exception {
        log.info("Starting seed data initialization...");

        // Seed users if not exists
        seedUsers();

        // Seed suppliers if not exists
        seedSuppliers();

        // Seed product variations if not exists
        seedProductVariations();

        // Seed orders
        seedOrders();

        // Seed stock imports
        seedStockImports();

        // Seed stock exports
        seedStockExports();

        // Seed stock adjustments
        seedStockAdjustments();

        log.info("Seed data initialization completed.");
    }

    private void seedUsers() {
        if (userRepository.count() == 0) {
            Account account1 = Account.builder()
                    .email("admin@techbox.com")
                    .passwordHash("$2a$10$dummyhashedpassword")
                    .build();
            User user1 = User.builder()
                    .firstName("Admin")
                    .lastName("User")
                    .phone("0123456789")
                    .account(account1)
                    .build();
            userRepository.save(user1);

            Account account2 = Account.builder()
                    .email("customer1@example.com")
                    .passwordHash("$2a$10$dummyhashedpassword")
                    .build();
            User user2 = User.builder()
                    .firstName("Customer")
                    .lastName("One")
                    .phone("0987654321")
                    .account(account2)
                    .build();
            userRepository.save(user2);

            log.info("Seeded 2 users");
        }
    }

    private void seedSuppliers() {
        if (supplierRepository.count() == 0) {
            Supplier supplier1 = Supplier.builder()
                    .name("TechBox Supplier")
                    .phone("0111111111")
                    .email("supplier@techbox.com")
                    .address("123 Supplier St, City")
                    .build();
            supplierRepository.save(supplier1);

            log.info("Seeded 1 supplier");
        }
    }

    private void seedProductVariations() {
        if (productVariationRepository.count() == 0) {
            ProductVariation pv1 = ProductVariation.builder()
                    .variationName("Laptop Model A")
                    .productId(1) // Dummy product ID
                    .price(BigDecimal.valueOf(500.00))
                    .sku("TB-LAP-001")
                    .stockQuantity(100)
                    .reservedQuantity(0)
                    .avgCostPrice(BigDecimal.valueOf(450.00))
                    .build();
            productVariationRepository.save(pv1);

            ProductVariation pv2 = ProductVariation.builder()
                    .variationName("Smartphone Model B")
                    .productId(2) // Dummy product ID
                    .price(BigDecimal.valueOf(300.00))
                    .sku("TB-PHONE-001")
                    .stockQuantity(200)
                    .reservedQuantity(0)
                    .avgCostPrice(BigDecimal.valueOf(250.00))
                    .build();
            productVariationRepository.save(pv2);

            log.info("Seeded 2 product variations");
        }
    }

    private void seedOrders() {
        if (orderRepository.count() == 0) {
            // Find user by email since no findByUsername
            User user = userRepository.findAll().stream()
                    .filter(u -> u.getAccount().getEmail().equals("customer1@example.com"))
                    .findFirst().orElseThrow();
            ProductVariation pv1 = productVariationRepository.findAll().stream()
                    .filter(pv -> pv.getSku().equals("TB-LAP-001"))
                    .findFirst().orElseThrow();
            ProductVariation pv2 = productVariationRepository.findAll().stream()
                    .filter(pv -> pv.getSku().equals("TB-PHONE-001"))
                    .findFirst().orElseThrow();

            // Create shipping info
            OrderShippingInfo shipping = OrderShippingInfo.builder()
                    .shippingName("Customer One")
                    .shippingPhone("0987654321")
                    .shippingAddress("123 Customer St, City")
                    .build();

            // Create payment info
            CodPayment payment = CodPayment.builder()
                    .paymentMethod(PaymentMethod.COD)
                    .paymentStatus(PaymentStatus.PENDING)
                    .totalAmount(BigDecimal.valueOf(800.00))
                    .build();

            Order order1 = Order.builder()
                    .user(user)
                    .status(OrderStatus.CONFIRMED)
                    .createdAt(LocalDateTime.now().minusDays(2))
                    .shippingInfo(shipping)
                    .paymentInfo(payment)
                    .build();

            OrderItem item1 = OrderItem.builder()
                    .productVariation(pv1)
                    .quantity(1)
                    .unitPrice(BigDecimal.valueOf(500.00))
                    .totalPrice(BigDecimal.valueOf(500.00))
                    .build();
            item1.setOrder(order1);

            OrderItem item2 = OrderItem.builder()
                    .productVariation(pv2)
                    .quantity(1)
                    .unitPrice(BigDecimal.valueOf(300.00))
                    .totalPrice(BigDecimal.valueOf(300.00))
                    .build();
            item2.setOrder(order1);

            order1.setOrderItems(Arrays.asList(item1, item2));
            orderRepository.save(order1);

            log.info("Seeded 1 order");
        }
    }

    private void seedStockImports() {
        if (stockImportRepository.count() == 0) {
            Supplier supplier = supplierRepository.findAll().get(0);
            ProductVariation pv1 = productVariationRepository.findAll().stream()
                    .filter(pv -> pv.getSku().equals("TB-LAP-001"))
                    .findFirst().orElseThrow();

            StockImport stockImport = StockImport.builder()
                    .userId(1)
                    .importDate(LocalDateTime.now().minusDays(5))
                    .supplierId(supplier.getSupplierId())
                    .totalCostValue(BigDecimal.valueOf(22500.00))
                    .note("Initial stock import")
                    .build();

            StockImportItem item = StockImportItem.builder()
                    .productVariation(pv1)
                    .quantity(50)
                    .costPrice(BigDecimal.valueOf(450.00))
                    .build();
            item.setStockImport(stockImport);

            stockImport.setItems(Arrays.asList(item));
            stockImportRepository.save(stockImport);

            log.info("Seeded 1 stock import");
        }
    }

    private void seedStockExports() {
        if (stockExportRepository.count() == 0) {
            ProductVariation pv2 = productVariationRepository.findBySku("TB-PHONE-001").orElseThrow();

            StockExport stockExport = StockExport.builder()
                    .userId(1)
                    .exportDate(LocalDateTime.now().minusDays(1))
                    .note("Sale export")
                    .build();

            StockExportItem item = StockExportItem.builder()
                    .productVariation(pv2)
                    .quantity(10)
                    .costPrice(BigDecimal.valueOf(300.00))
                    .build();
            item.setStockExport(stockExport);

            stockExport.setItems(Arrays.asList(item));
            stockExportRepository.save(stockExport);

            log.info("Seeded 1 stock export");
        }
    }

    private void seedStockAdjustments() {
        if (stockAdjustmentRepository.count() == 0) {
            ProductVariation pv1 = productVariationRepository.findBySku("TB-LAP-001").orElseThrow();

            StockAdjustment adjustment = StockAdjustment.builder()
                    .userId(1)
                    .checkName("Monthly Stock Check")
                    .adjustmentDate(LocalDateTime.now().minusDays(3))
                    .note("Stock adjustment after inventory check")
                    .build();

            StockAdjustmentItem item = StockAdjustmentItem.builder()
                    .productVariation(pv1)
                    .systemQty(100)
                    .realQty(95)
                    .diffQty(-5)
                    .costPrice(BigDecimal.valueOf(450.00))
                    .build();
            item.setStockAdjustment(adjustment);

            adjustment.setItems(Arrays.asList(item));
            stockAdjustmentRepository.save(adjustment);

            log.info("Seeded 1 stock adjustment");
        }
    }
}