# Scheduled Task: Auto Update Review Ratings

## Mô tả
Scheduled task tự động cập nhật đánh giá trung bình (average rating) cho tất cả sản phẩm mỗi ngày lúc 2:00 AM.

## Vị trí file
```
src/main/java/vn/techbox/techbox_store/review/scheduler/ReviewRatingScheduler.java
```

## Cách hoạt động

### 1. Thời gian chạy
- **Cron Expression**: `0 0 2 * * ?`
- **Giờ chạy**: 02:00:00 (2 giờ sáng) mỗi ngày
- **Múi giờ**: UTC (được cấu hình trong application)

### 2. Quá trình xử lý
1. Lấy tất cả sản phẩm chưa bị soft-delete (`deletedAt IS NULL`)
2. Với mỗi sản phẩm:
   - Tính toán rating trung bình từ bảng `product_reviews` (chỉ các review không bị xóa)
   - Tính tổng số rating từ các review hợp lệ
   - So sánh với giá trị cũ:
     - Nếu có thay đổi → lưu vào database
     - Nếu không thay đổi → bỏ qua (tối ưu performance)

### 3. Chi tiết câu query
```sql
-- Lấy average rating và số lượng rating
SELECT AVG(r.rating), COUNT(r) 
FROM product_reviews r 
WHERE r.product_id = :productId AND r.deleted_at IS NULL
```

## Cấu trúc code

### ReviewRatingScheduler (Component)
```java
@Component
@RequiredArgsConstructor
@Slf4j
public class ReviewRatingScheduler {
    private final ProductRepository productRepository;
    private final ReviewRepository reviewRepository;
    
    // Main scheduled method
    @Scheduled(cron = "0 0 2 * * ?")
    @Transactional
    public void updateAverageRatingsForAllProducts()
    
    // Helper method
    private boolean updateProductRating(Product product)
}
```

## Logging
Task sẽ output:
```
========== START: Cập nhật đánh giá trung bình cho tất cả sản phẩm ==========
Tìm thấy 150 sản phẩm cần cập nhật đánh giá
[DEBUG] Cập nhật đánh giá sản phẩm ID: 1 - Rating: 4.2 → 4.3, Total: 10 → 11
...
========== COMPLETE: Cập nhật đánh giá trung bình hoàn thành ==========
Tổng sản phẩm: 150, Cập nhật: 45, Bỏ qua: 105, Thời gian: 2345 ms
```

## Dependencies
- `ProductRepository` - Lấy danh sách sản phẩm
- `ReviewRepository` - Tính toán rating statistics
- `@Transactional` - Đảm bảo ACID cho các cập nhật
- `@Scheduled` - Spring Framework scheduling

## Cấu hình yêu cầu
Trong `TechboxStoreApplication.java`:
```java
@EnableScheduling  // Đã được kích hoạt
public class TechboxStoreApplication {
    ...
}
```

## Lợi ích
1. **Tự động hóa** - Không cần chạy thủ công
2. **Consistent** - Tất cả sản phẩm được update cùng lúc
3. **Efficient** - Chỉ update khi có thay đổi
4. **Off-peak** - Chạy lúc 2 AM để tránh ảnh hưởng traffic
5. **Logging** - Theo dõi được quá trình thực thi

## Có thể mở rộng
Có thể thêm các tính năng:
1. Configuration flag để enable/disable task
2. Lên lịch multiple times per day
3. Update chỉ các sản phẩm có thay đổi trong ngày
4. Email notification nếu có lỗi
5. Metrics/monitoring cho Actuator

## Testing
Để test locally, có thể:
1. Thay đổi cron expression về mỗi 1 phút:
   ```java
   @Scheduled(cron = "0 * * * * ?")  // Mỗi phút
   ```
2. Theo dõi logs trong console
3. Kiểm tra database xem ratings có được update không
