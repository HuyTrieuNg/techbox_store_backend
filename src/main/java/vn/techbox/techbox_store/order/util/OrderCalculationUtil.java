package vn.techbox.techbox_store.order.util;

import lombok.Getter;
import lombok.RequiredArgsConstructor;
import lombok.Setter;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Component;
import vn.techbox.techbox_store.order.model.Order;
import vn.techbox.techbox_store.product.model.ProductVariation;
import vn.techbox.techbox_store.product.repository.ProductVariationRepository;
import vn.techbox.techbox_store.promotion.model.Promotion;
import vn.techbox.techbox_store.promotion.repository.PromotionRepository;
import vn.techbox.techbox_store.voucher.model.Voucher;
import vn.techbox.techbox_store.voucher.repository.VoucherRepository;

import java.math.BigDecimal;
import java.time.LocalDateTime;
import java.util.List;

@Component
@RequiredArgsConstructor
@Slf4j
public class OrderCalculationUtil {

    private final PromotionRepository promotionRepository;
    private final VoucherRepository voucherRepository;
    private final ProductVariationRepository productVariationRepository;

    public BigDecimal calculatePromotionDiscount(Integer productVariationId, Integer quantity) {
        try {
            List<Promotion> activePromotions = promotionRepository
                    .findActivePromotionsByProductVariationId(productVariationId, LocalDateTime.now());

            if (activePromotions.isEmpty()) {
                log.debug("No active promotions found for product variation {}", productVariationId);
                return BigDecimal.ZERO;
            }

            BigDecimal productPrice = getProductVariationPrice(productVariationId);
            if (productPrice.compareTo(BigDecimal.ZERO) <= 0) {
                log.warn("Invalid product price {} for product variation {}", productPrice, productVariationId);
                return BigDecimal.ZERO;
            }

            BigDecimal maxDiscount = BigDecimal.ZERO;
            Promotion bestPromotion = null;

            for (Promotion promotion : activePromotions) {
                if (promotion.getCampaign() != null && promotion.getCampaign().isActive() && promotion.isValid()) {
                    BigDecimal discount = promotion.calculateDiscount(productPrice, quantity);

                    if (discount.compareTo(maxDiscount) > 0) {
                        maxDiscount = discount;
                        bestPromotion = promotion;
                        log.debug("Found better promotion discount {} for product {} from campaign {}",
                                discount, productVariationId, promotion.getCampaign().getName());
                    }
                }
            }

            if (bestPromotion != null) {
                log.info("Applied best promotion with discount {} for product variation {}", maxDiscount, productVariationId);
            }

            return maxDiscount;

        } catch (Exception e) {
            log.error("Error calculating promotion discount for product variation {}: {}",
                    productVariationId, e.getMessage(), e);
            return BigDecimal.ZERO;
        }
    }

    public BigDecimal calculateVoucherDiscount(BigDecimal totalAmount, String voucherCode) {
        if (voucherCode == null || voucherCode.trim().isEmpty()) {
            return BigDecimal.ZERO;
        }

        try {
            Voucher voucher = voucherRepository.findByCodeAndNotDeleted(voucherCode)
                    .orElse(null);

            if (voucher == null) {
                log.warn("Voucher not found: {}", voucherCode);
                return BigDecimal.ZERO;
            }

            if (!voucher.isValid()) {
                log.warn("Voucher is not valid: {}", voucherCode);
                return BigDecimal.ZERO;
            }

            if (!voucher.hasUsageLeft()) {
                log.warn("Voucher usage limit exceeded: {}", voucherCode);
                return BigDecimal.ZERO;
            }

            return voucher.calculateDiscount(totalAmount);

        } catch (Exception e) {
            log.error("Error calculating voucher discount for code {}: {}", voucherCode, e.getMessage());
            return BigDecimal.ZERO;
        }
    }

    public VoucherValidationResult validateVoucher(BigDecimal totalAmount, String voucherCode) {
        VoucherValidationResult result = new VoucherValidationResult();
        result.setVoucherCode(voucherCode);
        result.setDiscountAmount(BigDecimal.ZERO);
        result.setValid(false);

        if (voucherCode == null || voucherCode.trim().isEmpty()) {
            result.setValidationMessage("No voucher code provided");
            return result;
        }

        try {
            Voucher voucher = voucherRepository.findByCodeAndNotDeleted(voucherCode)
                    .orElse(null);

            if (voucher == null) {
                result.setValidationMessage("Voucher not found");
                return result;
            }

            if (!voucher.isValid()) {
                result.setValidationMessage("Voucher is expired or not yet active");
                return result;
            }

            if (!voucher.hasUsageLeft()) {
                result.setValidationMessage("Voucher usage limit exceeded");
                return result;
            }

            if (totalAmount.compareTo(voucher.getMinOrderAmount()) < 0) {
                result.setValidationMessage("Order amount does not meet minimum requirement: " +
                        voucher.getMinOrderAmount());
                result.setMinOrderAmount(voucher.getMinOrderAmount());
                return result;
            }

            BigDecimal discountAmount = voucher.calculateDiscount(totalAmount);

            result.setVoucherType(voucher.getVoucherType().name());
            result.setDiscountAmount(discountAmount);
            result.setMinOrderAmount(voucher.getMinOrderAmount());
            result.setDiscountDescription(buildVoucherDescription(voucher));
            result.setValid(true);
            result.setValidationMessage("Voucher applied successfully");

            return result;

        } catch (Exception e) {
            log.error("Error validating voucher {}: {}", voucherCode, e.getMessage());
            result.setValidationMessage("Error processing voucher");
            return result;
        }
    }

    public BigDecimal getProductVariationPrice(Integer productVariationId) {
        return productVariationRepository.findById(productVariationId)
                .map(ProductVariation::getPrice)
                .orElse(BigDecimal.ZERO);
    }

    public String buildVoucherDescription(Voucher voucher) {
        if (voucher.getVoucherType().name().equals("FIXED_AMOUNT")) {
            return "Giảm " + voucher.getValue() + "đ cho đơn hàng từ " + voucher.getMinOrderAmount() + "đ";
        } else {
            return "Giảm " + voucher.getValue() + "% cho đơn hàng từ " + voucher.getMinOrderAmount() + "đ";
        }
    }

    public BigDecimal calculateShippingFee(Order order) {
        if (order != null && order.getShippingInfo() != null) {
            String shippingMethod = order.getShippingInfo().getShippingMethod();
            String city = order.getShippingInfo().getShippingCity();

            if ("EXPRESS".equalsIgnoreCase(shippingMethod)) {
                return new BigDecimal("50000");
            } else if ("STANDARD".equalsIgnoreCase(shippingMethod)) {
                return new BigDecimal("30000");
            }
        }
        return new BigDecimal("30000");
    }

    public BigDecimal calculateShippingFee() {
        return new BigDecimal("30000");
    }

    public BigDecimal calculateTaxAmount(BigDecimal taxableAmount) {
        // TODO: Implement tax calculation if needed

        return BigDecimal.ZERO;
    }

    public boolean isFreeShipping(BigDecimal totalAmount) {
        return totalAmount.compareTo(new BigDecimal("500000")) >= 0;
    }

    @Setter
    @Getter
    public static class VoucherValidationResult {
        private String voucherCode;
        private String voucherType;
        private BigDecimal discountAmount;
        private BigDecimal minOrderAmount;
        private String discountDescription;
        private boolean isValid;
        private String validationMessage;
    }
}
