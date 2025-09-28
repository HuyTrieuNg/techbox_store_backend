package vn.techbox.techbox_store.product.repository;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import org.springframework.stereotype.Repository;
import vn.techbox.techbox_store.product.model.ProductVariationImage;

import java.util.List;

@Repository
public interface ProductVariationImageRepository extends JpaRepository<ProductVariationImage, Integer> {
    
    @Query("SELECT pvi FROM ProductVariationImage pvi WHERE pvi.productVariationId = :variationId ORDER BY pvi.id ASC")
    List<ProductVariationImage> findByProductVariationId(@Param("variationId") Integer variationId);
    
    @Query("DELETE FROM ProductVariationImage pvi WHERE pvi.productVariationId = :variationId")
    void deleteByProductVariationId(@Param("variationId") Integer variationId);
    
    @Query("DELETE FROM ProductVariationImage pvi WHERE pvi.imagePublicId = :publicId")
    void deleteByImagePublicId(@Param("publicId") String publicId);
}