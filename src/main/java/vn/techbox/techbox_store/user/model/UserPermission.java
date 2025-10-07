package vn.techbox.techbox_store.user.model;

import lombok.Getter;

@Getter
public enum UserPermission {
    // User Module Permissions
    USER_READ("USER_READ"),
    USER_WRITE("USER_WRITE"),
    USER_UPDATE("USER_UPDATE"),
    USER_DELETE("USER_DELETE"),

    // Product Module Permissions
    PRODUCT_READ("PRODUCT_READ"),
    PRODUCT_WRITE("PRODUCT_WRITE"),
    PRODUCT_UPDATE("PRODUCT_UPDATE"),
    PRODUCT_DELETE("PRODUCT_DELETE"),

    // Order Module Permissions
    ORDER_READ("ORDER_READ"),
    ORDER_WRITE("ORDER_WRITE"),
    ORDER_UPDATE("ORDER_UPDATE"),
    ORDER_DELETE("ORDER_DELETE"),

    // Promotion Module Permissions
    PROMOTION_READ("PROMOTION_READ"),
    PROMOTION_WRITE("PROMOTION_WRITE"),
    PROMOTION_UPDATE("PROMOTION_UPDATE"),
    PROMOTION_DELETE("PROMOTION_DELETE"),

    // Voucher Module Permissions
    VOUCHER_READ("VOUCHER_READ"),
    VOUCHER_WRITE("VOUCHER_WRITE"),
    VOUCHER_UPDATE("VOUCHER_UPDATE"),
    VOUCHER_DELETE("VOUCHER_DELETE"),

    // Campaign Module Permissions
    CAMPAIGN_READ("CAMPAIGN_READ"),
    CAMPAIGN_WRITE("CAMPAIGN_WRITE"),
    CAMPAIGN_UPDATE("CAMPAIGN_UPDATE"),
    CAMPAIGN_DELETE("CAMPAIGN_DELETE"),

    // Report Module Permissions
    REPORT_READ("REPORT_READ"),
    REPORT_GENERATE("REPORT_GENERATE");

    private final String permissionName;

    UserPermission(String permissionName) {
        this.permissionName = permissionName;
    }

    @Override
    public String toString() {
        return permissionName;
    }

    public String getModule() {
        return permissionName.split("_")[0];
    }

    public String getAction() {
        String[] parts = permissionName.split("_");
        return parts[parts.length - 1];
    }
}
