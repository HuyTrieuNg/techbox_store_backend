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
import vn.techbox.techbox_store.user.service.AuthService;

import javax.crypto.KeyGenerator;
import javax.crypto.SecretKey;
import java.security.NoSuchAlgorithmException;
import java.util.Base64;
import java.util.Date;
import java.util.HashMap;
import java.util.Map;
import java.util.function.Function;

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

    private String secretKey = "";
    private String refreshSecretKey = "";

    public AuthServiceImpl() {
        try {
            KeyGenerator keyGen = KeyGenerator.getInstance("HmacSHA256");
            SecretKey sk = keyGen.generateKey();
            secretKey = Base64.getUrlEncoder().withoutPadding().encodeToString(sk.getEncoded());

            // Generate separate key for refresh tokens
            SecretKey refreshSk = keyGen.generateKey();
            refreshSecretKey = Base64.getUrlEncoder().withoutPadding().encodeToString(refreshSk.getEncoded());

            System.out.println("Base64 Secret Key for jwt.io: " + secretKey);
            System.out.println("Base64 Refresh Secret Key for jwt.io: " + refreshSecretKey);
        } catch (NoSuchAlgorithmException e) {
            throw new RuntimeException(e);
        }
    }

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
        byte[] keyBytes = Base64.getUrlDecoder().decode(secretKey);
        return Keys.hmacShaKeyFor(keyBytes);
    }

    private SecretKey getRefreshKey() {
        byte[] keyBytes = Base64.getUrlDecoder().decode(refreshSecretKey);
        return Keys.hmacShaKeyFor(keyBytes);
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

}
