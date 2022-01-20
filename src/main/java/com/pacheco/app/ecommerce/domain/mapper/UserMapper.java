package com.pacheco.app.ecommerce.domain.mapper;

import com.pacheco.app.ecommerce.api.dto.UserDTO;
import com.pacheco.app.ecommerce.domain.model.account.User;

public class UserMapper {

    public static User toUser(UserDTO dto) {
        User user = new User();
        user.setEmail(dto.getEmail());
        user.setPassword(dto.getPassword());
        user.setName(dto.getName());

        return user;
    }

}
