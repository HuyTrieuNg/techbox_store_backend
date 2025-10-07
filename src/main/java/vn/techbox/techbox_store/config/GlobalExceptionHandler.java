package vn.techbox.techbox_store.config;

import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.security.authorization.AuthorizationDeniedException;
import org.springframework.security.access.AccessDeniedException;
import org.springframework.security.core.AuthenticationException;
import org.springframework.web.bind.annotation.ExceptionHandler;
import org.springframework.web.bind.annotation.RestControllerAdvice;
import org.springframework.web.context.request.WebRequest;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.time.LocalDateTime;
import java.time.format.DateTimeFormatter;
import java.util.HashMap;
import java.util.Map;
import vn.techbox.techbox_store.user.dto.ApiErrorResponse;

@RestControllerAdvice
public class GlobalExceptionHandler {

    private static final Logger logger = LoggerFactory.getLogger(GlobalExceptionHandler.class);

    @ExceptionHandler(AuthorizationDeniedException.class)
    public ResponseEntity<Map<String, Object>> handleAuthorizationDenied(
            AuthorizationDeniedException ex, WebRequest request) {
        logger.warn("Authorization denied: {}", ex.getMessage());

        Map<String, Object> errorResponse = new HashMap<>();
        errorResponse.put("error", "AUTHORIZATION_DENIED");
        errorResponse.put("message", "Access denied. You don't have permission to access this resource.");
        errorResponse.put("timestamp", LocalDateTime.now().format(DateTimeFormatter.ISO_LOCAL_DATE_TIME));
        errorResponse.put("path", request.getDescription(false).replace("uri=", ""));

        return ResponseEntity.status(HttpStatus.FORBIDDEN).body(errorResponse);
    }

    @ExceptionHandler(AccessDeniedException.class)
    public ResponseEntity<Map<String, Object>> handleAccessDenied(
            AccessDeniedException ex, WebRequest request) {
        logger.warn("Access denied: {}", ex.getMessage());

        Map<String, Object> errorResponse = new HashMap<>();
        errorResponse.put("error", "ACCESS_DENIED");
        errorResponse.put("message", "Access denied. You don't have permission to access this resource.");
        errorResponse.put("timestamp", LocalDateTime.now().format(DateTimeFormatter.ISO_LOCAL_DATE_TIME));
        errorResponse.put("path", request.getDescription(false).replace("uri=", ""));

        return ResponseEntity.status(HttpStatus.FORBIDDEN).body(errorResponse);
    }

    @ExceptionHandler(AuthenticationException.class)
    public ResponseEntity<Map<String, Object>> handleAuthentication(
            AuthenticationException ex, WebRequest request) {
        logger.warn("Authentication failed: {}", ex.getMessage());

        Map<String, Object> errorResponse = new HashMap<>();
        errorResponse.put("error", "AUTHENTICATION_FAILED");
        errorResponse.put("message", "Authentication failed. Please check your credentials.");
        errorResponse.put("timestamp", LocalDateTime.now().format(DateTimeFormatter.ISO_LOCAL_DATE_TIME));
        errorResponse.put("path", request.getDescription(false).replace("uri=", ""));

        return ResponseEntity.status(HttpStatus.UNAUTHORIZED).body(errorResponse);
    }

    @ExceptionHandler(IllegalArgumentException.class)
    public ResponseEntity<ApiErrorResponse> handleIllegalArgumentException(IllegalArgumentException ex) {
        logger.warn("Validation error: {}", ex.getMessage());
        ApiErrorResponse error = new ApiErrorResponse(
            "VALIDATION_ERROR",
            ex.getMessage(),
            false
        );
        return ResponseEntity.status(HttpStatus.BAD_REQUEST).body(error);
    }

    @ExceptionHandler(RuntimeException.class)
    public ResponseEntity<Map<String, Object>> handleRuntimeException(
            RuntimeException ex, WebRequest request) {
        logger.error("Runtime exception: {}", ex.getMessage());

        if (ex.getMessage() != null && ex.getMessage().contains("Invalid refresh token")) {
            Map<String, Object> errorResponse = new HashMap<>();
            errorResponse.put("error", "INVALID_REFRESH_TOKEN");
            errorResponse.put("message", "Invalid or expired refresh token. Please login again.");
            errorResponse.put("timestamp", LocalDateTime.now().format(DateTimeFormatter.ISO_LOCAL_DATE_TIME));
            errorResponse.put("path", request.getDescription(false).replace("uri=", ""));
            return ResponseEntity.status(HttpStatus.UNAUTHORIZED).body(errorResponse);
        }

        Map<String, Object> errorResponse = new HashMap<>();
        errorResponse.put("error", "RUNTIME_ERROR");
        errorResponse.put("message", ex.getMessage());
        errorResponse.put("timestamp", LocalDateTime.now().format(DateTimeFormatter.ISO_LOCAL_DATE_TIME));
        errorResponse.put("path", request.getDescription(false).replace("uri=", ""));

        return ResponseEntity.status(HttpStatus.BAD_REQUEST).body(errorResponse);
    }

    @ExceptionHandler(Exception.class)
    public ResponseEntity<Map<String, Object>> handleGenericException(
            Exception ex, WebRequest request) {
        logger.error("Unexpected error: {}", ex.getMessage(), ex);

        Map<String, Object> errorResponse = new HashMap<>();
        errorResponse.put("error", "INTERNAL_SERVER_ERROR");
        errorResponse.put("message", "An unexpected error occurred. Please try again later.");
        errorResponse.put("timestamp", LocalDateTime.now().format(DateTimeFormatter.ISO_LOCAL_DATE_TIME));
        errorResponse.put("path", request.getDescription(false).replace("uri=", ""));

        return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR).body(errorResponse);
    }
}
