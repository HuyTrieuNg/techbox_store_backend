package vn.techbox.techbox_store.user.model;

import lombok.Getter;

@Getter
public enum UserPermission {
    // User Module Permissions
    USER_READ("USER:READ"),
    USER_WRITE("USER:WRITE"),
    USER_UPDATE("USER:UPDATE"),
    USER_DELETE("USER:DELETE"),
    USER_REPORT("USER:REPORT"),

    // Product Module Permissions
    PRODUCT_READ("PRODUCT:READ"),
    PRODUCT_WRITE("PRODUCT:WRITE"),
    PRODUCT_UPDATE("PRODUCT:UPDATE"),
    PRODUCT_DELETE("PRODUCT:DELETE"),
    PRODUCT_REPORT("PRODUCT:REPORT"),

    CATEGORY_READ("CATEGORY:READ"),
    CATEGORY_WRITE("CATEGORY:WRITE"),
    CATEGORY_UPDATE("CATEGORY:UPDATE"),
    CATEGORY_DELETE("CATEGORY:DELETE"),

    BRAND_READ("BRAND:READ"),
    BRAND_WRITE("BRAND:WRITE"),
    BRAND_UPDATE("BRAND:UPDATE"),
    BRAND_DELETE("BRAND:DELETE"),

    // Order Module Permissions
    ORDER_READ("ORDER:READ"),
    ORDER_WRITE("ORDER:WRITE"),
    ORDER_UPDATE("ORDER:UPDATE"),
    ORDER_DELETE("ORDER:DELETE"),
    ORDER_REPORT("ORDER:REPORT"),

    // Voucher Module Permissions
    VOUCHER_READ("VOUCHER:READ"),
    VOUCHER_READ_ALL("VOUCHER:READ_ALL"),
    VOUCHER_WRITE("VOUCHER:WRITE"),
    VOUCHER_UPDATE("VOUCHER:UPDATE"),
    VOUCHER_DELETE("VOUCHER:DELETE"),
    VOUCHER_REPORT("VOUCHER:REPORT"),



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

    REPORT_READ("REPORT:USER"),
    REPORT_GENERATE("REPORT:INVENTORY"),
    REPORT_ORDER("REPORT:ORDER"),
    REPORT_PRODUCT("REPORT:PRODUCT");   
    

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
