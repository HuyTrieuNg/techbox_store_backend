package vn.techbox.techbox_store.product.dto;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.math.BigDecimal;
import java.util.List;

/**
 * DTO for product search and filter request
 */
@Data
@NoArgsConstructor
@AllArgsConstructor
@Builder
public class ProductFilterRequest {
    
    // Search by name
    private String name;
    
    // Filter by brand
    private Integer brandId;
    
    // Filter by category (parent or child)
    private Integer categoryId;
    
    // Filter by multiple categories
    private List<Integer> categoryIds;
    
    // Filter by attributes (attribute_id:value)
    private List<String> attributes; // Format: "attributeId:value" e.g., ["1:Red", "2:128GB"]
    
    // Price range
    private BigDecimal minPrice;
    private BigDecimal maxPrice;
    
    // Rating filter
    private Double minRating;
    
    // Sorting
    private String sortBy; // price, rating, reviewCount, createdAt
    private String sortDirection; // ASC, DESC
    
    // Pagination
    private Integer page;
    private Integer size;
}
