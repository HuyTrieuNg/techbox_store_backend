package vn.techbox.techbox_store.order.service;

import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import vn.techbox.techbox_store.order.model.Order;
import vn.techbox.techbox_store.order.model.OrderItem;
import vn.techbox.techbox_store.payment.model.Payment;

import java.math.BigDecimal;
import java.util.List;

@Service
@RequiredArgsConstructor
public class OrderCalculationService {

    public void calculateOrderAmounts(Order order, Payment paymentInfo, List<OrderItem> orderItems, String voucherCode) {
        // Tính tổng tiền từ các order items
        BigDecimal totalAmount = orderItems.stream()
                .map(OrderItem::getTotalPrice)
                .reduce(BigDecimal.ZERO, BigDecimal::add);

        paymentInfo.setTotalAmount(totalAmount);

        // Tính voucher discount
        BigDecimal voucherDiscount = calculateVoucherDiscount(totalAmount, voucherCode);
        paymentInfo.setVoucherDiscount(voucherDiscount);

        // Tính discount tổng
        BigDecimal discountAmount = voucherDiscount.add(
            orderItems.stream()
                .map(OrderItem::getDiscountAmount)
                .reduce(BigDecimal.ZERO, BigDecimal::add)
        );
        paymentInfo.setDiscountAmount(discountAmount);

        // Tính phí ship
        BigDecimal shippingFee = calculateShippingFee(order);
        paymentInfo.setShippingFee(shippingFee);

        // Tính thuế
        BigDecimal taxAmount = calculateTaxAmount(totalAmount.subtract(discountAmount));
        paymentInfo.setTaxAmount(taxAmount);

        // Tính tổng cuối cùng
        BigDecimal finalAmount = totalAmount
                .subtract(discountAmount)
                .add(shippingFee)
                .add(taxAmount);
        paymentInfo.setFinalAmount(finalAmount);
    }

    public void calculateOrderAmounts(Order order, String voucherCode) {
        if (order.getPaymentInfo() != null && order.getOrderItems() != null) {
            calculateOrderAmounts(order, order.getPaymentInfo(), order.getOrderItems(), voucherCode);
        }
    }

    private BigDecimal calculateVoucherDiscount(BigDecimal totalAmount, String voucherCode) {
        if (voucherCode == null || voucherCode.trim().isEmpty()) {
            return BigDecimal.ZERO;
        }

        // TODO: Implement voucher logic
        // Tạm thời return 0
        return BigDecimal.TEN;
    }

    private BigDecimal calculateShippingFee(Order order) {
        // TODO: Implement shipping fee calculation based on:
        // - Địa chỉ giao hàng
        // - Trọng lượng sản phẩm
        // - Phương thức vận chuyển

        // Tạm thời fix phí ship 30k
        return new BigDecimal("30000");
    }

    private BigDecimal calculateTaxAmount(BigDecimal taxableAmount) {
        // TODO: Implement tax calculation if needed
        // Vietnam VAT is 10% for most products
        return BigDecimal.ZERO;
    }

    public BigDecimal calculateItemTotal(OrderItem item) {
        return item.getUnitPrice()
                .multiply(BigDecimal.valueOf(item.getQuantity()))
                .subtract(item.getDiscountAmount());
    }
}
