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
import java.util.Collections;
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

        // Prefer seeded demo customers if available (try up to 50)
        List<User> seedUsers = new ArrayList<>();
        for (int i = 1; i <= 50; i++) {
            String email = "customer" + i + "@techbox.vn";
            Optional<User> uOpt = userRepository.findByAccountEmail(email);
            uOpt.ifPresent(seedUsers::add);
        }

        // Fallback to any users if demo customers not present
        if (seedUsers.isEmpty()) {
            log.warn("Demo customer accounts not found, falling back to available users");
            seedUsers.addAll(userRepository.findAll());
        }

        if (seedUsers.isEmpty()) {
            log.warn("No users available to associate reviews with, skipping ReviewSeeder");
            return;
        }

        List<Review> reviews = new ArrayList<>();
        Random rnd = new Random();

        for (Product p : products) {
            // create between 3 and 8 reviews per product (cap at available users)
            int desired = 3 + rnd.nextInt(6); // 3..8
            int count = Math.min(desired, seedUsers.size());

            // shuffle copy of users to avoid same users every product
            List<User> shuffled = new ArrayList<>(seedUsers);
            Collections.shuffle(shuffled, rnd);

            int created = 0;
            for (int i = 0; i < shuffled.size() && created < count; i++) {
                User u = shuffled.get(i);
                // avoid duplicate user for same product
                boolean already = reviewRepository.findByProductIdAndUserId(p.getId(), u.getId()).isPresent();
                if (already) continue;

                int rating = 3 + rnd.nextInt(3); // 3..5 to increase variety

                // varied content templates
                String[] templates = new String[] {
                        "%s là một sản phẩm tốt, tôi hài lòng. Điểm: %d/5",
                        "Không tệ, nhưng có thể cải thiện ở một số điểm. Điểm: %d/5",
                        "Rất ưng ý với %s, sẽ mua lại. Điểm: %d/5",
                        "Sản phẩm ổn, giao hàng nhanh. Điểm: %d/5",
                        "Chất lượng tốt so với giá. Điểm: %d/5"
                };
                String tmpl = templates[rnd.nextInt(templates.length)];
                String content = tmpl.contains("%s") ? String.format(tmpl, p.getName(), rating) : String.format(tmpl, rating);

                Review r = Review.builder()
                        .productId(p.getId())
                        .userId(u.getId())
                        .rating(rating)
                        .content(content)
                        .build();
                reviews.add(r);
                created++;
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
