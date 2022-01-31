package com.pacheco.app.ecommerce.domain.service;

import com.pacheco.app.ecommerce.domain.exception.UserNotFoundException;
import com.pacheco.app.ecommerce.domain.model.account.User;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.security.core.userdetails.UserDetailsService;
import org.springframework.security.core.userdetails.UsernameNotFoundException;
import org.springframework.stereotype.Component;
import org.springframework.stereotype.Service;

import java.util.List;
import java.util.stream.Collectors;

@Service
public class AuthenticationUserDetailsService implements UserDetailsService {

    @Autowired
    private UserService userService;

    @Override
    public UserDetails loadUserByUsername(String username) throws UsernameNotFoundException {
        User user = null;

        try {
            user = userService.findByEmail(username);
        }
        catch (UserNotFoundException e) {
            throw new UsernameNotFoundException(username);
        }

        return org.springframework.security.core.userdetails.User.builder()
                .username(username)
                .password(user.getPassword())
                .authorities(user.getRole().getAuthorities())
                .build();
    }
}
