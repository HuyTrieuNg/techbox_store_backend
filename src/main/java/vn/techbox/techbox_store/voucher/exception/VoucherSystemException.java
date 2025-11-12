package vn.techbox.techbox_store.voucher.exception;

/**
 * Exception for system-level errors (e.g., database errors, unexpected errors)
 */
public class VoucherSystemException extends VoucherException {
    public VoucherSystemException(String message) {
        super(message);
    }

    public VoucherSystemException(String message, Throwable cause) {
        super(message, cause);
    }
}

