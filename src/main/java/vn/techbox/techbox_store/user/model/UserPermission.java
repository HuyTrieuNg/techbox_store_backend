package vn.techbox.techbox_store.user.model;

import lombok.Getter;

@Getter
public enum UserPermission {
    // User Module Permissions
    USER_READ("USER:READ"),
    USER_WRITE("USER:WRITE"),
    USER_UPDATE("USER:UPDATE"),
    USER_DELETE("USER:DELETE"),

    // Product Module Permissions
    PRODUCT_READ("PRODUCT:READ"),
    PRODUCT_WRITE("PRODUCT:WRITE"),
    PRODUCT_UPDATE("PRODUCT:UPDATE"),
    PRODUCT_DELETE("PRODUCT:DELETE"),
    PRODUCT_REPORT("PRODUCT:REPORT"),

    // Order Module Permissions
    ORDER_READ("ORDER:READ"),
    ORDER_WRITE("ORDER:WRITE"),
    ORDER_UPDATE("ORDER:UPDATE"),
    ORDER_DELETE("ORDER:DELETE"),

    // Voucher Module Permissions
    VOUCHER_READ("VOUCHER:READ"),
    VOUCHER_READ_ALL("VOUCHER:READ_ALL"),
    VOUCHER_WRITE("VOUCHER:WRITE"),
    VOUCHER_UPDATE("VOUCHER:UPDATE"),
    VOUCHER_DELETE("VOUCHER:DELETE"),
    VOURCHER_REPORT("VOUCHER:REPORT"),

    // Campaign Module Permissions
    CAMPAIGN_READ("CAMPAIGN:READ"),
    CAMPAIGN_READ_ALL("CAMPAIGN:READ_ALL"),
    CAMPAIGN_WRITE("CAMPAIGN:WRITE"),
    CAMPAIGN_UPDATE("CAMPAIGN:UPDATE"),
    CAMPAIGN_DELETE("CAMPAIGN:DELETE"),

    // Promotion Module Permissions
    PROMOTION_READ("PROMOTION:READ"),
    PROMOTION_READ_ALL("PROMOTION:READ_ALL"),
    PROMOTION_WRITE("PROMOTION:WRITE"),
    PROMOTION_UPDATE("PROMOTION:UPDATE"),
    PROMOTION_DELETE("PROMOTION:DELETE"),
    // Report Module Permissions

    REVIEW_READ("REVIEW:READ"),
    REVIEW_WRITE("REVIEW:WRITE"),
    REVIEW_UPDATE("REVIEW:UPDATE"),
    REVIEW_DELETE("REVIEW:DELETE"),

    INVENTORY_READ("INVENTORY:READ"),
    INVENTORY_WRITE("INVENTORY:WRITE"),
    INVENTORY_UPDATE("INVENTORY:UPDATE"),
    INVENTORY_DELETE("INVENTORY:DELETE"),
    INVENTORY_REPORT("INVENTORY:REPORT"),

    REPORT_READ("REPORT:READ"),
    REPORT_GENERATE("REPORT:GENERATE");

    private final String permissionName;

    UserPermission(String permissionName) {
        this.permissionName = permissionName;
    }

    @Override
    public String toString() {
        return permissionName;
    }

    public String getModule() {
        return permissionName.split(":")[0];
    }

    public String getAction() {
        String[] parts = permissionName.split(":");
        return parts[parts.length - 1];
    }
    
}
