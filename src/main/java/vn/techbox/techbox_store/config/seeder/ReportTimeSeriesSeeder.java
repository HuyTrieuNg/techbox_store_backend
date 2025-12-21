package vn.techbox.techbox_store.config.seeder;

// Tạo dataseed cho báo cáo thời gian thực dựa trên đơn hàng đã tạo trong quá khứ

import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.jdbc.core.JdbcTemplate;
import org.springframework.stereotype.Component;
import org.springframework.transaction.annotation.Transactional;
import vn.techbox.techbox_store.order.model.Order;
import vn.techbox.techbox_store.order.model.OrderItem;
import vn.techbox.techbox_store.order.model.OrderShippingInfo;
import vn.techbox.techbox_store.order.model.OrderStatus;
import vn.techbox.techbox_store.order.repository.OrderRepository;
import vn.techbox.techbox_store.payment.model.CodPayment;
import vn.techbox.techbox_store.payment.model.Payment;
import vn.techbox.techbox_store.payment.model.VnpayPayment;
import vn.techbox.techbox_store.payment.model.PaymentStatus;
import vn.techbox.techbox_store.payment.model.PaymentMethod;
import vn.techbox.techbox_store.payment.repository.PaymentRepository;
import vn.techbox.techbox_store.product.model.ProductVariation;
import vn.techbox.techbox_store.product.repository.ProductVariationRepository;
import vn.techbox.techbox_store.inventory.model.StockExport;
import vn.techbox.techbox_store.inventory.model.StockExportItem;
import vn.techbox.techbox_store.inventory.repository.StockExportRepository;
import vn.techbox.techbox_store.user.repository.UserRepository;
import vn.techbox.techbox_store.user.repository.AccountRepository;
import vn.techbox.techbox_store.user.repository.RoleRepository;
import vn.techbox.techbox_store.user.model.User;
import vn.techbox.techbox_store.user.model.Account;
import vn.techbox.techbox_store.user.model.UserRole;
import org.springframework.security.crypto.password.PasswordEncoder;

import java.math.BigDecimal;
import java.time.LocalDateTime;
import java.time.LocalDate;
import java.util.ArrayList;
import java.util.HashSet;
import java.util.List;
import java.util.Random;

@Component
@RequiredArgsConstructor
@Slf4j
public class ReportTimeSeriesSeeder implements DataSeeder {

    private final OrderRepository orderRepository;
    private final ProductVariationRepository productVariationRepository;
    private final UserRepository userRepository;
    private final AccountRepository accountRepository;
    private final RoleRepository roleRepository;
    private final PasswordEncoder passwordEncoder;
    private final PaymentRepository paymentRepository;
    private final StockExportRepository stockExportRepository;
    private final JdbcTemplate jdbcTemplate;

    private final Random rand = new Random();

    @Override
    public int getOrder() {
        return 11; // after inventory seeders but before stock exports which rely on order ids
    }

    @Override
    public boolean shouldSkip() {
        // Skip if there are already some orders in the database
        return orderRepository.count() > 0;
    }

