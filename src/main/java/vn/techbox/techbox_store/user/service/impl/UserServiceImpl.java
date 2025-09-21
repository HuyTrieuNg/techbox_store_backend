package vn.techbox.techbox_store.user.service.impl;

import org.springframework.security.authentication.AuthenticationManager;
import org.springframework.security.authentication.UsernamePasswordAuthenticationToken;
import org.springframework.security.core.Authentication;
import org.springframework.security.crypto.bcrypt.BCryptPasswordEncoder;
import org.springframework.stereotype.Service;
import vn.techbox.techbox_store.user.dto.UserCreateRequest;
import vn.techbox.techbox_store.user.dto.UserLoginRequest;
import vn.techbox.techbox_store.user.dto.TokenResponse;
import vn.techbox.techbox_store.user.model.User;
import vn.techbox.techbox_store.user.repository.UserRepository;
import vn.techbox.techbox_store.user.service.AuthService;
import vn.techbox.techbox_store.user.service.UserService;

import java.util.List;
import java.util.Optional;

@Service
public class UserServiceImpl implements UserService {

    private final UserRepository userRepository;
    private final BCryptPasswordEncoder encoder = new BCryptPasswordEncoder(12);
    private final AuthenticationManager authManager;
    private final AuthService authService;

    public UserServiceImpl(UserRepository userRepository, AuthenticationManager authManager, AuthService authService) {
        this.userRepository = userRepository;
        this.authManager = authManager;
        this.authService = authService;
    }

    public User createUser(UserCreateRequest req) {
        User user = User.builder()
                .username(req.username())
                .email(req.email())
                .password(encoder.encode(req.password()))
                .role(req.role())
                .build();
        return userRepository.save(user);
    }

    public List<User> getAllUsers() {
        return userRepository.findAll();
    }

    public Optional<User> getUserById(Integer id) {
        return userRepository.findById(id);
    }

    public User updateUser(Integer id, User userDetails) {
        User user = userRepository.findById(id)
                .orElseThrow(() -> new RuntimeException("User not found with id: " + id));

        user.setUsername(userDetails.getUsername());
        user.setEmail(userDetails.getEmail());
        user.setRole(userDetails.getRole());
        return userRepository.save(user);
    }

    public void deleteUser(Integer id) {
        userRepository.deleteById(id);
    }

    public TokenResponse verify(UserLoginRequest req) {
        try {
            Authentication authentication =
                    authManager.authenticate(new UsernamePasswordAuthenticationToken(
                            req.username(), req.password()
                    ));
            if (authentication.isAuthenticated()) {
                String accessToken = authService.generateToken(req.username());
                String refreshToken = authService.generateRefreshToken(req.username());
                return new TokenResponse(accessToken, refreshToken, authService.getAccessTokenExpiry());
            }
            throw new RuntimeException("Authentication failed");
        } catch (Exception e) {
            System.out.println("Authentication failed: " + e.getMessage());
            throw new RuntimeException("Authentication failed: " + e.getMessage());
        }
    }

    public TokenResponse refreshToken(String refreshToken) {
        try {
            if (authService.validateRefreshToken(refreshToken)) {
                String username = authService.extractUserNameFromRefreshToken(refreshToken);
                String newAccessToken = authService.generateToken(username);
                String newRefreshToken = authService.generateRefreshToken(username);
                return new TokenResponse(newAccessToken, newRefreshToken, authService.getAccessTokenExpiry());
            }
            throw new RuntimeException("Invalid refresh token");
        } catch (Exception e) {
            System.out.println("Refresh token validation failed: " + e.getMessage());
            throw new RuntimeException("Invalid refresh token: " + e.getMessage());
        }
    }
}
