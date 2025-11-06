package vn.techbox.techbox_store.product.repository;

import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.JpaSpecificationExecutor;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import org.springframework.stereotype.Repository;
import vn.techbox.techbox_store.product.model.Product;
import vn.techbox.techbox_store.product.model.ProductVariation;

import java.util.List;
import java.util.Optional;

@Repository
public interface ProductRepository extends JpaRepository<Product, Integer>, JpaSpecificationExecutor<Product> {

       boolean existsByName(String name);

       boolean existsByNameAndIdNot(String name, Integer id);

       // Find all active products
       @Query("SELECT p FROM Product p WHERE p.status = 'PUBLISHED'")
       List<Product> findAllActive();

       // Find active (status = 'PUBLISHED') product by id
       @Query("SELECT p FROM Product p WHERE p.id = :id AND p.status = 'PUBLISHED'")
       Optional<Product> findActiveById(@Param("id") Integer id);
      
       // Find products by list of IDs and not deleted
       @Query("SELECT p FROM Product p WHERE p.id IN :ids AND p.deletedAt IS NULL")
       Page<Product> findByIdInAndDeletedAtIsNull(@Param("ids") List<Integer> ids, Pageable pageable);
}