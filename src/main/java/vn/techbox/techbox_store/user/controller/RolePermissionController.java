package vn.techbox.techbox_store.user.controller;

import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.tags.Tag;
import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.web.bind.annotation.*;
import vn.techbox.techbox_store.user.dto.*;
import vn.techbox.techbox_store.user.service.RolePermissionService;

import java.util.List;

@RestController
@RequestMapping("/roles")
@RequiredArgsConstructor
@Slf4j
@Tag(name = "Role & Permission Management", description = "APIs for managing roles and permissions")
public class RolePermissionController {

    private final RolePermissionService rolePermissionService;

    @GetMapping
    @PreAuthorize("hasRole('ADMIN') or hasAuthority('USER:READ')")
    @Operation(summary = "Get all roles", description = "Get list of all roles with their permissions")
    public ResponseEntity<List<RoleResponse>> getAllRoles() {
        log.info("Getting all roles");
        List<RoleResponse> roles = rolePermissionService.getAllRoles();
        return ResponseEntity.ok(roles);
    }

    @GetMapping("/{roleId}")
    @PreAuthorize("hasRole('ADMIN') or hasAuthority('USER:READ')")
    @Operation(summary = "Get role by ID", description = "Get role details by ID")
    public ResponseEntity<RoleResponse> getRoleById(@PathVariable Integer roleId) {
        log.info("Getting role by id: {}", roleId);
        RoleResponse role = rolePermissionService.getRoleById(roleId);
        return ResponseEntity.ok(role);
    }

    @GetMapping("/name/{name}")
    @PreAuthorize("hasRole('ADMIN') or hasAuthority('USER:READ')")
    @Operation(summary = "Get role by name", description = "Get role details by name")
    public ResponseEntity<RoleResponse> getRoleByName(@PathVariable String name) {
        log.info("Getting role by name: {}", name);
        RoleResponse role = rolePermissionService.getRoleByName(name);
        return ResponseEntity.ok(role);
    }

    @PostMapping
    @PreAuthorize("hasRole('ADMIN') or hasAuthority('USER:WRITE')")
    @Operation(summary = "Create new role", description = "Create a new role")
    public ResponseEntity<RoleResponse> createRole(@Valid @RequestBody RoleCreateRequest request) {
        log.info("Creating new role: {}", request.getName());
        RoleResponse role = rolePermissionService.createRole(request);
        return ResponseEntity.status(HttpStatus.CREATED).body(role);
    }

    @DeleteMapping("/{roleId}")
    @PreAuthorize("hasRole('ADMIN') or hasAuthority('USER:DELETE')")
    @Operation(summary = "Delete role", description = "Delete a role by ID")
    public ResponseEntity<Void> deleteRole(@PathVariable Integer roleId) {
        log.info("Deleting role: {}", roleId);
        rolePermissionService.deleteRole(roleId);
        return ResponseEntity.noContent().build();
    }

    @GetMapping("/permissions")
    @PreAuthorize("hasRole('ADMIN')")
    @Operation(summary = "Get all permissions", description = "Get list of all available permissions")
    public ResponseEntity<List<PermissionResponse>> getAllPermissions() {
        log.info("Getting all permissions");
        List<PermissionResponse> permissions = rolePermissionService.getAllPermissions();
        return ResponseEntity.ok(permissions);
    }

    @GetMapping("/permissions/module/{module}")
    @PreAuthorize("hasRole('ADMIN')")
    @Operation(summary = "Get permissions by module", description = "Get permissions filtered by module")
    public ResponseEntity<List<PermissionResponse>> getPermissionsByModule(@PathVariable String module) {
        log.info("Getting permissions for module: {}", module);
        List<PermissionResponse> permissions = rolePermissionService.getPermissionsByModule(module);
        return ResponseEntity.ok(permissions);
    }

    @GetMapping("/permissions/{permissionId}")
    @PreAuthorize("hasRole('ADMIN')")
    @Operation(summary = "Get permission by ID", description = "Get permission details by ID")
    public ResponseEntity<PermissionResponse> getPermissionById(@PathVariable Integer permissionId) {
        log.info("Getting permission by id: {}", permissionId);
        PermissionResponse permission = rolePermissionService.getPermissionById(permissionId);
        return ResponseEntity.ok(permission);
    }

