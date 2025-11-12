package vn.techbox.techbox_store.config.seeder.fortest;

import com.github.javafaker.Faker;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.boot.CommandLineRunner;
import org.springframework.context.annotation.Profile;
import org.springframework.core.annotation.Order;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Component;
import org.springframework.transaction.annotation.Transactional;
import vn.techbox.techbox_store.user.model.*;
import vn.techbox.techbox_store.user.repository.AccountRepository;
import vn.techbox.techbox_store.user.repository.RoleRepository;
import vn.techbox.techbox_store.user.repository.UserRepository;

import java.time.LocalDateTime;
import java.util.HashSet;

@Component
@Order(2)
public class InitialUserSeeder implements CommandLineRunner {
    private static final Logger log = LoggerFactory.getLogger(InitialUserSeeder.class);

    private final UserRepository userRepository;
    private final AccountRepository accountRepository;
    private final RoleRepository roleRepository;
    private final PasswordEncoder passwordEncoder;


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

        createSingleRoleUser("testcustomer1@techbox.vn", "customer123", "Customer", "Test", UserRole.ROLE_CUSTOMER);

        // Specific customer account for testing
        createSingleRoleUser("testCustomer@techbox.vn", "customer123", "Customer", "Test", UserRole.ROLE_CUSTOMER);
        createSingleRoleUser("testCustomer123@techbox.vn", "@Customer123", "Customer", "Test", UserRole.ROLE_CUSTOMER);

        // Test accounts for different scenarios
        createTestUser("locked@techbox.vn", "locked123", "Locked", "User", UserRole.ROLE_CUSTOMER, true, true, false);
        createTestUser("disabled@techbox.vn", "disabled123", "Disabled", "User", UserRole.ROLE_CUSTOMER, false, false, false);

        // Additional users for login test cases that should fail with INVALID_CREDENTIALS
        createSingleRoleUser("invalidemail", "somepassword", "Invalid", "Email", UserRole.ROLE_CUSTOMER);
        createSingleRoleUser("test@", "somepassword", "Test", "At", UserRole.ROLE_CUSTOMER);
        createSingleRoleUser(" test@techbox.vn ", "somepassword", "Test", "Spaces", UserRole.ROLE_CUSTOMER);
        createSingleRoleUser("TESTCUSTOMER@TECHBOX.VN", "somepassword", "Test", "Upper", UserRole.ROLE_CUSTOMER);
        createSingleRoleUser("user+test@techbox.vn", "somepassword", "User", "Plus", UserRole.ROLE_CUSTOMER);
        createSingleRoleUser("测试@techbox.vn", "somepassword", "Test", "Unicode", UserRole.ROLE_CUSTOMER);
        createSingleRoleUser("user@sub.techbox.vn", "somepassword", "User", "Sub", UserRole.ROLE_CUSTOMER);



    }

    private void createSingleRoleUser(String email, String rawPassword, String firstName, String lastName, UserRole roleEnum) {
        createTestUser(email, rawPassword, firstName, lastName, roleEnum, true, false, false);
    }

    private void createTestUser(String email, String rawPassword, String firstName, String lastName, UserRole roleEnum,
                               boolean isActive, boolean isLocked, boolean isDeleted) {
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
                .isActive(isActive)
                .isLocked(isLocked)
                .build();

        var user = User.builder()
                .firstName(firstName)
                .lastName(lastName)
                .account(account)
                .roles(new HashSet<>())
                .build();

        if (isDeleted) {
            user.setDeletedAt(LocalDateTime.now());
            account.setIsActive(false); // For deleted users, also set isActive to false
        }

        var saved = userRepository.save(user);
        saved.getRoles().add(roleOpt.get());
        userRepository.save(saved);
        log.info("Created user {} with role {} (active: {}, locked: {}, deleted: {})",
                email, roleEnum.getRoleName(), isActive, isLocked, isDeleted);
    }
}
