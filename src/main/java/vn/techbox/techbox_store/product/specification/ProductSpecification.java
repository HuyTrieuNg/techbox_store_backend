package vn.techbox.techbox_store.product.specification;

import jakarta.persistence.criteria.*;
import lombok.extern.slf4j.Slf4j;
import org.springframework.data.jpa.domain.Specification;
import vn.techbox.techbox_store.product.dto.ProductFilterRequest;
import vn.techbox.techbox_store.product.model.Product;
import vn.techbox.techbox_store.product.model.ProductAttribute;
import vn.techbox.techbox_store.product.model.ProductStatus;
import vn.techbox.techbox_store.product.model.ProductVariation;
import vn.techbox.techbox_store.promotion.model.Promotion;

import java.math.BigDecimal;
import java.util.ArrayList;
import java.util.List;

@Slf4j
public class ProductSpecification {
    /**
     * Filter by product status
     */
    public static Specification<Product> hasStatus(ProductStatus status) {
        return (root, query, criteriaBuilder) -> 
            criteriaBuilder.equal(root.get("status"), status);
    }
    
    /**
     * Filter by name (case-insensitive, partial match)
     */
    public static Specification<Product> nameLike(String name) {
        return (root, query, criteriaBuilder) -> 
            criteriaBuilder.like(
                criteriaBuilder.lower(root.get("name")),
                "%" + name.toLowerCase() + "%"
            );
    }
    
    /**
     * Filter by brand ID
     */
    public static Specification<Product> hasBrand(Integer brandId) {
        return (root, query, criteriaBuilder) -> 
            criteriaBuilder.equal(root.get("brandId"), brandId);
    }
    
    /**
     * Filter by multiple category IDs
     */
    public static Specification<Product> hasCategories(List<Integer> categoryIds) {
        return (root, query, criteriaBuilder) -> 
            root.get("categoryId").in(categoryIds);
    }
    
    /**
     * Filter by price range
     */
    public static Specification<Product> priceInRange(BigDecimal minPrice, BigDecimal maxPrice) {
        return (root, query, criteriaBuilder) -> {
            List<Predicate> predicates = new ArrayList<>();
            
            if (minPrice != null) {
                predicates.add(criteriaBuilder.greaterThanOrEqualTo(
                    root.get("displaySalePrice"), minPrice
                ));
            }
            
            if (maxPrice != null) {
                predicates.add(criteriaBuilder.lessThanOrEqualTo(
                    root.get("displaySalePrice"), maxPrice
                ));
            }
            
            return criteriaBuilder.and(predicates.toArray(new Predicate[0]));
        };
    }
    
    /**
     * Filter by minimum rating
     */
    public static Specification<Product> ratingGreaterThanOrEqual(Double minRating) {
        return (root, query, criteriaBuilder) -> 
            criteriaBuilder.greaterThanOrEqualTo(root.get("averageRating"), minRating);
    }
    
    /**
     * Filter by promotion ID
     * Finds products that have variations with the specified promotion
     */
    public static Specification<Product> hasPromotion(Integer promotionId) {
        return (root, query, criteriaBuilder) -> {
            if (query == null) {
                return criteriaBuilder.conjunction();
            }
            
            Subquery<Integer> promotionSubquery = query.subquery(Integer.class);
            Root<ProductVariation> variationRoot = promotionSubquery.from(ProductVariation.class);
            Join<ProductVariation, Promotion> promotionJoin = variationRoot.join("promotions");
            
            promotionSubquery.select(variationRoot.get("productId"));
            promotionSubquery.where(
                criteriaBuilder.and(
                    criteriaBuilder.equal(variationRoot.get("productId"), root.get("id")),
                    criteriaBuilder.equal(promotionJoin.get("id"), promotionId)
                )
            );
            
            return criteriaBuilder.exists(promotionSubquery);
        };
    }
    
    /**
     * Filter by product attributes
     * Format: ["attributeId:value", ...]
     */
    public static Specification<Product> hasAttributes(List<String> attributes) {
        return (root, query, criteriaBuilder) -> {
            if (query == null || attributes == null || attributes.isEmpty()) {
                return criteriaBuilder.conjunction();
            }
            
            List<Predicate> predicates = new ArrayList<>();
            
            for (String attr : attributes) {
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
                        log.warn("Invalid attribute filter format: {}", attr);
                    }
                }
            }
            
            return criteriaBuilder.and(predicates.toArray(new Predicate[0]));
        };
    }
}
