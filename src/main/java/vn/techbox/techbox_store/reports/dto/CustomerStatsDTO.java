package vn.techbox.techbox_store.reports.dto;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.util.List;

@Data
@Builder
@NoArgsConstructor
@AllArgsConstructor
public class CustomerStatsDTO {
    private Long totalCustomers;
    private Long newCustomersToday;
    private Long newCustomersThisWeek;
    private Long newCustomersThisMonth;
    private Double growthRate; // Percentage growth compared to previous period
    private List<TopCustomerDTO> topCustomers;
    private List<CustomerGrowthDTO> growthTrends;
}
