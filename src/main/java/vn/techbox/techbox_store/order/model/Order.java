package vn.techbox.techbox_store.order.model;

import jakarta.persistence.*;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;
import vn.techbox.techbox_store.payment.model.Payment;
import vn.techbox.techbox_store.payment.model.PaymentMethod;
import vn.techbox.techbox_store.payment.model.PaymentStatus;
import vn.techbox.techbox_store.user.model.User;

import java.time.LocalDateTime;
import java.util.List;

@Entity
@Table(name = "orders")
@Data
@Builder
@NoArgsConstructor
@AllArgsConstructor
public class Order {
    
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;
    
    @Column(name = "order_code", unique = true, nullable = false)
    private String orderCode;
    
    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "user_id", nullable = false)
    private User user;

    @Enumerated(EnumType.STRING)
    @Column(name = "status", nullable = false)
    private OrderStatus status;

    @Column(name = "note", columnDefinition = "TEXT")
    private String note;

    @Column(name = "created_at", nullable = false)
    private LocalDateTime createdAt;

    @Column(name = "updated_at")
    private LocalDateTime updatedAt;

    // Relationships with separated entities - Tối ưu lazy loading
    @OneToOne(cascade = CascadeType.ALL, fetch = FetchType.LAZY, optional = false)
    @JoinColumn(name = "shipping_info_id")
    private OrderShippingInfo shippingInfo;

    @OneToOne(cascade = CascadeType.ALL, fetch = FetchType.LAZY, optional = false)
    @JoinColumn(name = "payment_info_id")
    private Payment paymentInfo;

    @OneToMany(mappedBy = "order", cascade = CascadeType.ALL, fetch = FetchType.LAZY)
    private List<OrderItem> orderItems;

    @PrePersist
    protected void onCreate() {
        createdAt = LocalDateTime.now();
        updatedAt = LocalDateTime.now();
    }

    @PreUpdate
    protected void onUpdate() {
        updatedAt = LocalDateTime.now();
    }

    public PaymentMethod getPaymentMethod() {
        return paymentInfo != null ? paymentInfo.getPaymentMethod() : null;
    }

    public String getPaymentStatus() {
        return paymentInfo != null && paymentInfo.getPaymentStatus() != null
                ? paymentInfo.getPaymentStatus().name()
                : PaymentStatus.PENDING.name();
    }

    public String getShippingName() {
        return shippingInfo != null ? shippingInfo.getShippingName() : null;
    }

    public String getShippingPhone() {
        return shippingInfo != null ? shippingInfo.getShippingPhone() : null;
    }

    public String getShippingAddress() {
        return shippingInfo != null ? shippingInfo.getShippingAddress() : null;
    }

    public Integer getUserId() {
        return user != null ? user.getId() : null;
    }

    public String getVoucherCode() {
        return paymentInfo != null ? paymentInfo.getVoucherCode() : null;
    }
}
