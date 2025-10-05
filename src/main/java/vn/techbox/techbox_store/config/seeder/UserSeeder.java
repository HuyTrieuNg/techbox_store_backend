package vn.techbox.techbox_store.config.seeder;

import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Component;
import vn.techbox.techbox_store.user.model.User;
import vn.techbox.techbox_store.user.model.UserRole;
import vn.techbox.techbox_store.user.repository.UserRepository;

import java.util.Arrays;
import java.util.List;

@Component
@RequiredArgsConstructor
@Slf4j
public class UserSeeder implements DataSeeder {

    private final UserRepository userRepository;

    @Override
    public void seed() {
        // Note: Passwords are stored in plain text for development seeding only
        // In production, use proper password encoding
        List<User> users = Arrays.asList(
                // Admin user
                User.builder()
                        .username("admin")
                        .email("admin@techbox.vn")
                        .password("$2a$10$xGPEQJ6VzW0RzXo.F3dWJ.sDjEVvK1h9C1cjFBPw.2u.9LqCYFY3i") // Admin@123
                        .role(UserRole.admin)
                        .build(),
                
                // Staff users
                User.builder()
                        .username("staff1")
                        .email("staff1@techbox.vn")
                        .password("$2a$10$xGPEQJ6VzW0RzXo.F3dWJ.sDjEVvK1h9C1cjFBPw.2u.9LqCYFY3i") // Staff@123
                        .role(UserRole.staff)
                        .build(),
                
                User.builder()
                        .username("staff2")
                        .email("staff2@techbox.vn")
                        .password("$2a$10$xGPEQJ6VzW0RzXo.F3dWJ.sDjEVvK1h9C1cjFBPw.2u.9LqCYFY3i") // Staff@123
                        .role(UserRole.staff)
                        .build(),
                
                User.builder()
                        .username("staff3")
                        .email("staff3@techbox.vn")
                        .password("$2a$10$xGPEQJ6VzW0RzXo.F3dWJ.sDjEVvK1h9C1cjFBPw.2u.9LqCYFY3i") // Staff@123
                        .role(UserRole.staff)
                        .build(),
                
                // Customer users
                User.builder()
                        .username("customer1")
                        .email("customer1@gmail.com")
                        .password("$2a$10$xGPEQJ6VzW0RzXo.F3dWJ.sDjEVvK1h9C1cjFBPw.2u.9LqCYFY3i") // Customer@123
                        .role(UserRole.customer)
                        .build(),
                
                User.builder()
                        .username("customer2")
                        .email("customer2@gmail.com")
                        .password("$2a$10$xGPEQJ6VzW0RzXo.F3dWJ.sDjEVvK1h9C1cjFBPw.2u.9LqCYFY3i") // Customer@123
                        .role(UserRole.customer)
                        .build(),
                
                User.builder()
                        .username("customer3")
                        .email("customer3@gmail.com")
                        .password("$2a$10$xGPEQJ6VzW0RzXo.F3dWJ.sDjEVvK1h9C1cjFBPw.2u.9LqCYFY3i") // Customer@123
                        .role(UserRole.customer)
                        .build(),
                
                User.builder()
                        .username("customer4")
                        .email("customer4@gmail.com")
                        .password("$2a$10$xGPEQJ6VzW0RzXo.F3dWJ.sDjEVvK1h9C1cjFBPw.2u.9LqCYFY3i") // Customer@123
                        .role(UserRole.customer)
                        .build()
        );

        userRepository.saveAll(users);
        log.info("✓ Created {} users", users.size());
    }

    @Override
    public int getOrder() {
        return 1; // Run first
    }

    @Override
    public boolean shouldSkip() {
        return userRepository.count() > 0;
    }
}
