package vn.techbox.techbox_store.product.service.sync;

import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.http.MediaType;
import org.springframework.scheduling.annotation.Async;
import org.springframework.stereotype.Service;
import org.springframework.web.reactive.function.client.WebClient;
import reactor.core.publisher.Mono;
import vn.techbox.techbox_store.product.dto.productDto.ProductDetailResponse;

@Service
@RequiredArgsConstructor
@Slf4j
public class SyncServiceImpl implements SyncService {

    private final WebClient webClient;

    
    @Value("${sync.endpoint.api-key:}")
    private String apiKey;

    /**
     * Async method to sync product update to another server
     * @param product the product detail to sync
     */
    @Async
    public void syncProductUpdate(ProductDetailResponse product) {
        log.info("Starting async sync for product ID: {}", product.getId());

        webClient.post()
                .uri("/sync/products")
                .contentType(MediaType.APPLICATION_JSON)
                .header("X-API-Key", apiKey)
                .bodyValue(product)
                .retrieve()
                .bodyToMono(String.class)
                .doOnSuccess(response -> log.info("Successfully synced product ID: {}", product.getId()))
                .doOnError(error -> log.error("Failed to sync product ID: {}, error: {}", product.getId(), error.getMessage()))
                .onErrorResume(error -> {
                    log.warn("Sync failed for product ID: {}, will retry or ignore", product.getId());
                    return Mono.empty();
                })
                .subscribe();
    }

    @Async
    public void syncProductDelete(Integer productId) {
        log.info("Starting async sync for product deletion ID: {}", productId);

        webClient.delete()
                .uri("/sync/products/" + productId)
                .header("X-API-Key", apiKey) 
                .retrieve()
                .bodyToMono(String.class)
                .doOnSuccess(response -> log.info("Successfully synced deletion for product ID: {}", productId))
                .doOnError(error -> log.error("Failed to sync deletion for product ID: {}, error: {}", productId, error.getMessage()))
                .onErrorResume(error -> {
                    log.warn("Sync deletion failed for product ID: {}, will retry or ignore", productId);
                    return Mono.empty();
                })
                .subscribe();
    }

}