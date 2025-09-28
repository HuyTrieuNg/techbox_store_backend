package vn.techbox.techbox_store.voucher.dto;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.math.BigDecimal;

@Data
@NoArgsConstructor
@AllArgsConstructor
@Builder
public class VoucherValidationResponse {
    
    private boolean valid;
    private String message;
    private VoucherResponse voucher;
    private BigDecimal discountAmount;
    private BigDecimal finalAmount;
    
    public static VoucherValidationResponse invalid(String message) {
        return VoucherValidationResponse.builder()
                .valid(false)
                .message(message)
                .build();
    }
    
    public static VoucherValidationResponse valid(VoucherResponse voucher, BigDecimal discountAmount, BigDecimal finalAmount) {
        return VoucherValidationResponse.builder()
                .valid(true)
                .message("Voucher is valid")
                .voucher(voucher)
                .discountAmount(discountAmount)
                .finalAmount(finalAmount)
                .build();
    }
}