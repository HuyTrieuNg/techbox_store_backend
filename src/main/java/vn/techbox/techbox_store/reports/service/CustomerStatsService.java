package vn.techbox.techbox_store.reports.service;

import vn.techbox.techbox_store.reports.dto.CustomerGrowthDTO;
import vn.techbox.techbox_store.reports.dto.CustomerStatsDTO;
import vn.techbox.techbox_store.reports.dto.TopCustomerDTO;

import java.time.LocalDateTime;
import java.util.List;

public interface CustomerStatsService {
    
    /**
     * Get overall customer statistics
     */
    CustomerStatsDTO getCustomerOverview();
    
    /**
     * Get customer growth trends for a specific period
     * @param startDate Start date of the period
     * @param endDate End date of the period
     * @param groupBy Grouping option: "day", "week", or "month"
     */
    List<CustomerGrowthDTO> getCustomerGrowth(LocalDateTime startDate, LocalDateTime endDate, String groupBy);
    
    /**
     * Get top customers by total spending
     * @param limit Number of top customers to return
     */
    List<TopCustomerDTO> getTopCustomers(int limit);
}
