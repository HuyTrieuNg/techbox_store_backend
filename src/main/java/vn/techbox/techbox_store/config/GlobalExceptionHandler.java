package vn.techbox.techbox_store.config;

import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.security.authorization.AuthorizationDeniedException;
import org.springframework.security.access.AccessDeniedException;
import org.springframework.security.core.AuthenticationException;
import org.springframework.validation.FieldError;
import org.springframework.web.bind.MethodArgumentNotValidException;
import org.springframework.web.bind.annotation.ExceptionHandler;
import org.springframework.web.bind.annotation.RestControllerAdvice;
import org.springframework.web.context.request.WebRequest;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import vn.techbox.techbox_store.order.exception.OrderException;
import vn.techbox.techbox_store.voucher.exception.VoucherValidationException;
import vn.techbox.techbox_store.voucher.dto.VoucherErrorResponse;

import java.time.LocalDateTime;
import java.time.format.DateTimeFormatter;
import java.util.HashMap;
import java.util.Map;
import vn.techbox.techbox_store.user.dto.ApiErrorResponse;

@RestControllerAdvice
public class GlobalExceptionHandler {

    private static final Logger logger = LoggerFactory.getLogger(GlobalExceptionHandler.class);

    // ===== SECURITY EXCEPTIONS =====
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
    public ResponseEntity<Map<String, Object>> handleAuthenticationException(
            AuthenticationException ex, WebRequest request) {
        logger.warn("Authentication failed: {}", ex.getMessage());

        Map<String, Object> errorResponse = new HashMap<>();
        errorResponse.put("error", "AUTHENTICATION_FAILED");
        errorResponse.put("message", "Authentication failed. Please check your credentials.");
        errorResponse.put("timestamp", LocalDateTime.now().format(DateTimeFormatter.ISO_LOCAL_DATE_TIME));
        errorResponse.put("path", request.getDescription(false).replace("uri=", ""));

        return ResponseEntity.status(HttpStatus.UNAUTHORIZED).body(errorResponse);
    }

    // ===== ORDER EXCEPTIONS =====
    @ExceptionHandler(OrderException.class)
    public ResponseEntity<ApiErrorResponse> handleOrderException(OrderException ex) {
        logger.error("Order exception occurred: {}", ex.getMessage());

        ApiErrorResponse errorResponse = new ApiErrorResponse("Order Error", ex.getMessage());
        return ResponseEntity.badRequest().body(errorResponse);
    }

    // ===== VOUCHER EXCEPTIONS =====
    @ExceptionHandler(VoucherValidationException.class)
    public ResponseEntity<ApiErrorResponse> handleVoucherValidationException(VoucherValidationException ex) {
        logger.warn("Voucher validation exception: {}", ex.getMessage());

        ApiErrorResponse errorResponse = new ApiErrorResponse("Voucher Validation Error", ex.getMessage());
        return ResponseEntity.status(HttpStatus.BAD_REQUEST).body(errorResponse);
    }

    @ExceptionHandler(VoucherNotFoundException.class)
    public ResponseEntity<ApiErrorResponse> handleVoucherNotFoundException(VoucherNotFoundException ex) {
        logger.warn("Voucher not found: {}", ex.getMessage());

        ApiErrorResponse errorResponse = new ApiErrorResponse("Voucher Not Found", ex.getMessage());
        return ResponseEntity.status(HttpStatus.NOT_FOUND).body(errorResponse);
    }

    @ExceptionHandler(VoucherAlreadyExistsException.class)
    public ResponseEntity<ApiErrorResponse> handleVoucherAlreadyExistsException(VoucherAlreadyExistsException ex) {
        logger.warn("Voucher already exists: {}", ex.getMessage());

        ApiErrorResponse errorResponse = new ApiErrorResponse("Voucher Already Exists", ex.getMessage());
        return ResponseEntity.status(HttpStatus.CONFLICT).body(errorResponse);
    }

    @ExceptionHandler(VoucherSystemException.class)
    public ResponseEntity<ApiErrorResponse> handleVoucherSystemException(VoucherSystemException ex) {
        logger.error("Voucher system exception: {}", ex.getMessage(), ex);

        ApiErrorResponse errorResponse = new ApiErrorResponse("Voucher System Error", ex.getMessage());
        return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR).body(errorResponse);
    }

    @ExceptionHandler(VoucherException.class)
    public ResponseEntity<ApiErrorResponse> handleVoucherException(VoucherException ex) {
        logger.error("Voucher exception: {}", ex.getMessage(), ex);

        ApiErrorResponse errorResponse = new ApiErrorResponse("Voucher Error", ex.getMessage());
        return ResponseEntity.status(HttpStatus.BAD_REQUEST).body(errorResponse);
    }

    // ===== VALIDATION EXCEPTIONS =====
    @ExceptionHandler(MethodArgumentNotValidException.class)
    public ResponseEntity<Map<String, Object>> handleValidationExceptions(MethodArgumentNotValidException ex) {
        Map<String, Object> response = new HashMap<>();
        Map<String, String> errors = new HashMap<>();

        ex.getBindingResult().getAllErrors().forEach((error) -> {
            String fieldName = ((FieldError) error).getField();
            String errorMessage = error.getDefaultMessage();
            errors.put(fieldName, errorMessage);
        });

        response.put("timestamp", LocalDateTime.now());
        response.put("status", HttpStatus.BAD_REQUEST.value());
        response.put("error", "Validation Failed");
        response.put("errors", errors);

        logger.warn("Validation failed: {}", errors);
        return ResponseEntity.badRequest().body(response);
    }

    @ExceptionHandler(IllegalArgumentException.class)
    public ResponseEntity<ApiErrorResponse> handleIllegalArgumentException(IllegalArgumentException ex) {
        logger.error("Illegal argument exception: {}", ex.getMessage());

        ApiErrorResponse errorResponse = new ApiErrorResponse("Invalid Argument", ex.getMessage());
        return ResponseEntity.badRequest().body(errorResponse);
    }

    // ===== VOUCHER VALIDATION EXCEPTIONS =====
    @ExceptionHandler(VoucherValidationException.class)
    public ResponseEntity<VoucherErrorResponse> handleVoucherValidationException(
            VoucherValidationException ex, WebRequest request) {
        logger.warn("Voucher validation failed: {} - Code: {} - Type: {}",
                   ex.getMessage(), ex.getVoucherCode(), ex.getErrorType());

        VoucherErrorResponse errorResponse = VoucherErrorResponse.of(
            ex.getMessage(),
            ex.getVoucherCode(),
            ex.getErrorType()
        );

        return ResponseEntity.status(HttpStatus.BAD_REQUEST).body(errorResponse);
    }

    // ===== GENERIC EXCEPTION =====
    @ExceptionHandler(Exception.class)
    public ResponseEntity<ApiErrorResponse> handleGenericException(Exception ex) {
        logger.error("Unexpected error occurred: {}", ex.getMessage(), ex);

        ApiErrorResponse errorResponse = new ApiErrorResponse("Internal Server Error", "An unexpected error occurred");
        return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR).body(errorResponse);
    }
}
