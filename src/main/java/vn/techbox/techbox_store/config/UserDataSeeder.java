package vn.techbox.techbox_store.config;

import org.springframework.boot.CommandLineRunner;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import vn.techbox.techbox_store.model.User;
import vn.techbox.techbox_store.model.UserRole;
import vn.techbox.techbox_store.repository.UserRepository;

@Configuration
public class UserDataSeeder {
    @Bean
    CommandLineRunner seedUsers(UserRepository userRepository) {
        return args -> {
            if (userRepository.count() == 0) {
                User admin = new User();
                admin.setUsername("admin");
                admin.setEmail("admin@gmail.com");
                admin.setPasswordHash("admin123");
                admin.setRole(UserRole.admin);
                userRepository.save(admin);

                User user1 = new User();
                user1.setUsername("user1");
                user1.setEmail("user1@gmail.com");
                user1.setPasswordHash("user123");
                user1.setRole(UserRole.customer);
                userRepository.save(user1);

                User user2 = new User();
                user2.setUsername("user2");
                user2.setEmail("user2@gmail.com");
                user2.setPasswordHash("user456");
                user2.setRole(UserRole.customer);
                userRepository.save(user2);
            }
        };
    }
}
