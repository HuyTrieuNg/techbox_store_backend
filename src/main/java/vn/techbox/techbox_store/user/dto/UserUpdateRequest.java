package vn.techbox.techbox_store.user.dto;

import java.time.LocalDateTime;
import java.util.Set;

public record UserUpdateRequest(
    String email,
    String firstName,
    String lastName,
    String phone,
    String address,
    LocalDateTime dateOfBirth,
    Set<String> roleNames,
    Boolean isActive,
    Boolean isLocked
) {
}
