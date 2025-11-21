package vn.techbox.techbox_store.reports.dto;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.util.List;

@Data
@Builder
@NoArgsConstructor
@AllArgsConstructor
public class ProductStatsDTO {
    private Long totalProducts;
    private Long activeProducts;
    private Long draftProducts;
    private Long deletedProducts;
    private List<ProductByCategoryDTO> productsByCategory;
    private List<TopSellingProductDTO> topSellingProducts;
    private List<LowStockProductDTO> lowStockProducts;
    private Double averageProductRating;
}