    @Override
    @Transactional
    public void seed() {
        log.info("Starting report time-series seeding...");

        List<ProductVariation> variations = productVariationRepository.findAll();
        List<User> users = userRepository.findAll();

        if (variations.isEmpty() || users.isEmpty()) {
            log.warn("Missing base data (variations or users). Skipping report time-series seeding");
            return;
        }

        LocalDateTime now = LocalDateTime.now();
        // cap is today at midnight; seeded dates must be strictly before today
        LocalDateTime cap = now.toLocalDate().atStartOfDay();
        // Seed the last 12 months (one full year)
        LocalDateTime start = now.minusYears(1).withDayOfMonth(1).withHour(8).withMinute(0).withSecond(0).withNano(0);
        LocalDateTime end = now;

        List<Order> createdOrders = new ArrayList<>();

        // Add created demo users across months to drive Customer growth / new user reports.
        List<User> createdUsers = new ArrayList<>();
        var customerRoleOpt = roleRepository.findByName(UserRole.ROLE_CUSTOMER.getRoleName());
        
        if (customerRoleOpt.isEmpty()) {
            log.warn("Role {} not found. Skipping demo user creation", UserRole.ROLE_CUSTOMER.getRoleName());
        } else {
            var customerRole = customerRoleOpt.get();
            
            for (LocalDateTime month = start; month.isBefore(end); month = month.plusMonths(1)) {
                // create 3..5 demo customers per month (varied)
                int customersThisMonth = rand.nextInt(3) + 3; // 3..5
                for (int i = 0; i < customersThisMonth; i++) {
                    String seedEmail = "seed.customer." + month.getMonthValue() + "." + i + "@techbox.local";
                    
                    // Skip if account with email already exists
                    if (accountRepository.existsByEmail(seedEmail)) {
                        userRepository.findByAccountEmail(seedEmail).ifPresent(users::add);
                        continue;
                    }

                    // Create account with proper fields (matching InitialUserSeeder pattern)
                    Account account = Account.builder()
                        .email(seedEmail)
                        .passwordHash(passwordEncoder.encode("password"))
                        .isActive(true)
                        .isLocked(false)
                        .build();
                    account = accountRepository.save(account);

                    // Create user with roles
                    User u = User.builder()
                        .firstName("Seed")
                        .lastName("Customer-" + month.getMonthValue() + "-" + i)
                        .phone("090" + (1000000 + rand.nextInt(9000000)))
                        .account(account)
                        .roles(new HashSet<>())
                        .build();
                    User saved = userRepository.save(u);
                    
                    // Add customer role
                    saved.getRoles().add(customerRole);
                    saved = userRepository.save(saved);
                    
                    // Update created_at via SQL so reports use correct dates
                    LocalDateTime createdDate = randomDateInMonth(month, cap);
                    jdbcTemplate.update("UPDATE \"accounts\" SET created_at = ? WHERE id = ?", createdDate, account.getId());
                    jdbcTemplate.update("UPDATE \"users\" SET created_at = ? WHERE id = ?", createdDate, saved.getId());
                    
                    createdUsers.add(saved);
                    log.debug("Created demo user {} with CUSTOMER role", seedEmail);
                }
            }
            log.info("✓ Created {} demo users across timespan {} -> {}", createdUsers.size(), start.toLocalDate(), end.toLocalDate());
        }

        // Add created users into the pool
        users.addAll(createdUsers);

        // Get a staff user for stock export creation
        User staffUser = users.stream()
                .filter(u -> u.getAccount() != null && u.getAccount().getEmail() != null
                        && (u.getAccount().getEmail().contains("staff") || u.getAccount().getEmail().contains("admin")))
                .findFirst()
                .orElse(users.isEmpty() ? null : users.get(0));

        // For each month in last 6 months, generate between 5 and 20 orders
        for (LocalDateTime month = start; month.isBefore(end); month = month.plusMonths(1)) {
            // 15..60 orders per month
            int ordersInMonth = rand.nextInt(46) + 15; // 15..60
            for (int i = 0; i < ordersInMonth; i++) {
                LocalDateTime createdAt = randomDateInMonth(month, cap);
                Order order = createRandomOrder(createdAt, users, variations, cap);
                Order saved = orderRepository.save(order);
                // Ensure createdAt persisted (PrePersist may overwrite) -> update via native SQL
                jdbcTemplate.update("UPDATE orders SET created_at = ?, updated_at = ? WHERE id = ?",
                        createdAt, createdAt, saved.getId());
                createdOrders.add(saved);
                
                // Create stock export for this order
                if (staffUser != null) {
                    createStockExportForOrder(saved, createdAt, staffUser);
                }
            }
        }

        log.info("✓ Created {} orders across timespan {} -> {}", createdOrders.size(), start.toLocalDate(), end.toLocalDate());

        // (Demo users already seeded above and added into users list)
    }

