package vn.techbox.techbox_store.payment.repository;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import org.springframework.stereotype.Repository;
import vn.techbox.techbox_store.payment.model.Payment;
import vn.techbox.techbox_store.payment.model.PaymentMethod;
import vn.techbox.techbox_store.payment.model.PaymentStatus;

import java.math.BigDecimal;
import java.time.LocalDateTime;
import java.util.List;
import java.util.Optional;

@Repository
public interface PaymentRepository extends JpaRepository<Payment, Long> {

    Optional<Payment> findByPaymentTransactionId(String transactionId);

    @Query("SELECT opi FROM Payment opi WHERE opi.paymentStatus = :status")
    List<Payment> findByPaymentStatus(@Param("status") PaymentStatus status);

    @Query("SELECT opi FROM Payment opi WHERE opi.paymentMethod = :method")
    List<Payment> findByPaymentMethod(@Param("method") PaymentMethod method);

    @Query("SELECT opi FROM Payment opi WHERE opi.voucherCode = :voucherCode")
    List<Payment> findByVoucherCode(@Param("voucherCode") String voucherCode);

    @Query("SELECT SUM(opi.finalAmount) FROM Payment opi WHERE opi.paymentStatus = vn.techbox.techbox_store.payment.model.PaymentStatus.PAID AND opi.paymentCompletedAt BETWEEN :startDate AND :endDate")
    BigDecimal getTotalRevenueByDateRange(@Param("startDate") LocalDateTime startDate, @Param("endDate") LocalDateTime endDate);

    @Query("SELECT COUNT(opi) FROM Payment opi WHERE opi.paymentMethod = :method AND opi.paymentStatus = vn.techbox.techbox_store.payment.model.PaymentStatus.PAID")
    long countPaidOrdersByPaymentMethod(@Param("method") PaymentMethod method);
}
