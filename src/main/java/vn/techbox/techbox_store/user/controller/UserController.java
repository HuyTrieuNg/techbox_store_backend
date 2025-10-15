package vn.techbox.techbox_store.user.controller;

import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Pageable;
import org.springframework.data.domain.Sort;
import org.springframework.http.ResponseEntity;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.web.bind.annotation.*;
import vn.techbox.techbox_store.user.dto.*;
import vn.techbox.techbox_store.user.model.User;
import vn.techbox.techbox_store.user.service.UserService;

import java.net.URI;
import java.util.List;
import java.util.Map;
import java.util.stream.Collectors;

@RestController
@RequestMapping("/users")
public class UserController {

    private final UserService userService;

    public UserController(UserService userService) {
        this.userService = userService;
    }

    @GetMapping
    @PreAuthorize("hasAuthority('USER_READ')")
    public List<UserResponse> getAll() {
        return userService.getAllUsers().stream().map(UserResponse::from).collect(Collectors.toList());
    }

    @GetMapping("/paginated")
    @PreAuthorize("hasAuthority('USER_READ')")
    public ResponseEntity<PagedUserResponse> getAllPaginated(
            @RequestParam(defaultValue = "0") int page,
            @RequestParam(defaultValue = "10") int size,
            @RequestParam(defaultValue = "id") String sortBy,
            @RequestParam(defaultValue = "asc") String sortDir) {

        Sort.Direction direction = sortDir.equalsIgnoreCase("desc") ? Sort.Direction.DESC : Sort.Direction.ASC;
        Pageable pageable = PageRequest.of(page, size, Sort.by(direction, sortBy));

        Page<User> userPage = userService.getAllUsersWithPagination(pageable);
        Page<UserResponse> userResponsePage = userPage.map(UserResponse::from);

        return ResponseEntity.ok(PagedUserResponse.from(userResponsePage));
    }

    @GetMapping("/{id}")
    @PreAuthorize("hasAuthority('USER_READ') or @userService.isCurrentUser(#id)")
    public ResponseEntity<UserResponse> getOne(@PathVariable Integer id) {
        return userService.getUserById(id)
                .map(UserResponse::from)
                .map(ResponseEntity::ok)
                .orElse(ResponseEntity.notFound().build());
    }

    @PostMapping
    @PreAuthorize("hasAuthority('USER_WRITE')")
    public ResponseEntity<UserResponse> create(@RequestBody UserCreateRequest req) {
        User saved = userService.createUser(req);
        return ResponseEntity.created(URI.create("/api/users/" + saved.getId()))
                .body(UserResponse.from(saved));
    }

    @PatchMapping("/{id}")
    @PreAuthorize("hasAuthority('USER_WRITE') or @userService.isCurrentUser(#id)")
    public ResponseEntity<UserResponse> update(@PathVariable Integer id, @RequestBody UserUpdateRequest req) {
        User updated = userService.updateUser(id, req);
        return ResponseEntity.ok(UserResponse.from(updated));
    }

    @DeleteMapping("/{id}")
    @PreAuthorize("hasAuthority('USER_DELETE')")
    public ResponseEntity<Void> delete(@PathVariable Integer id) {
        // Check if user exists and is not already soft-deleted
        if (userService.getUserById(id).isEmpty()) {
            return ResponseEntity.notFound().build();
        }
        userService.deleteUser(id);
        return ResponseEntity.noContent().build();
    }

    @PatchMapping("/{id}/restore")
    @PreAuthorize("hasAuthority('USER_WRITE')")
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
    public ResponseEntity<UserResponse> getCurrentUserProfile() {
        Authentication authentication = SecurityContextHolder.getContext().getAuthentication();
        String email = authentication.getName();
        return userService.getUserByEmail(email)
                .map(UserResponse::from)
                .map(ResponseEntity::ok)
                .orElse(ResponseEntity.notFound().build());
    }

    @PatchMapping("/profile")
    @PreAuthorize("isAuthenticated()")
    public ResponseEntity<UserResponse> updateCurrentUserProfile(@RequestBody UserUpdateRequest req) {
        Authentication authentication = SecurityContextHolder.getContext().getAuthentication();
        String email = authentication.getName();
        return userService.getUserByEmail(email)
                .map(user -> {
                    User updated = userService.updateUser(user.getId(), req);
                    return ResponseEntity.ok(UserResponse.from(updated));
                })
                .orElse(ResponseEntity.notFound().build());
    }

    @PreAuthorize("hasRole('CUSTOMER')")
    @GetMapping("/debug/{userId}")
    public ResponseEntity<?> test(@PathVariable Integer userId) {
        boolean match = userService.isCurrentUser(userId);
        return ResponseEntity.ok(Map.of("match", match));
    }
}
