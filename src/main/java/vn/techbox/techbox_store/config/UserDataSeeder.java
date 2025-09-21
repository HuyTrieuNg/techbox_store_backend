package vn.techbox.techbox_store.config;

import org.springframework.boot.CommandLineRunner;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.security.crypto.bcrypt.BCryptPasswordEncoder;
import vn.techbox.techbox_store.user.model.User;
import vn.techbox.techbox_store.user.model.UserRole;
import vn.techbox.techbox_store.user.repository.UserRepository;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

@Configuration
public class UserDataSeeder {
    private static final Logger logger = LoggerFactory.getLogger(UserDataSeeder.class);
    private final BCryptPasswordEncoder encoder = new BCryptPasswordEncoder(12);

    @Bean
    CommandLineRunner seedUsers(UserRepository userRepository) {
        return args -> {
            try {
                if (userRepository.count() == 0) {
                    logger.info("Seeding initial user data...");

                    // Create admin user
                    User admin = User.builder()
                            .username("admin")
                            .email("admin@gmail.com")
                            .password(encoder.encode("admin123"))
                            .role(UserRole.admin)
                            .build();
                    userRepository.save(admin);
                    logger.info("Created admin user: {}", admin.getUsername());

                    // Create user1
                    User user1 = User.builder()
                            .username("user1")
                            .email("user1@gmail.com")
                            .password(encoder.encode("user123"))
                            .role(UserRole.customer)
                            .build();
                    userRepository.save(user1);
                    logger.info("Created user: {}", user1.getUsername());

                    // Create user2
                    User user2 = User.builder()
                            .username("user2")
                            .email("user2@gmail.com")
                            .password(encoder.encode("user456"))
                            .role(UserRole.customer)
                            .build();
                    userRepository.save(user2);
                    logger.info("Created user: {}", user2.getUsername());

                    logger.info("User data seeding completed successfully!");
                } else {
                    logger.info("Users already exist, skipping data seeding");
                }
            } catch (Exception e) {
                logger.error("Error seeding user data: {}", e.getMessage(), e);
                throw new RuntimeException("Failed to seed user data", e);
            }
        };
    }
}
