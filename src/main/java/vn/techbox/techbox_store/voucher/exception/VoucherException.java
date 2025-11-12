package vn.techbox.techbox_store.voucher.exception;

/**
 * Base exception class for voucher-related exceptions
 */
public class VoucherException extends RuntimeException {
    public VoucherException(String message) {
        super(message);
    }

    public VoucherException(String message, Throwable cause) {
        super(message, cause);
    }
}

