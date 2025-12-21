package vn.techbox.techbox_store.reports.controller;

import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.Parameter;
import io.swagger.v3.oas.annotations.tags.Tag;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.format.annotation.DateTimeFormat;
import org.springframework.http.ResponseEntity;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.web.bind.annotation.*;
import vn.techbox.techbox_store.reports.dto.OrderStatsDTO;
import vn.techbox.techbox_store.reports.dto.RevenueTrendDTO;
import vn.techbox.techbox_store.reports.service.OrderStatsService;

import java.math.BigDecimal;
import java.time.LocalDateTime;
import java.util.List;

@RestController
@RequestMapping("/reports/orders")
@RequiredArgsConstructor
@Slf4j
@Tag(name = "Order Statistics", description = "APIs for order statistics and reports")
public class OrderStatsController {

    private final OrderStatsService orderStatsService;

    @PreAuthorize("hasAuthority('REPORT:ORDER')")
    @GetMapping("/overview")
    @Operation(summary = "Get order overview statistics",
               description = "Returns overall order statistics including counts by status, total revenue, and average order value")
    public ResponseEntity<OrderStatsDTO> getOrderOverview() {
        log.info("GET /api/reports/orders/overview - Fetching order overview");
        OrderStatsDTO stats = orderStatsService.getOrderOverview();
        return ResponseEntity.ok(stats);
    }

    @PreAuthorize("hasAuthority('REPORT:ORDER')")
    @GetMapping("/revenue")
    @Operation(summary = "Get revenue for a date range",
               description = "Returns total revenue for the specified date range")
    public ResponseEntity<BigDecimal> getRevenue(
            @Parameter(description = "Start date (ISO format: yyyy-MM-dd'T'HH:mm:ss)")
            @RequestParam @DateTimeFormat(iso = DateTimeFormat.ISO.DATE_TIME) LocalDateTime startDate,
            
            @Parameter(description = "End date (ISO format: yyyy-MM-dd'T'HH:mm:ss)")
            @RequestParam @DateTimeFormat(iso = DateTimeFormat.ISO.DATE_TIME) LocalDateTime endDate
    ) {
        log.info("GET /api/reports/orders/revenue - Start: {}, End: {}", startDate, endDate);
        BigDecimal revenue = orderStatsService.getRevenue(startDate, endDate);
        return ResponseEntity.ok(revenue);
    }

    @PreAuthorize("hasAuthority('REPORT:ORDER')")
    @GetMapping("/trends")
    @Operation(summary = "Get revenue trends",
               description = "Returns revenue trends for a specific date range, grouped by day, week, or month")
    public ResponseEntity<List<RevenueTrendDTO>> getRevenueTrends(
            @Parameter(description = "Start date (ISO format: yyyy-MM-dd'T'HH:mm:ss)")
            @RequestParam @DateTimeFormat(iso = DateTimeFormat.ISO.DATE_TIME) LocalDateTime startDate,
            
            @Parameter(description = "End date (ISO format: yyyy-MM-dd'T'HH:mm:ss)")
            @RequestParam @DateTimeFormat(iso = DateTimeFormat.ISO.DATE_TIME) LocalDateTime endDate,
            
            @Parameter(description = "Group by: day, week, or month")
            @RequestParam(defaultValue = "month") String groupBy
    ) {
        log.info("GET /api/reports/orders/trends - Start: {}, End: {}, GroupBy: {}", startDate, endDate, groupBy);
        List<RevenueTrendDTO> trends = orderStatsService.getRevenueTrends(startDate, endDate, groupBy);
        return ResponseEntity.ok(trends);
    }
}
