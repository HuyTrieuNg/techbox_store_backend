package vn.techbox.techbox_store.reports.service;

import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import vn.techbox.techbox_store.order.model.OrderStatus;
import vn.techbox.techbox_store.reports.dto.OrderByStatusDTO;
import vn.techbox.techbox_store.reports.dto.OrderStatsDTO;
import vn.techbox.techbox_store.reports.dto.RevenueTrendDTO;
import vn.techbox.techbox_store.reports.repository.OrderStatsRepository;

import java.math.BigDecimal;
import java.time.LocalDateTime;
import java.util.List;

@Service
@RequiredArgsConstructor
@Slf4j
@Transactional(readOnly = true)
public class OrderStatsServiceImpl implements OrderStatsService {

    private final OrderStatsRepository orderStatsRepository;

    @Override
    public OrderStatsDTO getOrderOverview() {
        log.info("Fetching order overview statistics");
        
        Long totalOrders = orderStatsRepository.countTotalOrders();
        Long pendingOrders = orderStatsRepository.countOrdersByStatus(OrderStatus.PENDING);
        Long processingOrders = orderStatsRepository.countOrdersByStatus(OrderStatus.PROCESSING);
        Long shippedOrders = orderStatsRepository.countOrdersByStatus(OrderStatus.SHIPPING);
        Long deliveredOrders = orderStatsRepository.countOrdersByStatus(OrderStatus.DELIVERED);
        Long cancelledOrders = orderStatsRepository.countOrdersByStatus(OrderStatus.CANCELLED);
        
        BigDecimal totalRevenue = orderStatsRepository.calculateTotalRevenue();
        BigDecimal averageOrderValue = orderStatsRepository.calculateAverageOrderValue();
        
        List<OrderByStatusDTO> ordersByStatus = orderStatsRepository.findOrdersByStatus();
        
        // Get revenue trends for the last 6 months
        LocalDateTime now = LocalDateTime.now();
        LocalDateTime sixMonthsAgo = now.minusMonths(6);
        List<RevenueTrendDTO> revenueTrends = orderStatsRepository.findRevenueTrendsByMonth(sixMonthsAgo, now);
        
        return OrderStatsDTO.builder()
                .totalOrders(totalOrders)
                .pendingOrders(pendingOrders)
                .processingOrders(processingOrders)
                .shippedOrders(shippedOrders)
                .deliveredOrders(deliveredOrders)
                .cancelledOrders(cancelledOrders)
                .totalRevenue(totalRevenue)
                .averageOrderValue(averageOrderValue)
                .ordersByStatus(ordersByStatus)
                .revenueTrends(revenueTrends)
                .build();
    }

    @Override
    public BigDecimal getRevenue(LocalDateTime startDate, LocalDateTime endDate) {
        log.info("Fetching revenue from {} to {}", startDate, endDate);
        return orderStatsRepository.calculateRevenueByDateRange(startDate, endDate);
    }

    @Override
    public List<RevenueTrendDTO> getRevenueTrends(LocalDateTime startDate, LocalDateTime endDate, String groupBy) {
        log.info("Fetching revenue trends from {} to {} grouped by {}", startDate, endDate, groupBy);
        
        return switch (groupBy.toLowerCase()) {
            case "day" -> orderStatsRepository.findRevenueTrendsByDay(startDate, endDate);
            case "week" -> orderStatsRepository.findRevenueTrendsByWeek(startDate, endDate);
            case "month" -> orderStatsRepository.findRevenueTrendsByMonth(startDate, endDate);
            default -> {
                log.warn("Invalid groupBy parameter: {}. Defaulting to 'month'", groupBy);
                yield orderStatsRepository.findRevenueTrendsByMonth(startDate, endDate);
            }
        };
    }
}
