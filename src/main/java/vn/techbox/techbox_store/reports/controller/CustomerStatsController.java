package vn.techbox.techbox_store.reports.controller;

import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.Parameter;
import io.swagger.v3.oas.annotations.tags.Tag;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.format.annotation.DateTimeFormat;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;
import vn.techbox.techbox_store.reports.dto.CustomerGrowthDTO;
import vn.techbox.techbox_store.reports.dto.CustomerStatsDTO;
import vn.techbox.techbox_store.reports.dto.TopCustomerDTO;
import vn.techbox.techbox_store.reports.service.CustomerStatsService;

import java.time.LocalDateTime;
import java.util.List;

@RestController
@RequestMapping("/reports/customers")
@RequiredArgsConstructor
@Slf4j
@Tag(name = "Customer Statistics", description = "APIs for customer statistics and reports")
public class CustomerStatsController {

    private final CustomerStatsService customerStatsService;

    @GetMapping("/overview")
    @Operation(summary = "Get customer overview statistics", 
               description = "Returns overall customer statistics including total count, new customers, growth rate, and top customers")
    public ResponseEntity<CustomerStatsDTO> getCustomerOverview() {
        log.info("GET /api/reports/customers/overview - Fetching customer overview");
        CustomerStatsDTO stats = customerStatsService.getCustomerOverview();
        return ResponseEntity.ok(stats);
    }

    @GetMapping("/growth")
    @Operation(summary = "Get customer growth trends",
               description = "Returns customer growth trends for a specific date range, grouped by day, week, or month")
    public ResponseEntity<List<CustomerGrowthDTO>> getCustomerGrowth(
            @Parameter(description = "Start date (ISO format: yyyy-MM-dd'T'HH:mm:ss)")
            @RequestParam @DateTimeFormat(iso = DateTimeFormat.ISO.DATE_TIME) LocalDateTime startDate,
            
            @Parameter(description = "End date (ISO format: yyyy-MM-dd'T'HH:mm:ss)")
            @RequestParam @DateTimeFormat(iso = DateTimeFormat.ISO.DATE_TIME) LocalDateTime endDate,
            
            @Parameter(description = "Group by: day, week, or month")
            @RequestParam(defaultValue = "month") String groupBy
    ) {
        log.info("GET /api/reports/customers/growth - Start: {}, End: {}, GroupBy: {}", startDate, endDate, groupBy);
        List<CustomerGrowthDTO> growth = customerStatsService.getCustomerGrowth(startDate, endDate, groupBy);
        return ResponseEntity.ok(growth);
    }

    @GetMapping("/top")
    @Operation(summary = "Get top customers by spending",
               description = "Returns the top customers ranked by total spending amount")
    public ResponseEntity<List<TopCustomerDTO>> getTopCustomers(
            @Parameter(description = "Number of top customers to return")
            @RequestParam(defaultValue = "10") int limit
    ) {
        log.info("GET /api/reports/customers/top - Limit: {}", limit);
        
        // Validate limit
        if (limit < 1 || limit > 100) {
            limit = 10;
        }
        
        List<TopCustomerDTO> topCustomers = customerStatsService.getTopCustomers(limit);
        return ResponseEntity.ok(topCustomers);
    }
}
