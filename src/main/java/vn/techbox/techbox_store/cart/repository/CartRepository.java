package vn.techbox.techbox_store.cart.repository;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Modifying;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import org.springframework.stereotype.Repository;
import vn.techbox.techbox_store.cart.model.Cart;

import java.time.LocalDateTime;
import java.util.List;
import java.util.Optional;

@Repository
public interface CartRepository extends JpaRepository<Cart, Integer> {

    Optional<Cart> findByUserId(Integer userId);

    @Query("SELECT c FROM Cart c WHERE c.updatedAt < :cutoffTime")
    List<Cart> findCartsOlderThan(@Param("cutoffTime") LocalDateTime cutoffTime);

    @Modifying
    @Query("DELETE FROM Cart c WHERE c.updatedAt < :cutoffTime")
    int deleteCartsOlderThan(@Param("cutoffTime") LocalDateTime cutoffTime);

    boolean existsByUserId(Integer userId);

    long countByUserId(Integer userId);

    @Query("SELECT c FROM Cart c WHERE c.createdAt BETWEEN :startDate AND :endDate")
    List<Cart> findByCreatedAtBetween(@Param("startDate") LocalDateTime startDate,
                                     @Param("endDate") LocalDateTime endDate);

    @Query("SELECT c FROM Cart c WHERE c.updatedAt BETWEEN :startDate AND :endDate")
    List<Cart> findByUpdatedAtBetween(@Param("startDate") LocalDateTime startDate,
                                     @Param("endDate") LocalDateTime endDate);

    @Query("SELECT DISTINCT c FROM Cart c LEFT JOIN FETCH c.cartItems WHERE c.userId = :userId")
    Optional<Cart> findByUserIdWithItems(@Param("userId") Integer userId);
}
