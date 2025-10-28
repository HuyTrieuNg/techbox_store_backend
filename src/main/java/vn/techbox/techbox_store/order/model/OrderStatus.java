package vn.techbox.techbox_store.order.model;

import lombok.Getter;

@Getter
public enum OrderStatus {
    PENDING("PENDING", "Đang chờ xử lý"),
    CONFIRMED("CONFIRMED", "Đã xác nhận"),
    PROCESSING("PROCESSING", "Đang xử lý"),
    SHIPPING("SHIPPING", "Đang giao hàng"),
    DELIVERED("DELIVERED", "Đã giao hàng"),
    CANCELLED("CANCELLED", "Đã hủy"),
    RETURNED("RETURNED", "Đã trả hàng");

    private final String code;
    private final String description;

    OrderStatus(String code, String description) {
        this.code = code;
        this.description = description;
    }
}
