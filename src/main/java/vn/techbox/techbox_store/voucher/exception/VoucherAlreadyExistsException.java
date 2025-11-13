package vn.techbox.techbox_store.voucher.exception;

/**
 * Exception for when a voucher with the same code already exists
 */
public class VoucherAlreadyExistsException extends VoucherException {
    public VoucherAlreadyExistsException(String message) {
        super(message);
    }

    public VoucherAlreadyExistsException(String message, Throwable cause) {
        super(message, cause);
    }
}

