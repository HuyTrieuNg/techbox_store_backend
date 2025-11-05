package vn.techbox.techbox_store.payment.model;

import lombok.Getter;

@Getter
public enum PaymentMethod {
    COD("COD", "Cash on Delivery"),
    VNPAY("VNPAY", "VNPay Payment Gateway");

    private final String code;
    private final String description;

    PaymentMethod(String code, String description) {
        this.code = code;
        this.description = description;
    }
}
