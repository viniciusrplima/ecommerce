package com.pacheco.app.ecommerce.domain.service;

import com.pacheco.app.ecommerce.api.dto.UserDTO;
import com.pacheco.app.ecommerce.domain.model.account.User;
import com.pacheco.app.ecommerce.domain.repository.UserRepository;
import org.checkerframework.checker.units.qual.A;
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
        User user = toUser(dto);
        user.setPassword(passwordEncoder.encode(user.getPassword()));

        return repository.save(user);
    }

}
