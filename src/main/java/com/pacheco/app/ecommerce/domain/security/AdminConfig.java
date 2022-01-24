package com.pacheco.app.ecommerce.domain.security;

import com.pacheco.app.ecommerce.api.dto.UserDTO;
import com.pacheco.app.ecommerce.domain.exception.UserNotFoundException;
import com.pacheco.app.ecommerce.domain.model.account.UserRole;
import com.pacheco.app.ecommerce.domain.service.UserService;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.context.properties.ConfigurationProperties;
import org.springframework.context.annotation.Configuration;
import org.springframework.stereotype.Component;

import javax.annotation.PostConstruct;

@Configuration
public class AdminConfig {

    @Autowired
    private AdminProfile adminProfile;

    @Autowired
    private UserService userService;

    @PostConstruct
    public void createAdminUserIfNotExists() {
        try {
            userService.findByEmail(adminProfile.getUsername());
        }
        catch (UserNotFoundException e) {
            UserDTO user = new UserDTO();
            user.setEmail(adminProfile.getUsername());
            user.setPassword(adminProfile.getPassword());
            user.setName("admin");
            user.setRole("admin");

            userService.register(user);
        }
    }

    @ConfigurationProperties("application.security.admin")
    @Getter
    @Setter
    @NoArgsConstructor
    @Component
    public static class AdminProfile {
        private String username;
        private String password;
    }
}
