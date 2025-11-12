package vn.techbox.techbox_store.config.seeder.fortest;

import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;

import org.springframework.boot.CommandLineRunner;
import org.springframework.context.annotation.Profile;
import org.springframework.core.annotation.Order;
import org.springframework.stereotype.Component;
import org.springframework.transaction.annotation.Transactional;

import vn.techbox.techbox_store.config.seeder.DataSeeder;
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
@Order(3)
public class VoucherSeeder implements CommandLineRunner{
    private final VoucherRepository voucherRepository;

    @Override
    public void run(String... args) {
        if (shouldSkip()) {
            return;
        }
        try {
            seed();
        } catch (Exception e) {
            log.error("Failed to seed vouchers: {}", e.getMessage(), e);
        }
    }


    public boolean shouldSkip() {
        if (voucherRepository.findByCode("EXISTINGCODE").isPresent()) {
            log.info("Vouchers already exist, skipping seeder");
            return true;
        }
        return false;
    }

    @Transactional
    public void seed() {
        log.info("Starting Voucher seeding...");
        
        LocalDateTime now = LocalDateTime.now();
        List<Voucher> vouchers = new ArrayList<>();

        // Existing voucher for testing duplicates
        vouchers.add(Voucher.builder()
                .code("EXISTINGCODE")
                .voucherType(VoucherType.FIXED_AMOUNT)
                .value(new BigDecimal("50000.00"))
                .minOrderAmount(new BigDecimal("0.00"))
                .usageLimit(100)
                .validFrom(now.withMonth(12).withDayOfMonth(1).withHour(0).withMinute(0).withSecond(0).withNano(0))
                .validUntil(now.withMonth(12).withDayOfMonth(31).withHour(23).withMinute(59).withSecond(59).withNano(0))
                .build());  
        
        voucherRepository.saveAll(vouchers);
        log.info("Voucher seeding completed successfully");
    }
}
