package vn.techbox.techbox_store.product.dto;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;
import vn.techbox.techbox_store.product.model.ProductStatus;

import java.math.BigDecimal;
import java.util.List;

/**
 * DTO for product search and filter request
 */
@Data
@NoArgsConstructor
@AllArgsConstructor
@Builder(toBuilder = true)
public class ProductFilterRequest {
    
    // Search by name
    private String name;
    
    // Filter by brand
    private Integer brandId;
    
    // Filter by category (parent or child) - will automatically include all child categories
    private Integer categoryId;
    
    // Internal use only - auto-populated from categoryId
    // DO NOT send this field in API request
    private List<Integer> categoryIds;
    
    // Filter by attributes (attribute_id:value)
    private List<String> attributes; // Format: "attributeId:value" e.g., ["1:Red", "2:128GB"]
    
    // Price range
    private BigDecimal minPrice;
    private BigDecimal maxPrice;
    
    // Rating filter
    private Double minRating;
    
    // Filter by product status
    private ProductStatus status;
    
    // Filter by promotion
    private Integer promotionId;
    
    // Sorting
    private String sortBy; // price, rating, reviewCount, createdAt, newest (id DESC)
    private String sortDirection; // ASC, DESC
    
    // Pagination
    private Integer page;
    private Integer size;
}
