package vn.techbox.techbox_store.payment.service;

import org.springframework.stereotype.Service;
import vn.techbox.techbox_store.order.dto.PaymentRequest;
import vn.techbox.techbox_store.order.dto.PaymentResponse;
import vn.techbox.techbox_store.order.model.Order;
import vn.techbox.techbox_store.payment.model.Payment;
import vn.techbox.techbox_store.payment.model.PaymentMethod;

@Service
public interface PaymentService {
    Payment initiatePayment(Order order);
    PaymentResponse generatePaymentUrl(PaymentRequest request);
    boolean verifyPayment(String transactionId, String signature);
    PaymentResponse cancelPayment(String transactionId);
    boolean supports(PaymentMethod paymentMethod);
    PaymentMethod getPaymentMethod();
}
