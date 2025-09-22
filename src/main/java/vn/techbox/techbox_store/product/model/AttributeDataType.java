package vn.techbox.techbox_store.product.model;

public enum AttributeDataType {
    STRING("string"),
    INTEGER("integer"),
    DECIMAL("decimal"),
    BOOLEAN("boolean");

    private final String value;

    AttributeDataType(String value) {
        this.value = value;
    }

    public String getValue() {
        return value;
    }

    public static AttributeDataType fromValue(String value) {
        for (AttributeDataType type : AttributeDataType.values()) {
            if (type.value.equals(value)) {
                return type;
            }
        }
        throw new IllegalArgumentException("Unknown AttributeDataType: " + value);
    }
}