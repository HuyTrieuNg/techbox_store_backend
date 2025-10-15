package vn.techbox.techbox_store.user.controller;

import org.springframework.http.ResponseEntity;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.web.bind.annotation.*;
import vn.techbox.techbox_store.user.dto.AddressCreateRequest;
import vn.techbox.techbox_store.user.dto.AddressResponse;
import vn.techbox.techbox_store.user.dto.AddressUpdateRequest;
import vn.techbox.techbox_store.user.model.Address;
import vn.techbox.techbox_store.user.service.UserAddressService;

import java.net.URI;
import java.util.List;
import java.util.stream.Collectors;

@RestController
@RequestMapping("/users/{userId}/addresses")
public class UserAddressController {
    private final UserAddressService userAddressService;

    public UserAddressController(UserAddressService userAddressService) {
        this.userAddressService = userAddressService;
    }

    @PostMapping
    @PreAuthorize("hasRole('CUSTOMER') && @userService.isCurrentUser(#userId)")
    public ResponseEntity<AddressResponse> createAddress(@PathVariable Integer userId, @RequestBody AddressCreateRequest req) {
        Address created = userAddressService.createAddress(userId, req);
        return ResponseEntity.created(URI.create("/api/users/" + userId + "/addresses/" + created.getId()))
                .body(AddressResponse.from(created));
    }

    @GetMapping
    @PreAuthorize("hasRole('CUSTOMER') && @userService.isCurrentUser(#userId)")
    public ResponseEntity<List<AddressResponse>> getUserAddresses(@PathVariable Integer userId) {
        List<AddressResponse> addresses = userAddressService.getUserAddresses(userId).stream()
                .map(AddressResponse::from)
                .collect(Collectors.toList());
        return ResponseEntity.ok(addresses);
    }

    @GetMapping("/{addressId}")
    @PreAuthorize("hasRole('CUSTOMER') && @userService.isCurrentUser(#userId)")
    public ResponseEntity<AddressResponse> getAddress(@PathVariable Integer userId, @PathVariable Integer addressId) {
        return userAddressService.getAddressById(addressId, userId)
                .map(AddressResponse::from)
                .map(ResponseEntity::ok)
                .orElse(ResponseEntity.notFound().build());
    }

    @PatchMapping("/{addressId}")
    @PreAuthorize("hasRole('CUSTOMER') && @userService.isCurrentUser(#userId)")
    public ResponseEntity<AddressResponse> updateAddress(@PathVariable Integer userId, @PathVariable Integer addressId, @RequestBody AddressUpdateRequest req) {
        try {
            Address updated = userAddressService.updateAddress(addressId, userId, req);
            return ResponseEntity.ok(AddressResponse.from(updated));
        } catch (RuntimeException e) {
            if (e.getMessage().contains("Address not found")) {
                return ResponseEntity.notFound().build();
            }
            throw e;
        }
    }

    @DeleteMapping("/{addressId}")
    @PreAuthorize("hasRole('CUSTOMER') && @userService.isCurrentUser(#userId)")
    public ResponseEntity<Void> deleteAddress(@PathVariable Integer userId, @PathVariable Integer addressId) {
        try {
            userAddressService.deleteAddress(addressId, userId);
            return ResponseEntity.noContent().build();
        } catch (RuntimeException e) {
            if (e.getMessage().contains("Address not found")) {
                return ResponseEntity.notFound().build();
            }
            throw e;
        }
    }

    @PatchMapping("/{addressId}/set-default")
    @PreAuthorize("hasRole('CUSTOMER') && @userService.isCurrentUser(#userId)")
    public ResponseEntity<AddressResponse> setDefaultAddress(@PathVariable Integer userId, @PathVariable Integer addressId) {
        try {
            Address updated = userAddressService.setDefaultAddress(addressId, userId);
            return ResponseEntity.ok(AddressResponse.from(updated));
        } catch (RuntimeException e) {
            if (e.getMessage().contains("Address not found")) {
                return ResponseEntity.notFound().build();
            }
            throw e;
        }
    }
}
