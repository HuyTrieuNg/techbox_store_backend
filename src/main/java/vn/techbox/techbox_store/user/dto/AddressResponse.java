package vn.techbox.techbox_store.user.dto;

import vn.techbox.techbox_store.user.model.Address;

public record AddressResponse(
    Integer id,
    String streetAddress,
    String ward,
    String district,
    String city,
    String postalCode,
    Boolean isDefault,
    String addressType
) {
    public static AddressResponse from(Address address) {
        return new AddressResponse(
            address.getId(),
            address.getStreetAddress(),
            address.getWard(),
            address.getDistrict(),
            address.getCity(),
            address.getPostalCode(),
            address.getIsDefault(),
            address.getAddressType()
        );
    }
}
