package vn.techbox.techbox_store.user.service.impl;

import io.jsonwebtoken.Claims;
import io.jsonwebtoken.Jwts;
import io.jsonwebtoken.security.Keys;
import io.jsonwebtoken.security.SignatureException;
import io.jsonwebtoken.ExpiredJwtException;
import io.jsonwebtoken.MalformedJwtException;
import lombok.Getter;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.stereotype.Service;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import vn.techbox.techbox_store.user.dto.TokenResponse;
import vn.techbox.techbox_store.user.service.AuthService;

import javax.crypto.SecretKey;
import java.util.Date;
import java.util.HashMap;
import java.util.Map;
import java.util.function.Function;
import java.nio.charset.StandardCharsets;
import java.security.MessageDigest;
import java.security.NoSuchAlgorithmException;

@Service
public class AuthServiceImpl implements AuthService {
    private static final Logger logger = LoggerFactory.getLogger(AuthServiceImpl.class);

    // Getter methods for expiry times
    @Getter
    @Value("${jwt.access-token.expiry}")
    private long accessTokenExpiry;

    @Getter
    @Value("${jwt.refresh-token.expiry}")
    private long refreshTokenExpiry;

    @Value("${jwt.secret}")
    private String secretKey;

    @Value("${jwt.refresh-secret}")
    private String refreshSecretKey;

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

    public String generateRefreshToken(String username) {
        Map<String, Object> claims = new HashMap<>();
        claims.put("type", "refresh");

        return Jwts.builder()
                .claims()
                .add(claims)
                .subject(username)
                .issuedAt(new Date(System.currentTimeMillis()))
                .expiration(new Date(System.currentTimeMillis() + refreshTokenExpiry))
                .and()
                .signWith(getRefreshKey())
                .compact();
    }

    private SecretKey getKey() {
        return buildHmacKey(secretKey);
    }

    private SecretKey getRefreshKey() {
        return buildHmacKey(refreshSecretKey);
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

    public String extractUserNameFromRefreshToken(String token) {
        try {
            return extractClaimFromRefreshToken(token, Claims::getSubject);
        } catch (SignatureException e) {
            logger.error("Invalid refresh token signature while extracting username: {}", e.getMessage());
            throw e;
        } catch (ExpiredJwtException e) {
            logger.error("Refresh token expired while extracting username: {}", e.getMessage());
            throw e;
        } catch (MalformedJwtException e) {
            logger.error("Malformed refresh token while extracting username: {}", e.getMessage());
            throw e;
        } catch (Exception e) {
            logger.error("Error extracting username from refresh token: {}", e.getMessage());
            throw new RuntimeException("Error processing refresh token", e);
        }
    }

    private <T> T extractClaimFromRefreshToken(String token, Function<Claims, T> claimResolver) {
        final Claims claims = extractAllClaimsFromRefreshToken(token);
        return claimResolver.apply(claims);
    }

    private Claims extractAllClaimsFromRefreshToken(String token) {
        try {
            return Jwts.parser()
                    .verifyWith(getRefreshKey())
                    .build()
                    .parseSignedClaims(token)
                    .getPayload();
        } catch (SignatureException e) {
            logger.error("Invalid refresh token signature: {}", e.getMessage());
            throw e;
        } catch (ExpiredJwtException e) {
            logger.error("Refresh token expired: {}", e.getMessage());
            throw e;
        } catch (MalformedJwtException e) {
            logger.error("Malformed refresh token: {}", e.getMessage());
            throw e;
        } catch (Exception e) {
            logger.error("Error parsing refresh token: {}", e.getMessage());
            throw new RuntimeException("Error parsing refresh token", e);
        }
    }

    public boolean validateRefreshToken(String token) {
        try {
            final String userName = extractUserNameFromRefreshToken(token);
            return userName != null && !isRefreshTokenExpired(token);
        } catch (SignatureException | ExpiredJwtException | MalformedJwtException e) {
            logger.error("Refresh token validation failed: {}", e.getMessage());
            return false;
        } catch (Exception e) {
            logger.error("Unexpected error during refresh token validation: {}", e.getMessage());
            return false;
        }
    }

    private boolean isRefreshTokenExpired(String token) {
        try {
            return extractRefreshTokenExpiration(token).before(new Date());
        } catch (Exception e) {
            logger.error("Error checking refresh token expiration: {}", e.getMessage());
            return true;
        }
    }

    private Date extractRefreshTokenExpiration(String token) {
        return extractClaimFromRefreshToken(token, Claims::getExpiration);
    }


    public TokenResponse refreshToken(String refreshToken) {
        try {
            if (validateRefreshToken(refreshToken)) {
                String username = extractUserNameFromRefreshToken(refreshToken);
                String newAccessToken = generateToken(username);
                String newRefreshToken = generateRefreshToken(username);
                return new TokenResponse(newAccessToken, newRefreshToken, getAccessTokenExpiry());
            }
            throw new RuntimeException("Invalid refresh token");
        } catch (Exception e) {
            System.out.println("Refresh token validation failed: " + e.getMessage());
            throw new RuntimeException("Invalid refresh token: " + e.getMessage());
        }
    }

    @Value("${jwt.password-reset-expiry:1800000}")
    private long passwordResetTokenExpiry;

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
}

