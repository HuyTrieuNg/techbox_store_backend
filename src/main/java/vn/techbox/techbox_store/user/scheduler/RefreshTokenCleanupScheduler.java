package vn.techbox.techbox_store.user.scheduler;

import lombok.RequiredArgsConstructor;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.scheduling.annotation.Scheduled;
import org.springframework.stereotype.Component;
import vn.techbox.techbox_store.user.service.RefreshTokenService;

@Component
@RequiredArgsConstructor
public class RefreshTokenCleanupScheduler {

    private static final Logger logger = LoggerFactory.getLogger(RefreshTokenCleanupScheduler.class);
    private final RefreshTokenService refreshTokenService;

    // Run cleanup every 30 days at midnight
    @Scheduled(cron = "0 0 0 */30 * *")
    public void cleanupExpiredTokens() {
        logger.info("Starting scheduled cleanup of expired refresh tokens");
        try {
            refreshTokenService.cleanupExpiredTokens();
            logger.info("Successfully completed cleanup of expired refresh tokens");
        } catch (Exception e) {
            logger.error("Error during scheduled cleanup of refresh tokens: {}", e.getMessage(), e);
        }
    }
}
