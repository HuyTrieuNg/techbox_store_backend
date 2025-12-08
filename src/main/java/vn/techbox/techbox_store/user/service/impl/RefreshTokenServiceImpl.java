package vn.techbox.techbox_store.user.service.impl;

import lombok.RequiredArgsConstructor;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import vn.techbox.techbox_store.user.model.RefreshToken;
import vn.techbox.techbox_store.user.repository.RefreshTokenRepository;
import vn.techbox.techbox_store.user.service.RefreshTokenService;

import java.nio.charset.StandardCharsets;
import java.security.MessageDigest;
import java.security.NoSuchAlgorithmException;
import java.security.SecureRandom;
import java.time.LocalDateTime;
import java.util.Base64;

@Service
@RequiredArgsConstructor
public class RefreshTokenServiceImpl implements RefreshTokenService {

    private static final Logger logger = LoggerFactory.getLogger(RefreshTokenServiceImpl.class);
    private final RefreshTokenRepository refreshTokenRepository;
    private final SecureRandom secureRandom = new SecureRandom();

    @Value("${jwt.refresh-token.expiry}")
    private long refreshTokenExpiry; // milliseconds

    @Override
    @Transactional
    public RefreshToken createRefreshToken(Integer userId) {
        // Generate random token
        String rawToken = generateSecureToken();
        String tokenHash = hashToken(rawToken);

        LocalDateTime expiresAt = LocalDateTime.now()
            .plusSeconds(refreshTokenExpiry / 1000);

        RefreshToken refreshToken = RefreshToken.builder()
            .userId(userId)
            .tokenHash(tokenHash)
            .expiresAt(expiresAt)
            .revoked(false)
            .build();

        RefreshToken savedToken = refreshTokenRepository.save(refreshToken);

        RefreshToken resultToken = RefreshToken.builder()
            .id(savedToken.getId())
            .userId(savedToken.getUserId())
            .tokenHash(rawToken)
            .expiresAt(savedToken.getExpiresAt())
            .createdAt(savedToken.getCreatedAt())
            .revoked(savedToken.getRevoked())
            .build();

        logger.info("Created new refresh token for user: {}", userId);
        return resultToken;
    }

    @Override
    public RefreshToken validateRefreshToken(String token) {
        if (token == null || token.trim().isEmpty()) {
            throw new RuntimeException("Refresh token is required");
        }

        String tokenHash = hashToken(token);
        RefreshToken refreshToken = refreshTokenRepository.findByTokenHashAndRevokedFalse(tokenHash)
            .orElseThrow(() -> new RuntimeException("Invalid or revoked refresh token"));

        if (refreshToken.getExpiresAt().isBefore(LocalDateTime.now())) {
            throw new RuntimeException("Refresh token has expired");
        }

        return refreshToken;
    }

    @Override
    @Transactional
    public RefreshToken rotateRefreshToken(String oldToken) {
        // Validate old token
        RefreshToken oldRefreshToken = validateRefreshToken(oldToken);

        // Revoke old token
        revokeTokenByHash(oldToken);

        // Create new token for the same user with new expiry time
        RefreshToken newRefreshToken = createRefreshToken(oldRefreshToken.getUserId());

        logger.info("Rotated refresh token for user: {}", oldRefreshToken.getUserId());
        return newRefreshToken;
    }

    @Override
    @Transactional
    public void revokeTokenByHash(String rawToken) {
        String hashedToken = hashToken(rawToken);
        refreshTokenRepository.revokeTokenByHash(hashedToken);
        logger.info("Revoked refresh token with hash");
    }

    @Override
    @Transactional
    public void revokeAllUserTokens(Integer userId) {
        refreshTokenRepository.revokeAllTokensByUserId(userId);
        logger.info("Revoked all refresh tokens for user: {}", userId);
    }

    @Override
    @Transactional
    public void cleanupExpiredTokens() {
        LocalDateTime now = LocalDateTime.now();
        refreshTokenRepository.deleteExpiredAndRevokedTokens(now);
        logger.info("Cleaned up expired and revoked refresh tokens");
    }

    private String generateSecureToken() {
        byte[] randomBytes = new byte[32];
        secureRandom.nextBytes(randomBytes);
        return Base64.getUrlEncoder().withoutPadding().encodeToString(randomBytes);
    }

    private String hashToken(String token) {
        try {
            MessageDigest digest = MessageDigest.getInstance("SHA-256");
            byte[] hashedBytes = digest.digest(token.getBytes(StandardCharsets.UTF_8));
            return Base64.getEncoder().encodeToString(hashedBytes);
        } catch (NoSuchAlgorithmException e) {
            throw new RuntimeException("SHA-256 algorithm not available", e);
        }
    }
}
