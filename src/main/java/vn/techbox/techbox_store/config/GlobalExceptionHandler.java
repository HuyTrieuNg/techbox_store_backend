package vn.techbox.techbox_store.config;

import io.jsonwebtoken.ExpiredJwtException;
import io.jsonwebtoken.MalformedJwtException;
import io.jsonwebtoken.security.SignatureException;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.ExceptionHandler;
import org.springframework.web.bind.annotation.RestControllerAdvice;
import vn.techbox.techbox_store.user.dto.ApiErrorResponse;

@RestControllerAdvice
public class GlobalExceptionHandler {

    @ExceptionHandler(ExpiredJwtException.class)
    public ResponseEntity<ApiErrorResponse> handleExpiredJwtException(ExpiredJwtException ex) {
        ApiErrorResponse error = new ApiErrorResponse(
            "TOKEN_EXPIRED",
            "Access token has expired. Please use refresh token to obtain a new access token.",
            true
        );
        return ResponseEntity.status(HttpStatus.UNAUTHORIZED).body(error);
    }

    @ExceptionHandler(SignatureException.class)
    public ResponseEntity<ApiErrorResponse> handleSignatureException(SignatureException ex) {
        ApiErrorResponse error = new ApiErrorResponse(
            "INVALID_SIGNATURE",
            "Invalid JWT signature. Please login again.",
            false
        );
        return ResponseEntity.status(HttpStatus.UNAUTHORIZED).body(error);
    }

    @ExceptionHandler(MalformedJwtException.class)
    public ResponseEntity<ApiErrorResponse> handleMalformedJwtException(MalformedJwtException ex) {
        ApiErrorResponse error = new ApiErrorResponse(
            "MALFORMED_TOKEN",
            "Malformed JWT token. Please login again.",
            false
        );
        return ResponseEntity.status(HttpStatus.UNAUTHORIZED).body(error);
    }

    @ExceptionHandler(RuntimeException.class)
    public ResponseEntity<ApiErrorResponse> handleRuntimeException(RuntimeException ex) {
        if (ex.getMessage().contains("Invalid refresh token")) {
            ApiErrorResponse error = new ApiErrorResponse(
                "INVALID_REFRESH_TOKEN",
                "Invalid or expired refresh token. Please login again.",
                false
            );
            return ResponseEntity.status(HttpStatus.UNAUTHORIZED).body(error);
        }

        ApiErrorResponse error = new ApiErrorResponse(
            "INTERNAL_ERROR",
            "An internal error occurred",
            false
        );
        return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR).body(error);
    }
}
