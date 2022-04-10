package com.pacheco.app.ecommerce.api.controller;

import com.pacheco.app.ecommerce.api.mapper.AddressMapper;
import com.pacheco.app.ecommerce.api.mapper.UserMapper;
import com.pacheco.app.ecommerce.api.model.input.AddressInput;
import com.pacheco.app.ecommerce.api.model.input.EmailVerificationInput;
import com.pacheco.app.ecommerce.api.model.input.UserInput;
import com.pacheco.app.ecommerce.api.model.output.AddressModel;
import com.pacheco.app.ecommerce.api.model.output.CustomerModel;
import com.pacheco.app.ecommerce.core.validation.Groups;
import com.pacheco.app.ecommerce.domain.model.Address;
import com.pacheco.app.ecommerce.domain.model.account.Customer;
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

    @Autowired
    private AddressMapper addressMapper;

    @Autowired
    private UserMapper userMapper;

    @PostMapping(Routes.REGISTER)
    @ResponseStatus(HttpStatus.CREATED)
    public CustomerModel register(
            @RequestBody @Validated({Default.class, Groups.ConsumerInfo.class}) UserInput userDTO) {
        return userMapper.toCustomerRepresentation(
                userService.registerConsumer(userMapper.toCustomerModel(userDTO)));
    }

    @PostMapping(Routes.EMAIL_VERIFICATION)
    public void verifyEmail(@RequestBody EmailVerificationInput emailVerificationDTO) {
        userService.verifyEmailFromUser(emailVerificationDTO.getEmail(), emailVerificationDTO.getCode());
    }

    @GetMapping(Routes.USERS + ADDRESSES)
    public List<AddressModel> getAddresses() {
        return addressMapper.toRepresentationList(userService.getUserAddresses());
    }

    @PostMapping(Routes.USERS + ADDRESSES)
    @ResponseStatus(HttpStatus.CREATED)
    public AddressModel registerUserAdress(@RequestBody @Valid AddressInput addressDTO) {
        Address address = userService.registerAddress(addressMapper.toModel(addressDTO));
        return addressMapper.toRepresentation(address);
    }

    @PutMapping(Routes.USERS + ADDRESSES + "/{addressId}")
    public AddressModel updateAddress(@PathVariable Long addressId, @RequestBody @Valid AddressInput addressDTO) {
        Address address = userService.updateAddress(addressId, addressMapper.toModel(addressDTO));
        return addressMapper.toRepresentation(address);
    }

    @DeleteMapping(Routes.USERS + ADDRESSES + "/{addressId}")
    @ResponseStatus(HttpStatus.NO_CONTENT)
    public void removeAddress(@PathVariable Long addressId) {
        userService.removeAddress(addressId);
    }
}
