package vn.techbox.techbox_store.order.repository;

import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import org.springframework.stereotype.Repository;
import vn.techbox.techbox_store.order.model.Order;
import vn.techbox.techbox_store.order.model.OrderStatus;

import java.util.List;
import java.util.Optional;

@Repository
public interface OrderRepository extends JpaRepository<Order, Long> {

    Optional<Order> findByOrderCode(String orderCode);

    Page<Order> findByUser_IdOrderByCreatedAtDesc(Integer userId, Pageable pageable);

    Page<Order> findByStatus(OrderStatus status, Pageable pageable);

    Page<Order> findAllByOrderByCreatedAtDesc(Pageable pageable);

    // Find all orders except those with the given status (e.g., exclude PENDING)
    Page<Order> findByStatusNotOrderByCreatedAtDesc(OrderStatus status, Pageable pageable);

    @Query("SELECT o FROM Order o WHERE o.user.id = :userId AND o.status = :status ORDER BY o.createdAt DESC")
    Page<Order> findByUserIdAndStatusOrderByCreatedAtDesc(@Param("userId") Integer userId, @Param("status") OrderStatus status, Pageable pageable);

    @Query("SELECT COUNT(o) FROM Order o WHERE o.user.id = :userId AND o.status = :status")
    long countByUserIdAndStatus(@Param("userId") Integer userId, @Param("status") OrderStatus status);

    boolean existsByOrderCode(String orderCode);

    Optional<Order> findByPaymentInfo_Id(Long paymentInfoId);

    @Query("SELECT o FROM Order o LEFT JOIN FETCH o.orderItems oi LEFT JOIN FETCH oi.productVariation pv LEFT JOIN FETCH pv.product WHERE o.user.id = :userId ORDER BY o.createdAt DESC")
    Page<Order> findTopOrdersByUserId(@Param("userId") Integer userId, Pageable pageable);
}
