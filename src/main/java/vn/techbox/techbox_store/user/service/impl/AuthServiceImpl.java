package vn.techbox.techbox_store.user.service.impl;

import io.jsonwebtoken.Claims;
import io.jsonwebtoken.Jwts;
import io.jsonwebtoken.security.Keys;
import io.jsonwebtoken.security.SignatureException;
import io.jsonwebtoken.ExpiredJwtException;
import io.jsonwebtoken.MalformedJwtException;
import lombok.Getter;
import lombok.RequiredArgsConstructor;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.stereotype.Service;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import vn.techbox.techbox_store.user.dto.TokenResponse;
import vn.techbox.techbox_store.user.model.RefreshToken;
import vn.techbox.techbox_store.user.repository.UserRepository;
import vn.techbox.techbox_store.user.service.AuthService;
import vn.techbox.techbox_store.user.service.RefreshTokenService;
import vn.techbox.techbox_store.user.model.User;

import javax.crypto.SecretKey;
import java.util.Date;
import java.util.HashMap;
import java.util.Map;
import java.util.function.Function;
import java.nio.charset.StandardCharsets;
import java.security.MessageDigest;
import java.security.NoSuchAlgorithmException;

@Service
@RequiredArgsConstructor
public class AuthServiceImpl implements AuthService {
    private static final Logger logger = LoggerFactory.getLogger(AuthServiceImpl.class);

    private final RefreshTokenService refreshTokenService;
    private final UserRepository userRepository;

    @Getter
    @Value("${jwt.access-token.expiry}")
    private long accessTokenExpiry;

    @Getter
    @Value("${jwt.refresh-token.expiry}")
    private long refreshTokenExpiry;

    @Value("${jwt.secret}")
    private String secretKey;

    @Value("${jwt.password-reset-expiry:1800000}")
    private long passwordResetTokenExpiry;

    @Override
    public String generateToken(String username) {
        Map<String, Object> claims = new HashMap<>();
        claims.put("type", "access");

        return Jwts.builder()
                .claims()
                .add(claims)
                .subject(username)
                .issuedAt(new Date(System.currentTimeMillis()))
                .expiration(new Date(System.currentTimeMillis() + accessTokenExpiry))
                .and()
                .signWith(getKey())
                .compact();
    }

    @Override
    public String extractUserName(String token) {
        try {
            return extractClaim(token, Claims::getSubject);
        } catch (SignatureException e) {
            logger.error("Invalid JWT signature while extracting username: {}", e.getMessage());
            throw e;
        } catch (ExpiredJwtException e) {
            logger.error("JWT token expired while extracting username: {}", e.getMessage());
            throw e;
        } catch (MalformedJwtException e) {
            logger.error("Malformed JWT token while extracting username: {}", e.getMessage());
            throw e;
        } catch (Exception e) {
            logger.error("Error extracting username from token: {}", e.getMessage());
            throw new RuntimeException("Error processing JWT token", e);
        }
    }

    @Override
    public boolean validateToken(String token, UserDetails userDetails) {
        try {
            final String userName = extractUserName(token);
            return (userName.equals(userDetails.getUsername()) && !isTokenExpired(token));
        } catch (SignatureException | ExpiredJwtException | MalformedJwtException e) {
            logger.error("Token validation failed: {}", e.getMessage());
            return false;
        } catch (Exception e) {
            logger.error("Unexpected error during token validation: {}", e.getMessage());
            return false;
        }
    }

    @Override
    public TokenResponse generateTokenPair(Integer userId) {
        try {
            User user = userRepository.findById(userId)
                .orElseThrow(() -> new RuntimeException("User not found"));

            String accessToken = generateToken(user.getAccount().getEmail());
            RefreshToken refreshToken = refreshTokenService.createRefreshToken(userId);

            return new TokenResponse(accessToken, refreshToken.getTokenHash(), getAccessTokenExpiry());
        } catch (Exception e) {
            logger.error("Error generating token pair for user {}: {}", userId, e.getMessage());
            throw new RuntimeException("Error generating tokens", e);
        }
    }

    @Override
    public TokenResponse refreshToken(String refreshToken) {
        try {
            // Rotate the refresh token (validate old one and create new one)
            RefreshToken newRefreshToken = refreshTokenService.rotateRefreshToken(refreshToken);

            // Get user info to generate new access token
            User user = userRepository.findById(newRefreshToken.getUserId())
                .orElseThrow(() -> new RuntimeException("User not found"));

            String newAccessToken = generateToken(user.getAccount().getEmail());

            return new TokenResponse(newAccessToken, newRefreshToken.getTokenHash(), getAccessTokenExpiry());
        } catch (Exception e) {
            logger.error("Token refresh failed: {}", e.getMessage());
            throw new RuntimeException("Invalid refresh token: " + e.getMessage());
        }
    }

