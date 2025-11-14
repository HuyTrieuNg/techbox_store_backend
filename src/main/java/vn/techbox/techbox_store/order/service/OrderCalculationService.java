package vn.techbox.techbox_store.order.service;

import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Service;
import vn.techbox.techbox_store.order.dto.DiscountCalculationRequest;
import vn.techbox.techbox_store.order.dto.DiscountCalculationResponse;
import vn.techbox.techbox_store.order.model.Order;
import vn.techbox.techbox_store.order.model.OrderItem;
import vn.techbox.techbox_store.order.util.OrderCalculationUtil;
import vn.techbox.techbox_store.payment.model.Payment;
import vn.techbox.techbox_store.product.model.ProductVariation;
import vn.techbox.techbox_store.product.repository.ProductVariationRepository;

import java.math.BigDecimal;
import java.util.ArrayList;
import java.util.List;

@Service
@RequiredArgsConstructor
@Slf4j
public class OrderCalculationService {

    private final OrderCalculationUtil orderUtil;
    private final ProductVariationRepository productVariationRepository;

    public DiscountCalculationResponse calculateDiscounts(DiscountCalculationRequest request) {
        log.info("Calculating discounts for {} items with voucher: {}",
                request.getOrderItems().size(), request.getVoucherCode());

        List<DiscountCalculationResponse.ItemDiscountDetail> itemDiscounts = new ArrayList<>();
        BigDecimal totalOriginalAmount = BigDecimal.ZERO;
        BigDecimal totalPromotionDiscount = BigDecimal.ZERO;

        for (DiscountCalculationRequest.OrderItemRequest item : request.getOrderItems()) {
            ProductVariation productVariation = productVariationRepository.findById(item.getProductVariationId())
                    .orElse(null);

            if (productVariation != null) {
                BigDecimal originalPrice = productVariation.getPrice();
                totalOriginalAmount = totalOriginalAmount.add(
                    originalPrice.multiply(BigDecimal.valueOf(item.getQuantity()))
                );
            }
        }

        // Tính promotion discount cho từng item
        for (DiscountCalculationRequest.OrderItemRequest item : request.getOrderItems()) {
            ProductVariation productVariation = productVariationRepository.findById(item.getProductVariationId())
                    .orElse(null);

            if (productVariation == null) {
                continue;
            }

            // Sử dụng giá gốc từ database thay vì giá từ frontend
            BigDecimal originalPrice = productVariation.getPrice();
            BigDecimal originalAmount = originalPrice.multiply(BigDecimal.valueOf(item.getQuantity()));

            BigDecimal promotionDiscount = orderUtil.calculatePromotionDiscount(
                    item.getProductVariationId(),
                    item.getQuantity()
            );

            totalPromotionDiscount = totalPromotionDiscount.add(promotionDiscount);

            itemDiscounts.add(DiscountCalculationResponse.ItemDiscountDetail.builder()
                    .productVariationId(item.getProductVariationId())
                    .productName(productVariation.getProduct().getName())
                    .quantity(item.getQuantity())
                    .unitPrice(originalPrice)
                    .originalAmount(originalAmount)
                    .promotionDiscount(promotionDiscount)
                    .finalAmount(originalAmount.subtract(promotionDiscount))
                    .build());
        }

        // Tính tổng tiền sau promotion discount
        BigDecimal totalAfterPromotion = totalOriginalAmount.subtract(totalPromotionDiscount);

        // Tính voucher discount
        DiscountCalculationResponse.VoucherDiscountDetail voucherDetails =
                calculateVoucherDiscountDetail(totalAfterPromotion, request.getVoucherCode());

        BigDecimal voucherDiscount = voucherDetails.getDiscountAmount();
        BigDecimal totalDiscount = totalPromotionDiscount.add(voucherDiscount);

        // Tính phí ship và thuế
        BigDecimal shippingFee = orderUtil.calculateShippingFee();
        BigDecimal taxAmount = orderUtil.calculateTaxAmount(totalAfterPromotion.subtract(voucherDiscount));

        // Tính tổng cuối cùng
        BigDecimal finalAmount = totalAfterPromotion
                .subtract(voucherDiscount)
                .add(shippingFee)
                .add(taxAmount);

        return DiscountCalculationResponse.builder()
                .totalAmount(totalOriginalAmount)
                .promotionDiscount(totalPromotionDiscount)
                .voucherDiscount(voucherDiscount)
                .totalDiscount(totalDiscount)
                .finalAmount(finalAmount)
                .shippingFee(shippingFee)
                .taxAmount(taxAmount)
                .itemDiscounts(itemDiscounts)
                .voucherDetails(voucherDetails)
                .build();
    }

