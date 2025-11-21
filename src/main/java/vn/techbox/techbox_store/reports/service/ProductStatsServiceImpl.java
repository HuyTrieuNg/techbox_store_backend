package vn.techbox.techbox_store.reports.service;

import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import vn.techbox.techbox_store.product.model.ProductStatus;
import vn.techbox.techbox_store.reports.dto.InventoryConfigDTO;
import vn.techbox.techbox_store.reports.dto.LowStockProductDTO;
import vn.techbox.techbox_store.reports.dto.PagedLowStockProductDTO;
import vn.techbox.techbox_store.reports.dto.ProductByCategoryDTO;
import vn.techbox.techbox_store.reports.dto.ProductStatsDTO;
import vn.techbox.techbox_store.reports.dto.TopSellingProductDTO;
import vn.techbox.techbox_store.reports.repository.ProductStatsRepository;

import java.util.List;

@Service
@RequiredArgsConstructor
@Slf4j
@Transactional(readOnly = true)
public class ProductStatsServiceImpl implements ProductStatsService {

    private final ProductStatsRepository productStatsRepository;
    
    @Value("${inventory.min-stock-threshold}")
    private int minStockThreshold;

    @Override
    public ProductStatsDTO getProductOverview() {
        log.info("Fetching product overview statistics");
        
        Long totalProducts = productStatsRepository.countTotalProducts();
        Long activeProducts = productStatsRepository.countProductsByStatus(ProductStatus.PUBLISHED);
        Long draftProducts = productStatsRepository.countProductsByStatus(ProductStatus.DRAFT);
        Long deletedProducts = productStatsRepository.countProductsByStatus(ProductStatus.DELETED);
        
        List<ProductByCategoryDTO> productsByCategory = productStatsRepository.findProductsByCategory();
        List<TopSellingProductDTO> topSellingProducts = productStatsRepository.findTopSellingProducts(10);
        List<LowStockProductDTO> lowStockProducts = productStatsRepository.findLowStockProducts(10);
        
        Double averageRating = productStatsRepository.getAverageProductRating();
        if (averageRating == null) {
            averageRating = 0.0;
        }
        
        return ProductStatsDTO.builder()
                .totalProducts(totalProducts)
                .activeProducts(activeProducts)
                .draftProducts(draftProducts)
                .deletedProducts(deletedProducts)
                .productsByCategory(productsByCategory)
                .topSellingProducts(topSellingProducts)
                .lowStockProducts(lowStockProducts)
                .averageProductRating(averageRating)
                .build();
    }

    @Override
    public List<ProductByCategoryDTO> getProductsByCategory() {
        log.info("Fetching products grouped by category");
        return productStatsRepository.findProductsByCategory();
    }

    @Override
    public List<TopSellingProductDTO> getTopSellingProducts(int limit) {
        log.info("Fetching top {} selling products", limit);
        return productStatsRepository.findTopSellingProducts(limit);
    }

    @Override
    public List<LowStockProductDTO> getLowStockProducts(int threshold) {
        log.info("Fetching low stock products with threshold {}", threshold);
        return productStatsRepository.findLowStockProducts(threshold);
    }
    
    @Override
    public PagedLowStockProductDTO getLowStockProductsPaged(int threshold, int page, int size) {
        log.info("Fetching low stock products (paged) - threshold: {}, page: {}, size: {}", threshold, page, size);
        
        List<LowStockProductDTO> allProducts = productStatsRepository.findLowStockProducts(threshold);
        
        // Manual pagination
        int totalElements = allProducts.size();
        int totalPages = (int) Math.ceil((double) totalElements / size);
        int fromIndex = page * size;
        int toIndex = Math.min(fromIndex + size, totalElements);
        
        List<LowStockProductDTO> pageContent = fromIndex < totalElements 
            ? allProducts.subList(fromIndex, toIndex) 
            : List.of();
        
        return PagedLowStockProductDTO.builder()
                .content(pageContent)
                .page(page)
                .size(size)
                .totalElements(totalElements)
                .totalPages(totalPages)
                .last(page >= totalPages - 1)
                .build();
    }
    
    @Override
    public InventoryConfigDTO getInventoryConfig() {
        log.info("Fetching inventory configuration");
        return InventoryConfigDTO.builder()
                .minStockThreshold(minStockThreshold)
                .build();
    }
}
