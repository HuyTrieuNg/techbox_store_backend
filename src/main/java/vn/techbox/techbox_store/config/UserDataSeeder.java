package vn.techbox.techbox_store.config;

import org.springframework.boot.CommandLineRunner;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.security.crypto.bcrypt.BCryptPasswordEncoder;
import org.springframework.transaction.annotation.Transactional;
import vn.techbox.techbox_store.user.model.*;
import vn.techbox.techbox_store.user.repository.*;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.util.HashSet;
import java.util.Set;

@Configuration
public class UserDataSeeder {
    private static final Logger logger = LoggerFactory.getLogger(UserDataSeeder.class);
    private final BCryptPasswordEncoder encoder = new BCryptPasswordEncoder(12);

    @Bean
    CommandLineRunner seedUsers(UserRepository userRepository,
                               AccountRepository accountRepository,
                               RoleRepository roleRepository,
                               PermissionRepository permissionRepository) {
        return args -> {
            try {
                if (userRepository.count() == 0) {
                    logger.info("Seeding initial data...");

                    seedData(userRepository, accountRepository, roleRepository, permissionRepository);

                    logger.info("Data seeding completed successfully!");
                } else {
                    logger.info("Data already exists, skipping seeding");
                }
            } catch (Exception e) {
                logger.error("Error seeding data: {}", e.getMessage(), e);
                throw new RuntimeException("Failed to seed data", e);
            }
        };
    }

    @Transactional
    public void seedData(UserRepository userRepository,
                        AccountRepository accountRepository,
                        RoleRepository roleRepository,
                        PermissionRepository permissionRepository) {
        createPermissions(permissionRepository);
        createRoles(roleRepository, permissionRepository);
        createUsers(userRepository, accountRepository, roleRepository);
    }

    private void createPermissions(PermissionRepository permissionRepository) {
        logger.info("Creating permissions...");

        for (UserPermission permission : UserPermission.values()) {
            if (!permissionRepository.existsByName(permission.getPermissionName())) {
                Permission perm = Permission.builder()
                        .name(permission.getPermissionName())
                        .description("Permission for " + permission.getModule() + " " + permission.getAction())
                        .module(permission.getModule())
                        .action(permission.getAction())
                        .build();
                permissionRepository.save(perm);
                logger.info("Created permission: {}", perm.getName());
            }
        }
    }

    private void createRoles(RoleRepository roleRepository, PermissionRepository permissionRepository) {
        logger.info("Creating roles...");

        // Create roles dynamically from enum
        for (UserRole userRole : UserRole.values()) {
            if (!roleRepository.existsByName(userRole.getRoleName())) {
                Set<Permission> rolePermissions = getRolePermissions(userRole, permissionRepository);

                Role role = Role.builder()
                        .name(userRole.getRoleName())
                        .description(getRoleDescription(userRole))
                        .build();

                // Save role first without permissions to avoid detached entity issue
                Role savedRole = roleRepository.save(role);

                // Then add permissions using the managed entity
                if (!rolePermissions.isEmpty()) {
                    savedRole.setPermissions(rolePermissions);
                    roleRepository.save(savedRole);
                }

                logger.info("Created role: {} with {} permissions", savedRole.getName(), rolePermissions.size());
            }
        }
    }

    private Set<Permission> getRolePermissions(UserRole userRole, PermissionRepository permissionRepository) {
        Set<Permission> permissions = new java.util.HashSet<>();

        switch (userRole) {
            case ROLE_ADMIN:
                // Admin gets all permissions - reload from repository to ensure managed state
                permissions.addAll(permissionRepository.findAll());
                break;

            case ROLE_STAFF:
                // Staff gets permissions for managing products, orders, promotions, vouchers, campaigns
                permissions.addAll(getPermissionsByModules(permissionRepository,
                        Set.of("PRODUCT", "ORDER", "PROMOTION", "VOUCHER", "CAMPAIGN")));
                // Remove delete permissions for staff
                permissions.removeIf(p -> p.getAction().equals("DELETE"));
                break;

            case ROLE_CUSTOMER:
                // Customer gets only read permissions for products and orders
                permissions.addAll(getPermissionsByModulesAndActions(permissionRepository,
                        Set.of("PRODUCT", "ORDER"), Set.of("READ")));
                break;
        }

        return permissions;
    }

