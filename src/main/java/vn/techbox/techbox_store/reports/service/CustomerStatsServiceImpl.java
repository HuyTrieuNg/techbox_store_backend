package vn.techbox.techbox_store.reports.service;

import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import vn.techbox.techbox_store.reports.dto.CustomerGrowthDTO;
import vn.techbox.techbox_store.reports.dto.CustomerStatsDTO;
import vn.techbox.techbox_store.reports.dto.TopCustomerDTO;
import vn.techbox.techbox_store.reports.repository.CustomerStatsRepository;

import java.time.LocalDateTime;
import java.time.LocalTime;
import java.time.temporal.ChronoUnit;
import java.util.List;

@Service
@RequiredArgsConstructor
@Slf4j
@Transactional(readOnly = true)
public class CustomerStatsServiceImpl implements CustomerStatsService {

    private final CustomerStatsRepository customerStatsRepository;

    @Override
    public CustomerStatsDTO getCustomerOverview() {
        log.info("Fetching customer overview statistics");
        
        LocalDateTime now = LocalDateTime.now();
        LocalDateTime startOfToday = now.toLocalDate().atStartOfDay();
        LocalDateTime startOfWeek = now.minusWeeks(1);
        LocalDateTime startOfMonth = now.minusMonths(1);
        LocalDateTime startOfPreviousMonth = now.minusMonths(2);
        
        // Get counts
        Long totalCustomers = customerStatsRepository.countTotalCustomers();
        Long newCustomersToday = customerStatsRepository.countNewCustomersSince(startOfToday);
        Long newCustomersThisWeek = customerStatsRepository.countNewCustomersSince(startOfWeek);
        Long newCustomersThisMonth = customerStatsRepository.countNewCustomersSince(startOfMonth);
        
        // Calculate growth rate (comparing this month to previous month)
        Long customersThisMonth = customerStatsRepository.countNewCustomersBetween(startOfMonth, now);
        Long customersPreviousMonth = customerStatsRepository.countNewCustomersBetween(startOfPreviousMonth, startOfMonth);
        
        Double growthRate = 0.0;
        if (customersPreviousMonth > 0) {
            growthRate = ((customersThisMonth - customersPreviousMonth) * 100.0) / customersPreviousMonth;
        }
        
        // Get top customers
        List<TopCustomerDTO> topCustomers = customerStatsRepository.findTopCustomersBySpending(10);
        
        // Get growth trends for the last 6 months
        LocalDateTime sixMonthsAgo = now.minusMonths(6);
        List<CustomerGrowthDTO> growthTrends = customerStatsRepository.findCustomerGrowthByMonth(sixMonthsAgo, now);
        
        return CustomerStatsDTO.builder()
                .totalCustomers(totalCustomers)
                .newCustomersToday(newCustomersToday)
                .newCustomersThisWeek(newCustomersThisWeek)
                .newCustomersThisMonth(newCustomersThisMonth)
                .growthRate(growthRate)
                .topCustomers(topCustomers)
                .growthTrends(growthTrends)
                .build();
    }

    @Override
    public List<CustomerGrowthDTO> getCustomerGrowth(LocalDateTime startDate, LocalDateTime endDate, String groupBy) {
        log.info("Fetching customer growth from {} to {} grouped by {}", startDate, endDate, groupBy);
        
        return switch (groupBy.toLowerCase()) {
            case "day" -> customerStatsRepository.findCustomerGrowthByDay(startDate, endDate);
            case "week" -> customerStatsRepository.findCustomerGrowthByWeek(startDate, endDate);
            case "month" -> customerStatsRepository.findCustomerGrowthByMonth(startDate, endDate);
            default -> {
                log.warn("Invalid groupBy parameter: {}. Defaulting to 'month'", groupBy);
                yield customerStatsRepository.findCustomerGrowthByMonth(startDate, endDate);
            }
        };
    }

    @Override
    public List<TopCustomerDTO> getTopCustomers(int limit) {
        log.info("Fetching top {} customers by spending", limit);
        return customerStatsRepository.findTopCustomersBySpending(limit);
    }
}
