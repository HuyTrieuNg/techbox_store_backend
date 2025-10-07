package vn.techbox.techbox_store.inventory.controller;

import lombok.RequiredArgsConstructor;
import org.springframework.format.annotation.DateTimeFormat;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;
import vn.techbox.techbox_store.inventory.dto.*;
import vn.techbox.techbox_store.inventory.service.InventoryReportService;

import java.time.LocalDate;
import java.util.List;

@RestController
@RequestMapping("/inventory")
@RequiredArgsConstructor
public class InventoryReportController {

    private final InventoryReportService inventoryReportService;

    /**
     * 1. Get current stock balance
     * GET /api/inventory/stock-balance
     */
    @GetMapping("/stock-balance")
    public ResponseEntity<List<StockBalanceDTO>> getStockBalance(
            @RequestParam(required = false) Integer categoryId,
            @RequestParam(required = false) Integer brandId,
            @RequestParam(required = false) String keyword,
            @RequestParam(required = false, defaultValue = "false") Boolean lowStock,
            @RequestParam(required = false, defaultValue = "false") Boolean outOfStock
    ) {
        List<StockBalanceDTO> result = inventoryReportService.getStockBalance(
                categoryId,
                brandId,
                keyword,
                lowStock,
                outOfStock
        );
        return ResponseEntity.ok(result);
    }

    /**
     * 2. Get product history (import/export movements)
     * GET /api/inventory/product-history/{productVariationId}
     */
    @GetMapping("/product-history/{productVariationId}")
    public ResponseEntity<List<StockMovementDTO>> getProductHistory(
            @PathVariable Integer productVariationId,
            @RequestParam(required = false) @DateTimeFormat(iso = DateTimeFormat.ISO.DATE) LocalDate fromDate,
            @RequestParam(required = false) @DateTimeFormat(iso = DateTimeFormat.ISO.DATE) LocalDate toDate
    ) {
        List<StockMovementDTO> result = inventoryReportService.getProductHistory(
                productVariationId,
                fromDate,
                toDate
        );
        return ResponseEntity.ok(result);
    }

    /**
     * 3. Get stock value report over time
     * GET /api/inventory/stock-value-report
     */
    @GetMapping("/stock-value-report")
    public ResponseEntity<List<StockValueReportDTO>> getStockValueReport(
            @RequestParam(required = false) @DateTimeFormat(iso = DateTimeFormat.ISO.DATE) LocalDate fromDate,
            @RequestParam(required = false) @DateTimeFormat(iso = DateTimeFormat.ISO.DATE) LocalDate toDate,
            @RequestParam(required = false, defaultValue = "day") String groupBy
    ) {
        List<StockValueReportDTO> result = inventoryReportService.getStockValueReport(
                fromDate,
                toDate,
                groupBy
        );
        return ResponseEntity.ok(result);
    }

    /**
     * 4. Get top products by import/export
     * GET /api/inventory/top-products
     */
    @GetMapping("/top-products")
    public ResponseEntity<List<TopProductDTO>> getTopProducts(
            @RequestParam(required = false) @DateTimeFormat(iso = DateTimeFormat.ISO.DATE) LocalDate fromDate,
            @RequestParam(required = false) @DateTimeFormat(iso = DateTimeFormat.ISO.DATE) LocalDate toDate,
            @RequestParam(required = false, defaultValue = "import") String type,
            @RequestParam(required = false, defaultValue = "10") Integer limit
    ) {
        List<TopProductDTO> result = inventoryReportService.getTopProducts(
                fromDate,
                toDate,
                type,
                limit
        );
        return ResponseEntity.ok(result);
    }

    /**
     * 5. Get inventory alerts (out of stock, low stock, overstock)
     * GET /api/inventory/alerts
     */
    @GetMapping("/alerts")
    public ResponseEntity<InventoryAlertsDTO> getInventoryAlerts() {
        InventoryAlertsDTO result = inventoryReportService.getInventoryAlerts();
        return ResponseEntity.ok(result);
    }
}
