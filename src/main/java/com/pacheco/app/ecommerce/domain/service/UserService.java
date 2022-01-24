package com.pacheco.app.ecommerce.domain.service;

import com.pacheco.app.ecommerce.api.dto.AddressDTO;
import com.pacheco.app.ecommerce.api.dto.UserDTO;
import com.pacheco.app.ecommerce.domain.exception.BusinessException;
import com.pacheco.app.ecommerce.domain.exception.UserAddressNotFound;
import com.pacheco.app.ecommerce.domain.exception.UserAlreadyExists;
import com.pacheco.app.ecommerce.domain.exception.UserNotFoundException;
import com.pacheco.app.ecommerce.domain.model.Address;
import com.pacheco.app.ecommerce.domain.model.account.Customer;
import com.pacheco.app.ecommerce.domain.model.account.User;
import com.pacheco.app.ecommerce.domain.repository.AddressRepository;
import com.pacheco.app.ecommerce.domain.repository.UserRepository;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Service;

import java.util.List;
import java.util.Optional;

import static com.pacheco.app.ecommerce.domain.mapper.AddressMapper.toAddress;
import static com.pacheco.app.ecommerce.domain.mapper.UserMapper.*;

@Service
public class UserService {

    @Autowired
    private UserRepository userRepository;

    @Autowired
    private AddressRepository addressRepository;

    @Autowired
    private PasswordEncoder passwordEncoder;

    public User registerConsumer(UserDTO customerDTO) {
        Customer customer = toCustomer(customerDTO);
        return saveUser(customer);
    }

    public User register(UserDTO userDTO) {
        User user = toUser(userDTO);
        return saveUser(user);
    }

    public User saveUser(User user) {
        Optional<User> alreadyRegistredUser = userRepository.findByEmail(user.getEmail());

        if (alreadyRegistredUser.isPresent()) {
            throw new UserAlreadyExists(user.getEmail());
        }

        user.setPassword(passwordEncoder.encode(user.getPassword()));

        return userRepository.save(user);
    }

    public User findByEmail(String email) {
        return userRepository.findByEmail(email).orElseThrow(() -> new UserNotFoundException(email));
    }

    public Customer findCustomer(String email) {
        User user = findByEmail(email);
        if (user instanceof Customer) {
            return (Customer) user;
        }
        else {
            throw new BusinessException(String.format("user with email '%s' is not a customer", email));
        }
    }

    public String getCurrentUsername() {
        return SecurityContextHolder.getContext().getAuthentication().getName();
    }

    public Customer getCurrentCustomer() {
        return findCustomer(getCurrentUsername());
    }

    public List<Address> getUserAddresses() {
        return getCurrentCustomer().getAddresses();
    }

    public Address registerAddress(AddressDTO addressDTO) {
        Customer customer = getCurrentCustomer();
        Address address = toAddress(addressDTO);

        address = addressRepository.save(address);
        customer.getAddresses().add(address);
        userRepository.save(customer);

        return address;
    }

    public Address updateAddress(Long addressId, AddressDTO addressDTO) {
        Customer customer = getCurrentCustomer();
        Optional<Address> address = customer.getAddresses().stream()
                .filter(add -> add.getId().equals(addressId))
                .findFirst();

        if (address.isEmpty()) {
            throw new UserAddressNotFound(addressId, customer.getEmail());
        }

        Address newAddress = toAddress(addressDTO);
        newAddress.setId(addressId);

        return addressRepository.save(newAddress);
    }

    public void removeAddress(Long addressId) {
        Customer customer = getCurrentCustomer();
        Optional<Address> address = customer.getAddresses().stream()
                .filter(add -> add.getId().equals(addressId))
                .findFirst();

        if (address.isEmpty()) {
            throw new UserAddressNotFound(addressId, customer.getEmail());
        }

        customer.getAddresses().remove(address.get());
        userRepository.save(customer);

        addressRepository.delete(address.get());
    }
}
