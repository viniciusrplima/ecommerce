package com.pacheco.app.ecommerce.domain.security;

import com.pacheco.app.ecommerce.api.controller.Routes;
import com.pacheco.app.ecommerce.domain.model.account.UserPermission;
import com.pacheco.app.ecommerce.domain.security.jwt.JwtAuthenticationFilter;
import com.pacheco.app.ecommerce.domain.security.jwt.JwtConfig;
import com.pacheco.app.ecommerce.domain.security.jwt.JwtRequestFilter;
import com.pacheco.app.ecommerce.domain.security.jwt.JwtTokenUtil;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.http.HttpMethod;
import org.springframework.security.authentication.AuthenticationManager;
import org.springframework.security.config.BeanIds;
import org.springframework.security.config.annotation.web.builders.HttpSecurity;
import org.springframework.security.config.annotation.web.configuration.EnableWebSecurity;
import org.springframework.security.config.annotation.web.configuration.WebSecurityConfigurerAdapter;
import org.springframework.security.config.http.SessionCreationPolicy;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.security.web.authentication.UsernamePasswordAuthenticationFilter;

import java.util.Arrays;
import java.util.List;


@Configuration
@EnableWebSecurity
public class SecurityConfiguration extends WebSecurityConfigurerAdapter {

    @Autowired
    private JwtConfig jwtConfig;

    @Autowired
    private JwtTokenUtil jwtTokenUtil;

    @Autowired
    private PasswordEncoder passwordEncoder;

    @Autowired
    private JwtRequestFilter jwtRequestFilter;

    @Override
    protected void configure(HttpSecurity http) throws Exception {
        http
            .cors().disable()
            .csrf().disable()
            .sessionManagement().sessionCreationPolicy(SessionCreationPolicy.STATELESS)
            .and()
            .addFilter(new JwtAuthenticationFilter(authenticationManagerBean(), jwtConfig, jwtTokenUtil))
            .addFilterBefore(jwtRequestFilter, UsernamePasswordAuthenticationFilter.class)
            .authorizeRequests()
            .antMatchers(Routes.REGISTER).permitAll()
            .antMatchers(generalizeRoutes(Routes.USERS, Routes.CART)).authenticated()
            .antMatchers(HttpMethod.GET, generalizeRoutes(Routes.MANAGEMENT)).hasAnyAuthority(UserPermission.USER_READ.getPermission())
            .antMatchers(HttpMethod.POST, generalizeRoutes(Routes.MANAGEMENT)).hasAnyAuthority(UserPermission.USER_WRITE.getPermission())
            .antMatchers(HttpMethod.PUT, generalizeRoutes(Routes.MANAGEMENT)).hasAnyAuthority(UserPermission.USER_WRITE.getPermission())
            .antMatchers(HttpMethod.DELETE, generalizeRoutes(Routes.MANAGEMENT)).hasAnyAuthority(UserPermission.USER_DELETE.getPermission())
            .antMatchers(HttpMethod.GET, generalizeRoutes(Routes.PRODUCTS, Routes.PRODUCT_TYPES)).permitAll()
            .antMatchers(HttpMethod.POST, generalizeRoutes(Routes.PRODUCTS, Routes.PRODUCT_TYPES)).hasAnyAuthority(UserPermission.PRODUCT_WRITE.getPermission())
            .antMatchers(HttpMethod.PUT, generalizeRoutes(Routes.PRODUCTS, Routes.PRODUCT_TYPES)).hasAnyAuthority(UserPermission.PRODUCT_WRITE.getPermission())
            .antMatchers(HttpMethod.DELETE, generalizeRoutes(Routes.PRODUCTS, Routes.PRODUCT_TYPES)).hasAnyAuthority(UserPermission.PRODUCT_DELETE.getPermission())
            .anyRequest().authenticated();
    }

    public String[] generalizeRoutes(String... routes) {
        return Arrays.asList(routes).stream()
                .map(route -> (route + "/**"))
                .toArray(String[]::new);
    }

    @Bean(name = BeanIds.AUTHENTICATION_MANAGER)
    @Override
    public AuthenticationManager authenticationManagerBean() throws Exception {
        return super.authenticationManagerBean();
    }
}
