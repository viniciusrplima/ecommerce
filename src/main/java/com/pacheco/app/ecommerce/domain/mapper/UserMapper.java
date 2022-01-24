package com.pacheco.app.ecommerce.domain.mapper;

import com.pacheco.app.ecommerce.api.dto.UserDTO;
import com.pacheco.app.ecommerce.domain.model.account.Customer;
import com.pacheco.app.ecommerce.domain.model.account.User;
import com.pacheco.app.ecommerce.domain.model.account.UserRole;

public class UserMapper {

    public static User toUser(UserDTO userDTO) {
        return User.builder()
                .name(userDTO.getName())
                .email(userDTO.getEmail())
                .password(userDTO.getPassword())
                .role(UserRole.valueOf(userDTO.getRole().toUpperCase()))
                .build();
    }

    public static Customer toCustomer(UserDTO userDTO) {
        return Customer.customerBuilder()
                .email(userDTO.getEmail())
                .name(userDTO.getName())
                .password(userDTO.getPassword())
                .cpf(userDTO.getCpf())
                .phone(userDTO.getPhone())
                .role(UserRole.CUSTOMER)
                .build();
    }

}
