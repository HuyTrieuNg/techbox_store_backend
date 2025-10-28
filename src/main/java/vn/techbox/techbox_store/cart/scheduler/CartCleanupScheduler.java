package vn.techbox.techbox_store.cart.scheduler;

import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.boot.autoconfigure.condition.ConditionalOnProperty;
import org.springframework.scheduling.annotation.Scheduled;
import org.springframework.stereotype.Component;
import vn.techbox.techbox_store.cart.service.CartService;

@Component
@RequiredArgsConstructor
@Slf4j
@ConditionalOnProperty(value = "app.scheduling.enabled", havingValue = "true", matchIfMissing = true)
public class CartCleanupScheduler {

    private final CartService cartService;

    @Scheduled(cron = "0 0 2 * * ?")
    public void cleanupOldCarts() {
        try {
            log.info("Starting scheduled cleanup of old user carts");

            int deletedCount = cartService.deleteOldCarts(30);

            log.info("Completed cart cleanup. Deleted {} old user carts", deletedCount);

        } catch (Exception e) {
            log.error("Error during cart cleanup: {}", e.getMessage(), e);
        }
    }
}
