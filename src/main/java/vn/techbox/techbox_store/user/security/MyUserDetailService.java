package vn.techbox.techbox_store.user.security;

import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.security.core.userdetails.UserDetailsService;
import org.springframework.security.core.userdetails.UsernameNotFoundException;
import org.springframework.stereotype.Service;
import vn.techbox.techbox_store.user.model.Permission;
import vn.techbox.techbox_store.user.model.Role;
import vn.techbox.techbox_store.user.model.User;
import vn.techbox.techbox_store.user.repository.UserRepository;

import java.util.Set;
import java.util.stream.Collectors;

@Service
public class MyUserDetailService implements UserDetailsService {

    private final UserRepository userRepository;

    public MyUserDetailService(UserRepository userRepository) {
        this.userRepository = userRepository;
    }

    @Override
    public UserDetails loadUserByUsername(String email) throws UsernameNotFoundException {
        User user = userRepository.findByEmailForAuth(email);

        if (user == null) {
            throw new UsernameNotFoundException("User not found with email: " + email);
        }

        Set<String> roles = user.getRoles().stream()
                .map(Role::getName)
                .collect(Collectors.toSet());

        Set<String> permissions = user.getRoles().stream()
                .flatMap(role -> role.getPermissions().stream())
                .map(Permission::getName)
                .collect(Collectors.toSet());

        return new UserPrincipal(
                user.getId(),
                user.getAccount().getEmail(),
                user.getAccount().getPasswordHash(),
                user.getFirstName(),
                user.getLastName(),
                user.getAccount().getIsActive(),
                user.getAccount().getIsLocked(),
                roles,
                permissions
        );
    }
}
