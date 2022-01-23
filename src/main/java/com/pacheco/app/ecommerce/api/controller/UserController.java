package com.pacheco.app.ecommerce.api.controller;

import com.pacheco.app.ecommerce.api.dto.UserDTO;
import com.pacheco.app.ecommerce.core.validation.Groups;
import com.pacheco.app.ecommerce.domain.model.account.User;
import com.pacheco.app.ecommerce.domain.model.account.UserRole;
import com.pacheco.app.ecommerce.domain.repository.UserRepository;
import com.pacheco.app.ecommerce.domain.service.UserService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.validation.annotation.Validated;
import org.springframework.web.bind.annotation.*;

import javax.validation.Valid;
import javax.validation.groups.Default;
import java.util.List;

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
    public User registerUser(@RequestBody @Validated({Default.class, Groups.UserRole.class}) UserDTO userDTO) {
        return userService.register(userDTO, UserRole.valueOf(userDTO.getRole().toUpperCase()));
    }
}
