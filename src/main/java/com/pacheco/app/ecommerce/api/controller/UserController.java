package com.pacheco.app.ecommerce.api.controller;

import com.pacheco.app.ecommerce.api.dto.AddressDTO;
import com.pacheco.app.ecommerce.api.dto.UserDTO;
import com.pacheco.app.ecommerce.core.validation.Groups;
import com.pacheco.app.ecommerce.domain.model.Address;
import com.pacheco.app.ecommerce.domain.model.account.User;
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

    private final static String ADDRESSES = "/addresses";

    @Autowired
    private UserService userService;

    @Autowired
    private UserRepository userRepository;

    @PostMapping(Routes.REGISTER)
    @ResponseStatus(HttpStatus.CREATED)
    public User register(@RequestBody @Validated({Default.class, Groups.ConsumerInfo.class}) UserDTO userDTO) {
        return userService.registerConsumer(userDTO);
    }

    @GetMapping(Routes.USERS + ADDRESSES)
    public List<Address> getAddresses() {
        return userService.getUserAddresses();
    }

    @PostMapping(Routes.USERS + ADDRESSES)
    @ResponseStatus(HttpStatus.CREATED)
    public Address registerUserAdress(@RequestBody @Valid AddressDTO addressDTO) {
        return userService.registerAddress(addressDTO);
    }

    @PutMapping(Routes.USERS + ADDRESSES + "/{addressId}")
    public Address updateAddress(@PathVariable Long addressId, @RequestBody @Valid AddressDTO addressDTO) {
        return userService.updateAddress(addressId, addressDTO);
    }

    @DeleteMapping(Routes.USERS + ADDRESSES + "/{addressId}")
    @ResponseStatus(HttpStatus.NO_CONTENT)
    public void removeAddress(@PathVariable Long addressId) {
        userService.removeAddress(addressId);
    }
}
