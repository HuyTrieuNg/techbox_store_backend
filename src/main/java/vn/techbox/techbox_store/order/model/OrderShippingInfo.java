package vn.techbox.techbox_store.order.model;

import jakarta.persistence.*;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

@Entity
@Table(name = "order_shipping_info")
@Data
@Builder
@NoArgsConstructor
@AllArgsConstructor
public class OrderShippingInfo {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @Column(name = "shipping_name", nullable = false, length = 100)
    private String shippingName;

    @Column(name = "shipping_phone", nullable = false, length = 20)
    private String shippingPhone;

    @Column(name = "shipping_email", length = 100)
    private String shippingEmail;

    @Column(name = "shipping_address", nullable = false, columnDefinition = "TEXT")
    private String shippingAddress;

    @Column(name = "shipping_ward", length = 100)
    private String shippingWard;

    @Column(name = "shipping_district", length = 100)
    private String shippingDistrict;

    @Column(name = "shipping_city", length = 100)
    private String shippingCity;

    @Column(name = "shipping_postal_code", length = 20)
    private String shippingPostalCode;

    @Builder.Default
    @Column(name = "shipping_country", length = 50)
    private String shippingCountry = "Vietnam";

    @Builder.Default
    @Column(name = "shipping_method", length = 50)
    private String shippingMethod = "STANDARD"; // STANDARD, EXPRESS, SAME_DAY

    @Column(name = "estimated_delivery_date")
    private java.time.LocalDate estimatedDeliveryDate;

    @Column(name = "actual_delivery_date")
    private java.time.LocalDateTime actualDeliveryDate;

    @Column(name = "tracking_number", length = 100)
    private String trackingNumber;

    @Column(name = "carrier", length = 50)
    private String carrier;

    @Column(name = "delivery_instructions", columnDefinition = "TEXT")
    private String deliveryInstructions;
}
