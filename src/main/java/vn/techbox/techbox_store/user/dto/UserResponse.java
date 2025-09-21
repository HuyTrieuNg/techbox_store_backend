package vn.techbox.techbox_store.user.dto;

import vn.techbox.techbox_store.user.model.User;
import vn.techbox.techbox_store.user.model.UserRole;

public record UserResponse(Integer id, String username, String email, UserRole role) {
    public static UserResponse from(User u) {
        return new UserResponse(u.getId(), u.getUsername(), u.getEmail(), u.getRole());
    }
}
