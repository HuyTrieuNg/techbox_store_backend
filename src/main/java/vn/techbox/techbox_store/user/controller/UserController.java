package vn.techbox.techbox_store.user.controller;

import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Pageable;
import org.springframework.data.domain.Sort;
import org.springframework.http.ResponseEntity;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.security.core.GrantedAuthority;
import org.springframework.security.core.annotation.AuthenticationPrincipal;
import org.springframework.web.bind.annotation.*;
import vn.techbox.techbox_store.user.dto.*;
import vn.techbox.techbox_store.user.model.User;
import vn.techbox.techbox_store.user.security.UserPrincipal;
import vn.techbox.techbox_store.user.service.UserService;

import java.net.URI;
import java.util.*;
import java.util.stream.Collectors;

@RestController
@RequestMapping("/users")
public class UserController {

    private final UserService userService;

    public UserController(UserService userService) {
        this.userService = userService;
    }

    @GetMapping
    @PreAuthorize("hasAuthority('USER:READ')")
    public ResponseEntity<PagedUserResponse> getAll(
            @RequestParam(defaultValue = "0") int page,
            @RequestParam(defaultValue = "10") int size,
            @RequestParam(defaultValue = "id") String sortBy,
            @RequestParam(defaultValue = "asc") String sortDir,
            @RequestParam(defaultValue = "false") boolean includeDeleted) {

        Sort.Direction direction = sortDir.equalsIgnoreCase("desc") ? Sort.Direction.DESC : Sort.Direction.ASC;
        Pageable pageable = PageRequest.of(page, size, Sort.by(direction, sortBy));

        Page<User> userPage = userService.getAllUsersWithPagination(pageable, includeDeleted);
        Page<UserResponse> userResponsePage = userPage.map(UserResponse::from);

        return ResponseEntity.ok(PagedUserResponse.from(userResponsePage));
    }

    @GetMapping("/by-role/{roleName}")
    @PreAuthorize("hasAuthority('USER:READ')")
    public ResponseEntity<PagedUserResponse> getUsersByRole(
            @PathVariable String roleName,
            @RequestParam(defaultValue = "0") int page,
            @RequestParam(defaultValue = "10") int size,
            @RequestParam(defaultValue = "id") String sortBy,
            @RequestParam(defaultValue = "asc") String sortDir,
            @RequestParam(defaultValue = "false") boolean includeDeleted) {

        // Tự động thêm ROLE_ prefix nếu chưa có
        String normalizedRoleName = roleName.startsWith("ROLE_") ? roleName : "ROLE_" + roleName;

        Sort.Direction direction = sortDir.equalsIgnoreCase("desc") ? Sort.Direction.DESC : Sort.Direction.ASC;
        Pageable pageable = PageRequest.of(page, size, Sort.by(direction, sortBy));

        Page<User> userPage = userService.getUsersByRole(normalizedRoleName, pageable, includeDeleted);
        Page<UserResponse> userResponsePage = userPage.map(UserResponse::from);

        return ResponseEntity.ok(PagedUserResponse.from(userResponsePage));
    }

    @GetMapping("/{id}")
    @PreAuthorize("hasAuthority('USER:READ') or @userService.isCurrentUser(#userPrincipal, #id)")
    public ResponseEntity<UserResponse> getOne(
            @AuthenticationPrincipal UserPrincipal userPrincipal,
            @PathVariable Integer id) {
        return userService.getUserById(id)
                .map(UserResponse::from)
                .map(ResponseEntity::ok)
                .orElse(ResponseEntity.notFound().build());
    }

    @PostMapping
    @PreAuthorize("hasAuthority('USER:WRITE')")
    public ResponseEntity<UserResponse> create(@RequestBody UserCreateRequest req) {
        User saved = userService.createUser(req);
        return ResponseEntity.created(URI.create("/api/users/" + saved.getId()))
                .body(UserResponse.from(saved));
    }

    @PatchMapping("/{id}")
    @PreAuthorize("hasAuthority('USER:UPDATE') or @userService.isCurrentUser(#userPrincipal, #id)")
    public ResponseEntity<UserResponse> update(
            @AuthenticationPrincipal UserPrincipal userPrincipal,
            @PathVariable Integer id,
            @RequestBody UserUpdateRequest req) {
        User updated = userService.updateUser(id, req);
        return ResponseEntity.ok(UserResponse.from(updated));
    }

    @DeleteMapping("/{id}")
    @PreAuthorize("hasAuthority('USER:DELETE')")
    public ResponseEntity<Void> delete(@PathVariable Integer id) {
        // Check if user exists and is not already soft-deleted
        if (userService.getUserById(id).isEmpty()) {
            return ResponseEntity.notFound().build();
        }
        userService.deleteUser(id);
        return ResponseEntity.noContent().build();
    }

    @PatchMapping("/{id}/restore")
    @PreAuthorize("hasAuthority('USER:WRITE')")
    public ResponseEntity<UserResponse> restore(@PathVariable Integer id) {
        try {
            userService.restoreUser(id);
            User restoredUser = userService.getUserById(id)
                .orElseThrow(() -> new RuntimeException("Failed to retrieve restored user"));
            return ResponseEntity.ok(UserResponse.from(restoredUser));
        } catch (RuntimeException e) {
            if (e.getMessage().contains("User not found")) {
                return ResponseEntity.notFound().build();
            }
            if (e.getMessage().contains("User is not deleted")) {
                return ResponseEntity.badRequest().build();
            }
            throw e;
        }
    }