    public void calculateOrderAmounts(Order order, Payment paymentInfo, List<OrderItem> orderItems, String voucherCode) {
        BigDecimal totalAmountBeforePromotion = orderItems.stream()
                .map(item -> item.getUnitPrice().multiply(BigDecimal.valueOf(item.getQuantity())))
                .reduce(BigDecimal.ZERO, BigDecimal::add);

        // Tính tổng promotion discount và cập nhật lại total price cho từng item
        calculatePromotionDiscounts(orderItems);
        BigDecimal totalAmount = orderItems.stream()
                .map(OrderItem::getTotalPrice)
                .reduce(BigDecimal.ZERO, BigDecimal::add);

        paymentInfo.setTotalAmount(totalAmountBeforePromotion);

        // Tính voucher discount
        BigDecimal voucherDiscount = orderUtil.calculateVoucherDiscount(totalAmount, voucherCode);
        paymentInfo.setVoucherDiscount(voucherDiscount);

        // Tính tổng discount (promotion + voucher)
        BigDecimal promotionDiscount = orderItems.stream()
                .map(OrderItem::getDiscountAmount)
                .reduce(BigDecimal.ZERO, BigDecimal::add);

        BigDecimal totalDiscount = promotionDiscount.add(voucherDiscount);
        paymentInfo.setDiscountAmount(totalDiscount);

        BigDecimal shippingFee = orderUtil.calculateShippingFee(order);
        paymentInfo.setShippingFee(shippingFee);
        BigDecimal taxAmount = orderUtil.calculateTaxAmount(totalAmount.subtract(voucherDiscount));
        paymentInfo.setTaxAmount(taxAmount);

        // Tính tổng cuối cùng
        BigDecimal finalAmount = totalAmount
                .subtract(voucherDiscount)
                .add(shippingFee)
                .add(taxAmount);
        paymentInfo.setFinalAmount(finalAmount);
    }

    public void calculateOrderAmounts(Order order, String voucherCode) {
        if (order.getPaymentInfo() != null && order.getOrderItems() != null) {
            calculateOrderAmounts(order, order.getPaymentInfo(), order.getOrderItems(), voucherCode);
        }
    }

    public void calculatePromotionDiscounts(List<OrderItem> orderItems) {
        log.info("Calculating promotion discounts for {} items", orderItems.size());

        for (OrderItem item : orderItems) {
            // Đảm bảo unitPrice là giá gốc từ database
            BigDecimal originalPrice = item.getProductVariation().getPrice();
            item.setUnitPrice(originalPrice);

            BigDecimal promotionDiscount = orderUtil.calculatePromotionDiscount(
                    item.getProductVariation().getId(),
                    item.getQuantity()
            );
            item.setDiscountAmount(promotionDiscount);

            // Cập nhật lại total price sau khi áp dụng discount
            BigDecimal itemTotal = originalPrice.multiply(BigDecimal.valueOf(item.getQuantity()));
            BigDecimal discountedPrice = itemTotal.subtract(promotionDiscount);
            item.setTotalPrice(discountedPrice);

            if (promotionDiscount.compareTo(BigDecimal.ZERO) > 0) {
                log.info("Applied promotion discount {} for product variation {}",
                        promotionDiscount, item.getProductVariation().getId());
            }
        }
    }

    public BigDecimal calculateItemTotal(OrderItem item) {
        return item.getUnitPrice()
                .multiply(BigDecimal.valueOf(item.getQuantity()))
                .subtract(item.getDiscountAmount());
    }

    private DiscountCalculationResponse.VoucherDiscountDetail calculateVoucherDiscountDetail(
            BigDecimal totalAmount, String voucherCode) {

        OrderCalculationUtil.VoucherValidationResult validationResult =
                orderUtil.validateVoucher(totalAmount, voucherCode);

        return DiscountCalculationResponse.VoucherDiscountDetail.builder()
                .voucherCode(validationResult.getVoucherCode())
                .voucherType(validationResult.getVoucherType())
                .discountAmount(validationResult.getDiscountAmount())
                .minOrderAmount(validationResult.getMinOrderAmount())
                .discountDescription(validationResult.getDiscountDescription())
                .isValid(validationResult.isValid())
                .validationMessage(validationResult.getValidationMessage())
                .build();
    }
}
