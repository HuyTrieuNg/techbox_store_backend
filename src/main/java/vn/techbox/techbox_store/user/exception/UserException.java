package vn.techbox.techbox_store.user.exception;

/**
 * Base exception class for user-related exceptions
 */
public class UserException extends RuntimeException {
    public UserException(String message) {
        super(message);
    }

    public UserException(String message, Throwable cause) {
        super(message, cause);
    }
}