package vn.techbox.techbox_store.promotion.model;

public enum PromotionType {
    PERCENTAGE("percentage", "%"),
    FIXED("fixed", "$");

    private final String value;
    private final String symbol;

    PromotionType(String value, String symbol) {
        this.value = value;
        this.symbol = symbol;
    }

    public String getValue() {
        return value;
    }

    public String getSymbol() {
        return symbol;
    }

}