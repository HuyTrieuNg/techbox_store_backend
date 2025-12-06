package vn.techbox.techbox_store.user.service.impl;

import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import vn.techbox.techbox_store.user.dto.*;
import vn.techbox.techbox_store.user.model.Permission;
import vn.techbox.techbox_store.user.model.Role;
import vn.techbox.techbox_store.user.repository.PermissionRepository;
import vn.techbox.techbox_store.user.repository.RoleRepository;
import vn.techbox.techbox_store.user.service.RolePermissionService;

import java.util.HashSet;
import java.util.List;
import java.util.Set;
import java.util.stream.Collectors;

@Service
@RequiredArgsConstructor
@Slf4j
public class RolePermissionServiceImpl implements RolePermissionService {

    private final RoleRepository roleRepository;
    private final PermissionRepository permissionRepository;

    @Override
    @Transactional(readOnly = true)
    public List<RoleResponse> getAllRoles() {
        log.info("Fetching all roles");
        return roleRepository.findAllWithPermissions().stream()
                .map(this::mapToRoleResponse)
                .collect(Collectors.toList());
    }

    @Override
    @Transactional(readOnly = true)
    public RoleResponse getRoleById(Integer roleId) {
        log.info("Fetching role by id: {}", roleId);
        Role role = roleRepository.findById(roleId)
                .orElseThrow(() -> new RuntimeException("Role not found with id: " + roleId));
        return mapToRoleResponse(role);
    }

    @Override
    @Transactional(readOnly = true)
    public RoleResponse getRoleByName(String name) {
        log.info("Fetching role by name: {}", name);
        Role role = roleRepository.findByNameWithPermissions(name)
                .orElseThrow(() -> new RuntimeException("Role not found with name: " + name));
        return mapToRoleResponse(role);
    }

    @Override
    @Transactional
    public RoleResponse createRole(RoleCreateRequest request) {
        log.info("Creating new role: {}", request.getName());
        
        if (roleRepository.existsByName(request.getName())) {
            throw new RuntimeException("Role already exists with name: " + request.getName());
        }

        Role role = Role.builder()
                .name(request.getName())
                .description(request.getDescription())
                .permissions(new HashSet<>())
                .build();

        Role savedRole = roleRepository.save(role);
        log.info("Role created successfully: {}", savedRole.getName());
        
        return mapToRoleResponse(savedRole);
    }

    @Override
    @Transactional
    public void deleteRole(Integer roleId) {
        log.info("Soft deleting role with id: {}", roleId);

        Role role = roleRepository.findById(roleId)
                .orElseThrow(() -> new RuntimeException("Role not found with id: " + roleId));

        if (!role.getUsers().isEmpty()) {
            throw new RuntimeException("Cannot delete role. It is assigned to " + role.getUsers().size() + " user(s)");
        }

        // Soft delete
        role.setDeletedAt(java.time.LocalDateTime.now());
        roleRepository.save(role);
        log.info("Role soft deleted successfully: {}", roleId);
    }

    @Override
    @Transactional(readOnly = true)
    public List<PermissionResponse> getAllPermissions() {
        log.info("Fetching all permissions");
        return permissionRepository.findAll().stream()
                .map(this::mapToPermissionResponse)
                .collect(Collectors.toList());
    }

    @Override
    @Transactional(readOnly = true)
    public List<PermissionResponse> getPermissionsByModule(String module) {
        log.info("Fetching permissions for module: {}", module);
        return permissionRepository.findByModule(module).stream()
                .map(this::mapToPermissionResponse)
                .collect(Collectors.toList());
    }

    @Override
    @Transactional(readOnly = true)
    public PermissionResponse getPermissionById(Integer permissionId) {
        log.info("Fetching permission by id: {}", permissionId);
        Permission permission = permissionRepository.findById(permissionId)
                .orElseThrow(() -> new RuntimeException("Permission not found with id: " + permissionId));
        return mapToPermissionResponse(permission);
    }

    @Override
    @Transactional
    public RoleResponse assignPermissionsToRole(AssignPermissionsRequest request) {
        log.info("Assigning permissions to role: {}", request.getRoleId());
        
        Role role = roleRepository.findById(request.getRoleId())
                .orElseThrow(() -> new RuntimeException("Role not found with id: " + request.getRoleId()));

        Set<Permission> permissions = new HashSet<>();
        for (Integer permissionId : request.getPermissionIds()) {
            Permission permission = permissionRepository.findById(permissionId)
                    .orElseThrow(() -> new RuntimeException("Permission not found with id: " + permissionId));
            permissions.add(permission);
        }

        role.setPermissions(permissions);
        Role updatedRole = roleRepository.save(role);
        
        log.info("Permissions assigned successfully to role: {}", updatedRole.getName());
        return mapToRoleResponse(updatedRole);
    }

    @Override
    @Transactional
    public RoleResponse removePermissionFromRole(Integer roleId, Integer permissionId) {
        log.info("Removing permission {} from role {}", permissionId, roleId);
        
        Role role = roleRepository.findById(roleId)
                .orElseThrow(() -> new RuntimeException("Role not found with id: " + roleId));

        Permission permission = permissionRepository.findById(permissionId)
                .orElseThrow(() -> new RuntimeException("Permission not found with id: " + permissionId));

        role.getPermissions().remove(permission);
        Role updatedRole = roleRepository.save(role);
        
        log.info("Permission removed successfully from role");
        return mapToRoleResponse(updatedRole);
    }

    @Override
    @Transactional
    public RoleResponse addPermissionToRole(Integer roleId, Integer permissionId) {
        log.info("Adding permission {} to role {}", permissionId, roleId);
        
        Role role = roleRepository.findById(roleId)
                .orElseThrow(() -> new RuntimeException("Role not found with id: " + roleId));

        Permission permission = permissionRepository.findById(permissionId)
                .orElseThrow(() -> new RuntimeException("Permission not found with id: " + permissionId));

        role.getPermissions().add(permission);
        Role updatedRole = roleRepository.save(role);
        
        log.info("Permission added successfully to role");
        return mapToRoleResponse(updatedRole);
    }

    @Override
    @Transactional
    public void deletePermission(Integer permissionId) {
        log.info("Soft deleting permission with id: {}", permissionId);

        Permission permission = permissionRepository.findById(permissionId)
                .orElseThrow(() -> new RuntimeException("Permission not found with id: " + permissionId));

        if (!permission.getRoles().isEmpty()) {
            throw new RuntimeException("Cannot delete permission. It is assigned to " + permission.getRoles().size() + " role(s)");
        }

        // Soft delete
        permission.setDeletedAt(java.time.LocalDateTime.now());
        permissionRepository.save(permission);
        log.info("Permission soft deleted successfully: {}", permissionId);
    }

    private RoleResponse mapToRoleResponse(Role role) {
        Set<PermissionResponse> permissionResponses = role.getPermissions().stream()
                .map(this::mapToPermissionResponse)
                .collect(Collectors.toSet());

        return RoleResponse.builder()
                .id(role.getId())
                .name(role.getName())
                .description(role.getDescription())
                .permissions(permissionResponses)
                .createdAt(role.getCreatedAt())
                .updatedAt(role.getUpdatedAt())
                .build();
    }

    private PermissionResponse mapToPermissionResponse(Permission permission) {
        return PermissionResponse.builder()
                .id(permission.getId())
                .name(permission.getName())
                .description(permission.getDescription())
                .module(permission.getModule())
                .action(permission.getAction())
                .createdAt(permission.getCreatedAt())
                .updatedAt(permission.getUpdatedAt())
                .build();
    }
}

