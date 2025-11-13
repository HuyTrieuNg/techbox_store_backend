package vn.techbox.techbox_store.user.exception;

/**
 * Exception thrown when a user account is locked
 */
public class UserAccountLockedException extends UserException {
    public UserAccountLockedException(String message) {
        super(message);
    }
}