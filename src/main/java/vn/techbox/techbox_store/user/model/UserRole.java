package vn.techbox.techbox_store.user.model;

import lombok.Getter;

@Getter
public enum UserRole {
    ROLE_ADMIN("ROLE_ADMIN"),
    ROLE_STAFF("ROLE_STAFF"),
    ROLE_CUSTOMER("ROLE_CUSTOMER");

    private final String roleName;

    UserRole(String roleName) {
        this.roleName = roleName;
    }

    @Override
    public String toString() {
        return roleName;
    }
}
