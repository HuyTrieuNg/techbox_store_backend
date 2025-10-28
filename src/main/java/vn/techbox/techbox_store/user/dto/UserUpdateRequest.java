package vn.techbox.techbox_store.user.dto;

import java.time.LocalDateTime;
import java.util.Set;
import java.util.List;

public record UserUpdateRequest(
    String email,
    String firstName,
    String lastName,
    String phone,
    List<AddressUpdateRequest> addresses,
    LocalDateTime dateOfBirth,
    Set<String> roleNames,
    Boolean isActive,
    Boolean isLocked
) {
}
