package vn.techbox.techbox_store.user.dto;

import vn.techbox.techbox_store.user.model.UserRole;

public record UserUpdateRequest(String username, String email, UserRole role) {
}
