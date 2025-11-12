package vn.techbox.techbox_store.voucher.exception;

/**
 * Exception for when a voucher is not found
 */
public class VoucherNotFoundException extends VoucherException {
    public VoucherNotFoundException(String message) {
        super(message);
    }

    public VoucherNotFoundException(String message, Throwable cause) {
        super(message, cause);
    }
}

