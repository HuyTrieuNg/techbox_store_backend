package vn.techbox.techbox_store.product.repository;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import org.springframework.stereotype.Repository;
import vn.techbox.techbox_store.product.model.VariationAttribute;
import vn.techbox.techbox_store.product.model.VariationAttributeId;

import java.util.List;

@Repository
public interface VariationAttributeRepository extends JpaRepository<VariationAttribute, VariationAttributeId> {
    
    @Query("SELECT va FROM VariationAttribute va WHERE va.productVariationId = :productVariationId")
    List<VariationAttribute> findByProductVariationId(@Param("productVariationId") Integer productVariationId);
    
    @Query("SELECT va FROM VariationAttribute va WHERE va.productVariationId IN :productVariationIds")
    List<VariationAttribute> findByProductVariationIdIn(@Param("productVariationIds") List<Integer> productVariationIds);
    
    @Query("SELECT va FROM VariationAttribute va WHERE va.attributeId = :attributeId")
    List<VariationAttribute> findByAttributeId(@Param("attributeId") Integer attributeId);
    
    @Query("DELETE FROM VariationAttribute va WHERE va.productVariationId = :productVariationId")
    void deleteByProductVariationId(@Param("productVariationId") Integer productVariationId);
}
