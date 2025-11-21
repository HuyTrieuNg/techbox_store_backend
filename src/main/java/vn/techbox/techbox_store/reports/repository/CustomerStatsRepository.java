package vn.techbox.techbox_store.reports.repository;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import org.springframework.stereotype.Repository;
import vn.techbox.techbox_store.reports.dto.CustomerGrowthDTO;
import vn.techbox.techbox_store.reports.dto.TopCustomerDTO;
import vn.techbox.techbox_store.user.model.User;

import java.time.LocalDateTime;
import java.util.List;

@Repository
public interface CustomerStatsRepository extends JpaRepository<User, Integer> {

    /**
     * Get total count of active customers (excluding soft-deleted)
     */
    @Query("SELECT COUNT(u) FROM User u WHERE u.account.isActive = true AND u.deletedAt IS NULL")
    Long countTotalCustomers();

    /**
     * Get count of new customers registered after a specific date
     */
    @Query("SELECT COUNT(u) FROM User u WHERE u.account.isActive = true AND u.deletedAt IS NULL AND u.createdAt >= :startDate")
    Long countNewCustomersSince(@Param("startDate") LocalDateTime startDate);

    /**
     * Get count of new customers within a date range
     */
    @Query("SELECT COUNT(u) FROM User u WHERE u.account.isActive = true AND u.deletedAt IS NULL AND u.createdAt BETWEEN :startDate AND :endDate")
    Long countNewCustomersBetween(@Param("startDate") LocalDateTime startDate, @Param("endDate") LocalDateTime endDate);

    /**
     * Get top customers by total order value
     * Returns customer info along with their total spending and order count
     */
    @Query("""
        SELECT new vn.techbox.techbox_store.reports.dto.TopCustomerDTO(
            u.id,
            u.firstName,
            u.lastName,
            u.account.email,
            CAST(COUNT(o.id) AS long),
            CAST(COALESCE(SUM(p.totalAmount), 0.0) AS double),
            MAX(o.createdAt)
        )
        FROM User u
        LEFT JOIN Order o ON o.user.id = u.id
        LEFT JOIN Payment p ON p.id = o.paymentInfo.id
        WHERE u.account.isActive = true AND u.deletedAt IS NULL
        GROUP BY u.id, u.firstName, u.lastName, u.account.email
        HAVING COUNT(o.id) > 0
        ORDER BY SUM(p.totalAmount) DESC
        LIMIT :limit
    """)
    List<TopCustomerDTO> findTopCustomersBySpending(@Param("limit") int limit);

    /**
     * Get customer growth trends by month
     * Returns the number of new customers registered each month
     */
    @Query("""
        SELECT new vn.techbox.techbox_store.reports.dto.CustomerGrowthDTO(
            CAST(FUNCTION('TO_CHAR', u.createdAt, 'YYYY-MM') AS string),
            COUNT(u.id)
        )
        FROM User u
        WHERE u.account.isActive = true 
        AND u.deletedAt IS NULL 
        AND u.createdAt BETWEEN :startDate AND :endDate
        GROUP BY FUNCTION('TO_CHAR', u.createdAt, 'YYYY-MM')
        ORDER BY FUNCTION('TO_CHAR', u.createdAt, 'YYYY-MM')
    """)
    List<CustomerGrowthDTO> findCustomerGrowthByMonth(
        @Param("startDate") LocalDateTime startDate,
        @Param("endDate") LocalDateTime endDate
    );

    /**
     * Get customer growth trends by week
     */
    @Query("""
        SELECT new vn.techbox.techbox_store.reports.dto.CustomerGrowthDTO(
            CAST(FUNCTION('TO_CHAR', u.createdAt, 'IYYY-IW') AS string),
            COUNT(u.id)
        )
        FROM User u
        WHERE u.account.isActive = true 
        AND u.deletedAt IS NULL 
        AND u.createdAt BETWEEN :startDate AND :endDate
        GROUP BY FUNCTION('TO_CHAR', u.createdAt, 'IYYY-IW')
        ORDER BY FUNCTION('TO_CHAR', u.createdAt, 'IYYY-IW')
    """)
    List<CustomerGrowthDTO> findCustomerGrowthByWeek(
        @Param("startDate") LocalDateTime startDate,
        @Param("endDate") LocalDateTime endDate
    );

    /**
     * Get customer growth trends by day
     */
    @Query("""
        SELECT new vn.techbox.techbox_store.reports.dto.CustomerGrowthDTO(
            CAST(FUNCTION('TO_CHAR', u.createdAt, 'YYYY-MM-DD') AS string),
            COUNT(u.id)
        )
        FROM User u
        WHERE u.account.isActive = true 
        AND u.deletedAt IS NULL 
        AND u.createdAt BETWEEN :startDate AND :endDate
        GROUP BY FUNCTION('TO_CHAR', u.createdAt, 'YYYY-MM-DD')
        ORDER BY FUNCTION('TO_CHAR', u.createdAt, 'YYYY-MM-DD')
    """)
    List<CustomerGrowthDTO> findCustomerGrowthByDay(
        @Param("startDate") LocalDateTime startDate,
        @Param("endDate") LocalDateTime endDate
    );
}
