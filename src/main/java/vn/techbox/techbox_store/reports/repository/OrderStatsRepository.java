package vn.techbox.techbox_store.reports.repository;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import org.springframework.stereotype.Repository;
import vn.techbox.techbox_store.order.model.Order;
import vn.techbox.techbox_store.order.model.OrderStatus;
import vn.techbox.techbox_store.reports.dto.OrderByStatusDTO;
import vn.techbox.techbox_store.reports.dto.RevenueTrendDTO;

import java.math.BigDecimal;
import java.time.LocalDateTime;
import java.util.List;

@Repository
public interface OrderStatsRepository extends JpaRepository<Order, Long> {

    /**
     * Count total orders
     */
    @Query("SELECT COUNT(o) FROM Order o")
    Long countTotalOrders();

    /**
     * Count orders by status
     */
    @Query("SELECT COUNT(o) FROM Order o WHERE o.status = :status")
    Long countOrdersByStatus(@Param("status") OrderStatus status);

    /**
     * Get orders grouped by status
     */
    @Query("""
        SELECT new vn.techbox.techbox_store.reports.dto.OrderByStatusDTO(
            CAST(o.status AS string),
            CAST(COUNT(o.id) AS long)
        )
        FROM Order o
        GROUP BY o.status
        ORDER BY COUNT(o.id) DESC
    """)
    List<OrderByStatusDTO> findOrdersByStatus();

    /**
     * Calculate total revenue (sum of all payment amounts)
     */
    @Query("""
        SELECT COALESCE(SUM(p.totalAmount), 0)
        FROM Order o
        JOIN Payment p ON p.id = o.paymentInfo.id
        WHERE o.status != vn.techbox.techbox_store.order.model.OrderStatus.CANCELLED
    """)
    BigDecimal calculateTotalRevenue();

    /**
     * Calculate total revenue for a date range
     */
    @Query("""
        SELECT COALESCE(SUM(p.totalAmount), 0)
        FROM Order o
        JOIN Payment p ON p.id = o.paymentInfo.id
        WHERE o.status != vn.techbox.techbox_store.order.model.OrderStatus.CANCELLED
        AND o.createdAt BETWEEN :startDate AND :endDate
    """)
    BigDecimal calculateRevenueByDateRange(
        @Param("startDate") LocalDateTime startDate,
        @Param("endDate") LocalDateTime endDate
    );

    /**
     * Calculate average order value
     */
    @Query("""
        SELECT COALESCE(AVG(p.totalAmount), 0)
        FROM Order o
        JOIN Payment p ON p.id = o.paymentInfo.id
        WHERE o.status != vn.techbox.techbox_store.order.model.OrderStatus.CANCELLED
    """)
    BigDecimal calculateAverageOrderValue();

    /**
     * Get revenue trends by day
     */
    @Query("""
        SELECT new vn.techbox.techbox_store.reports.dto.RevenueTrendDTO(
            CAST(FUNCTION('TO_CHAR', o.createdAt, 'YYYY-MM-DD') AS string),
            COUNT(o.id),
            COALESCE(SUM(p.totalAmount), 0)
        )
        FROM Order o
        JOIN Payment p ON p.id = o.paymentInfo.id
        WHERE o.status != vn.techbox.techbox_store.order.model.OrderStatus.CANCELLED
        AND o.createdAt BETWEEN :startDate AND :endDate
        GROUP BY FUNCTION('TO_CHAR', o.createdAt, 'YYYY-MM-DD')
        ORDER BY FUNCTION('TO_CHAR', o.createdAt, 'YYYY-MM-DD')
    """)
    List<RevenueTrendDTO> findRevenueTrendsByDay(
        @Param("startDate") LocalDateTime startDate,
        @Param("endDate") LocalDateTime endDate
    );

    /**
     * Get revenue trends by week
     */
    @Query("""
        SELECT new vn.techbox.techbox_store.reports.dto.RevenueTrendDTO(
            CAST(FUNCTION('TO_CHAR', o.createdAt, 'IYYY-IW') AS string),
            COUNT(o.id),
            COALESCE(SUM(p.totalAmount), 0)
        )
        FROM Order o
        JOIN Payment p ON p.id = o.paymentInfo.id
        WHERE o.status != vn.techbox.techbox_store.order.model.OrderStatus.CANCELLED
        AND o.createdAt BETWEEN :startDate AND :endDate
        GROUP BY FUNCTION('TO_CHAR', o.createdAt, 'IYYY-IW')
        ORDER BY FUNCTION('TO_CHAR', o.createdAt, 'IYYY-IW')
    """)
    List<RevenueTrendDTO> findRevenueTrendsByWeek(
        @Param("startDate") LocalDateTime startDate,
        @Param("endDate") LocalDateTime endDate
    );

    /**
     * Get revenue trends by month
     */
    @Query("""
        SELECT new vn.techbox.techbox_store.reports.dto.RevenueTrendDTO(
            CAST(FUNCTION('TO_CHAR', o.createdAt, 'YYYY-MM') AS string),
            COUNT(o.id),
            COALESCE(SUM(p.totalAmount), 0)
        )
        FROM Order o
        JOIN Payment p ON p.id = o.paymentInfo.id
        WHERE o.status != vn.techbox.techbox_store.order.model.OrderStatus.CANCELLED
        AND o.createdAt BETWEEN :startDate AND :endDate
        GROUP BY FUNCTION('TO_CHAR', o.createdAt, 'YYYY-MM')
        ORDER BY FUNCTION('TO_CHAR', o.createdAt, 'YYYY-MM')
    """)
    List<RevenueTrendDTO> findRevenueTrendsByMonth(
        @Param("startDate") LocalDateTime startDate,
        @Param("endDate") LocalDateTime endDate
    );
}
