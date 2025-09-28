package vn.techbox.techbox_store.voucher.dto;

import jakarta.validation.constraints.*;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;
import vn.techbox.techbox_store.voucher.model.VoucherType;

import java.math.BigDecimal;
import java.time.LocalDateTime;

@Data
@NoArgsConstructor
@AllArgsConstructor
@Builder
public class VoucherUpdateRequest {
    
    @Size(min = 3, max = 50, message = "Voucher code must be between 3 and 50 characters")
    @Pattern(regexp = "^[A-Z0-9_-]+$", message = "Voucher code can only contain uppercase letters, numbers, underscores and hyphens")
    private String code;
    
    private VoucherType voucherType;
    
    @DecimalMin(value = "0.01", message = "Value must be greater than 0")
    @DecimalMax(value = "999999.99", message = "Value must be less than 1,000,000")
    private BigDecimal value;
    
    @DecimalMin(value = "0.00", message = "Minimum order amount must be non-negative")
    private BigDecimal minOrderAmount;
    
    @Min(value = 1, message = "Usage limit must be at least 1")
    @Max(value = 1000000, message = "Usage limit must be less than 1,000,001")
    private Integer usageLimit;
    
    private LocalDateTime validFrom;
    
    private LocalDateTime validUntil;
    
    @AssertTrue(message = "Valid until must be after valid from")
    private boolean isValidUntilAfterValidFrom() {
        if (validFrom == null || validUntil == null) {
            return true; // Allow partial updates
        }
        return validUntil.isAfter(validFrom);
    }
    
    @AssertTrue(message = "For percentage vouchers, value must be between 1 and 100")
    private boolean isPercentageValueValid() {
        if (voucherType == null || value == null) {
            return true; // Allow partial updates
        }
        if (voucherType == VoucherType.PERCENTAGE) {
            return value.compareTo(BigDecimal.ONE) >= 0 && value.compareTo(BigDecimal.valueOf(100)) <= 0;
        }
        return true;
    }
}