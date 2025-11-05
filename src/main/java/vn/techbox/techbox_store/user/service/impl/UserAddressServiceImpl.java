package vn.techbox.techbox_store.user.service.impl;

import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import vn.techbox.techbox_store.user.dto.AddressCreateRequest;
import vn.techbox.techbox_store.user.dto.AddressUpdateRequest;
import vn.techbox.techbox_store.user.model.Address;
import vn.techbox.techbox_store.user.model.User;
import vn.techbox.techbox_store.user.repository.AddressRepository;
import vn.techbox.techbox_store.user.repository.UserRepository;
import vn.techbox.techbox_store.user.service.UserAddressService;

import java.time.LocalDateTime;
import java.util.List;
import java.util.Optional;

@Service
public class UserAddressServiceImpl implements UserAddressService {

    private final AddressRepository addressRepository;
    private final UserRepository userRepository;

    @Value("${user.address.max-per-user}")
    private int maxAddressesPerUser;

    public UserAddressServiceImpl(AddressRepository addressRepository, UserRepository userRepository) {
        this.addressRepository = addressRepository;
        this.userRepository = userRepository;
    }

    @Transactional
    public Address createAddress(Integer userId, AddressCreateRequest req) {
        User user = userRepository.findById(userId)
                .orElseThrow(() -> new RuntimeException("User not found with id: " + userId));

        long count = addressRepository.countByUserIdAndNotDeleted(userId);
        if (count >= maxAddressesPerUser) {
            throw new IllegalArgumentException("Address limit exceeded. Max allowed per user: " + maxAddressesPerUser);
        }

        boolean shouldBeDefault = req.isDefault() != null ? req.isDefault() : false;
        if (count == 0) {
            shouldBeDefault = true;
        }
        if (shouldBeDefault) {
            addressRepository.findByUserIdAndNotDeleted(userId)
                    .forEach(addr -> addr.setIsDefault(false));
        }

        Address address = Address.builder()
                .streetAddress(req.streetAddress())
                .ward(req.ward())
                .district(req.district())
                .city(req.city())
                .postalCode(req.postalCode())
                .isDefault(shouldBeDefault)
                .addressType(req.addressType() != null ? req.addressType() : "HOME")
                .user(user)
                .build();

        return addressRepository.save(address);
    }

    public List<Address> getUserAddresses(Integer userId) {
        return addressRepository.findByUserIdAndNotDeleted(userId);
    }

    public Optional<Address> getAddressById(Integer addressId, Integer userId) {
        return addressRepository.findByIdAndUserIdAndNotDeleted(addressId, userId);
    }

    @Transactional
    public Address updateAddress(Integer addressId, Integer userId, AddressUpdateRequest req) {
        Address address = addressRepository.findByIdAndUserIdAndNotDeleted(addressId, userId)
                .orElseThrow(() -> new RuntimeException("Address not found"));

        if (req.streetAddress() != null) {
            address.setStreetAddress(req.streetAddress());
        }
        if (req.ward() != null) {
            address.setWard(req.ward());
        }
        if (req.district() != null) {
            address.setDistrict(req.district());
        }
        if (req.city() != null) {
            address.setCity(req.city());
        }
        if (req.postalCode() != null) {
            address.setPostalCode(req.postalCode());
        }
        if (req.addressType() != null) {
            address.setAddressType(req.addressType());
        }

        // Handle default address change
        if (req.isDefault() != null && req.isDefault() && !address.getIsDefault()) {
            // Remove default from other addresses
            addressRepository.findByUserIdAndNotDeleted(userId)
                    .forEach(addr -> {
                        if (!addr.getId().equals(addressId)) {
                            addr.setIsDefault(false);
                        }
                    });
            address.setIsDefault(true);
        } else if (req.isDefault() != null && !req.isDefault()) {
            address.setIsDefault(false);
        }

        return addressRepository.save(address);
    }

    @Transactional
    public void deleteAddress(Integer addressId, Integer userId) {
        Address address = addressRepository.findByIdAndUserIdAndNotDeleted(addressId, userId)
                .orElseThrow(() -> new RuntimeException("Address not found"));

        boolean wasDefault = Boolean.TRUE.equals(address.getIsDefault());

        // Soft delete the address
        address.setDeletedAt(LocalDateTime.now());
        addressRepository.save(address);

        if (wasDefault) {
            List<Address> remaining = addressRepository.findByUserIdAndNotDeleted(userId);
            if (!remaining.isEmpty()) {
                remaining.forEach(a -> a.setIsDefault(false));
                Address newDefault = remaining.getFirst();
                newDefault.setIsDefault(true);
                addressRepository.save(newDefault);
            }
        }
    }

    @Transactional
    public Address setDefaultAddress(Integer addressId, Integer userId) {
        Address address = addressRepository.findByIdAndUserIdAndNotDeleted(addressId, userId)
                .orElseThrow(() -> new RuntimeException("Address not found"));

        addressRepository.findByUserIdAndNotDeleted(userId)
                .forEach(addr -> addr.setIsDefault(false));

        address.setIsDefault(true);
        return addressRepository.save(address);
    }
}
