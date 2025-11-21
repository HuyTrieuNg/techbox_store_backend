package vn.techbox.techbox_store.reports.dto;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.math.BigDecimal;
import java.time.LocalDateTime;

@Data
@Builder
@NoArgsConstructor
@AllArgsConstructor
public class StockMovementDTO {
    private String type; // "IMPORT" or "EXPORT"
    private Integer transactionId;
    private LocalDateTime transactionDate;
    private Integer totalItems;
    private BigDecimal totalValue;
    private String supplierName; // For imports
    private String note;
}
