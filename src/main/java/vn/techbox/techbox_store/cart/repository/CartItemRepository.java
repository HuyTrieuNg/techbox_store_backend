package vn.techbox.techbox_store.cart.repository;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Modifying;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import org.springframework.stereotype.Repository;
import vn.techbox.techbox_store.cart.model.CartItem;

import java.util.List;
import java.util.Optional;

@Repository
public interface CartItemRepository extends JpaRepository<CartItem, Integer> {

    @Query("SELECT ci FROM CartItem ci WHERE ci.cart.id = :cartId AND ci.productVariation.id = :productVariationId")
    Optional<CartItem> findByCartIdAndProductVariationId(@Param("cartId") Integer cartId,
                                                        @Param("productVariationId") Integer productVariationId);

    @Query("SELECT ci FROM CartItem ci JOIN FETCH ci.productVariation pv JOIN FETCH pv.product WHERE ci.cart.id = :cartId")
    List<CartItem> findByCartIdWithProductDetails(@Param("cartId") Integer cartId);

    @Modifying
    @Query("DELETE FROM CartItem ci WHERE ci.cart.id = :cartId AND ci.productVariation.id = :productVariationId")
    void deleteByCartIdAndProductVariationId(@Param("cartId") Integer cartId,
                                            @Param("productVariationId") Integer productVariationId);

    @Modifying
    @Query("DELETE FROM CartItem ci WHERE ci.cart.id = :cartId")
    void deleteByCartId(@Param("cartId") Integer cartId);

    @Query("SELECT COUNT(ci) FROM CartItem ci WHERE ci.cart.id = :cartId")
    long countByCartId(@Param("cartId") Integer cartId);

    List<CartItem> findByProductVariationId(Integer productVariationId);

    @Query("SELECT SUM(ci.quantity) FROM CartItem ci WHERE ci.cart.id = :cartId")
    Integer sumQuantityByCartId(@Param("cartId") Integer cartId);
}
