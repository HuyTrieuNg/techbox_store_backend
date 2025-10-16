package vn.techbox.techbox_store.payment.controller;

import io.swagger.v3.oas.annotations.Operation;
import lombok.extern.slf4j.Slf4j;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;
import vn.techbox.techbox_store.order.service.OrderService;

import java.util.Map;

@RestController
@Slf4j
@RequestMapping("/orders/{orderId}/payments")
public class PaymentController {
    private final OrderService orderService;

    public PaymentController(OrderService orderService) {
        this.orderService = orderService;
    }

    @PostMapping("/confirm")
    @Operation(summary = "Confirm payment", description = "Confirm payment callback from payment gateway")
    public ResponseEntity<Map<String, Object>> confirmPayment(
            @PathVariable Long orderId,
            @RequestParam String transactionId,
            @RequestParam(required = false) String signature) {

        log.info("Confirming payment for order: {}", orderId);

        boolean confirmed = orderService.confirmPayment(orderId, transactionId, signature);

        Map<String, Object> response = Map.of(
                "success", confirmed,
                "message", confirmed ? "Payment confirmed successfully" : "Payment confirmation failed"
        );

        return ResponseEntity.ok(response);
    }
}
