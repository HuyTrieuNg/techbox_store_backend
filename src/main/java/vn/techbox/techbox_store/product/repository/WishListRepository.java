package vn.techbox.techbox_store.product.repository;

import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;
import vn.techbox.techbox_store.product.model.WishList;

import java.util.List;
import java.util.Optional;

@Repository
public interface WishListRepository extends JpaRepository<WishList, Integer> {
    Page<WishList> findByUserId(Integer userId, Pageable pageable);

    Optional<WishList> findByUserIdAndProductId(Integer userId, Integer productId);

    boolean existsByUserIdAndProductId(Integer userId, Integer productId);
    
    List<WishList> findByUserIdAndProductIdIn(Integer userId, List<Integer> productIds);

    void deleteByUserIdAndProductId(Integer userId, Integer productId);
}
