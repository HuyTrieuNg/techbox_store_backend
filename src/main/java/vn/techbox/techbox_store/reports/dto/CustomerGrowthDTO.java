package vn.techbox.techbox_store.reports.dto;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@Builder
@NoArgsConstructor
@AllArgsConstructor
public class CustomerGrowthDTO {
    private String period; // e.g., "2024-01", "2024-W01"
    private Long newCustomers;
}
