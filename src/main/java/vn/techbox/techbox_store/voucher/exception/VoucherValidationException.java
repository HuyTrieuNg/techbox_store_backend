package vn.techbox.techbox_store.voucher.exception;

public class VoucherValidationException extends RuntimeException {

    private final String voucherCode;
    private final String errorType;

    public VoucherValidationException(String message) {
        super(message);
        this.voucherCode = null;
        this.errorType = "VALIDATION_FAILED";
    }

    public VoucherValidationException(String message, String voucherCode) {
        super(message);
        this.voucherCode = voucherCode;
        this.errorType = "VALIDATION_FAILED";
    }

    public VoucherValidationException(String message, String voucherCode, String errorType) {
        super(message);
        this.voucherCode = voucherCode;
        this.errorType = errorType;
    }

    public String getVoucherCode() {
        return voucherCode;
    }

    public String getErrorType() {
        return errorType;
    }
}
