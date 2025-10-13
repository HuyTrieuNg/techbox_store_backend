package vn.techbox.techbox_store.payment.model;

import jakarta.persistence.*;
import lombok.Data;
import vn.techbox.techbox_store.order.model.Order;

import java.time.LocalDateTime;

@Entity
@Table(name = "payment_transactions", indexes = {
        @Index(name = "idx_payment_order_id", columnList = "order_id")
}) 
// thêm index để tối ưu truy vấn theo order_id
@Data
public class PaymentTransaction {
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @Column(nullable = false, unique = true)
    private String txnRef;

    @Column(nullable = false)
    private Long amount;

    @Column(nullable = false)
    private String orderInfo;

  // liên kết tới Order entity (tạo FK order_id)
    @OneToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "order_id", unique = true)
    private Order order;

    @Column(nullable = false)
    private String responseCode;

    private String transactionNo;

    private String bankCode;

    private String bankTranNo;

    private String cardType;

    @Column(nullable = false)
    @Enumerated(EnumType.STRING)
    private PaymentStatus status;

    @Column(nullable = false)
    private LocalDateTime createdAt;

    private LocalDateTime paidAt;

    @PrePersist
    protected void onCreate() {
        createdAt = LocalDateTime.now();
    }

    public enum PaymentStatus {
        PENDING,
        SUCCESS,
        FAILED,
        CANCELLED
    }
}