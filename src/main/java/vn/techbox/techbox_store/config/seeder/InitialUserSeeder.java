package vn.techbox.techbox_store.config.seeder;

import com.github.javafaker.Faker;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.boot.CommandLineRunner;
import org.springframework.core.annotation.Order;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Component;
import org.springframework.transaction.annotation.Transactional;
import vn.techbox.techbox_store.user.model.*;
import vn.techbox.techbox_store.user.repository.AccountRepository;
import vn.techbox.techbox_store.user.repository.RoleRepository;
import vn.techbox.techbox_store.user.repository.UserRepository;

import java.util.HashSet;
import java.util.Locale;

@Component
@Order(2)
public class InitialUserSeeder implements CommandLineRunner {
    private static final Logger log = LoggerFactory.getLogger(InitialUserSeeder.class);

    private final UserRepository userRepository;
    private final AccountRepository accountRepository;
    private final RoleRepository roleRepository;
    private final PasswordEncoder passwordEncoder;
    private final Faker faker = new Faker(new Locale("vi-VN"));


    public InitialUserSeeder(UserRepository userRepository,
                             AccountRepository accountRepository,
                             RoleRepository roleRepository,
                             PasswordEncoder passwordEncoder) {
        this.userRepository = userRepository;
        this.accountRepository = accountRepository;
        this.roleRepository = roleRepository;
        this.passwordEncoder = passwordEncoder;
    }

    @Override
    public void run(String... args) {
        if (userRepository.count() > 0) {
            log.info("Users already present, skip initial user seeding");
            return;
        }
        try {
            seedDefaultUsers();
        } catch (Exception e) {
            log.error("Failed to seed initial users: {}", e.getMessage(), e);
        }
    }

    @Transactional
    protected void seedDefaultUsers() {
        log.info("Seeding default users (email-based login)...");
        // Admin
        createSingleRoleUser("admin@techbox.vn", "admin123", "Admin", "System", UserRole.ROLE_ADMIN);

        // Staff accounts
        createSingleRoleUser("staff1@techbox.vn", "staff123", "Staff", "One", UserRole.ROLE_STAFF);
        createSingleRoleUser("staff2@techbox.vn", "staff123", "Staff", "Two", UserRole.ROLE_STAFF);

        createSingleRoleUser("customer1@techbox.vn", "customer123", "Customer", "One", UserRole.ROLE_CUSTOMER);
        // Customer demo accounts
        log.info("Seeding 50 random customer accounts...");
        for (int i = 0; i < 50; i++) {
            String firstName = faker.name().firstName();
            String lastName = faker.name().lastName();
            String email = faker.internet().safeEmailAddress(firstName.toLowerCase() + "." + lastName.toLowerCase());
            createSingleRoleUser(email, "customer123", firstName, lastName, UserRole.ROLE_CUSTOMER);
        }
        log.info("Default user seeding done");
    }

    private void createSingleRoleUser(String email, String rawPassword, String firstName, String lastName, UserRole roleEnum) {
        if (accountRepository.existsByEmail(email)) {
            log.debug("Account with email {} already exists", email);
            return;
        }
        var roleOpt = roleRepository.findByName(roleEnum.getRoleName());
        if (roleOpt.isEmpty()) {
            log.warn("Role {} not found. Skipping user {}", roleEnum.getRoleName(), email);
            return;
        }
        var account = Account.builder()
                .email(email)
                .passwordHash(passwordEncoder.encode(rawPassword))
                .isActive(true)
                .isLocked(false)
                .build();

        var user = User.builder()
                .firstName(firstName)
                .lastName(lastName)
                .account(account)
                .roles(new HashSet<>())
                .build();
        var saved = userRepository.save(user);
        saved.getRoles().add(roleOpt.get());
        userRepository.save(saved);
        log.info("Created user {} with role {}", email, roleEnum.getRoleName());
    }
}
