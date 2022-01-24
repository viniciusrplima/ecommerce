package com.pacheco.app.ecommerce.domain.service;

import com.pacheco.app.ecommerce.api.dto.UserDTO;
import com.pacheco.app.ecommerce.domain.exception.UserNotFoundException;
import com.pacheco.app.ecommerce.domain.model.account.Customer;
import com.pacheco.app.ecommerce.domain.model.account.User;
import com.pacheco.app.ecommerce.domain.repository.AddressRepository;
import com.pacheco.app.ecommerce.domain.repository.UserRepository;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Service;

import static com.pacheco.app.ecommerce.domain.mapper.UserMapper.*;

@Service
public class UserService {

    @Autowired
    private UserRepository repository;

    @Autowired
    private AddressRepository addressRepository;

    @Autowired
    private PasswordEncoder passwordEncoder;

    public User registerConsumer(UserDTO customerDTO) {
        Customer customer = toCustomer(customerDTO);
        customer.setPassword(passwordEncoder.encode(customerDTO.getPassword()));

        return repository.save(customer);
    }

    public User register(UserDTO userDTO) {
        User user = toUser(userDTO);
        user.setPassword(passwordEncoder.encode(user.getPassword()));

        return repository.save(user);
    }

    public User findByEmail(String email) {
        return repository.findByEmail(email).orElseThrow(() -> new UserNotFoundException(email));
    }
}
