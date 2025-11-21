package vn.techbox.techbox_store.reports.service;

import vn.techbox.techbox_store.reports.dto.OrderStatsDTO;
import vn.techbox.techbox_store.reports.dto.RevenueTrendDTO;

import java.math.BigDecimal;
import java.time.LocalDateTime;
import java.util.List;

public interface OrderStatsService {
    
    /**
     * Get overall order statistics
     */
    OrderStatsDTO getOrderOverview();
    
    /**
     * Get revenue for a specific date range
     * @param startDate Start date of the period
     * @param endDate End date of the period
     */
    BigDecimal getRevenue(LocalDateTime startDate, LocalDateTime endDate);
    
    /**
     * Get revenue trends for a specific period
     * @param startDate Start date of the period
     * @param endDate End date of the period
     * @param groupBy Grouping option: "day", "week", or "month"
     */
    List<RevenueTrendDTO> getRevenueTrends(LocalDateTime startDate, LocalDateTime endDate, String groupBy);
}
