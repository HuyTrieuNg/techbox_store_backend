package vn.techbox.techbox_store.user.service.impl;

import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.security.authentication.AuthenticationManager;
import org.springframework.security.authentication.UsernamePasswordAuthenticationToken;
import org.springframework.security.core.Authentication;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import vn.techbox.techbox_store.user.dto.*;
import vn.techbox.techbox_store.user.model.Account;
import vn.techbox.techbox_store.user.model.Address;
import vn.techbox.techbox_store.user.model.Role;
import vn.techbox.techbox_store.user.model.User;
import vn.techbox.techbox_store.user.repository.AccountRepository;
import vn.techbox.techbox_store.user.repository.RoleRepository;
import vn.techbox.techbox_store.user.repository.UserRepository;
import vn.techbox.techbox_store.user.security.UserPrincipal;
import vn.techbox.techbox_store.user.service.AuthService;
import vn.techbox.techbox_store.user.service.UserService;

import java.time.LocalDateTime;
import java.util.ArrayList;
import java.util.HashSet;
import java.util.List;
import java.util.Optional;
import java.util.Set;

import org.springframework.beans.factory.annotation.Value;

@Service("userService")
public class UserServiceImpl implements UserService {

    private final UserRepository userRepository;
    private final AccountRepository accountRepository;
    private final RoleRepository roleRepository;
    private final AuthenticationManager authManager;
    private final AuthService authService;
    private final PasswordEncoder encoder;

    @Value("${user.address.max-per-user}")
    private int maxAddressesPerUser;

    public UserServiceImpl(UserRepository userRepository,
                          AccountRepository accountRepository,
                          RoleRepository roleRepository,
                          AuthenticationManager authManager,
                          AuthService authService,
                          PasswordEncoder encoder) {
        this.userRepository = userRepository;
        this.accountRepository = accountRepository;
        this.roleRepository = roleRepository;
        this.authManager = authManager;
        this.authService = authService;
        this.encoder = encoder;
    }

    @Transactional
    public User createUser(UserCreateRequest req) {
        if (req.email() == null || req.email().isBlank()) {
            throw new RuntimeException("Email is required");
        }
        if (accountRepository.existsByEmail(req.email())) {
            throw new RuntimeException("Email already exists: " + req.email());
        }

        if (req.addresses() != null && req.addresses().size() > maxAddressesPerUser) {
            throw new RuntimeException("Address limit exceeded. Max allowed per user: " + maxAddressesPerUser);
        }

        Account account = Account.builder()
                .email(req.email())
                .passwordHash(encoder.encode(req.password()))
                .isActive(true)
                .isLocked(false)
                .build();

        Set<Role> roles = new HashSet<>();
        if (req.roleNames() != null && !req.roleNames().isEmpty()) {
            for (String roleName : req.roleNames()) {
                Role role = roleRepository.findByName(roleName)
                        .orElseThrow(() -> new RuntimeException("Role not found: " + roleName));
                roles.add(role);
            }
        } else {
            Role customerRole = roleRepository.findByName("ROLE_CUSTOMER")
                    .orElseThrow(() -> new RuntimeException("Default CUSTOMER role not found"));
            roles.add(customerRole);
        }

        User user = User.builder()
                .firstName(req.firstName())
                .lastName(req.lastName())
                .phone(req.phone())
                .dateOfBirth(req.dateOfBirth())
                .account(account)
                .roles(roles)
                .addresses(new ArrayList<>())
                .build();

        // Save user first to get ID
        User savedUser = userRepository.save(user);

        // Create addresses if provided
        if (req.addresses() != null && !req.addresses().isEmpty()) {
            List<Address> addresses = new ArrayList<>();
            boolean hasDefault = req.addresses().stream().anyMatch(addr -> addr.isDefault() != null && addr.isDefault());

            for (int i = 0; i < req.addresses().size(); i++) {
                AddressCreateRequest addrReq = req.addresses().get(i);
                Address address = Address.builder()
                        .streetAddress(addrReq.streetAddress())
                        .ward(addrReq.ward())
                        .district(addrReq.district())
                        .city(addrReq.city())
                        .postalCode(addrReq.postalCode())
                        .isDefault(!hasDefault && i == 0 || (addrReq.isDefault() != null ? addrReq.isDefault() : false))
                        .addressType(addrReq.addressType() != null ? addrReq.addressType() : "HOME")
                        .user(savedUser)
                        .build();
                addresses.add(address);
            }
            savedUser.setAddresses(addresses);
            savedUser = userRepository.save(savedUser);
        }

        return savedUser;
    }

    public List<User> getAllUsers() {
        return userRepository.findAll();
    }

    public Page<User> getAllUsersWithPagination(Pageable pageable) {
        return userRepository.findAllWithAddresses(pageable);
    }

    @Override
    public Page<User> getAllUsersWithPagination(Pageable pageable, boolean includeDeleted) {
        if (includeDeleted) {
            return userRepository.findAllWithAddressesIncludingDeleted(pageable);
        }
        return userRepository.findAllWithAddresses(pageable);
    }

    public Optional<User> getUserById(Integer id) {
        return Optional.ofNullable(userRepository.findByIdWithRoles(id));
    }

    public Optional<User> getUserByEmail(String email) {
        return userRepository.findByAccountEmail(email);
    }

    public Optional<User> getUserByEmailWithAddresses(String email) {
        return userRepository.findByAccountEmailWithAddresses(email);
    }

