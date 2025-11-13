package vn.techbox.techbox_store.user.exception;

/**
 * Exception thrown when user provides invalid credentials
 */
public class UserInvalidCredentialsException extends UserException {
    public UserInvalidCredentialsException(String message) {
        super(message);
    }
}