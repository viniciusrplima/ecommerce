package com.pacheco.app.ecommerce.domain.service;

import com.pacheco.app.ecommerce.api.model.input.AddressInput;
import com.pacheco.app.ecommerce.api.model.input.UserInput;
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

import javax.transaction.Transactional;
import java.util.List;
import java.util.Optional;

import static com.pacheco.app.ecommerce.api.mapper.AddressMapper.toAddress;
import static com.pacheco.app.ecommerce.api.mapper.UserMapper.*;

@Service
public class UserService {

    @Autowired
    private UserRepository userRepository;

    @Autowired
    private AddressRepository addressRepository;

    @Autowired
    private PasswordEncoder passwordEncoder;

    @Transactional
    public User registerConsumer(UserInput customerDTO) {
        Customer customer = toCustomer(customerDTO);
        return saveUser(customer);
    }

    @Transactional
    public User register(UserInput userDTO) {
        User user = toUser(userDTO);
        return saveUser(user);
    }

    @Transactional
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

    @Transactional
    public Address registerAddress(AddressInput addressDTO) {
        Customer customer = getCurrentCustomer();
        Address address = toAddress(addressDTO);

        address = addressRepository.save(address);
        customer.getAddresses().add(address);
        userRepository.save(customer);

        return address;
    }

    @Transactional
    public Address updateAddress(Long addressId, AddressInput addressDTO) {
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

    @Transactional
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