    @GetMapping("/profile")
    @PreAuthorize("isAuthenticated()")
    public ResponseEntity<UserResponse> getCurrentUserProfile(@AuthenticationPrincipal UserPrincipal userPrincipal) {
        Optional<User> currentUser = userService.getUserByEmailWithAddresses(userPrincipal.getUsername());
        return currentUser
                .map(UserResponse::from)
                .map(ResponseEntity::ok)
                .orElse(ResponseEntity.notFound().build());
    }

    @PatchMapping("/profile")
    @PreAuthorize("isAuthenticated()")
    public ResponseEntity<UserResponse> updateCurrentUserProfile(@AuthenticationPrincipal UserPrincipal userPrincipal,
                                                                 @RequestBody UserUpdateRequest req) {
        User updated = userService.updateUser(userPrincipal.getId(), req);
        return ResponseEntity.ok(UserResponse.from(updated));
    }

    @PreAuthorize("hasRole('CUSTOMER')")
    @GetMapping("/debug/{userId}")
    public ResponseEntity<?> test(
            @AuthenticationPrincipal UserPrincipal userPrincipal,
            @PathVariable Integer userId) {
        boolean match = userService.isCurrentUser(userPrincipal, userId);
        return ResponseEntity.ok(Map.of("match", match));
    }

    @GetMapping("/me")
    @PreAuthorize("isAuthenticated()")
    public ResponseEntity<Map<String, Object>> getCurrentUser(
            @AuthenticationPrincipal UserPrincipal userPrincipal) {

        if (userPrincipal == null) {
            return ResponseEntity.status(401).body(Map.of("error", "Unauthorized"));
        }

        // Lấy role từ authorities
        Set<String> roles = userPrincipal.getAuthorities().stream()
                .map(GrantedAuthority::getAuthority)
                .filter(auth -> auth.startsWith("ROLE_"))
                .collect(Collectors.toSet());

        Map<String, Object> response = Map.of(
            "id", userPrincipal.getId(),
            "username", userPrincipal.getUsername(),
            "email", userPrincipal.email(),
            "firstName", userPrincipal.firstName(),
            "lastName", userPrincipal.lastName(),
            "authenticated", true,
            "roles", roles
        );

        return ResponseEntity.ok(response);
    }

    @GetMapping("/me/authorities")
    @PreAuthorize("isAuthenticated()")
    public ResponseEntity<Map<String, Object>> getCurrentUserAuthorities(@AuthenticationPrincipal UserPrincipal userPrincipal) {
        Map<String, Object> response = new HashMap<>();

        // Lấy thông tin từ UserPrincipal
        response.put("username", userPrincipal.getUsername());
        response.put("authenticated", true);
        response.put("userId", userPrincipal.getId());
        response.put("userEmail", userPrincipal.email());
        response.put("firstName", userPrincipal.firstName());
        response.put("lastName", userPrincipal.lastName());

        // Lấy tất cả authorities từ UserPrincipal
        Set<String> authorities = userPrincipal.getAuthorities().stream()
                .map(GrantedAuthority::getAuthority)
                .collect(Collectors.toSet());

        // Phân loại roles và permissions
        Set<String> roles = new HashSet<>();
        Set<String> permissions = new HashSet<>();

        for (String authority : authorities) {
            if (authority.startsWith("ROLE_")) {
                roles.add(authority);
            } else {
                permissions.add(authority);
            }
        }

        response.put("allAuthorities", authorities);
        response.put("roles", roles);
        response.put("permissions", permissions);
        response.put("authorityCount", authorities.size());

        return ResponseEntity.ok(response);
    }

    @GetMapping("/debug/security-context")
    @PreAuthorize("isAuthenticated()")
    public ResponseEntity<Map<String, Object>> debugSecurityContext(@AuthenticationPrincipal UserPrincipal userPrincipal) {
        Map<String, Object> debug = new HashMap<>();

        debug.put("principal", "UserPrincipal");
        debug.put("username", userPrincipal.getUsername());
        debug.put("userId", userPrincipal.getId());
        debug.put("authenticated", userPrincipal.isEnabled());
        debug.put("accountNonLocked", userPrincipal.isAccountNonLocked());
        debug.put("enabled", userPrincipal.isEnabled());

        // Chi tiết về authorities
        List<Map<String, String>> authoritiesDetail = userPrincipal.getAuthorities().stream()
                .map(auth -> {
                    Map<String, String> authMap = new HashMap<>();
                    authMap.put("authority", auth.getAuthority());
                    authMap.put("class", auth.getClass().getSimpleName());
                    return authMap;
                })
                .collect(Collectors.toList());

        debug.put("authorities", authoritiesDetail);
        debug.put("totalAuthorities", userPrincipal.getAuthorities().size());

        return ResponseEntity.ok(debug);
    }
}
