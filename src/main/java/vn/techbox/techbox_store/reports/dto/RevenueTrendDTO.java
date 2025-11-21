package vn.techbox.techbox_store.reports.dto;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.math.BigDecimal;

@Data
@Builder
@NoArgsConstructor
@AllArgsConstructor
public class RevenueTrendDTO {
    private String period; // e.g., "2024-01-15", "2024-W03", "2024-01"
    private Long orderCount;
    private BigDecimal revenue;
}
