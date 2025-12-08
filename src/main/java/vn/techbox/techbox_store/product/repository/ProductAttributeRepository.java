package vn.techbox.techbox_store.product.repository;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Modifying;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import org.springframework.stereotype.Repository;
import vn.techbox.techbox_store.product.model.ProductAttribute;
import vn.techbox.techbox_store.product.model.ProductAttributeId;

import java.util.List;

@Repository
public interface ProductAttributeRepository extends JpaRepository<ProductAttribute, ProductAttributeId> {
    
    @Query("SELECT pa FROM ProductAttribute pa WHERE pa.productId = :productId")
    List<ProductAttribute> findByProductId(@Param("productId") Integer productId);
    
    @Query("SELECT pa FROM ProductAttribute pa WHERE pa.attributeId = :attributeId")
    List<ProductAttribute> findByAttributeId(@Param("attributeId") Integer attributeId);
    
    @Query("SELECT DISTINCT pa.value FROM ProductAttribute pa WHERE pa.attributeId = :attributeId AND LOWER(pa.value) LIKE LOWER(CONCAT('%', :value, '%'))")
    List<String> findDistinctValuesByAttributeIdAndValueContaining(@Param("attributeId") Integer attributeId, @Param("value") String value);
    @Modifying
    @Query("DELETE FROM ProductAttribute pa WHERE pa.productId = :productId")
    void deleteByProductId(@Param("productId") Integer productId);
}
