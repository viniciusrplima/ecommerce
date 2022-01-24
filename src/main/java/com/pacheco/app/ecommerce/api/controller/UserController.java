package com.pacheco.app.ecommerce.api.controller;

import com.pacheco.app.ecommerce.api.dto.UserDTO;
import com.pacheco.app.ecommerce.core.validation.Groups;
import com.pacheco.app.ecommerce.domain.model.account.User;
import com.pacheco.app.ecommerce.domain.repository.UserRepository;
import com.pacheco.app.ecommerce.domain.service.UserService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.validation.annotation.Validated;
import org.springframework.web.bind.annotation.*;

import javax.validation.groups.Default;

@RestController
public class UserController {

    @Autowired
    private UserService userService;

    @Autowired
    private UserRepository userRepository;

    @PostMapping(Routes.REGISTER)
    @ResponseStatus(HttpStatus.CREATED)
    public User register(@RequestBody @Validated({Default.class, Groups.ConsumerInfo.class}) UserDTO userDTO) {
        return userService.registerConsumer(userDTO);
    }

    /*@PostMapping(Routes.USERS + "/addresses")
    @ResponseStatus(HttpStatus.CREATED)
    public Address registerUserAdress(@RequestBody @Valid AddressDTO addressDTO) {
        return userService.registerAddress(addressDTO);
    }*/
}
