package vn.techbox.techbox_store.payment.service;

import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Service;
import vn.techbox.techbox_store.inventory.service.InventoryReservationService;
import vn.techbox.techbox_store.order.model.Order;
import vn.techbox.techbox_store.order.model.OrderStatus;
import vn.techbox.techbox_store.order.repository.OrderRepository;
import vn.techbox.techbox_store.payment.model.Payment;
import vn.techbox.techbox_store.payment.model.PaymentStatus;
import vn.techbox.techbox_store.payment.model.VnpayPayment;
import vn.techbox.techbox_store.payment.repository.PaymentRepository;
import vn.techbox.techbox_store.payment.util.VnPayUtils;
import vn.techbox.techbox_store.voucher.service.VoucherReservationService;

import java.math.BigDecimal;
import java.time.LocalDateTime;
import java.util.HashMap;
import java.util.Map;
import java.util.Optional;

@Service
@RequiredArgsConstructor
@Slf4j
public class VnPayCallbackService {

    private final PaymentRepository paymentRepository;
    private final OrderRepository orderRepository;
    private final InventoryReservationService inventoryReservationService;
    private final VoucherReservationService voucherReservationService;

    @Value("${vnpay.hash-secret:}")
    private String hashSecret;

    public Map<String, String> handleIpn(Map<String, String> originalParams) {
        Map<String, String> params = new HashMap<>(originalParams);
        log.info("VNPay IPN received params: {}", params);

        String vnpSecureHash = params.remove("vnp_SecureHash");
        params.remove("vnp_SecureHashType");
        String canonical = VnPayUtils.buildCanonicalQuery(params);
        String expected = VnPayUtils.hmacSHA512(hashSecret, canonical);
        if (vnpSecureHash == null || !vnpSecureHash.equalsIgnoreCase(expected)) {
            return Map.of("RspCode", "97", "Message", "Invalid Signature");
        }

        String txnRef = params.get("vnp_TxnRef");
        String transactionId = txnRef != null ? "VNPAY_" + txnRef : null;
        String respCode = params.get("vnp_ResponseCode");
        String amountStr = params.get("vnp_Amount");

        if (transactionId == null) {
            return Map.of("RspCode", "01", "Message", "Order not found");
        }
        Optional<Payment> optPayment = paymentRepository.findByPaymentTransactionId(transactionId);
        if (optPayment.isEmpty()) {
            return Map.of("RspCode", "01", "Message", "Order not found");
        }
        Payment payment = optPayment.get();
        Optional<Order> optOrder = orderRepository.findByPaymentInfo_Id(payment.getId());
        if (optOrder.isEmpty()) {
            return Map.of("RspCode", "01", "Message", "Order not found");
        }
        Order order = optOrder.get();

        try {
            if (amountStr != null) {
                BigDecimal vnpAmount = new BigDecimal(amountStr).movePointLeft(2); // VNPAY amount is in cents
                if (payment.getFinalAmount() != null && payment.getFinalAmount().compareTo(vnpAmount) != 0) {
                    return Map.of("RspCode", "04", "Message", "Invalid amount");
                }
            }
        } catch (Exception e) {
            log.warn("Invalid vnp_Amount: {}", amountStr);
            return Map.of("RspCode", "04", "Message", "Invalid amount");
        }

        if (payment.getPaymentStatus() == PaymentStatus.PAID) {
            return Map.of("RspCode", "02", "Message", "Order already confirmed");
        }

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
            // Payment successful - confirm reservations
            try {
                inventoryReservationService.confirmReservations(order.getId().intValue());
                voucherReservationService.confirmReservations(order.getId().intValue());

                payment.setPaymentStatus(PaymentStatus.PAID);
                payment.setPaymentCompletedAt(LocalDateTime.now());
                order.setStatus(OrderStatus.CONFIRMED);

                log.info("Payment confirmed and reservations converted to actual usage for order: {}", order.getId());
            } catch (Exception e) {
                log.error("Failed to confirm reservations for order: {}", order.getId(), e);
                // If reservation confirmation fails, we should still mark payment as paid
                // but log the error for manual intervention
                payment.setPaymentStatus(PaymentStatus.PAID);
                payment.setPaymentCompletedAt(LocalDateTime.now());
                order.setStatus(OrderStatus.CONFIRMED);
            }
        } else {
            // Payment failed - release reservations
            try {
                inventoryReservationService.releaseReservations(order.getId().intValue());
                voucherReservationService.releaseReservations(order.getId().intValue());

                payment.setPaymentStatus(PaymentStatus.FAILED);
                payment.setPaymentFailedAt(LocalDateTime.now());

                log.info("Payment failed and reservations released for order: {}", order.getId());
            } catch (Exception e) {
                log.error("Failed to release reservations for failed payment, order: {}", order.getId(), e);
                // Still mark payment as failed even if reservation release fails
                payment.setPaymentStatus(PaymentStatus.FAILED);
                payment.setPaymentFailedAt(LocalDateTime.now());
            }
        }
        paymentRepository.save(payment);
        orderRepository.save(order);

        return Map.of("RspCode", "00", "Message", "Confirm Success");
    }
}
