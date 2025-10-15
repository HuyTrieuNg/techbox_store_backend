package vn.techbox.techbox_store.payment.controller;

import jakarta.servlet.http.HttpServletRequest;
import lombok.RequiredArgsConstructor;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;
import vn.techbox.techbox_store.payment.service.VNPayService;

import java.util.Map;

@RestController
@RequestMapping("/payment/vnpay")
@RequiredArgsConstructor
public class VNPayController {

    private final VNPayService vnPayService;

    @PostMapping("/create")
    public ResponseEntity<?> createPayment(
            @RequestParam Long amount,
            @RequestParam String orderInfo,
            @RequestParam Long orderId, //phải tự tạo orderId
            HttpServletRequest request) {

        String paymentUrl = vnPayService.createPaymentUrl(amount, orderInfo, orderId ,request);
        return ResponseEntity.ok(Map.of("paymentUrl", paymentUrl));
    }

    @GetMapping("/callback")
    public ResponseEntity<?> paymentCallback(@RequestParam Map<String, String> params) {
        boolean isValid = vnPayService.verifyPayment(params);

        if (isValid) {
            String responseCode = params.get("vnp_ResponseCode");
            if ("00".equals(responseCode)) {
                // Thanh toán thành công
                return ResponseEntity.ok(Map.of("status", "success", "message", "Payment successful"));
            } else {
                // Thanh toán thất bại
                return ResponseEntity.ok(Map.of("status", "failed", "message", "Payment failed"));
            }
        }

        return ResponseEntity.badRequest().body(Map.of("status", "error", "message", "Invalid signature"));
    }

    // IPN URL - VNPay gọi để cập nhật DB
    @GetMapping("/ipn")
    public ResponseEntity<?> paymentIpn(@RequestParam Map<String, String> params) {
        Map<String, String> result = vnPayService.processIpn(params);
        return ResponseEntity.ok(result);
    }
}