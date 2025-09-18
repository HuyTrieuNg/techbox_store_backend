package vn.techbox.techbox_store.controller;

import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;
import vn.techbox.techbox_store.model.User;
import vn.techbox.techbox_store.model.UserRole;
import vn.techbox.techbox_store.service.UserService;

import java.net.URI;
import java.util.List;
import java.util.stream.Collectors;

@RestController
@RequestMapping("/api/users")
public class UserController {

    private final UserService userService;

    public UserController(UserService userService) {
        this.userService = userService;
    }

    @GetMapping
    public List<UserResponse> getAll() {
        return userService.getAllUsers().stream().map(UserResponse::from).collect(Collectors.toList());
    }

    @GetMapping("/{id}")
    public ResponseEntity<UserResponse> getOne(@PathVariable Integer id) {
        return userService.getUserById(id)
                .map(UserResponse::from)
                .map(ResponseEntity::ok)
                .orElse(ResponseEntity.notFound().build());
    }

    @PostMapping
    public ResponseEntity<UserResponse> create(@RequestBody UserCreateRequest req) {
        User u = new User();
        u.setUsername(req.username());
        u.setEmail(req.email());
        u.setRole(req.role());
        // For demo only; normally hash the password
        u.setPasswordHash(req.password() != null ? req.password() : "default_password");
        User saved = userService.createUser(u);
        return ResponseEntity.created(URI.create("/api/users/" + saved.getId()))
                .body(UserResponse.from(saved));
    }

    @PutMapping("/{id}")
    public ResponseEntity<UserResponse> update(@PathVariable Integer id, @RequestBody UserUpdateRequest req) {
        User details = new User();
        details.setUsername(req.username());
        details.setEmail(req.email());
        details.setRole(req.role());
        User updated = userService.updateUser(id, details);
        return ResponseEntity.ok(UserResponse.from(updated));
    }

    @DeleteMapping("/{id}")
    public ResponseEntity<Void> delete(@PathVariable Integer id) {
        if (userService.getUserById(id).isEmpty()) {
            return ResponseEntity.notFound().build();
        }
        userService.deleteUser(id);
        return ResponseEntity.noContent().build();
    }

    public record UserCreateRequest(String username, String email, String password, UserRole role) {
    }

    public record UserUpdateRequest(String username, String email, UserRole role) {
    }

    public record UserResponse(Integer id, String username, String email, UserRole role) {
        public static UserResponse from(User u) {
            return new UserResponse(u.getId(), u.getUsername(), u.getEmail(), u.getRole());
        }
    }
}
