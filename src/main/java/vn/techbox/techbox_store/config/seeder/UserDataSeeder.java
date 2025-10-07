package vn.techbox.techbox_store.config.seeder;

import jakarta.transaction.Transactional;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.boot.CommandLineRunner;
import org.springframework.core.annotation.Order;
import org.springframework.stereotype.Component;
import vn.techbox.techbox_store.user.model.Permission;
import vn.techbox.techbox_store.user.model.UserPermission;
import vn.techbox.techbox_store.user.model.UserRole;
import vn.techbox.techbox_store.user.repository.PermissionRepository;
import vn.techbox.techbox_store.user.repository.RoleRepository;

import java.util.Set;

@Component
@Order(1)
public class UserDataSeeder implements CommandLineRunner {
    private static final Logger logger = LoggerFactory.getLogger(UserDataSeeder.class);

    private final RoleRepository roleRepository;
    private final PermissionRepository permissionRepository;

    public UserDataSeeder(RoleRepository roleRepository, PermissionRepository permissionRepository) {
        this.roleRepository = roleRepository;
        this.permissionRepository = permissionRepository;
    }

    @Override
    public void run(String... args) {
        try {
            boolean needPermissions = permissionRepository.count() == 0;
            boolean needRoles = roleRepository.count() == 0;
            if (needPermissions || needRoles) {
                logger.info("Seeding permissions & roles...");
                seedPermissionsAndRoles();
                logger.info("Permissions & roles seeding finished");
            } else {
                logger.info("Permissions & roles already exist, skipping");
            }
        } catch (Exception e) {
            logger.error("Failed seeding permissions/roles: {}", e.getMessage(), e);
            throw new RuntimeException("Permission/Role seeding failed", e);
        }
    }

    @Transactional
    public void seedPermissionsAndRoles() {
        createPermissions();
        createRoles();
    }

    private void createPermissions() {
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

    private void createRoles() {
        for (UserRole userRole : UserRole.values()) {
            if (!roleRepository.existsByName(userRole.getRoleName())) {
                var role = vn.techbox.techbox_store.user.model.Role.builder()
                        .name(userRole.getRoleName())
                        .description(getRoleDescription(userRole))
                        .build();
                var savedRole = roleRepository.save(role);
                var rolePermissions = getRolePermissions(userRole);
                if (!rolePermissions.isEmpty()) {
                    savedRole.setPermissions(rolePermissions);
                    roleRepository.save(savedRole);
                }
                logger.info("Created role {} with {} permissions", savedRole.getName(), rolePermissions.size());
            }
        }
    }

    private String getRoleDescription(UserRole userRole) {
        return switch (userRole) {
            case ROLE_ADMIN -> "Administrator with full system access";
            case ROLE_STAFF -> "Staff member with restricted management permissions";
            case ROLE_CUSTOMER -> "Customer with basic access";
        };
    }

    private Set<Permission> getRolePermissions(UserRole userRole) {
        Set<vn.techbox.techbox_store.user.model.Permission> permissions = new java.util.HashSet<>();
        switch (userRole) {
            case ROLE_ADMIN -> permissions.addAll(permissionRepository.findAll());
            case ROLE_STAFF -> {
                permissions.addAll(getPermissionsByModules(Set.of("PRODUCT", "ORDER", "PROMOTION", "VOUCHER", "CAMPAIGN")));
                permissions.removeIf(p -> p.getAction().equals("DELETE"));
            }
            case ROLE_CUSTOMER -> permissions.addAll(getPermissionsByModulesAndActions(Set.of("PRODUCT", "ORDER"), Set.of("READ")));
        }
        return permissions;
    }

    private Set<Permission> getPermissionsByModules(Set<String> modules) {
        Set<vn.techbox.techbox_store.user.model.Permission> permissions = new java.util.HashSet<>();
        for (String module : modules) {
            permissions.addAll(permissionRepository.findByModule(module));
        }
        return permissions;
    }

    private Set<Permission> getPermissionsByModulesAndActions(Set<String> modules, Set<String> actions) {
        Set<vn.techbox.techbox_store.user.model.Permission> permissions = new java.util.HashSet<>();
        for (String module : modules) {
            for (String action : actions) {
                permissionRepository.findByModuleAndAction(module, action).ifPresent(permissions::add);
            }
        }
        return permissions;
    }
}