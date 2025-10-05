package vn.techbox.techbox_store.user.service;

import org.springframework.stereotype.Service;
import vn.techbox.techbox_store.user.dto.UserCreateRequest;
import vn.techbox.techbox_store.user.dto.UserLoginRequest;
import vn.techbox.techbox_store.user.dto.UserUpdateRequest;
import vn.techbox.techbox_store.user.dto.TokenResponse;
import vn.techbox.techbox_store.user.model.User;

import java.util.List;
import java.util.Optional;

@Service
public interface UserService {
    User createUser(UserCreateRequest req);
    List<User> getAllUsers();
    Optional<User> getUserById(Integer id);
    Optional<User> getUserByUsername(String username);
    User updateUser(Integer id, UserUpdateRequest req);
    void deleteUser(Integer id);
    void restoreUser(Integer id);
    boolean isCurrentUser(Integer userId);
    TokenResponse verify(UserLoginRequest req);
}
