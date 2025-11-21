package vn.techbox.techbox_store.reports.service;

import vn.techbox.techbox_store.reports.dto.InventoryConfigDTO;
import vn.techbox.techbox_store.reports.dto.LowStockProductDTO;
import vn.techbox.techbox_store.reports.dto.PagedLowStockProductDTO;
import vn.techbox.techbox_store.reports.dto.ProductByCategoryDTO;
import vn.techbox.techbox_store.reports.dto.ProductStatsDTO;
import vn.techbox.techbox_store.reports.dto.TopSellingProductDTO;

import java.util.List;

public interface ProductStatsService {
    
    /**
     * Get overall product statistics
     */
    ProductStatsDTO getProductOverview();
    
    /**
     * Get products grouped by category
     */
    List<ProductByCategoryDTO> getProductsByCategory();
    
    /**
     * Get top selling products
     * @param limit Number of top products to return
     */
    List<TopSellingProductDTO> getTopSellingProducts(int limit);
    
    /**
     * Get low stock products
     * @param threshold Stock quantity threshold
     */
    List<LowStockProductDTO> getLowStockProducts(int threshold);
    
    /**
     * Get low stock products with pagination
     * @param threshold Stock quantity threshold
     * @param page Page number (0-indexed)
     * @param size Page size
     */
    PagedLowStockProductDTO getLowStockProductsPaged(int threshold, int page, int size);
    
    /**
     * Get inventory configuration
     */
    InventoryConfigDTO getInventoryConfig();
}
