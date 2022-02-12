package com.pacheco.app.ecommerce.util;

import com.pacheco.app.ecommerce.api.model.input.UserInput;
import com.pacheco.app.ecommerce.domain.model.account.Customer;
import com.pacheco.app.ecommerce.domain.model.account.User;
import com.pacheco.app.ecommerce.domain.model.account.UserRole;
import com.pacheco.app.ecommerce.domain.security.jwt.JwtTokenUtil;
import com.pacheco.app.ecommerce.domain.service.UserService;
import lombok.Getter;
import lombok.NoArgsConstructor;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.security.authentication.UsernamePasswordAuthenticationToken;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.security.web.authentication.WebAuthenticationDetailsSource;
import org.springframework.stereotype.Component;

import java.util.ArrayList;
import java.util.List;

@Component
@Getter
@NoArgsConstructor
public class AuthenticationUtil {

    @Autowired private UserService userService;
    @Autowired private JwtTokenUtil jwtTokenUtil;

    private List<User> users;
    private User admin;
    private Customer customer;

    public void setUp() {
        users = new ArrayList<>();

        User user = new User();
        user.setEmail("admin@admin.com");
        user.setPassword("admin");
        user.setRole(UserRole.ADMIN);
        users.add(user);

        Customer customer = new Customer();
        customer.setEmail("customer@customer.com");
        customer.setPassword("customer");
        customer.setRole(UserRole.CUSTOMER);
        customer.setCpf("1235636998");
        customer.setPhone("25555666");
        users.add(customer);

        this.admin = userService.register(user);
        this.customer = (Customer) userService.registerConsumer(customer);
    }

    public void setContextUser(User user) {
        UserDetails userDetails = org.springframework.security.core.userdetails.User.builder()
                .username(user.getEmail())
                .password(user.getPassword())
                .roles(user.getRole().name())
                .authorities(user.getRole().getPermissions())
                .build();

        UsernamePasswordAuthenticationToken usernamePasswordAuthenticationToken =
                new UsernamePasswordAuthenticationToken(userDetails, null, userDetails.getAuthorities());

        SecurityContextHolder.getContext().setAuthentication(usernamePasswordAuthenticationToken);
    }

    public String getAdminToken() {
        return "Bearer " + jwtTokenUtil.generateToken(admin.getEmail());
    }

    public String getCustomerToken() {
        return "Bearer " + jwtTokenUtil.generateToken(customer.getEmail());
    }
}
