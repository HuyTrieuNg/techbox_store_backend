package vn.techbox.techbox_store.voucher.dto;

import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.NotNull;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.math.BigDecimal;

@Data
@NoArgsConstructor
@AllArgsConstructor
@Builder
public class VoucherValidationRequest {
    
    @NotBlank(message = "Voucher code is required")
    private String code;
    
    @NotNull(message = "User ID is required")
    private Integer userId;
    
    @NotNull(message = "Order amount is required")
    private BigDecimal orderAmount;
}