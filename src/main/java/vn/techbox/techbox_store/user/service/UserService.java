package vn.techbox.techbox_store.user.service;

import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import vn.techbox.techbox_store.user.dto.*;
import vn.techbox.techbox_store.user.model.User;
import vn.techbox.techbox_store.user.security.UserPrincipal;

import java.util.List;
import java.util.Optional;

public interface UserService {
    User createUser(UserCreateRequest req);
    List<User> getAllUsers();
    Page<User> getAllUsersWithPagination(Pageable pageable);
    Page<User> getAllUsersWithPagination(Pageable pageable, boolean includeDeleted);
    Optional<User> getUserById(Integer id);
    Optional<User> getUserByEmail(String email);
    Optional<User> getUserByEmailWithAddresses(String email);
    User updateUser(Integer id, UserUpdateRequest req);
    void deleteUser(Integer id);
    void restoreUser(Integer id);
    boolean isCurrentUser(UserPrincipal userPrincipal, Integer userId);
    TokenResponse verify(UserLoginRequest req);
    void updatePasswordByEmail(String email, String newPassword);
    Page<User> getUsersByRole(String roleName, Pageable pageable);
    Page<User> getUsersByRole(String roleName, Pageable pageable, boolean includeDeleted);
}
