package vn.techbox.techbox_store.user.service;

import org.springframework.stereotype.Service;
import vn.techbox.techbox_store.user.dto.AddressCreateRequest;
import vn.techbox.techbox_store.user.dto.AddressUpdateRequest;
import vn.techbox.techbox_store.user.model.Address;

import java.util.List;
import java.util.Optional;

@Service
public interface UserAddressService {
    Address createAddress(Integer userId, AddressCreateRequest req);
    List<Address> getUserAddresses(Integer userId);
    Optional<Address> getAddressById(Integer addressId, Integer userId);
    Address updateAddress(Integer addressId, Integer userId, AddressUpdateRequest req);
    void deleteAddress(Integer addressId, Integer userId);
    Address setDefaultAddress(Integer addressId, Integer userId);
}
