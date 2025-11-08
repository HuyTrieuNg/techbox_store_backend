package vn.techbox.techbox_store.payment.service.impl;

import org.springframework.stereotype.Service;
import vn.techbox.techbox_store.order.dto.PaymentRequest;
import vn.techbox.techbox_store.order.dto.PaymentResponse;
import vn.techbox.techbox_store.order.model.Order;
import vn.techbox.techbox_store.payment.model.CodPayment;
import vn.techbox.techbox_store.payment.model.Payment;
import vn.techbox.techbox_store.payment.model.PaymentMethod;
import vn.techbox.techbox_store.payment.model.PaymentStatus;
import vn.techbox.techbox_store.payment.repository.PaymentRepository;
import vn.techbox.techbox_store.payment.service.PaymentService;

import java.util.UUID;

@Service
public class CodPaymentService implements PaymentService {

    private final PaymentRepository paymentRepository;

    public CodPaymentService(PaymentRepository paymentRepository) {
        this.paymentRepository = paymentRepository;
    }

    @Override
    public Payment initiatePayment(Order order) {
        CodPayment payment = CodPayment.builder()
                .build();
        payment.setPaymentMethod(PaymentMethod.COD);
        payment.setPaymentStatus(PaymentStatus.PENDING);
        return payment;
    }

    @Override
    public PaymentResponse generatePaymentUrl(PaymentRequest request) {
        PaymentResponse response = new PaymentResponse();
        String transactionId = "COD_" + UUID.randomUUID();
        response.setTransactionId(transactionId);
        response.setStatus("PENDING");
        response.setMessage("Đơn hàng COD đã được tạo thành công. Thanh toán khi nhận hàng.");
        response.setPaymentUrl(null);
        return response;
    }

    @Override
    public boolean verifyPayment(String transactionId, String signature) {
        return transactionId != null && transactionId.startsWith("COD_");
    }

    @Override
    public PaymentResponse cancelPayment(String transactionId) {
        PaymentResponse response = new PaymentResponse();
        response.setTransactionId(transactionId);
        response.setStatus("CANCELLED");
        response.setMessage("Đơn hàng COD đã được hủy.");
        return response;
    }

    @Override
    public boolean supports(PaymentMethod paymentMethod) {
        return PaymentMethod.COD.equals(paymentMethod);
    }

    @Override
    public PaymentMethod getPaymentMethod() {
        return PaymentMethod.COD;
    }
}
