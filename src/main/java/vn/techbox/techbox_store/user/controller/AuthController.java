package vn.techbox.techbox_store.user.controller;

import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;
import vn.techbox.techbox_store.user.dto.*;
import vn.techbox.techbox_store.user.model.User;
import vn.techbox.techbox_store.user.service.AuthService;
import vn.techbox.techbox_store.user.service.UserService;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.net.URI;

@RestController
@RequestMapping("/api/auth")
public class AuthController {
    private static final Logger logger = LoggerFactory.getLogger(AuthController.class);
    private final AuthService authService;
    private final UserService userService;

    public AuthController(AuthService authService, UserService userService) {
        this.authService = authService;
        this.userService = userService;
    }

    @PostMapping("/login")
    public ResponseEntity<TokenResponse> login(@RequestBody UserLoginRequest req) {
        try {
            TokenResponse tokenResponse = userService.verify(req);
            logger.info("User {} logged in successfully", req.email());
            return ResponseEntity.ok(tokenResponse);
        } catch (Exception e) {
            logger.error("Login failed for user {}: {}", req.email(), e.getMessage());
            return ResponseEntity.badRequest().build();
        }
    }

    @PostMapping("/register")
    public ResponseEntity<UserResponse> register(@RequestBody UserCreateRequest req) {
        try {
            User savedUser = userService.createUser(req);
            logger.info("User {} registered successfully", req.email());
            return ResponseEntity.created(URI.create("/api/users/" + savedUser.getId()))
                    .body(UserResponse.from(savedUser));
        } catch (Exception e) {
            logger.error("Registration failed for user {}: {}", req.email(), e.getMessage());
            return ResponseEntity.badRequest().build();
        }
    }

    @PostMapping("/refresh-token")
    public ResponseEntity<?> refreshToken(@RequestBody RefreshTokenRequest req) {
        try {
            TokenResponse tokenResponse = authService.refreshToken(req.refreshToken());
            logger.info("Token refreshed successfully");
            return ResponseEntity.ok(tokenResponse);
        } catch (Exception e) {
            logger.error("Token refresh failed: {}", e.getMessage());

            ApiErrorResponse errorResponse = new ApiErrorResponse(
                "REFRESH_FAILED",
                "Failed to refresh token: " + e.getMessage(),
                false
            );
            return ResponseEntity.status(401).body(errorResponse);
        }
    }
}