    @PostMapping("/permissions")
    @PreAuthorize("hasRole('ADMIN')")
    @Operation(summary = "Create new permission", description = "Create a new permission")
    public ResponseEntity<PermissionResponse> createPermission(@Valid @RequestBody PermissionCreateRequest request) {
        log.info("Creating new permission: {}", request.getName());
        PermissionResponse permission = rolePermissionService.createPermission(request);
        return ResponseEntity.status(HttpStatus.CREATED).body(permission);
    }

    @DeleteMapping("/permissions/{permissionId}")
    @PreAuthorize("hasRole('ADMIN')")
    @Operation(summary = "Delete permission", description = "Soft delete a permission by ID")
    public ResponseEntity<Void> deletePermission(@PathVariable Integer permissionId) {
        log.info("Deleting permission: {}", permissionId);
        rolePermissionService.deletePermission(permissionId);
        return ResponseEntity.noContent().build();
    }

    @GetMapping("/modules")
    @PreAuthorize("hasRole('ADMIN')")
    @Operation(summary = "Get all module permissions", description = "Get list of all module permissions with their permissions")
    public ResponseEntity<List<ModulePermissionResponse>> getAllModulePermissions() {
        log.info("Getting all module permissions");
        List<ModulePermissionResponse> modules = rolePermissionService.getAllModulePermissions();
        return ResponseEntity.ok(modules);
    }

    @GetMapping("/modules/{moduleName}")
    @PreAuthorize("hasRole('ADMIN')")
    @Operation(summary = "Get module permission by name", description = "Get module permission details by name")
    public ResponseEntity<ModulePermissionResponse> getModulePermissionByName(@PathVariable String moduleName) {
        log.info("Getting module permission by name: {}", moduleName);
        ModulePermissionResponse module = rolePermissionService.getModulePermissionByName(moduleName);
        return ResponseEntity.ok(module);
    }

    @PostMapping("/modules")
    @PreAuthorize("hasRole('ADMIN')")
    @Operation(summary = "Create new module permission", description = "Create a new module permission")
    public ResponseEntity<ModulePermissionResponse> createModulePermission(@Valid @RequestBody ModulePermissionCreateRequest request) {
        log.info("Creating new module permission: {}", request.getModuleName());
        ModulePermissionResponse module = rolePermissionService.createModulePermission(request);
        return ResponseEntity.status(HttpStatus.CREATED).body(module);
    }

    @DeleteMapping("/modules/{moduleName}")
    @PreAuthorize("hasRole('ADMIN')")
    @Operation(summary = "Delete module permission", description = "Delete a module permission by name")
    public ResponseEntity<Void> deleteModulePermission(@PathVariable String moduleName) {
        log.info("Deleting module permission: {}", moduleName);
        rolePermissionService.deleteModulePermission(moduleName);
        return ResponseEntity.noContent().build();
    }

    @PutMapping("/{roleId}/permissions")
    @PreAuthorize("hasRole('ADMIN')")
    @Operation(summary = "Assign permissions to role", description = "Replace all permissions of a role with new set")
    public ResponseEntity<RoleResponse> assignPermissionsToRole(
            @PathVariable Integer roleId,
            @Valid @RequestBody AssignPermissionsRequest request) {
        log.info("Assigning permissions to role: {}", roleId);
        request.setRoleId(roleId);
        RoleResponse role = rolePermissionService.assignPermissionsToRole(request);
        return ResponseEntity.ok(role);
    }

    @PostMapping("/{roleId}/permissions/{permissionId}")
    @PreAuthorize("hasRole('ADMIN')")
    @Operation(summary = "Add permission to role", description = "Add a single permission to a role")
    public ResponseEntity<RoleResponse> addPermissionToRole(
            @PathVariable Integer roleId,
            @PathVariable Integer permissionId) {
        log.info("Adding permission {} to role {}", permissionId, roleId);
        RoleResponse role = rolePermissionService.addPermissionToRole(roleId, permissionId);
        return ResponseEntity.ok(role);
    }

    @DeleteMapping("/{roleId}/permissions/{permissionId}")
    @PreAuthorize("hasRole('ADMIN')")
    @Operation(summary = "Remove permission from role", description = "Remove a single permission from a role")
    public ResponseEntity<RoleResponse> removePermissionFromRole(
            @PathVariable Integer roleId,
            @PathVariable Integer permissionId) {
        log.info("Removing permission {} from role {}", permissionId, roleId);
        RoleResponse role = rolePermissionService.removePermissionFromRole(roleId, permissionId);
        return ResponseEntity.ok(role);
    }
}
