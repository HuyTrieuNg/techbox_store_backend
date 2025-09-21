package vn.techbox.techbox_store.product.repository;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import org.springframework.stereotype.Repository;
import vn.techbox.techbox_store.product.model.Category;

import java.util.List;
import java.util.Optional;

@Repository
public interface CategoryRepository extends JpaRepository<Category, Integer> {
    
    Optional<Category> findByName(String name);
    
    List<Category> findByParentCategoryIdIsNull();
    
    List<Category> findByParentCategoryId(Integer parentCategoryId);
    
    @Query("SELECT c FROM Category c WHERE c.parentCategory.id = :parentId")
    List<Category> findChildCategories(@Param("parentId") Integer parentId);
    
    @Query("SELECT c FROM Category c WHERE c.parentCategoryId IS NULL")
    List<Category> findRootCategories();
    
    boolean existsByName(String name);
    
    boolean existsByNameAndIdNot(String name, Integer id);
}