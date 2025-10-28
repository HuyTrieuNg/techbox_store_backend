package vn.techbox.techbox_store.user.service;

import org.springframework.security.core.userdetails.UserDetails;
import vn.techbox.techbox_store.user.dto.TokenResponse;

public interface AuthService {
    String generateToken(String username);
    String generateRefreshToken(String username);
    String extractUserName(String token);
    boolean validateToken(String token, UserDetails userDetails);
    String extractUserNameFromRefreshToken(String token);
    boolean validateRefreshToken(String token);
    long getAccessTokenExpiry();
    long getRefreshTokenExpiry();
    TokenResponse refreshToken(String refreshToken);
}
