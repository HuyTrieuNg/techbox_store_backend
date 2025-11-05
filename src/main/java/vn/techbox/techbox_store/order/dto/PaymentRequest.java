package vn.techbox.techbox_store.order.dto;

import lombok.Data;
import vn.techbox.techbox_store.payment.model.PaymentMethod;

import java.math.BigDecimal;

@Data
public class PaymentRequest {
    private Long orderId;
    private PaymentMethod paymentMethod;
    private BigDecimal amount;
    private String returnUrl;
    private String cancelUrl;
}


