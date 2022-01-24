package com.pacheco.app.ecommerce.util;

import com.pacheco.app.ecommerce.api.model.input.UserInput;
import com.pacheco.app.ecommerce.domain.model.account.User;
import com.pacheco.app.ecommerce.domain.security.jwt.JwtTokenUtil;
import com.pacheco.app.ecommerce.domain.service.UserService;
import lombok.Getter;
import lombok.NoArgsConstructor;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;

@Component
@Getter
@NoArgsConstructor
public class AuthenticationUtil {

    @Autowired
    private UserService userService;

    @Autowired
    private JwtTokenUtil jwtTokenUtil;

    private User admin;
    private String token;

    public void setUp() {
        UserInput userDTO = new UserInput();
        userDTO.setEmail("admin@admin.com");
        userDTO.setPassword("admin");
        userDTO.setRole("admin");

        this.admin = userService.register(userDTO);
        this.token = jwtTokenUtil.generateToken(admin.getEmail());
    }

    public String getBearerToken() {
        return "Bearer " + token;
    }
}