    private String getRoleDescription(UserRole userRole) {
        return switch (userRole) {
            case ROLE_ADMIN -> "Administrator with full system access";
            case ROLE_STAFF -> "Staff member with management permissions";
            case ROLE_CUSTOMER -> "Customer with basic access permissions";
        };
    }

    private Set<Permission> getPermissionsByModules(PermissionRepository permissionRepository, Set<String> modules) {
        Set<Permission> permissions = new java.util.HashSet<>();
        for (String module : modules) {
            permissions.addAll(permissionRepository.findByModule(module));
        }
        return permissions;
    }

    private Set<Permission> getPermissionsByModulesAndActions(PermissionRepository permissionRepository,
                                                            Set<String> modules, Set<String> actions) {
        Set<Permission> permissions = new java.util.HashSet<>();
        for (String module : modules) {
            for (String action : actions) {
                permissionRepository.findByModuleAndAction(module, action)
                    .ifPresent(permissions::add);
            }
        }
        return permissions;
    }

    private void createUsers(UserRepository userRepository,
                            AccountRepository accountRepository,
                            RoleRepository roleRepository) {
        logger.info("Creating users...");

        // Create admin user
        if (!accountRepository.existsByUsername("admin")) {
            Account adminAccount = Account.builder()
                    .username("admin")
                    .email("admin@techbox.vn")
                    .passwordHash(encoder.encode("admin123"))
                    .isActive(true)
                    .isLocked(false)
                    .build();

            User admin = User.builder()
                    .firstName("Admin")
                    .lastName("System")
                    .phone("0123456789")
                    .address("Ho Chi Minh City")
                    .account(adminAccount)
                    .roles(new HashSet<>()) // Create empty set first
                    .build();

            // Save user first
            User savedAdmin = userRepository.save(admin);

            // Then add roles to the managed entity
            Role adminRole = roleRepository.findByName(UserRole.ROLE_ADMIN.getRoleName()).orElseThrow();
            savedAdmin.getRoles().add(adminRole);
            userRepository.save(savedAdmin);

            logger.info("Created admin user: {}", savedAdmin.getAccount().getUsername());
        }

        // Create staff user
        if (!accountRepository.existsByUsername("staff")) {
            Account staffAccount = Account.builder()
                    .username("staff")
                    .email("staff@techbox.vn")
                    .passwordHash(encoder.encode("staff123"))
                    .isActive(true)
                    .isLocked(false)
                    .build();

            User staff = User.builder()
                    .firstName("Staff")
                    .lastName("User")
                    .phone("0987654321")
                    .address("Ho Chi Minh City")
                    .account(staffAccount)
                    .roles(new HashSet<>()) // Create empty set first
                    .build();

            // Save user first
            User savedStaff = userRepository.save(staff);

            // Then add roles to the managed entity
            Role staffRole = roleRepository.findByName(UserRole.ROLE_STAFF.getRoleName()).orElseThrow();
            savedStaff.getRoles().add(staffRole);
            userRepository.save(savedStaff);

            logger.info("Created staff user: {}", savedStaff.getAccount().getUsername());
        }

        // Create customer user
        if (!accountRepository.existsByUsername("customer")) {
            Account customerAccount = Account.builder()
                    .username("customer")
                    .email("customer@gmail.com")
                    .passwordHash(encoder.encode("customer123"))
                    .isActive(true)
                    .isLocked(false)
                    .build();

            User customer = User.builder()
                    .firstName("John")
                    .lastName("Doe")
                    .phone("0123987654")
                    .address("Ho Chi Minh City")
                    .account(customerAccount)
                    .roles(new HashSet<>()) // Create empty set first
                    .build();

            // Save user first
            User savedCustomer = userRepository.save(customer);

            // Then add roles to the managed entity
            Role customerRole = roleRepository.findByName(UserRole.ROLE_CUSTOMER.getRoleName()).orElseThrow();
            savedCustomer.getRoles().add(customerRole);
            userRepository.save(savedCustomer);

            logger.info("Created customer user: {}", savedCustomer.getAccount().getUsername());
        }
    }
}
