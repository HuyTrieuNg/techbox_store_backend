package vn.techbox.techbox_store.product.repository;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import org.springframework.stereotype.Repository;
import vn.techbox.techbox_store.product.model.Attribute;

import java.util.List;
import java.util.Optional;

@Repository
public interface AttributeRepository extends JpaRepository<Attribute, Integer> {
    
    Optional<Attribute> findByName(String name);
    
    Optional<Attribute> findById(Integer id);

    boolean existsByName(String name);
    
    boolean existsByNameAndIdNot(String name, Integer id);
    
    // Search attributes by name containing
    @Query("SELECT a FROM Attribute a WHERE LOWER(a.name) LIKE LOWER(CONCAT('%', :keyword, '%'))")
    List<Attribute> searchByName(@Param("keyword") String keyword);
}