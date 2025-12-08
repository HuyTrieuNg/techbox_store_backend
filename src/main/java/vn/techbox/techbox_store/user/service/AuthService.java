package vn.techbox.techbox_store.user.service;

import org.springframework.security.core.userdetails.UserDetails;
import vn.techbox.techbox_store.user.dto.TokenResponse;

public interface AuthService {
    String generateToken(String username);
    TokenResponse generateTokenPair(Integer userId);
    String extractUserName(String token);
    boolean validateToken(String token, UserDetails userDetails);
    long getAccessTokenExpiry();
    long getRefreshTokenExpiry();
    TokenResponse refreshToken(String refreshToken);
    void logout(String refreshToken);
    void logoutAll(Integer userId);
    String generatePasswordResetToken(String email, long accountUpdatedAt);
    String extractUserNameFromResetToken(String token);
    long extractAccountUpdatedAtFromResetToken(String token);
    boolean validateResetToken(String token, String email, long accountUpdatedAt);
}
