package vn.techbox.techbox_store.voucher.dto;

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
public class VoucherResponse {
    
    private Integer id;
    private String code;
    private VoucherType voucherType;
    private BigDecimal value;
    private BigDecimal minOrderAmount;
    private Integer usageLimit;
    private Integer usedCount;
    private LocalDateTime validFrom;
    private LocalDateTime validUntil;
    private LocalDateTime createdAt;
    private LocalDateTime updatedAt;
    private boolean isActive;
    private boolean isValid;
    private boolean hasUsageLeft;
    
    // Helper fields for UI
    private String displayValue;
    private String displayValidityPeriod;
    private String status;
    
    public static VoucherResponse fromEntity(vn.techbox.techbox_store.voucher.model.Voucher voucher) {
        if (voucher == null) return null;
        
        VoucherResponseBuilder builder = VoucherResponse.builder()
                .id(voucher.getId())
                .code(voucher.getCode())
                .voucherType(voucher.getVoucherType())
                .value(voucher.getValue())
                .minOrderAmount(voucher.getMinOrderAmount())
                .usageLimit(voucher.getUsageLimit())
                .validFrom(voucher.getValidFrom())
                .validUntil(voucher.getValidUntil())
                .createdAt(voucher.getCreatedAt())
                .updatedAt(voucher.getUpdatedAt())
                .isActive(!voucher.isDeleted())
                .isValid(voucher.isValid())
                .hasUsageLeft(voucher.hasUsageLeft());
        
        // Calculate used count
        int usedCount = voucher.getUserVouchers() != null ? voucher.getUserVouchers().size() : 0;
        builder.usedCount(usedCount);
        
        // Set display value
        String displayValue;
        if (voucher.getVoucherType() == VoucherType.FIXED_AMOUNT) {
            displayValue = "$" + voucher.getValue().toPlainString();
        } else {
            displayValue = voucher.getValue().toPlainString() + "%";
        }
        builder.displayValue(displayValue);
        
        // Set validity period
        builder.displayValidityPeriod(voucher.getValidFrom() + " - " + voucher.getValidUntil());
        
        // Set status
        String status;
        if (voucher.isDeleted()) {
            status = "DELETED";
        } else if (!voucher.isValid()) {
            status = "EXPIRED";
        } else if (!voucher.hasUsageLeft()) {
            status = "EXHAUSTED";
        } else {
            status = "ACTIVE";
        }
        builder.status(status);
        
        return builder.build();
    }
}