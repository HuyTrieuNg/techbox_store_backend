package vn.techbox.techbox_store.promotion.scheduler;

import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.scheduling.annotation.Scheduled;
import org.springframework.stereotype.Component;
import vn.techbox.techbox_store.product.service.ProductPriceUpdateService;
import vn.techbox.techbox_store.promotion.model.Campaign;
import vn.techbox.techbox_store.promotion.repository.CampaignRepository;

import java.time.LocalDateTime;
import java.util.List;

/**
 * Scheduled task để cập nhật giá sản phẩm định kỳ
 * Chạy mỗi 5 phút để kiểm tra campaigns có start/end trong khoảng thời gian vừa qua
 */
@Slf4j
@Component
@RequiredArgsConstructor
public class PromotionScheduler {
    
    private final CampaignRepository campaignRepository;
    private final ProductPriceUpdateService productPriceUpdateService;
    
    /**
     * Chạy mỗi 5 phút (300000 milliseconds)
     * Cập nhật giá cho các sản phẩm trong campaigns vừa start hoặc end
     */
    @Scheduled(fixedRate = 300000) // 5 phút
    public void updateProductPricesForRecentCampaigns() {
        log.info("Starting scheduled product price update...");
        
        LocalDateTime now = LocalDateTime.now();
        LocalDateTime fiveMinutesAgo = now.minusMinutes(5);
        
        try {
            // Tìm các campaigns có startDate hoặc endDate trong 5 phút qua
            List<Campaign> recentCampaigns = campaignRepository
                    .findCampaignsWithRecentStartOrEnd(fiveMinutesAgo, now);
            
            if (recentCampaigns.isEmpty()) {
                log.info("No campaigns with recent start/end found");
                return;
            }
            
            log.info("Found {} campaigns with recent start/end", recentCampaigns.size());
            
            // Cập nhật giá cho từng campaign
            for (Campaign campaign : recentCampaigns) {
                try {
                    log.info("Updating pricing for campaign: {} (ID: {})", campaign.getName(), campaign.getId());
                    productPriceUpdateService.updateProductPricingByCampaign(campaign.getId());
                } catch (Exception e) {
                    log.error("Error updating pricing for campaign ID: {}", campaign.getId(), e);
                }
            }
            
            log.info("Completed scheduled product price update");
            
        } catch (Exception e) {
            log.error("Error in scheduled product price update", e);
        }
    }
    
    /**
     * Chạy mỗi ngày lúc 2 giờ sáng để cập nhật lại toàn bộ giá
     * Đảm bảo consistency cho tất cả sản phẩm
     */
    @Scheduled(cron = "0 0 2 * * *") // 2:00 AM mỗi ngày
    public void fullProductPriceUpdate() {
        log.info("Starting full product price update...");
        
        try {
            productPriceUpdateService.updateAllProductPricing();
            log.info("Completed full product price update");
        } catch (Exception e) {
            log.error("Error in full product price update", e);
        }
    }
}
