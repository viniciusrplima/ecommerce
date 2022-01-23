package com.pacheco.app.ecommerce.api.controller;

import com.pacheco.app.ecommerce.api.dto.UserDTO;
import com.pacheco.app.ecommerce.domain.model.account.User;
import com.pacheco.app.ecommerce.domain.model.account.UserRole;
import com.pacheco.app.ecommerce.domain.repository.UserRepository;
import com.pacheco.app.ecommerce.domain.service.UserService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.web.bind.annotation.*;

import javax.validation.Valid;
import javax.validation.ValidationException;
import java.util.List;
import java.util.Locale;

@RestController
public class UserController {

    @Autowired
    private UserService userService;

    @Autowired
    private UserRepository userRepository;

    @PostMapping(Routes.REGISTER)
    @ResponseStatus(HttpStatus.CREATED)
    public User register(@RequestBody @Valid UserDTO userDTO) {
        return userService.register(userDTO);
    }

    @GetMapping(Routes.USERS)
    public List<User> getUsers() {
        return userRepository.findAll();
    }

    @PostMapping(Routes.USERS)
    @ResponseStatus(HttpStatus.CREATED)
    public User registerUser(@RequestBody UserDTO userDTO) {
        if (userDTO.getRole() == null || userDTO.getRole().isBlank()) {
            throw new ValidationException("role could not be blank");
        }
        return userService.register(userDTO, UserRole.valueOf(userDTO.getRole().toUpperCase(Locale.ROOT)));
    }
}
