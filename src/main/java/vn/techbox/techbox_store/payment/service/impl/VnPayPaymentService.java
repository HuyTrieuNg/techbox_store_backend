package vn.techbox.techbox_store.payment.service.impl;

import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Service;
import vn.techbox.techbox_store.order.dto.PaymentRequest;
import vn.techbox.techbox_store.order.dto.PaymentResponse;
import vn.techbox.techbox_store.order.model.Order;
import vn.techbox.techbox_store.payment.model.PaymentMethod;
import vn.techbox.techbox_store.payment.model.VnpayPayment;
import vn.techbox.techbox_store.payment.model.Payment;
import vn.techbox.techbox_store.payment.service.PaymentService;
import vn.techbox.techbox_store.payment.util.VnPayUtils;

import java.text.SimpleDateFormat;
import java.util.*;

@Service
public class VnPayPaymentService implements PaymentService {

    @Value("${vnpay.tmn-code:}")
    private String tmnCode;

    @Value("${vnpay.hash-secret:}")
    private String hashSecret;

    @Value("${vnpay.url:}")
    private String vnpayUrl;

    @Value("${vnpay.return-url:}")
    private String defaultReturnUrl;

    @Override
    public Payment initiatePayment(Order order) {
        VnpayPayment payment = VnpayPayment.builder().build();
        payment.setPaymentMethod(PaymentMethod.VNPAY);
        payment.setPaymentStatus("PENDING");
        return payment;
    }

    @Override
    public PaymentResponse generatePaymentUrl(PaymentRequest request) {
        PaymentResponse response = new PaymentResponse();

        String txnRef = generateTxnRef();
        String orderInfo = "Payment for Order " + request.getOrderId();
        String returnUrl = request.getReturnUrl() != null ? request.getReturnUrl() : defaultReturnUrl;

        Map<String, String> vnpParams = new HashMap<>();
        vnpParams.put("vnp_Version", "2.1.0");
        vnpParams.put("vnp_Command", "pay");
        vnpParams.put("vnp_TmnCode", tmnCode);
        vnpParams.put("vnp_Amount", request.getAmount().multiply(new java.math.BigDecimal(100)).toBigInteger().toString());
        vnpParams.put("vnp_CurrCode", "VND");
        vnpParams.put("vnp_TxnRef", txnRef);
        vnpParams.put("vnp_OrderInfo", orderInfo);
        vnpParams.put("vnp_OrderType", "other");
        vnpParams.put("vnp_Locale", "vn");
        vnpParams.put("vnp_ReturnUrl", returnUrl);
        vnpParams.put("vnp_IpAddr", "0.0.0.0");
        vnpParams.put("vnp_CreateDate", new SimpleDateFormat("yyyyMMddHHmmss").format(new Date()));

        Calendar cal = Calendar.getInstance();
        cal.add(Calendar.MINUTE, 15);
        vnpParams.put("vnp_ExpireDate", new SimpleDateFormat("yyyyMMddHHmmss").format(cal.getTime()));

        String canonicalQuery = VnPayUtils.buildCanonicalQuery(vnpParams);
        String secureHash = VnPayUtils.hmacSHA512(hashSecret, canonicalQuery);

        String paymentUrl = vnpayUrl + "?" + canonicalQuery + "&vnp_SecureHash=" + secureHash;

        response.setTransactionId("VNPAY_" + txnRef);
        response.setStatus("PENDING");
        response.setMessage("Redirecting to VNPay payment gateway...");
        response.setPaymentUrl(paymentUrl);
        return response;
    }

    @Override
    public boolean verifyPayment(String transactionId, String signature) {
        return transactionId != null && transactionId.startsWith("VNPAY_");
    }

    @Override
    public PaymentResponse cancelPayment(String transactionId) {
        PaymentResponse response = new PaymentResponse();
        response.setTransactionId(transactionId);
        response.setStatus("CANCELLED");
        response.setMessage("VNPay payment has been cancelled.");
        return response;
    }

    @Override
    public boolean supports(PaymentMethod paymentMethod) {
        return PaymentMethod.VNPAY.equals(paymentMethod);
    }

    @Override
    public PaymentMethod getPaymentMethod() {
        return PaymentMethod.VNPAY;
    }

    private String generateTxnRef() {
        return UUID.randomUUID().toString().replace("-", "").substring(0, 12).toUpperCase();
    }
}
