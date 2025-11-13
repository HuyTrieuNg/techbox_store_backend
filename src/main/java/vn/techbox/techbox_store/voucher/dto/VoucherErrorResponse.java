package vn.techbox.techbox_store.voucher.dto;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@NoArgsConstructor
@AllArgsConstructor
@Builder
public class VoucherErrorResponse {

    private String error;
    private String message;
    private String voucherCode;
    private String errorType;
    private Long timestamp;

    public static VoucherErrorResponse of(String message, String voucherCode, String errorType) {
        return VoucherErrorResponse.builder()
                .error("VOUCHER_VALIDATION_FAILED")
                .message(message)
                .voucherCode(voucherCode)
                .errorType(errorType)
                .timestamp(System.currentTimeMillis())
                .build();
    }

    public static VoucherErrorResponse of(String message) {
        return VoucherErrorResponse.builder()
                .error("VOUCHER_VALIDATION_FAILED")
                .message(message)
                .errorType("VALIDATION_FAILED")
                .timestamp(System.currentTimeMillis())
                .build();
    }
}
