package vn.techbox.techbox_store.user.service;

import vn.techbox.techbox_store.user.dto.UserCreateRequest;
import vn.techbox.techbox_store.user.dto.UserLoginRequest;
import vn.techbox.techbox_store.user.dto.TokenResponse;
import vn.techbox.techbox_store.user.model.User;

import java.util.List;
import java.util.Optional;

public interface UserService {
    User createUser(UserCreateRequest req);
    List<User> getAllUsers();
    Optional<User> getUserById(Integer id);
    User updateUser(Integer id, User userDetails);
    void deleteUser(Integer id);
    TokenResponse verify(UserLoginRequest req);
    TokenResponse refreshToken(String refreshToken);
}
