package vn.techbox.techbox_store.payment.service;

import jakarta.servlet.http.HttpServletRequest;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import vn.techbox.techbox_store.order.model.Order;
import vn.techbox.techbox_store.order.repository.OrderRepository;
import vn.techbox.techbox_store.payment.config.VNPayConfig;
import vn.techbox.techbox_store.payment.model.PaymentTransaction;
import vn.techbox.techbox_store.payment.repository.PaymentTransactionRepository;
import vn.techbox.techbox_store.payment.util.VNPayUtil;

import java.net.URLEncoder;
import java.nio.charset.StandardCharsets;
import java.text.SimpleDateFormat;
import java.time.LocalDateTime;
import java.util.*;

@Service
@RequiredArgsConstructor
public class VNPayService {

    private final VNPayConfig vnPayConfig;
    private final PaymentTransactionRepository paymentRepository;
    private final OrderRepository orderRepository;


    public String createPaymentUrl(Long amount, String orderInfo, Long orderId, HttpServletRequest request) {

        // TODO Kiểm tra tồn tại của orderId
        // Giả sử orderId hợp lệ và lấy thông tin order từ DB
        
        Map<String, String> vnpParams = new HashMap<>();
        vnpParams.put("vnp_Version", vnPayConfig.getVersion());
        vnpParams.put("vnp_Command", vnPayConfig.getCommand());
        vnpParams.put("vnp_TmnCode", vnPayConfig.getTmnCode());
        vnpParams.put("vnp_Amount", String.valueOf(amount * 100));
        vnpParams.put("vnp_CurrCode", "VND");

        String vnpTxnRef = VNPayUtil.getRandomNumber(8);
        vnpParams.put("vnp_TxnRef", vnpTxnRef);

        vnpParams.put("vnp_OrderInfo", orderInfo);
        vnpParams.put("vnp_OrderType", vnPayConfig.getOrderType());
        vnpParams.put("vnp_Locale", "vn");
        vnpParams.put("vnp_ReturnUrl", vnPayConfig.getReturnUrl());
        vnpParams.put("vnp_IpAddr", VNPayUtil.getIpAddress(request));

        Calendar cld = Calendar.getInstance(TimeZone.getTimeZone("Etc/GMT+7"));
        SimpleDateFormat formatter = new SimpleDateFormat("yyyyMMddHHmmss");
        String vnpCreateDate = formatter.format(cld.getTime());
        vnpParams.put("vnp_CreateDate", vnpCreateDate);

        cld.add(Calendar.MINUTE, 15);
        String vnpExpireDate = formatter.format(cld.getTime());
        vnpParams.put("vnp_ExpireDate", vnpExpireDate);


        // --- TẠO VÀ LƯU PaymentTransaction VÀO DB  ----
        PaymentTransaction tx = new PaymentTransaction();
        tx.setTxnRef(vnpTxnRef);
        tx.setAmount(amount);
        tx.setOrderInfo(orderInfo);
        tx.setResponseCode("");
        tx.setStatus(PaymentTransaction.PaymentStatus.PENDING);
        if (orderId == null) {
            throw new IllegalArgumentException("orderId is required for online payment");
        }
        Optional<Order> optOrder = orderRepository.findById(orderId);
        if (optOrder.isEmpty()) {
            throw new IllegalArgumentException("Order not found: " + orderId);
        }
        tx.setOrder(optOrder.get()); // set owning side -> sẽ lưu order_id
        paymentRepository.save(tx);
        // ------------------------------------------------




        List<String> fieldNames = new ArrayList<>(vnpParams.keySet());
        Collections.sort(fieldNames);

        StringBuilder hashData = new StringBuilder();
        StringBuilder query = new StringBuilder();

        for (String fieldName : fieldNames) {
            String fieldValue = vnpParams.get(fieldName);
            if (fieldValue != null && !fieldValue.isEmpty()) {
                hashData.append(fieldName).append('=').append(URLEncoder.encode(fieldValue, StandardCharsets.US_ASCII));
                query.append(URLEncoder.encode(fieldName, StandardCharsets.US_ASCII))
                        .append('=')
                        .append(URLEncoder.encode(fieldValue, StandardCharsets.US_ASCII));

                if (fieldNames.indexOf(fieldName) < fieldNames.size() - 1) {
                    hashData.append('&');
                    query.append('&');
                }
            }
        }

        String vnpSecureHash = VNPayUtil.hmacSHA512(vnPayConfig.getHashSecret(), hashData.toString());
        query.append("&vnp_SecureHash=").append(vnpSecureHash);

        return vnPayConfig.getUrl() + "?" + query.toString();
    }