    private LocalDateTime randomDateInMonth(LocalDateTime monthStart, LocalDateTime cap) {
        // Cap ensures we never generate a datetime on/after today's midnight
        LocalDate capDate = cap.toLocalDate();
        LocalDate first = monthStart.toLocalDate();
        LocalDate last = monthStart.plusMonths(1).minusDays(1).toLocalDate();

        // If this month is the current month, constrain last day to yesterday (cap - 1 day)
        if (first.getMonth() == capDate.getMonth() && first.getYear() == capDate.getYear()) {
            LocalDate cappedLast = capDate.minusDays(1);
            if (cappedLast.isBefore(first)) {
                // cap is earlier than the first day of this month (rare), fall back to first day
                last = first;
            } else {
                last = cappedLast;
            }
        }

        int startDay = first.getDayOfMonth();
        int endDay = last.getDayOfMonth();
        int randomDay = rand.nextInt(endDay - startDay + 1) + startDay;
        int randomHour = 8 + rand.nextInt(9); // working hours 8..16
        int randomMin = rand.nextInt(60);
        LocalDateTime candidate = LocalDateTime.of(monthStart.getYear(), monthStart.getMonthValue(), randomDay, randomHour, randomMin);

        // Just in case candidate moved to or after cap (shouldn't happen due to last calculation), clamp it to cap - 1 second
        if (!candidate.isBefore(cap)) {
            candidate = cap.minusSeconds(1);
        }
        return candidate;
    }

    private Order createRandomOrder(LocalDateTime createdAt, List<User> users, List<ProductVariation> variations, LocalDateTime cap) {
        // Choose random customer
        User buyer = users.get(rand.nextInt(users.size()));

        String[] shippingMethods = new String[]{"STANDARD", "EXPRESS", "SAME_DAY", "PICKUP"};
        String shippingMethod = shippingMethods[rand.nextInt(shippingMethods.length)];
        OrderShippingInfo shipping = OrderShippingInfo.builder()
            .shippingName(buyer.getFirstName() + " " + buyer.getLastName())
            .shippingPhone(buyer.getPhone() == null ? "0900000000" : buyer.getPhone())
            .shippingAddress("123 Demo St, District 1, HCM")
            .shippingCity("Ho Chi Minh City")
            .shippingMethod(shippingMethod)
            .build();

        // Build items
        int itemsCount = rand.nextInt(3) + 1; // 1..3 items
        List<OrderItem> items = new ArrayList<>();
        BigDecimal subtotal = BigDecimal.ZERO;
        for (int i = 0; i < itemsCount; i++) {
            ProductVariation pv = variations.get(rand.nextInt(variations.size()));
            int qty = rand.nextInt(3) + 1; // 1..3
            BigDecimal unitPrice = pv.getPrice();
            BigDecimal total = unitPrice.multiply(BigDecimal.valueOf(qty));
            OrderItem item = OrderItem.builder()
                    .productVariation(pv)
                    .productName(pv.getProduct() != null ? pv.getProduct().getName() : "Product")
                    .productVariationName(pv.getVariationName())
                    .quantity(qty)
                    .unitPrice(unitPrice)
                    .totalPrice(total)
                    .build();
            items.add(item);
            subtotal = subtotal.add(total);
        }

        // Shipping fee
        BigDecimal shippingFee = BigDecimal.valueOf(30000 + rand.nextInt(70000));

        // Discount simplistic: occasional voucher
        BigDecimal discount = BigDecimal.ZERO;
        if (rand.nextDouble() < 0.15) {
            discount = subtotal.multiply(BigDecimal.valueOf(0.05)); // 5% discount
        }

        BigDecimal finalAmount = subtotal.add(shippingFee).subtract(discount);

        // Compute max allowed hours between createdAt and cap so we don't set payment dates in the future
        long maxHoursBetween = java.time.Duration.between(createdAt, cap).toHours();
        int maxExtraHours = (int)Math.max(0, Math.min(48, maxHoursBetween));

        // Payment: randomly choose between COD and VNPAY
        Payment paymentInfo = null;
        double paymentPick = rand.nextDouble();
        if (paymentPick < 0.5) {
            int addHours = maxExtraHours > 0 ? rand.nextInt(maxExtraHours + 1) : 0;
            CodPayment payment = CodPayment.builder()
                .paymentMethod(PaymentMethod.COD)
                .paymentStatus(rand.nextDouble() < 0.95 ? PaymentStatus.PAID : PaymentStatus.PENDING)
                .totalAmount(subtotal)
                .finalAmount(finalAmount)
                .paymentInitiatedAt(createdAt)
                .paymentCompletedAt(createdAt.plusHours(addHours))
                .build();
            paymentInfo = paymentRepository.save(payment);
        } else {
            int addHoursForVnp = maxExtraHours > 0 ? rand.nextInt(maxExtraHours + 1) : 0;
            VnpayPayment vnp = VnpayPayment.builder()
                .paymentMethod(PaymentMethod.VNPAY)
                .paymentStatus(rand.nextDouble() < 0.95 ? PaymentStatus.PAID : PaymentStatus.PENDING)
                .totalAmount(subtotal)
                .finalAmount(finalAmount)
                .vnpTransactionNo("VNP" + Math.abs(rand.nextInt()))
                .vnpTxnRef("REF" + Math.abs(rand.nextInt()))
                .vnpResponseCode(rand.nextDouble() < 0.95 ? "00" : "01")
                .vnpBankCode("TESTBANK")
                .vnpOrderInfo("Seeded vnpay payment")
                .vnpSecureHash("SEC" + Math.abs(rand.nextInt()))
                .vnpPaymentDate(createdAt.plusHours(addHoursForVnp))
                .paymentInitiatedAt(createdAt)
                .paymentCompletedAt(createdAt.plusHours(addHoursForVnp))
                .build();
            paymentInfo = paymentRepository.save(vnp);
        }

        // Determine status distribution (weights across realistic lifecycle)
        OrderStatus status;
        double pickStatus = rand.nextDouble();
        if (pickStatus < 0.10) status = OrderStatus.PENDING;
        else if (pickStatus < 0.20) status = OrderStatus.CONFIRMED;
        else if (pickStatus < 0.45) status = OrderStatus.PROCESSING;
        else if (pickStatus < 0.65) status = OrderStatus.SHIPPING;
        else if (pickStatus < 0.90) status = OrderStatus.DELIVERED;
        else if (pickStatus < 0.97) status = OrderStatus.CANCELLED;
        else status = OrderStatus.RETURNED;

        Order order = Order.builder()
                .orderCode("ORD-" + System.currentTimeMillis() + "-" + rand.nextInt(1000))
                .user(buyer)
                .status(status)
                .shippingInfo(shipping)
                .paymentInfo(paymentInfo)
                .note("Seeded order for reports")
                .orderItems(items)
                .build();

        // link order back to items
        for (OrderItem it : items) {
            it.setOrder(order);
        }

        // set createdAt via native update after save
        order.setCreatedAt(createdAt);
        return order;
    }

