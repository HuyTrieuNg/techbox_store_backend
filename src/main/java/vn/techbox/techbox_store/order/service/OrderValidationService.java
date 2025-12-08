package vn.techbox.techbox_store.order.service;

import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import vn.techbox.techbox_store.order.dto.CreateOrderRequest;
import vn.techbox.techbox_store.order.model.Order;
import vn.techbox.techbox_store.order.model.OrderStatus;
import vn.techbox.techbox_store.payment.model.PaymentMethod;
import vn.techbox.techbox_store.product.repository.ProductVariationRepository;
import vn.techbox.techbox_store.voucher.model.Voucher;
import vn.techbox.techbox_store.voucher.model.UserVoucher;
import vn.techbox.techbox_store.voucher.model.VoucherReservation;
import vn.techbox.techbox_store.voucher.repository.UserVoucherRepository;
import vn.techbox.techbox_store.voucher.repository.VoucherRepository;
import vn.techbox.techbox_store.voucher.repository.VoucherReservationRepository;
import vn.techbox.techbox_store.voucher.exception.VoucherValidationException;

import java.util.List;
import java.util.Optional;
import java.util.Set;

@Service
@RequiredArgsConstructor
public class OrderValidationService {

    private final ProductVariationRepository productVariationRepository;
    private final UserVoucherRepository userVoucherRepository;
    private final VoucherRepository voucherRepository;
    private final VoucherReservationRepository voucherReservationRepository;

    private static final Set<OrderStatus> CANCELLABLE_STATUSES = Set.of(
        OrderStatus.PENDING,
        OrderStatus.CONFIRMED
    );

    private static final Set<OrderStatus> TERMINAL_STATUSES = Set.of(
        OrderStatus.DELIVERED,
        OrderStatus.CANCELLED,
        OrderStatus.RETURNED
    );

    public void validateCreateOrderRequest(CreateOrderRequest request) {
        validateOrderItems(request.getOrderItems());
        validatePaymentMethod(request.getPaymentMethod());
        validateShippingInformation(request);
    }

    public void validateCreateOrderRequest(CreateOrderRequest request, Integer userId) {
        validateOrderItems(request.getOrderItems());
        validatePaymentMethod(request.getPaymentMethod());
        validateShippingInformation(request);
        if (request.getVoucherCode() != null && !request.getVoucherCode().trim().isEmpty()) {
            validateVoucherCode(request.getVoucherCode(), userId);
        }
    }

    public void validateStatusTransition(OrderStatus currentStatus, OrderStatus newStatus) {
        if (currentStatus == newStatus) {
            throw new RuntimeException("Order is already in " + newStatus + " status");
        }

        if (TERMINAL_STATUSES.contains(currentStatus)) {
            throw new RuntimeException("Cannot change status from " + currentStatus);
        }

        switch (currentStatus) {
            case PENDING:
                if (!Set.of(OrderStatus.CONFIRMED, OrderStatus.CANCELLED).contains(newStatus)) {
                    throw new RuntimeException("Invalid status transition from " + currentStatus + " to " + newStatus);
                }
                break;
            case CONFIRMED:
                if (!Set.of(OrderStatus.PROCESSING, OrderStatus.CANCELLED).contains(newStatus)) {
                    throw new RuntimeException("Invalid status transition from " + currentStatus + " to " + newStatus);
                }
                break;
            case PROCESSING:
                if (!Set.of(OrderStatus.SHIPPING, OrderStatus.CANCELLED).contains(newStatus)) {
                    throw new RuntimeException("Invalid status transition from " + currentStatus + " to " + newStatus);
                }
                break;
            case SHIPPING:
                if (!Set.of(OrderStatus.DELIVERED, OrderStatus.RETURNED).contains(newStatus)) {
                    throw new RuntimeException("Invalid status transition from " + currentStatus + " to " + newStatus);
                }
                break;
            default:
                throw new RuntimeException("Invalid status transition from " + currentStatus + " to " + newStatus);
        }
    }

    public void validateCancellation(Order order) {
        if (!CANCELLABLE_STATUSES.contains(order.getStatus())) {
            throw new RuntimeException("Cannot cancel order in " + order.getStatus() + " status");
        }
    }

    private void validateOrderItems(List<CreateOrderRequest.OrderItemRequest> orderItems) {
        if (orderItems == null || orderItems.isEmpty()) {
            throw new RuntimeException("Order items cannot be empty");
        }

        for (CreateOrderRequest.OrderItemRequest item : orderItems) {
            if (!productVariationRepository.existsById(item.getProductVariationId().intValue())) {
                throw new RuntimeException("Product variation not found: " + item.getProductVariationId());
            }

            if (item.getQuantity() <= 0) {
                throw new RuntimeException("Quantity must be greater than 0");
            }

            // TODO: Validate stock availability
        }
    }

    private void validatePaymentMethod(PaymentMethod paymentMethod) {
        if (paymentMethod == null) {
            throw new RuntimeException("Payment method is required");
        }
    }

    private void validateShippingInformation(CreateOrderRequest request) {
        if (request.getShippingName() == null || request.getShippingName().trim().isEmpty()) {
            throw new RuntimeException("Shipping name is required");
        }

        if (request.getShippingPhone() == null || request.getShippingPhone().trim().isEmpty()) {
            throw new RuntimeException("Shipping phone is required");
        }

        if (request.getShippingAddress() == null || request.getShippingAddress().trim().isEmpty()) {
            throw new RuntimeException("Shipping address is required");
        }
    }

    private void validateVoucherCode(String voucherCode, Integer userId) {
        // Check if voucher exists and is valid
        Voucher voucher = voucherRepository.findByCodeAndNotDeleted(voucherCode)
                .orElseThrow(() -> new VoucherValidationException(
                    "Mã voucher không tồn tại hoặc đã hết hạn",
                    voucherCode,
                    "VOUCHER_NOT_FOUND"
                ));

        if (!voucher.isValid()) {
            throw new VoucherValidationException(
                "Mã voucher đã hết hạn sử dụng",
                voucherCode,
                "VOUCHER_EXPIRED"
            );
        }

        if (!voucher.hasUsageLeft()) {
            throw new VoucherValidationException(
                "Mã voucher đã hết lượt sử dụng",
                voucherCode,
                "VOUCHER_LIMIT_EXCEEDED"
            );
        }

        // Check if user has already used this voucher
        Optional<UserVoucher> existingUsage = userVoucherRepository
                .findByUserIdAndVoucherCode(userId, voucherCode);
        if (existingUsage.isPresent()) {
            throw new VoucherValidationException(
                "Bạn đã sử dụng mã voucher này rồi",
                voucherCode,
                "VOUCHER_ALREADY_USED"
            );
        }

        // Check if user has a reserved voucher (đang được đặt chỗ trong đơn hàng khác)
        Optional<VoucherReservation> existingReservation = voucherReservationRepository
                .findByUserIdAndVoucherCodeAndReserved(userId, voucherCode);
        if (existingReservation.isPresent()) {
            throw new VoucherValidationException(
                "Bạn đang sử dụng mã voucher này trong đơn hàng khác chưa hoàn thành",
                voucherCode,
                "VOUCHER_ALREADY_RESERVED"
            );
        }
    }
}
