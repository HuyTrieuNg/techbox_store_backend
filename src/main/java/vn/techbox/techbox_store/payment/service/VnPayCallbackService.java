package vn.techbox.techbox_store.payment.service;

import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Service;
import vn.techbox.techbox_store.order.model.Order;
import vn.techbox.techbox_store.order.model.OrderStatus;
import vn.techbox.techbox_store.order.repository.OrderRepository;
import vn.techbox.techbox_store.payment.model.Payment;
import vn.techbox.techbox_store.payment.model.VnpayPayment;
import vn.techbox.techbox_store.payment.repository.PaymentRepository;
import vn.techbox.techbox_store.payment.util.VnPayUtils;

import java.math.BigDecimal;
import java.time.LocalDateTime;
import java.util.*;

@Service
@RequiredArgsConstructor
@Slf4j
public class VnPayCallbackService {

    private final PaymentRepository paymentRepository;
    private final OrderRepository orderRepository;

    @Value("${vnpay.hash-secret:}")
    private String hashSecret;

    public Map<String, Object> handleResult(Map<String, String> params, boolean isIpn) {
        log.info("VNPay {} received params: {}", isIpn ? "IPN" : "Callback", params);
        Map<String, Object> response = new HashMap<>();

        // Extract and remove signature parameters from the map used for hashing
        String vnpSecureHash = params.remove("vnp_SecureHash");
        params.remove("vnp_SecureHashType");

        // Verify signature
        String canonical = VnPayUtils.buildCanonicalQuery(params);
        String expected = VnPayUtils.hmacSHA512(hashSecret, canonical);
        if (vnpSecureHash == null || !vnpSecureHash.equalsIgnoreCase(expected)) {
            response.put("success", false);
            response.put("RspCode", "97");
            response.put("Message", "Invalid Signature");
            return response;
        }

        String txnRef = params.get("vnp_TxnRef");
        String transactionId = txnRef != null ? "VNPAY_" + txnRef : null;
        String respCode = params.get("vnp_ResponseCode");
        String amountStr = params.get("vnp_Amount");

        if (transactionId == null) {
            response.put("success", false);
            response.put("RspCode", "24");
            response.put("Message", "Missing TxnRef");
            return response;
        }

        Optional<Payment> optPayment = paymentRepository.findByPaymentTransactionId(transactionId);
        if (optPayment.isEmpty()) {
            response.put("success", false);
            response.put("RspCode", "01");
            response.put("Message", "Order not found");
            return response;
        }

        Payment payment = optPayment.get();
        Optional<Order> optOrder = orderRepository.findByPaymentInfo_Id(payment.getId());
        if (optOrder.isEmpty()) {
            response.put("success", false);
            response.put("RspCode", "01");
            response.put("Message", "Order not found");
            return response;
        }
        Order order = optOrder.get();

        // Idempotency
        if ("PAID".equalsIgnoreCase(payment.getPaymentStatus())) {
            response.put("success", true);
            response.put("RspCode", "00");
            response.put("Message", "Order already paid");
            response.put("orderCode", order.getOrderCode());
            response.put("transactionId", transactionId);
            return response;
        }

        // Amount check
        try {
            if (amountStr != null) {
                BigDecimal vnpAmount = new BigDecimal(amountStr).divide(new BigDecimal(100));
                if (payment.getFinalAmount() != null && payment.getFinalAmount().compareTo(vnpAmount) != 0) {
                    response.put("success", false);
                    response.put("RspCode", "04");
                    response.put("Message", "Invalid Amount");
                    return response;
                }
            }
        } catch (Exception e) {
            log.warn("Invalid vnp_Amount: {}", amountStr);
        }

        // Update payment details if VNPAY payment
        if (payment instanceof VnpayPayment vnp) {
            vnp.setVnpTxnRef(txnRef);
            vnp.setVnpResponseCode(respCode);
            vnp.setVnpBankCode(params.get("vnp_BankCode"));
            vnp.setVnpOrderInfo(params.get("vnp_OrderInfo"));
            vnp.setVnpTransactionNo(params.get("vnp_TransactionNo"));
            vnp.setVnpSecureHash(vnpSecureHash);
            try {
                String payDate = params.get("vnp_PayDate");
                if (payDate != null && payDate.length() == 14) {
                    var dt = java.time.LocalDateTime.parse(payDate, java.time.format.DateTimeFormatter.ofPattern("yyyyMMddHHmmss"));
                    vnp.setVnpPaymentDate(dt);
                }
            } catch (Exception ignore) {}
        }

        if ("00".equals(respCode)) {
            // Success
            payment.setPaymentStatus("PAID");
            payment.setPaymentCompletedAt(LocalDateTime.now());
            order.setStatus(OrderStatus.CONFIRMED);
            paymentRepository.save(payment);
            orderRepository.save(order);

            response.put("success", true);
            response.put("RspCode", "00");
            response.put("Message", "Payment confirmed successfully");
        } else {
            // Failure -> rollback
            payment.setPaymentStatus("FAILED");
            payment.setPaymentFailedAt(LocalDateTime.now());
            payment.setPaymentFailureReason("VNPay response: " + respCode);
            order.setStatus(OrderStatus.CANCELLED);
            paymentRepository.save(payment);
            orderRepository.save(order);

            response.put("success", false);
            response.put("RspCode", "24");
            response.put("Message", "Payment failed with code: " + respCode);
        }

        response.put("orderCode", order.getOrderCode());
        response.put("transactionId", transactionId);
        return response;
    }

    public Map<String, String> handleIpn(Map<String, String> params) {
        Map<String, Object> result = handleResult(new HashMap<>(params), true);
        boolean success = Boolean.TRUE.equals(result.get("success"));
        String rspCode = (String) result.getOrDefault("RspCode", success ? "00" : "97");
        String message = (String) result.getOrDefault("Message", success ? "Confirm Success" : "Invalid Signature");
        return Map.of("RspCode", rspCode, "Message", message);
    }
}

