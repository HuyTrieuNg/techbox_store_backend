package vn.techbox.techbox_store.user.service;

import vn.techbox.techbox_store.user.model.RefreshToken;

public interface RefreshTokenService {
    RefreshToken createRefreshToken(Integer userId);
    RefreshToken validateRefreshToken(String token);
    RefreshToken rotateRefreshToken(String oldToken);
    void revokeTokenByHash(String tokenHash);
    void revokeAllUserTokens(Integer userId);
    void cleanupExpiredTokens();
}
