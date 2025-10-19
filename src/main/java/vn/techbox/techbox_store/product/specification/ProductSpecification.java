package vn.techbox.techbox_store.product.specification;

import jakarta.persistence.criteria.*;
import org.springframework.data.jpa.domain.Specification;
import vn.techbox.techbox_store.product.dto.ProductFilterRequest;
import vn.techbox.techbox_store.product.model.Product;
import vn.techbox.techbox_store.product.model.ProductAttribute;

import java.util.ArrayList;
import java.util.List;

/**
 * Specification for dynamic product filtering
 */
public class ProductSpecification {
    
    public static Specification<Product> filterProducts(ProductFilterRequest filter) {
        return (root, query, criteriaBuilder) -> {
            List<Predicate> predicates = new ArrayList<>();
            
            // Always exclude soft-deleted products
            predicates.add(criteriaBuilder.isNull(root.get("deletedAt")));
            
            // Filter by name (case-insensitive partial match)
            if (filter.getName() != null && !filter.getName().trim().isEmpty()) {
                predicates.add(criteriaBuilder.like(
                        criteriaBuilder.lower(root.get("name")),
                        "%" + filter.getName().toLowerCase() + "%"
                ));
            }
            
            // Filter by brand
            if (filter.getBrandId() != null) {
                predicates.add(criteriaBuilder.equal(root.get("brandId"), filter.getBrandId()));
            }
            
            // Filter by single category
            if (filter.getCategoryId() != null) {
                predicates.add(criteriaBuilder.equal(root.get("categoryId"), filter.getCategoryId()));
            }
            
            // Filter by multiple categories (parent or children)
            if (filter.getCategoryIds() != null && !filter.getCategoryIds().isEmpty()) {
                predicates.add(root.get("categoryId").in(filter.getCategoryIds()));
            }
            
            // Filter by price range (using displaySalePrice)
            if (filter.getMinPrice() != null) {
                predicates.add(criteriaBuilder.greaterThanOrEqualTo(
                        root.get("displaySalePrice"), filter.getMinPrice()
                ));
            }
            
            if (filter.getMaxPrice() != null) {
                predicates.add(criteriaBuilder.lessThanOrEqualTo(
                        root.get("displaySalePrice"), filter.getMaxPrice()
                ));
            }
            
            // Filter by minimum rating
            if (filter.getMinRating() != null) {
                predicates.add(criteriaBuilder.greaterThanOrEqualTo(
                        root.get("averageRating"), filter.getMinRating()
                ));
            }
            
            // Filter by product attributes (product-level attributes)
            if (filter.getAttributes() != null && !filter.getAttributes().isEmpty() && query != null) {
                for (String attr : filter.getAttributes()) {
                    String[] parts = attr.split(":", 2);
                    if (parts.length == 2) {
                        try {
                            Integer attributeId = Integer.parseInt(parts[0]);
                            String value = parts[1];
                            
                            Subquery<Integer> attributeSubquery = query.subquery(Integer.class);
                            Root<ProductAttribute> attrRoot = attributeSubquery.from(ProductAttribute.class);
                            attributeSubquery.select(attrRoot.get("productId"));
                            attributeSubquery.where(
                                    criteriaBuilder.and(
                                            criteriaBuilder.equal(attrRoot.get("productId"), root.get("id")),
                                            criteriaBuilder.equal(attrRoot.get("attributeId"), attributeId),
                                            criteriaBuilder.like(
                                                    criteriaBuilder.lower(attrRoot.get("value")),
                                                    "%" + value.toLowerCase() + "%"
                                            )
                                    )
                            );
                            predicates.add(criteriaBuilder.exists(attributeSubquery));
                        } catch (NumberFormatException e) {
                            // Skip invalid attribute filter
                        }
                    }
                }
            }
            
            // Ensure distinct results when using joins
            if (query != null) {
                query.distinct(true);
            }
            
            return criteriaBuilder.and(predicates.toArray(new Predicate[0]));
        };
    }
}