    public boolean isCurrentUser(UserPrincipal userPrincipal, Integer userId) {
        return userPrincipal.getId().equals(userId);
    }

    @Transactional
    public User updateUser(Integer id, UserUpdateRequest req) {
        User user = userRepository.findByIdWithRoles(id);
        if (user == null) {
            throw new RuntimeException("User not found with id: " + id);
        }

        // Update user info
        if (req.firstName() != null) {
            user.setFirstName(req.firstName());
        }
        if (req.lastName() != null) {
            user.setLastName(req.lastName());
        }
        if (req.phone() != null) {
            user.setPhone(req.phone());
        }
        if (req.dateOfBirth() != null) {
            user.setDateOfBirth(req.dateOfBirth());
        }

        // Update account info
        Account account = user.getAccount();
        if (req.email() != null && !req.email().equals(account.getEmail())) {
            if (accountRepository.existsByEmail(req.email())) {
                throw new RuntimeException("Email already exists: " + req.email());
            }
            account.setEmail(req.email());
        }

        if (req.isActive() != null) {
            account.setIsActive(req.isActive());
        }

        if (req.isLocked() != null) {
            account.setIsLocked(req.isLocked());
        }

        // Update roles if provided
        if (req.roleNames() != null && !req.roleNames().isEmpty()) {
            Set<Role> roles = new HashSet<>();
            for (String roleName : req.roleNames()) {
                Role role = roleRepository.findByName(roleName)
                    .orElseThrow(() -> new RuntimeException("Role not found: " + roleName));
                roles.add(role);
            }
            user.setRoles(roles);
        }

        // Update addresses if provided (this replaces all addresses - for individual address updates, use address endpoints)
        if (req.addresses() != null) {
            if (req.addresses().size() > maxAddressesPerUser) {
                throw new RuntimeException("Address limit exceeded. Max allowed per user: " + maxAddressesPerUser);
            }
            // Soft delete existing addresses
            user.getAddresses().forEach(addr -> addr.setDeletedAt(LocalDateTime.now()));

            // Create new addresses
            List<Address> newAddresses = new ArrayList<>();
            boolean hasDefault = req.addresses().stream().anyMatch(addr -> addr.isDefault() != null && addr.isDefault());

            for (int i = 0; i < req.addresses().size(); i++) {
                AddressUpdateRequest addrReq = req.addresses().get(i);
                Address address = Address.builder()
                        .streetAddress(addrReq.streetAddress())
                        .ward(addrReq.ward())
                        .district(addrReq.district())
                        .city(addrReq.city())
                        .postalCode(addrReq.postalCode())
                        .isDefault(!hasDefault && i == 0 || (addrReq.isDefault() != null ? addrReq.isDefault() : false))
                        .addressType(addrReq.addressType() != null ? addrReq.addressType() : "HOME")
                        .user(user)
                        .build();
                newAddresses.add(address);
            }
            user.getAddresses().addAll(newAddresses);
        }

        return userRepository.save(user);
    }

    public void deleteUser(Integer id) {
        User user = userRepository.findByIdWithRoles(id);
        if (user == null) {
            throw new RuntimeException("User not found with id: " + id);
        }

        // Chỉ set isActive = false, không động đến deletedAt
        user.getAccount().setIsActive(false);

        userRepository.save(user);
    }

    public void restoreUser(Integer id) {
        User user = userRepository.findByIdIncludingDeleted(id)
            .orElseThrow(() -> new RuntimeException("User not found with id: " + id));

        if (user.getAccount().getIsActive()) {
            throw new RuntimeException("User is not deleted");
        }

        // Restore bằng cách set isActive = true
        user.getAccount().setIsActive(true);

        userRepository.save(user);
    }

    public TokenResponse verify(UserLoginRequest req) {
        try {
            Authentication authentication = authManager.authenticate(
                    new UsernamePasswordAuthenticationToken(req.email(), req.password())
            );

            if (authentication.isAuthenticated()) {
                // Get user to get userId for token generation
                User user = getUserByEmail(req.email())
                    .orElseThrow(() -> new RuntimeException("User not found"));

                // Use new token pair generation method that stores refresh token in database
                return authService.generateTokenPair(user.getId());
            }
            throw new RuntimeException("Authentication failed");
        } catch (Exception e) {
            throw new RuntimeException("Login failed: " + e.getMessage());
        }
    }

    @Override
    @Transactional
    public void updatePasswordByEmail(String email, String newPassword) {
        Optional<User> optUser = getUserByEmail(email);
        if (optUser.isEmpty()) {
            throw new RuntimeException("User not found for email: " + email);
        }
        User user = optUser.get();
        Account account = user.getAccount();
        account.setPasswordHash(encoder.encode(newPassword));
        userRepository.save(user);
    }

    @Override
    public Page<User> getUsersByRole(String roleName, Pageable pageable) {
        return userRepository.findByRoleName(roleName, pageable);
    }

    @Override
    public Page<User> getUsersByRole(String roleName, Pageable pageable, boolean includeDeleted) {
        if (includeDeleted) {
            return userRepository.findByRoleNameIncludingDeleted(roleName, pageable);
        }
        return userRepository.findByRoleName(roleName, pageable);
    }

    @Override
    public Page<User> searchUsersByName(String searchTerm, Pageable pageable) {
        return userRepository.searchByName(searchTerm, pageable);
    }
}
