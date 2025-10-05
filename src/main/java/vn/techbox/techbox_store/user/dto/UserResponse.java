package vn.techbox.techbox_store.user.dto;

import vn.techbox.techbox_store.user.model.User;

import java.time.LocalDateTime;
import java.util.Set;
import java.util.stream.Collectors;

public record UserResponse(
    Integer id,
    String username,
    String email,
    String firstName,
    String lastName,
    String phone,
    String address,
    LocalDateTime dateOfBirth,
    Set<String> roles,
    Boolean isActive,
    Boolean isLocked,
    LocalDateTime createdAt
) {
    public static UserResponse from(User u) {
        return new UserResponse(
            u.getId(),
            u.getAccount().getUsername(),
            u.getAccount().getEmail(),
            u.getFirstName(),
            u.getLastName(),
            u.getPhone(),
            u.getAddress(),
            u.getDateOfBirth(),
            u.getRoles().stream().map(r -> r.getName()).collect(Collectors.toSet()),
            u.getAccount().getIsActive(),
            u.getAccount().getIsLocked(),
            u.getCreatedAt()
        );
    }
}
