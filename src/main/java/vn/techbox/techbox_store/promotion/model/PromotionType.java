package vn.techbox.techbox_store.promotion.model;

public enum PromotionType {
    PERCENTAGE("percentage"),
    FIXED("fixed");

    private final String value;

    PromotionType(String value) {
        this.value = value;
    }

    public String getValue() {
        return value;
    }

}