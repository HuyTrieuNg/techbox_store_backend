package vn.techbox.techbox_store.config.seeder;

import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Component;
import org.springframework.transaction.annotation.Transactional;
import vn.techbox.techbox_store.user.model.User;
import vn.techbox.techbox_store.user.repository.UserRepository;
import vn.techbox.techbox_store.voucher.model.UserVoucher;
import vn.techbox.techbox_store.voucher.model.Voucher;
import vn.techbox.techbox_store.voucher.model.VoucherType;
import vn.techbox.techbox_store.voucher.repository.UserVoucherRepository;
import vn.techbox.techbox_store.voucher.repository.VoucherRepository;

import java.math.BigDecimal;
import java.time.LocalDateTime;
import java.util.ArrayList;
import java.util.List;

@Component
@RequiredArgsConstructor
@Slf4j
public class VoucherSeeder implements DataSeeder {

    private final VoucherRepository voucherRepository;
    private final UserVoucherRepository userVoucherRepository;
    private final UserRepository userRepository;

    @Override
    public int getOrder() {
        return 9; // After Promotion
    }

    @Override
    public boolean shouldSkip() {
        long count = voucherRepository.count();
        if (count > 0) {
            log.info("Vouchers already exist ({} found), skipping seeder", count);
            return true;
        }
        return false;
    }

    @Override
    @Transactional
    public void seed() {
        log.info("Starting Voucher seeding...");
        
        LocalDateTime now = LocalDateTime.now();
        List<Voucher> vouchers = new ArrayList<>();
        
        // Voucher 1: Welcome voucher - Giảm 10% tối đa 100k
        vouchers.add(Voucher.builder()
                .code("WELCOME10")
                .voucherType(VoucherType.PERCENTAGE)
                .value(new BigDecimal("10.00"))
                .minOrderAmount(new BigDecimal("500000")) // Đơn tối thiểu 500k
                .usageLimit(100)
                .validFrom(now.minusDays(10))
                .validUntil(now.plusDays(50))
                .build());
        
        // Voucher 2: Tech Sale - Giảm cố định 500k
        vouchers.add(Voucher.builder()
                .code("TECHSALE500")
                .voucherType(VoucherType.FIXED_AMOUNT)
                .value(new BigDecimal("500000"))
                .minOrderAmount(new BigDecimal("5000000")) // Đơn tối thiểu 5 triệu
                .usageLimit(50)
                .validFrom(now.minusDays(5))
                .validUntil(now.plusDays(25))
                .build());
        
        // Voucher 3: Big Sale - Giảm 20% tối đa 2 triệu
        vouchers.add(Voucher.builder()
                .code("BIGSALE20")
                .voucherType(VoucherType.PERCENTAGE)
                .value(new BigDecimal("20.00"))
                .minOrderAmount(new BigDecimal("3000000")) // Đơn tối thiểu 3 triệu
                .usageLimit(30)
                .validFrom(now.minusDays(3))
                .validUntil(now.plusDays(17))
                .build());
        
        // Voucher 4: New User - Giảm 15% cho khách hàng mới
        vouchers.add(Voucher.builder()
                .code("NEWUSER15")
                .voucherType(VoucherType.PERCENTAGE)
                .value(new BigDecimal("15.00"))
                .minOrderAmount(new BigDecimal("1000000")) // Đơn tối thiểu 1 triệu
                .usageLimit(200)
                .validFrom(now.minusDays(20))
                .validUntil(now.plusDays(40))
                .build());
        
        // Voucher 5: Mega Deal - Giảm 1 triệu
        vouchers.add(Voucher.builder()
                .code("MEGADEAL1M")
                .voucherType(VoucherType.FIXED_AMOUNT)
                .value(new BigDecimal("1000000"))
                .minOrderAmount(new BigDecimal("10000000")) // Đơn tối thiểu 10 triệu
                .usageLimit(20)
                .validFrom(now.minusDays(2))
                .validUntil(now.plusDays(8))
                .build());
        
        // Voucher 6: Flash Sale - Giảm 30% (hết hạn)
        vouchers.add(Voucher.builder()
                .code("FLASH30")
                .voucherType(VoucherType.PERCENTAGE)
                .value(new BigDecimal("30.00"))
                .minOrderAmount(new BigDecimal("2000000"))
                .usageLimit(100)
                .validFrom(now.minusDays(35))
                .validUntil(now.minusDays(5)) // Đã hết hạn
                .build());
        
        // Save vouchers
        vouchers = voucherRepository.saveAll(vouchers);
        log.info("✓ Created {} vouchers", vouchers.size());
        
        // Create some user voucher usages
        List<User> users = userRepository.findAll();
        if (!users.isEmpty() && vouchers.size() >= 3) {
            List<UserVoucher> userVouchers = new ArrayList<>();
            
            // User 1 đã sử dụng WELCOME10
            if (users.size() > 0) {
                userVouchers.add(UserVoucher.builder()
                        .userId(users.get(0).getId())
                        .voucherCode(vouchers.get(0).getCode())
                        .usedAt(now.minusDays(7))
                        .orderId(1001) // Order ID giả định
                        .build());
            }
            
            // User 2 đã sử dụng TECHSALE500
            if (users.size() > 1) {
                userVouchers.add(UserVoucher.builder()
                        .userId(users.get(1).getId())
                        .voucherCode(vouchers.get(1).getCode())
                        .usedAt(now.minusDays(4))
                        .orderId(1002)
                        .build());
            }
            
            // User 3 đã sử dụng BIGSALE20
            if (users.size() > 2) {
                userVouchers.add(UserVoucher.builder()
                        .userId(users.get(2).getId())
                        .voucherCode(vouchers.get(2).getCode())
                        .usedAt(now.minusDays(2))
                        .orderId(1003)
                        .build());
            }
            
            // User 1 cũng đã sử dụng NEWUSER15
            if (users.size() > 0 && vouchers.size() > 3) {
                userVouchers.add(UserVoucher.builder()
                        .userId(users.get(0).getId())
                        .voucherCode(vouchers.get(3).getCode())
                        .usedAt(now.minusDays(15))
                        .orderId(1004)
                        .build());
            }
            
            userVoucherRepository.saveAll(userVouchers);
            log.info("✓ Created {} user voucher usages", userVouchers.size());
        }
        
        log.info("Voucher seeding completed successfully");
    }
}
