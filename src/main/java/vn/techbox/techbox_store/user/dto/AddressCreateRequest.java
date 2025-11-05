package vn.techbox.techbox_store.user.dto;

public record AddressCreateRequest(
    String streetAddress,
    String ward,
    String district,
    String city,
    String postalCode,
    Boolean isDefault,
    String addressType
) {
}
