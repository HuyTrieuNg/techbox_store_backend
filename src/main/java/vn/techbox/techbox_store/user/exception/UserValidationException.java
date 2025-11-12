package vn.techbox.techbox_store.user.exception;

/**
 * Exception thrown when user validation fails during registration or update
 */
public class UserValidationException extends UserException {
    public UserValidationException(String message) {
        super(message);
    }

    public UserValidationException(String message, Throwable cause) {
        super(message, cause);
    }
}
