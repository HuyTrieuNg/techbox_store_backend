package vn.techbox.techbox_store.review.scheduler;

import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.scheduling.annotation.Scheduled;
import org.springframework.stereotype.Component;
import org.springframework.transaction.annotation.Transactional;
import vn.techbox.techbox_store.product.model.Product;
import vn.techbox.techbox_store.product.repository.ProductRepository;
import vn.techbox.techbox_store.review.repository.ReviewRepository;

import java.util.List;

/**
 * Scheduled task để tự động cập nhật đánh giá trung bình cho tất cả sản phẩm
 * Chạy lúc 2 giờ sáng hàng ngày (02:00:00)
 */
@Component
@RequiredArgsConstructor
@Slf4j
public class ReviewRatingScheduler {
    
    private final ProductRepository productRepository;
    private final ReviewRepository reviewRepository;
    
    /**
     * Cập nhật đánh giá trung bình cho tất cả sản phẩm
     * Cron expression: "0 0 2 * * ?" = lúc 2:00:00 hàng ngày
     * - 0: giây
     * - 0: phút
     * - 2: giờ (2 AM)
     * - *: ngày (mỗi ngày)
     * - *: tháng (mỗi tháng)
     * - ?: day of week (không xác định)
     */
    @Scheduled(cron = "0 0 2 * * ?")
    @Transactional
    public void updateAverageRatingsForAllProducts() {
        long startTime = System.currentTimeMillis();
        log.info("========== START: Cập nhật đánh giá trung bình cho tất cả sản phẩm ==========");
        
        try {
            // Lấy tất cả sản phẩm chưa bị xóa
            List<Product> allProducts = productRepository.findByDeletedAtIsNull();
            
            if (allProducts.isEmpty()) {
                log.info("Không có sản phẩm nào để cập nhật đánh giá");
                return;
            }
            
            
            int updatedCount = 0;
            int skippedCount = 0;
            
            for (Product product : allProducts) {
                try {
                    if (updateProductRating(product)) {
                        updatedCount++;
                    } else {
                        skippedCount++;
                    }
                } catch (Exception e) {
                    log.error("Lỗi khi cập nhật đánh giá cho sản phẩm ID: {}", product.getId(), e);
                }
            }
            
            long endTime = System.currentTimeMillis();
            long duration = endTime - startTime;
            
            log.info("========== COMPLETE: Cập nhật đánh giá trung bình hoàn thành ==========");
            log.info("Tổng sản phẩm: {}, Cập nhật: {}, Bỏ qua: {}, Thời gian: {} ms", 
                    allProducts.size(), updatedCount, skippedCount, duration);
            
        } catch (Exception e) {
            log.error("Lỗi khi thực hiện scheduled task cập nhật đánh giá", e);
        }
    }
    
    /**
     * Cập nhật đánh giá trung bình cho một sản phẩm
     * @param product sản phẩm cần cập nhật
     * @return true nếu có thay đổi, false nếu không có thay đổi
     */
    private boolean updateProductRating(Product product) {
        Integer productId = product.getId();
        
        // Lấy thông tin đánh giá từ repository
        List<Object[]> ratingStats = reviewRepository.calculateAverageRatingAndCount(productId);
        
        Double newAverageRating;
        Integer newTotalRatings;
        
        if (!ratingStats.isEmpty()) {
            Object[] stats = ratingStats.get(0);
            newAverageRating = stats[0] != null ? ((Number) stats[0]).doubleValue() : 0.0;
            newTotalRatings = stats[1] != null ? ((Number) stats[1]).intValue() : 0;
        } else {
            newAverageRating = 0.0;
            newTotalRatings = 0;
        }
        
        // Lấy giá trị cũ
        Double oldAverageRating = product.getAverageRating() != null ? product.getAverageRating() : 0.0;
        Integer oldTotalRatings = product.getTotalRatings() != null ? product.getTotalRatings() : 0;
        
        // Kiểm tra xem có thay đổi không
        boolean hasChanged = !oldAverageRating.equals(newAverageRating) || 
                           !oldTotalRatings.equals(newTotalRatings);
        
        if (hasChanged) {
            log.debug("Cập nhật đánh giá sản phẩm ID: {} - Rating: {} → {}, Total: {} → {}", 
                    productId, oldAverageRating, newAverageRating, oldTotalRatings, newTotalRatings);
            
            product.setAverageRating(newAverageRating);
            product.setTotalRatings(newTotalRatings);
            productRepository.save(product);
        }
        
        return hasChanged;
    }
}
