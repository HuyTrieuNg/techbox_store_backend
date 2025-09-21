package vn.techbox.techbox_store.user.service.impl;

import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.security.core.userdetails.UsernameNotFoundException;
import org.springframework.stereotype.Service;
import vn.techbox.techbox_store.user.model.User;
import vn.techbox.techbox_store.user.model.UserPrincipal;
import vn.techbox.techbox_store.user.repository.UserRepository;
import vn.techbox.techbox_store.user.service.MyUserDetailService;

@Service
public class MyUserDetailServiceImpl implements MyUserDetailService {
    private final UserRepository userRepository;

    public MyUserDetailServiceImpl(UserRepository userRepository) {
        this.userRepository = userRepository;
    }

    @Override
    public UserDetails loadUserByUsername(String username) throws UsernameNotFoundException {
        User user = userRepository.findByUsername(username);
        if (user == null) {
            System.out.println("User not found with username: " + username);
            throw new UsernameNotFoundException("User not found with username: " + username);
        }
        return new UserPrincipal(user);
    }
}
