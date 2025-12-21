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
import vn.techbox.techbox_store.reports.dto.InventoryStatsDTO;
import vn.techbox.techbox_store.reports.dto.StockMovementDTO;
import vn.techbox.techbox_store.reports.service.InventoryStatsService;

import java.time.LocalDateTime;
import java.util.List;

@RestController
@RequestMapping("/reports/inventory")
@RequiredArgsConstructor
@Slf4j
@Tag(name = "Inventory Statistics", description = "APIs for inventory statistics and reports")
public class InventoryStatsController {

    private final InventoryStatsService inventoryStatsService;

    @PreAuthorize("hasAuthority('REPORT:INVENTORY')")
    @GetMapping("/overview")
    @Operation(summary = "Get inventory overview statistics",
               description = "Returns overall inventory statistics including total value, stock counts, and recent movements")
    public ResponseEntity<InventoryStatsDTO> getInventoryOverview() {
        log.info("GET /api/reports/inventory/overview - Fetching inventory overview");
        InventoryStatsDTO stats = inventoryStatsService.getInventoryOverview();
        return ResponseEntity.ok(stats);
    }

    @PreAuthorize("hasAuthority('REPORT:INVENTORY')")
    @GetMapping("/movements")
    @Operation(summary = "Get stock movements",
               description = "Returns stock import and export movements for a specific date range")
    public ResponseEntity<List<StockMovementDTO>> getStockMovements(
            @Parameter(description = "Start date (ISO format: yyyy-MM-dd'T'HH:mm:ss)")
            @RequestParam @DateTimeFormat(iso = DateTimeFormat.ISO.DATE_TIME) LocalDateTime startDate,
            
            @Parameter(description = "End date (ISO format: yyyy-MM-dd'T'HH:mm:ss)")
            @RequestParam @DateTimeFormat(iso = DateTimeFormat.ISO.DATE_TIME) LocalDateTime endDate
    ) {
        log.info("GET /api/reports/inventory/movements - Start: {}, End: {}", startDate, endDate);
        List<StockMovementDTO> movements = inventoryStatsService.getStockMovements(startDate, endDate);
        return ResponseEntity.ok(movements);
    }
}
