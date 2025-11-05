package vn.techbox.techbox_store.order.controller;

import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;
import vn.techbox.techbox_store.order.dto.DiscountCalculationRequest;
import vn.techbox.techbox_store.order.dto.DiscountCalculationResponse;
import vn.techbox.techbox_store.order.service.OrderCalculationService;

import jakarta.validation.Valid;

@RestController
@RequestMapping("/orders")
@RequiredArgsConstructor
@Slf4j
public class OrderDiscountController {

    private final OrderCalculationService orderCalculationService;

    @PostMapping("/calculate-discount")
    public ResponseEntity<DiscountCalculationResponse> calculateDiscount(
            @Valid @RequestBody DiscountCalculationRequest request) {

        log.info("Calculating discount for order with {} items", request.getOrderItems().size());
        DiscountCalculationResponse response = orderCalculationService.calculateDiscounts(request);
        return ResponseEntity.ok(response);
    }
}
