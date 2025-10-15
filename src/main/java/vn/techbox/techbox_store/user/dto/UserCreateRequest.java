package vn.techbox.techbox_store.user.dto;

import java.time.LocalDateTime;
import java.util.Set;
import java.util.List;

public record UserCreateRequest(
    String email,
    String password,
    String firstName,
    String lastName,
    String phone,
    List<AddressCreateRequest> addresses,
    LocalDateTime dateOfBirth,
    Set<String> roleNames
) {
}
