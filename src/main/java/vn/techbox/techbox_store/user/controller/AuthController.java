package vn.techbox.techbox_store.user.controller;

import org.springframework.beans.factory.annotation.Value;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;
import vn.techbox.techbox_store.user.dto.*;
import vn.techbox.techbox_store.mail.service.EmailService;
import vn.techbox.techbox_store.user.model.Account;
import java.time.ZoneId;
import java.time.ZonedDateTime;
import java.net.URLEncoder;
import java.nio.charset.StandardCharsets;
import vn.techbox.techbox_store.user.model.User;
import vn.techbox.techbox_store.user.service.AuthService;
import vn.techbox.techbox_store.user.service.UserService;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.net.URI;

@RestController
@RequestMapping("/auth")
public class AuthController {
    private static final Logger logger = LoggerFactory.getLogger(AuthController.class);
    private final AuthService authService;
    private final UserService userService;
    private final EmailService emailService;

    @Value("${frontend.url}")
    private String frontendUrl;

    public AuthController(AuthService authService, UserService userService, EmailService emailService) {
        this.authService = authService;
        this.userService = userService;
        this.emailService = emailService;
    }

    @PostMapping("/forgot-password")
    public ResponseEntity<?> forgotPassword(@RequestBody ForgotPasswordRequest req) {
        try {
            // Do not reveal whether email exists; always return success for security
            userService.getUserByEmail(req.email()).ifPresent(user -> {
                Account account = user.getAccount();
                long updatedAt = account.getUpdatedAt() != null ?
                    account.getUpdatedAt().atZone(ZoneId.systemDefault()).toInstant().toEpochMilli() : 0L;
                String token = authService.generatePasswordResetToken(account.getEmail(), updatedAt);

                // Build reset URL (frontend should use this to open reset page)
                String resetPageUrl = String.format("%s/reset-password?token=%s", frontendUrl, URLEncoder.encode(token, StandardCharsets.UTF_8));
                String html = "<p>Click the link below to reset your Techbox password:</p>" +
                        "<a href=\"" + resetPageUrl + "\">Reset Password</a>" +
                        "<p>If you did not request this, you can safely ignore this email.</p>";

                emailService.sendHtmlMessage(account.getEmail(), "Techbox Password Reset", html);
            });

            return ResponseEntity.ok().build();
        } catch (Exception e) {
            logger.error("Forgot password request failed: {}", e.getMessage());
            return ResponseEntity.status(500).build();
        }
    }

    @PostMapping("/reset-password")
    public ResponseEntity<?> resetPassword(@RequestBody ResetPasswordRequest req) {
        try {
            String token = req.token();
            String email = authService.extractUserNameFromResetToken(token);
            long tokenUpdatedAt = authService.extractAccountUpdatedAtFromResetToken(token);

            userService.getUserByEmail(email).ifPresentOrElse(user -> {
                Account account = user.getAccount();
                long accountUpdatedAt = account.getUpdatedAt() != null ?
                    account.getUpdatedAt().atZone(ZoneId.systemDefault()).toInstant().toEpochMilli() : 0L;
                if (!authService.validateResetToken(token, email, accountUpdatedAt)) {
                    throw new RuntimeException("Invalid or expired reset token");
                }
                userService.updatePasswordByEmail(email, req.newPassword());
            }, () -> {
                throw new RuntimeException("Invalid reset token: user not found");
            });

            return ResponseEntity.ok().build();
        } catch (Exception e) {
            logger.error("Reset password failed: {}", e.getMessage());
            ApiErrorResponse error = new ApiErrorResponse("RESET_FAILED", e.getMessage());
            return ResponseEntity.status(400).body(error);
        }
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