    public boolean verifyPayment(Map<String, String> params) {
        String vnpSecureHash = params.get("vnp_SecureHash");
        params.remove("vnp_SecureHash");
        params.remove("vnp_SecureHashType");

        List<String> fieldNames = new ArrayList<>(params.keySet());
        Collections.sort(fieldNames);

        StringBuilder hashData = new StringBuilder();
        for (String fieldName : fieldNames) {
            String fieldValue = params.get(fieldName);
            if (fieldValue != null && !fieldValue.isEmpty()) {
                hashData.append(fieldName).append('=').append(URLEncoder.encode(fieldValue, StandardCharsets.US_ASCII));
                if (fieldNames.indexOf(fieldName) < fieldNames.size() - 1) {
                    hashData.append('&');
                }
            }
        }

        String signValue = VNPayUtil.hmacSHA512(vnPayConfig.getHashSecret(), hashData.toString());
        return signValue.equals(vnpSecureHash);
    }

    // Method xử lý IPN callback
    @Transactional
    public Map<String, String> processIpn(Map<String, String> params) {
        String txnRef = params.get("vnp_TxnRef");
        String responseCode = params.get("vnp_ResponseCode");

        // Verify signature
        if (!verifyPayment(new HashMap<>(params))) {
            return createIpnResponse("97", "Invalid signature");
        }

        // Find transaction
        Optional<PaymentTransaction> transactionOpt = paymentRepository.findByTxnRef(txnRef);
        if (transactionOpt.isEmpty()) {
            return createIpnResponse("01", "Order not found");
        }

        PaymentTransaction transaction = transactionOpt.get();

        // Check if already processed
        if (transaction.getStatus() != PaymentTransaction.PaymentStatus.PENDING) {
            return createIpnResponse("02", "Order already confirmed");
        }

        // Verify amount
        Long vnpAmount = Long.parseLong(params.get("vnp_Amount")) / 100;
        if (!vnpAmount.equals(transaction.getAmount())) {
            return createIpnResponse("04", "Invalid amount");
        }

        // Update transaction
        transaction.setResponseCode(responseCode);
        transaction.setTransactionNo(params.get("vnp_TransactionNo"));
        transaction.setBankCode(params.get("vnp_BankCode"));
        transaction.setBankTranNo(params.get("vnp_BankTranNo"));
        transaction.setCardType(params.get("vnp_CardType"));
        transaction.setPaidAt(LocalDateTime.now());

        if ("00".equals(responseCode)) {
            transaction.setStatus(PaymentTransaction.PaymentStatus.SUCCESS);
            paymentRepository.save(transaction);
            // TODO: Cập nhật order status, inventory, etc.
            return createIpnResponse("00", "Confirm success");
        } else {
            transaction.setStatus(PaymentTransaction.PaymentStatus.FAILED);
            paymentRepository.save(transaction);
            return createIpnResponse("00", "Confirm success");
        }
    }

    private Map<String, String> createIpnResponse(String rspCode, String message) {
        Map<String, String> response = new HashMap<>();
        response.put("RspCode", rspCode);
        response.put("Message", message);
        return response;
    }



}