    @Override
    public void logout(String refreshToken) {
        try {
            refreshTokenService.revokeTokenByHash(refreshToken);
            logger.info("User logged out successfully");
        } catch (Exception e) {
            logger.error("Logout failed: {}", e.getMessage());
            throw new RuntimeException("Logout failed", e);
        }
    }

    @Override
    public void logoutAll(Integer userId) {
        try {
            refreshTokenService.revokeAllUserTokens(userId);
            logger.info("All sessions logged out for user: {}", userId);
        } catch (Exception e) {
            logger.error("Logout all failed for user {}: {}", userId, e.getMessage());
            throw new RuntimeException("Logout all failed", e);
        }
    }

    // ============================================
    // PASSWORD RESET TOKEN METHODS
    // ============================================

    @Override
    public String generatePasswordResetToken(String email, long accountUpdatedAt) {
        Map<String, Object> claims = new HashMap<>();
        claims.put("type", "password-reset");
        claims.put("accountUpdatedAt", accountUpdatedAt);

        return Jwts.builder()
                .claims()
                .add(claims)
                .subject(email)
                .issuedAt(new Date(System.currentTimeMillis()))
                .expiration(new Date(System.currentTimeMillis() + passwordResetTokenExpiry))
                .and()
                .signWith(getKey())
                .compact();
    }

    @Override
    public String extractUserNameFromResetToken(String token) {
        try {
            return extractClaim(token, Claims::getSubject);
        } catch (SignatureException e) {
            logger.error("Invalid JWT signature while extracting email from reset token: {}", e.getMessage());
            throw e;
        } catch (ExpiredJwtException e) {
            logger.error("Reset token expired: {}", e.getMessage());
            throw e;
        } catch (MalformedJwtException e) {
            logger.error("Malformed reset token: {}", e.getMessage());
            throw e;
        } catch (Exception e) {
            logger.error("Error extracting email from reset token: {}", e.getMessage());
            throw new RuntimeException("Error processing reset token", e);
        }
    }

    @Override
    public long extractAccountUpdatedAtFromResetToken(String token) {
        try {
            Claims claims = extractAllClaims(token);
            Object updatedAt = claims.get("accountUpdatedAt");
            if (updatedAt instanceof Number) {
                return ((Number) updatedAt).longValue();
            }
            return 0L;
        } catch (Exception e) {
            logger.error("Error extracting accountUpdatedAt from reset token: {}", e.getMessage());
            throw new RuntimeException("Error processing reset token", e);
        }
    }

    @Override
    public boolean validateResetToken(String token, String email, long accountUpdatedAt) {
        try {
            final String tokenEmail = extractUserNameFromResetToken(token);
            final long tokenUpdatedAt = extractAccountUpdatedAtFromResetToken(token);
            return (tokenEmail.equals(email) && 
                    tokenUpdatedAt == accountUpdatedAt && 
                    !isTokenExpired(token));
        } catch (SignatureException | ExpiredJwtException | MalformedJwtException e) {
            logger.error("Reset token validation failed: {}", e.getMessage());
            return false;
        } catch (Exception e) {
            logger.error("Unexpected error during reset token validation: {}", e.getMessage());
            return false;
        }
    }

    private SecretKey getKey() {
        return buildHmacKey(secretKey);
    }

    private SecretKey buildHmacKey(String secret) {
        byte[] keyBytes = secret.getBytes(StandardCharsets.UTF_8);

        if (keyBytes.length < 32) {
            keyBytes = sha256(keyBytes);
        }

        return Keys.hmacShaKeyFor(keyBytes);
    }

    private byte[] sha256(byte[] input) {
        try {
            MessageDigest digest = MessageDigest.getInstance("SHA-256");
            return digest.digest(input);
        } catch (NoSuchAlgorithmException e) {
            throw new IllegalStateException("SHA-256 algorithm not available", e);
        }
    }

    private <T> T extractClaim(String token, Function<Claims, T> claimResolver) {
        final Claims claims = extractAllClaims(token);
        return claimResolver.apply(claims);
    }

    private Claims extractAllClaims(String token) {
        try {
            return Jwts.parser()
                    .verifyWith(getKey())
                    .build()
                    .parseSignedClaims(token)
                    .getPayload();
        } catch (SignatureException e) {
            logger.error("Invalid JWT signature: {}", e.getMessage());
            throw e;
        } catch (ExpiredJwtException e) {
            logger.error("JWT token expired: {}", e.getMessage());
            throw e;
        } catch (MalformedJwtException e) {
            logger.error("Malformed JWT token: {}", e.getMessage());
            throw e;
        } catch (Exception e) {
            logger.error("Error parsing JWT token: {}", e.getMessage());
            throw new RuntimeException("Error parsing JWT token", e);
        }
    }

    private boolean isTokenExpired(String token) {
        try {
            return extractExpiration(token).before(new Date());
        } catch (Exception e) {
            logger.error("Error checking token expiration: {}", e.getMessage());
            return true;
        }
    }

    private Date extractExpiration(String token) {
        return extractClaim(token, Claims::getExpiration);
    }
}
