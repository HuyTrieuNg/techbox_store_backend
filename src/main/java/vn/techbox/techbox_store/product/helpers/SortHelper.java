package vn.techbox.techbox_store.product.helpers;

import org.springframework.data.domain.Sort;
import org.springframework.stereotype.Component;

@Component
public class SortHelper {
    
    public Sort buildSort(String sortBy, String sortDirection) {
        // Map sortBy field names
        String field = switch (sortBy != null ? sortBy.toLowerCase() : "id") {
            case "price" -> "displaySalePrice";
            case "rating" -> "averageRating";
            case "time"-> "createdAt";
            case "name" -> "name";
            default -> "id";
        };

        Sort.Direction direction = "desc".equalsIgnoreCase(sortDirection) 
                ? Sort.Direction.DESC
                : Sort.Direction.ASC;
        
        return Sort.by(direction, field);
    }
}
