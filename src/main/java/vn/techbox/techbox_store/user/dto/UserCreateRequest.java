package vn.techbox.techbox_store.user.dto;

import java.time.LocalDateTime;
import java.util.Set;

public record UserCreateRequest(
    String email,
    String password,
    String firstName,
    String lastName,
    String phone,
    String address,
    LocalDateTime dateOfBirth,
    Set<String> roleNames
) {
}
