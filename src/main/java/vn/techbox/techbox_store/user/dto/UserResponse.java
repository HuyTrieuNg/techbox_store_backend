package vn.techbox.techbox_store.user.dto;

import vn.techbox.techbox_store.user.model.Role;
import vn.techbox.techbox_store.user.model.User;

import java.time.LocalDateTime;
import java.util.List;
import java.util.Set;
import java.util.stream.Collectors;

public record UserResponse(
    Integer id,
    String email,
    String firstName,
    String lastName,
    String phone,
    List<AddressResponse> addresses,
    LocalDateTime dateOfBirth,
    Set<String> roles,
    Boolean isActive,
    LocalDateTime createdAt
) {
    public static UserResponse from(User u) {
        return new UserResponse(
            u.getId(),
            u.getAccount().getEmail(),
            u.getFirstName(),
            u.getLastName(),
            u.getPhone(),
            u.getAddresses().stream()
                .filter(address -> address.getDeletedAt() == null)
                .map(AddressResponse::from)
                .collect(Collectors.toList()),
            u.getDateOfBirth(),
            u.getRoles().stream().map(Role::getName).collect(Collectors.toSet()),
            u.getAccount().getIsActive(),
            u.getCreatedAt()
        );
    }
}
