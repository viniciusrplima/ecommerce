package com.pacheco.app.ecommerce.domain.service;

import com.pacheco.app.ecommerce.api.dto.UserDTO;
import com.pacheco.app.ecommerce.domain.exception.UserNotFoundException;
import com.pacheco.app.ecommerce.domain.model.account.User;
import com.pacheco.app.ecommerce.domain.model.account.UserRole;
import com.pacheco.app.ecommerce.domain.repository.UserRepository;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Service;

import static com.pacheco.app.ecommerce.domain.mapper.UserMapper.toUser;

@Service
public class UserService {

    @Autowired
    private UserRepository repository;

    @Autowired
    private PasswordEncoder passwordEncoder;

    public User register(UserDTO dto) {
        return register(dto, UserRole.CUSTOMER);
    }

    public User register(UserDTO dto, UserRole role) {
        User user = toUser(dto);
        user.setPassword(passwordEncoder.encode(user.getPassword()));
        user.setRole(role);

        return repository.save(user);
    }

    public User findByEmail(String email) {
        return repository.findByEmail(email).orElseThrow(() -> new UserNotFoundException(email));
    }
}