    /**
     * Create stock export for an order with items from OrderItems
     */
    private void createStockExportForOrder(Order order, LocalDateTime exportDate, User staffUser) {
        try {
            // Create stock export
            StockExport export = StockExport.builder()
                    .userId(staffUser.getId())
                    .orderId(order.getId().intValue())
                    .exportDate(exportDate)
                    .totalCogsValue(BigDecimal.ZERO)
                    .note("Stock export for order " + order.getOrderCode())
                    .build();
            
            // Add items from order
            for (OrderItem orderItem : order.getOrderItems()) {
                ProductVariation pv = orderItem.getProductVariation();
                if (pv != null) {
                    BigDecimal costPrice = pv.getAvgCostPrice() != null ? pv.getAvgCostPrice() : 
                                          pv.getPrice().multiply(BigDecimal.valueOf(0.7));
                    
                    StockExportItem item = StockExportItem.builder()
                            .stockExport(export)
                            .productVariation(pv)
                            .quantity(orderItem.getQuantity())
                            .costPrice(costPrice)
                            .build();
                    export.addItem(item);
                }
            }
            
            // Calculate total COGS value
            export.calculateTotalCogsValue();
            
            // Save export
            StockExport saved = stockExportRepository.save(export);
            
            // Update created_at via SQL for correct timestamps
            jdbcTemplate.update("UPDATE stock_exports SET created_at = ? WHERE id = ?", 
                    exportDate, saved.getId());
            
            log.debug("Created stock export {} for order {}", saved.getDocumentCode(), order.getOrderCode());
        } catch (Exception e) {
            log.warn("Failed to create stock export for order {}: {}", order.getId(), e.getMessage());
        }
    }
}

