package vn.techbox.techbox_store.user.dto;

public record TokenResponse(
        String accessToken,
        String refreshToken,
        String tokenType,
        long expiresIn
) {
    public TokenResponse(String accessToken, String refreshToken, long accessTokenExpiryMs) {
        this(accessToken, refreshToken, "Bearer", accessTokenExpiryMs / 1000); // Convert to seconds
    }
}
