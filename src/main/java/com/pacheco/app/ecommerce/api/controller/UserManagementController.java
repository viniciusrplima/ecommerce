package com.pacheco.app.ecommerce.api.controller;

import com.pacheco.app.ecommerce.api.mapper.UserMapper;
import com.pacheco.app.ecommerce.api.model.input.UserInput;
import com.pacheco.app.ecommerce.api.model.output.UserModel;
import com.pacheco.app.ecommerce.core.validation.Groups;
import com.pacheco.app.ecommerce.domain.repository.UserRepository;
import com.pacheco.app.ecommerce.domain.service.UserService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.validation.annotation.Validated;
import org.springframework.web.bind.annotation.*;

import javax.validation.groups.Default;
import java.util.List;

@RestController
@RequestMapping(Routes.MANAGEMENT + Routes.USERS)
public class UserManagementController {

    @Autowired
    private UserRepository userRepository;

    @Autowired
    private UserService userService;

    @Autowired
    private UserMapper userMapper;

    @GetMapping
    public List<UserModel> getUsers() {
        return userMapper.toUserRepresentationList(userRepository.findAll());
    }

    @PostMapping
    @ResponseStatus(HttpStatus.CREATED)
    public UserModel registerUser(@RequestBody @Validated({Default.class, Groups.UserRole.class}) UserInput userDTO) {
        return userMapper.toUserRepresentation(
                userService.register(userMapper.toUserModel(userDTO)));
    }
}
