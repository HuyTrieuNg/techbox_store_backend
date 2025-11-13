package vn.techbox.techbox_store.voucher.exception;

/**
 * Exception for voucher validation errors
 * Used when business rules validation fails (e.g., invalid date range, invalid percentage value)
 */
public class VoucherValidationException extends VoucherException {
    public VoucherValidationException(String message) {
        super(message);
    }

    public VoucherValidationException(String message, Throwable cause) {
        super(message, cause);
    }
}

