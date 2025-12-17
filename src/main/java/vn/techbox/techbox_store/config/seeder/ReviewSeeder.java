package vn.techbox.techbox_store.config.seeder;

import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Component;
import org.springframework.transaction.annotation.Transactional;
import vn.techbox.techbox_store.product.model.Product;
import vn.techbox.techbox_store.product.repository.ProductRepository;
import vn.techbox.techbox_store.review.model.Review;
import vn.techbox.techbox_store.review.repository.ReviewRepository;
import vn.techbox.techbox_store.user.model.User;
import vn.techbox.techbox_store.user.repository.UserRepository;

import java.util.ArrayList;
import java.util.List;
import java.util.Optional;
import java.util.Random;

@Component
@RequiredArgsConstructor
@Slf4j
public class ReviewSeeder implements DataSeeder {

    private final ReviewRepository reviewRepository;
    private final UserRepository userRepository;
    private final ProductRepository productRepository;

    @Override
    @Transactional
    public void seed() {
        log.info("Starting Review seeding...");

        List<Product> products = productRepository.findAll();
        if (products.isEmpty()) {
            log.warn("No products found, skipping ReviewSeeder");
            return;
        }

        // Prefer seeded demo customers if available
        List<User> seedUsers = new ArrayList<>();
        for (int i = 1; i <= 20; i++) {
            String email = "customer" + i + "@techbox.vn";
            Optional<User> uOpt = userRepository.findByAccountEmail(email);
            uOpt.ifPresent(seedUsers::add);
        }

        // Fallback to any users if demo customers not present
        if (seedUsers.isEmpty()) {
            log.warn("Demo customer accounts not found, falling back to first available users");
            seedUsers.addAll(userRepository.findAll().stream().limit(5).toList());
        }

        if (seedUsers.isEmpty()) {
            log.warn("No users available to associate reviews with, skipping ReviewSeeder");
            return;
        }

        List<Review> reviews = new ArrayList<>();
        Random rnd = new Random(12345);

        for (Product p : products) {
            // create between 1 and 3 reviews per product (capped by available users)
            int maxPerProduct = Math.min(20, seedUsers.size());
            int count = 1 + rnd.nextInt(maxPerProduct);
            for (int i = 0; i < count; i++) {
                User u = seedUsers.get((p.getId() + i) % seedUsers.size());
                // avoid duplicate user for same product
                boolean already = reviewRepository.findByProductIdAndUserId(p.getId(), u.getId()).isPresent();
                if (already) continue;

                int rating = 3 + rnd.nextInt(3); // 3..5
                String content = String.format("Đây là review mẫu cho sản phẩm '%s' của %s %s. Điểm: %d/5 sao.",
                        p.getName(), u.getFirstName(), u.getLastName(), rating);

                Review r = Review.builder()
                        .productId(p.getId())
                        .userId(u.getId())
                        .rating(rating)
                        .content(content)
                        .build();
                reviews.add(r);
            }
        }

        if (!reviews.isEmpty()) {
            reviewRepository.saveAll(reviews);
            log.info("✓ Created {} review(s)", reviews.size());
        } else {
            log.info("No new reviews to create (possible duplicates), skipping save");
        }
    }

    @Override
    public int getOrder() {
        return 10; // after products (ProductSeeder uses 5)
    }

    @Override
    public boolean shouldSkip() {
        long count = reviewRepository.count();
        if (count > 0) {
            log.info("Reviews already exist ({} found), skipping ReviewSeeder", count);
            return true;
        }
        log.info("No reviews found, will run ReviewSeeder");
        return false;
    }
}
