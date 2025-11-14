package vn.techbox.techbox_store.product.specification;

import jakarta.persistence.criteria.*;
import lombok.extern.slf4j.Slf4j;
import org.springframework.data.jpa.domain.Specification;
import org.springframework.stereotype.Component;

import vn.techbox.techbox_store.product.dto.productDto.ProductFilterRequest;
import vn.techbox.techbox_store.product.model.Product;
import vn.techbox.techbox_store.product.model.ProductAttribute;
import vn.techbox.techbox_store.product.model.ProductStatus;
import vn.techbox.techbox_store.product.model.ProductVariation;
import vn.techbox.techbox_store.promotion.model.Promotion;

import java.math.BigDecimal;
import java.util.ArrayList;
import java.util.List;

@Component
@Slf4j
public class ProductSpecification {


    public Specification<Product> buildFilterSpecification(ProductFilterRequest filter) {
        Specification<Product> spec = Specification.where(null);
        
        // Apply status filter
        if (filter.getStatus() != null) {
            spec = spec.and(ProductSpecification.hasStatus(filter.getStatus()));
        }
        
        // Apply name filter
        if (filter.getName() != null && !filter.getName().trim().isEmpty()) {
            spec = spec.and(ProductSpecification.nameLike(filter.getName()));
        }
        
        // Apply SPU filter
        if (filter.getSpu() != null && !filter.getSpu().trim().isEmpty()) {
            spec = spec.and(ProductSpecification.spuLike(filter.getSpu()));
        }
        
        // Apply brand filter
        if (filter.getBrandId() != null) {
            spec = spec.and(ProductSpecification.hasBrand(filter.getBrandId()));
        }
        
        // Apply category filter
        if (filter.getCategoryIds() != null && !filter.getCategoryIds().isEmpty()) {
            spec = spec.and(ProductSpecification.hasCategories(filter.getCategoryIds()));
        }
        
        // Apply price range filter
        if (filter.getMinPrice() != null || filter.getMaxPrice() != null) {
            spec = spec.and(ProductSpecification.priceInRange(filter.getMinPrice(), filter.getMaxPrice()));
        }
        
        // Apply rating filter
        if (filter.getMinRating() != null) {
            spec = spec.and(ProductSpecification.ratingGreaterThanOrEqual(filter.getMinRating()));
        }
        
        // Apply campaign filter
        if (filter.getCampaignId() != null) {
            spec = spec.and(ProductSpecification.hasCampaignId(filter.getCampaignId()));
        }
        
        // Apply attributes filter
        if (filter.getAttributes() != null && !filter.getAttributes().isEmpty()) {
            spec = spec.and(ProductSpecification.hasAttributes(filter.getAttributes()));
        }
        
        return spec;
    }

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
     * Filter by SPU (case-insensitive, partial match)
     */
    public static Specification<Product> spuLike(String spu) {
        return (root, query, criteriaBuilder) -> 
            criteriaBuilder.like(
                criteriaBuilder.lower(root.get("spu")),
                "%" + spu.toLowerCase() + "%"
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
     * Filter by campaign ID
     * Finds products that have variations with promotions in the specified campaign
     */
    public static Specification<Product> hasCampaignId(Integer campaignId) {
        return (root, query, criteriaBuilder) -> {
            if (query == null) {
                return criteriaBuilder.conjunction();
            }
            
            Subquery<Integer> campaignSubquery = query.subquery(Integer.class);
            Root<ProductVariation> variationRoot = campaignSubquery.from(ProductVariation.class);
            Join<ProductVariation, Promotion> promotionJoin = variationRoot.join("promotions");
            
            campaignSubquery.select(variationRoot.get("productId"));
            campaignSubquery.where(
                criteriaBuilder.and(
                    criteriaBuilder.equal(variationRoot.get("productId"), root.get("id")),
                    criteriaBuilder.equal(promotionJoin.get("campaign").get("id"), campaignId)
                )
            );
            
            return criteriaBuilder.exists(campaignSubquery);
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
