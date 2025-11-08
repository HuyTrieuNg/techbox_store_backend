package vn.techbox.techbox_store.config.seeder;

import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Component;
import org.springframework.transaction.annotation.Transactional;
import vn.techbox.techbox_store.promotion.model.Campaign;
import vn.techbox.techbox_store.promotion.repository.CampaignRepository;

import java.time.LocalDateTime;
import java.util.ArrayList;
import java.util.List;

@Component
@RequiredArgsConstructor
@Slf4j
public class CampaignSeeder implements DataSeeder {

    private final CampaignRepository campaignRepository;

    @Override
    public int getOrder() {
        return 7; // After Inventory
    }

    @Override
    public boolean shouldSkip() {
        // Always run this seeder to ensure data is fresh
        return false;
    }

    @Override
    @Transactional
    public void seed() {
        log.info("Starting Campaign seeding...");
        
        // Clean up existing data to ensure idempotency
        campaignRepository.deleteAllInBatch();
        
        LocalDateTime now = LocalDateTime.now();
        List<Campaign> campaigns = new ArrayList<>();
        
        // Campaign 1: Đang active
        campaigns.add(Campaign.builder()
                .name("Mega Sale 12.12")
                .description("Khuyến mãi khủng trong ngày 12/12 - Giảm giá mạnh cho tất cả sản phẩm điện tử")
                .startDate(now.minusMinutes(1))
                .endDate(now.plusDays(25))
                .build());
        
        // Campaign 2: Sắp diễn ra
        campaigns.add(Campaign.builder()
                .name("Christmas Sale 2024")
                .description("Giảm giá đặc biệt mùa Giáng Sinh - Ưu đãi lớn cho mọi sản phẩm")
                .startDate(now.plusDays(10))
                .endDate(now.plusDays(40))
                .build());
        
        // Campaign 3: Đang active
        campaigns.add(Campaign.builder()
                .name("Black Friday Tech Sale")
                .description("Tuần lễ vàng công nghệ - Giảm tới 50% cho laptop và điện thoại")
                .startDate(now.minusDays(2))
                .endDate(now.plusDays(5))
                .build());
        
        // Campaign 4: Đã hết hạn
        campaigns.add(Campaign.builder()
                .name("Back to School")
                .description("Ưu đãi cho sinh viên - Giảm giá laptop, tablet cho mùa học mới")
                .startDate(now.minusDays(45))
                .endDate(now.minusDays(15))
                .build());
        
        // Campaign 5: Đang active - dành cho accessories
        campaigns.add(Campaign.builder()
                .name("Audio Week")
                .description("Tuần lễ âm thanh - Giảm giá tai nghe, loa không dây")
                .startDate(now.minusDays(3))
                .endDate(now.plusDays(4))
                .build());
        
        campaignRepository.saveAll(campaigns);
        log.info("✓ Created {} campaigns", campaigns.size());
        log.info("Campaign seeding completed successfully");
    }
}
