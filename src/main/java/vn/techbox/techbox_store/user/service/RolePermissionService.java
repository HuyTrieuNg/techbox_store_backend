package vn.techbox.techbox_store.user.service;

import vn.techbox.techbox_store.user.dto.*;

import java.util.List;

public interface RolePermissionService {

    // Role management
    List<RoleResponse> getAllRoles();
    RoleResponse getRoleById(Integer roleId);
    RoleResponse getRoleByName(String name);
    RoleResponse createRole(RoleCreateRequest request);
    void deleteRole(Integer roleId);

    // Permission management
    List<PermissionResponse> getAllPermissions();
    List<PermissionResponse> getPermissionsByModule(String module);
    PermissionResponse getPermissionById(Integer permissionId);
    PermissionResponse createPermission(PermissionCreateRequest request);
    void deletePermission(Integer permissionId);

    // Module Permission management
    List<ModulePermissionResponse> getAllModulePermissions();
    ModulePermissionResponse getModulePermissionByName(String moduleName);
    ModulePermissionResponse createModulePermission(ModulePermissionCreateRequest request);
    void deleteModulePermission(String moduleName);

    // Role-Permission assignment
    RoleResponse assignPermissionsToRole(AssignPermissionsRequest request);
    RoleResponse removePermissionFromRole(Integer roleId, Integer permissionId);
    RoleResponse addPermissionToRole(Integer roleId, Integer permissionId);
}
