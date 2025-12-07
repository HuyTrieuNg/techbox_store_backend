package vn.techbox.techbox_store.user.dto;

public record ResetPasswordRequest(String token, String newPassword) {}
