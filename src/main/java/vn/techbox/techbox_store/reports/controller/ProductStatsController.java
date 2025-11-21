package vn.techbox.techbox_store.reports.controller;

import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.Parameter;
import io.swagger.v3.oas.annotations.tags.Tag;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;
import vn.techbox.techbox_store.reports.dto.InventoryConfigDTO;
import vn.techbox.techbox_store.reports.dto.LowStockProductDTO;
import vn.techbox.techbox_store.reports.dto.PagedLowStockProductDTO;
import vn.techbox.techbox_store.reports.dto.ProductByCategoryDTO;
import vn.techbox.techbox_store.reports.dto.ProductStatsDTO;
import vn.techbox.techbox_store.reports.dto.TopSellingProductDTO;
import vn.techbox.techbox_store.reports.service.ProductStatsService;

import java.util.List;

@RestController
@RequestMapping("/reports/products")
@RequiredArgsConstructor
@Slf4j
@Tag(name = "Product Statistics", description = "APIs for product statistics and reports")
public class ProductStatsController {

    private final ProductStatsService productStatsService;

    @GetMapping("/overview")
    @Operation(summary = "Get product overview statistics",
               description = "Returns overall product statistics including counts by status, categories, top sellers, and low stock alerts")
    public ResponseEntity<ProductStatsDTO> getProductOverview() {
        log.info("GET /api/reports/products/overview - Fetching product overview");
        ProductStatsDTO stats = productStatsService.getProductOverview();
        return ResponseEntity.ok(stats);
    }

    @GetMapping("/by-category")
    @Operation(summary = "Get products grouped by category",
               description = "Returns the number of products in each category")
    public ResponseEntity<List<ProductByCategoryDTO>> getProductsByCategory() {
        log.info("GET /api/reports/products/by-category - Fetching products by category");
        List<ProductByCategoryDTO> products = productStatsService.getProductsByCategory();
        return ResponseEntity.ok(products);
    }

    @GetMapping("/top-selling")
    @Operation(summary = "Get top selling products",
               description = "Returns the top selling products ranked by total quantity sold")
    public ResponseEntity<List<TopSellingProductDTO>> getTopSellingProducts(
            @Parameter(description = "Number of top products to return")
            @RequestParam(defaultValue = "10") int limit
    ) {
        log.info("GET /api/reports/products/top-selling - Limit: {}", limit);
        
        // Validate limit
        if (limit < 1 || limit > 100) {
            limit = 10;
        }
        
        List<TopSellingProductDTO> topProducts = productStatsService.getTopSellingProducts(limit);
        return ResponseEntity.ok(topProducts);
    }

    @GetMapping("/low-stock")
    @Operation(summary = "Get low stock products",
               description = "Returns products with stock quantity below the specified threshold")
    public ResponseEntity<List<LowStockProductDTO>> getLowStockProducts(
            @Parameter(description = "Stock quantity threshold")
            @RequestParam(defaultValue = "10") int threshold
    ) {
        log.info("GET /api/reports/products/low-stock - Threshold: {}", threshold);
        
        // Validate threshold
        if (threshold < 0) {
            threshold = 10;
        }
        
        List<LowStockProductDTO> lowStockProducts = productStatsService.getLowStockProducts(threshold);
        return ResponseEntity.ok(lowStockProducts);
    }
    
    @GetMapping("/low-stock/paged")
    @Operation(summary = "Get low stock products with pagination",
               description = "Returns paginated list of products with stock quantity below the specified threshold")
    public ResponseEntity<PagedLowStockProductDTO> getLowStockProductsPaged(
            @Parameter(description = "Stock quantity threshold")
            @RequestParam(required = false) Integer threshold,
            @Parameter(description = "Page number (0-indexed)")
            @RequestParam(defaultValue = "0") int page,
            @Parameter(description = "Page size")
            @RequestParam(defaultValue = "10") int size
    ) {
        // Use config threshold if not provided
        if (threshold == null) {
            InventoryConfigDTO config = productStatsService.getInventoryConfig();
            threshold = config.getMinStockThreshold();
        }
        
        log.info("GET /api/reports/products/low-stock/paged - Threshold: {}, Page: {}, Size: {}", threshold, page, size);
        
        // Validate parameters
        if (threshold < 0) threshold = 30;
        if (page < 0) page = 0;
        if (size < 1 || size > 100) size = 10;
        
        PagedLowStockProductDTO pagedProducts = productStatsService.getLowStockProductsPaged(threshold, page, size);
        return ResponseEntity.ok(pagedProducts);
    }
    
    @GetMapping("/config")
    @Operation(summary = "Get inventory configuration",
               description = "Returns inventory configuration including min stock threshold")
    public ResponseEntity<InventoryConfigDTO> getInventoryConfig() {
        log.info("GET /api/reports/products/config - Fetching inventory configuration");
        InventoryConfigDTO config = productStatsService.getInventoryConfig();
        return ResponseEntity.ok(config);
    }
}
