package vn.techbox.techbox_store.user.dto;

import vn.techbox.techbox_store.user.model.UserRole;

public record UserCreateRequest(String username, String email, String password, UserRole role) {
}
