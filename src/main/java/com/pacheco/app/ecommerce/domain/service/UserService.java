package com.pacheco.app.ecommerce.domain.service;

import com.pacheco.app.ecommerce.domain.exception.BusinessException;
import com.pacheco.app.ecommerce.domain.exception.UserAddressNotFound;
import com.pacheco.app.ecommerce.domain.exception.UserAlreadyExists;
import com.pacheco.app.ecommerce.domain.exception.UserNotFoundException;
import com.pacheco.app.ecommerce.domain.model.Address;
import com.pacheco.app.ecommerce.domain.model.account.Customer;
import com.pacheco.app.ecommerce.domain.model.account.EmailVerification;
import com.pacheco.app.ecommerce.domain.model.account.User;
import com.pacheco.app.ecommerce.domain.model.account.UserRole;
import com.pacheco.app.ecommerce.domain.repository.AddressRepository;
import com.pacheco.app.ecommerce.domain.repository.EmailVerificationRepository;
import com.pacheco.app.ecommerce.domain.repository.UserRepository;
import com.pacheco.app.ecommerce.infrastructure.email.EmailFactory;
import com.pacheco.app.ecommerce.infrastructure.email.EmailObject;
import com.pacheco.app.ecommerce.infrastructure.email.EmailService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Service;

import javax.transaction.Transactional;
import java.util.List;
import java.util.Optional;
import java.util.Random;

@Service
public class UserService {

    @Autowired private UserRepository userRepository;
    @Autowired private AddressRepository addressRepository;
    @Autowired private PasswordEncoder passwordEncoder;
    @Autowired private EmailVerificationRepository emailVerificationRepository;
    @Autowired private EmailFactory emailFactory;
    @Autowired private EmailService emailService;

    @Transactional
    public Customer registerConsumer(Customer customer) {
        customer.setRole(UserRole.CUSTOMER);
        customer.setActive(false);
        customer = (Customer) saveUser(customer);

        EmailVerification emailVerification = generateEmailVerificationCode(customer);
        sendEmailToVerifyEmail(emailVerification);

        return customer;
    }

    @Transactional
    public User register(User user) {
        user.setActive(true);
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
    public Address registerAddress(Address address) {
        Customer customer = getCurrentCustomer();

        address = addressRepository.save(address);
        customer.getAddresses().add(address);
        userRepository.save(customer);

        return address;
    }

    @Transactional
    public Address updateAddress(Long addressId, Address address) {
        Customer customer = getCurrentCustomer();
        Optional<Address> oldAddress = customer.getAddresses().stream()
                .filter(add -> add.getId().equals(addressId))
                .findFirst();

        if (oldAddress.isEmpty()) {
            throw new UserAddressNotFound(addressId, customer.getEmail());
        }

        Address newAddress = address;
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

    public void verifyEmailFromUser(String email, String code) {
        EmailVerification emailVerification = emailVerificationRepository.findById(email)
                .orElseThrow(() -> new UserNotFoundException(email));

        if (emailVerification.getCode().equals(code)) {
            emailVerificationRepository.delete(emailVerification);
            User user = findByEmail(email);
            user.setActive(true);
            userRepository.save(user);
        }
        else {
            throw new BusinessException("The verification code don't match");
        }
    }

    private EmailVerification generateEmailVerificationCode(Customer customer) {
        EmailVerification emailVerification = new EmailVerification();
        emailVerification.setUserEmail(customer.getEmail());
        emailVerification.setCode(String.valueOf(new Random().nextInt(89999) + 10000));

        return emailVerificationRepository.save(emailVerification);
    }

    private void sendEmailToVerifyEmail(EmailVerification emailVerification) {
        EmailObject emailObject = emailFactory.createEmailVerificationEmail(
                emailVerification.getUserEmail(), emailVerification.getCode());
        emailService.sendEmail(emailObject);
    }

}
