package com.pacheco.app.ecommerce.domain.security;

import com.pacheco.app.ecommerce.api.controller.Routes;
import com.pacheco.app.ecommerce.api.exceptionhandler.ApiExceptionHandler;
import com.pacheco.app.ecommerce.domain.model.account.UserPermission;
import com.pacheco.app.ecommerce.domain.security.jwt.JwtAuthenticationFilter;
import com.pacheco.app.ecommerce.domain.security.jwt.JwtConfig;
import com.pacheco.app.ecommerce.domain.security.jwt.JwtRequestFilter;
import com.pacheco.app.ecommerce.domain.security.jwt.JwtTokenUtil;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.security.authentication.AuthenticationManager;
import org.springframework.security.config.BeanIds;
import org.springframework.security.config.annotation.web.builders.HttpSecurity;
import org.springframework.security.config.annotation.web.configuration.EnableWebSecurity;
import org.springframework.security.config.annotation.web.configuration.WebSecurityConfigurerAdapter;
import org.springframework.security.config.http.SessionCreationPolicy;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.security.web.authentication.UsernamePasswordAuthenticationFilter;
import org.springframework.web.cors.CorsConfiguration;
import org.springframework.web.cors.CorsConfigurationSource;
import org.springframework.web.cors.UrlBasedCorsConfigurationSource;

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

    @Autowired
    private ApiExceptionHandler apiExceptionHandler;

    @Override
    protected void configure(HttpSecurity http) throws Exception {
        http
            .cors().and()
            .csrf().disable()
            .sessionManagement().sessionCreationPolicy(SessionCreationPolicy.STATELESS)
            .and()
            .addFilter(new JwtAuthenticationFilter(
                    authenticationManagerBean(), jwtConfig, jwtTokenUtil, apiExceptionHandler))
            .addFilterBefore(jwtRequestFilter, UsernamePasswordAuthenticationFilter.class);

        PermissionsConfigurer.configure(http)
                .authorize()
                .permissionTo(Routes.REGISTER, Routes.EMAIL_VERIFICATION).free()
                .permissionTo(Routes.MANAGEMENT)
                    .read(UserPermission.USER_READ)
                    .write(UserPermission.USER_WRITE)
                    .delete(UserPermission.USER_DELETE)
                .permissionTo(Routes.PRODUCTS, Routes.PRODUCT_TYPES)
                    .freeRead()
                    .write(UserPermission.PRODUCT_WRITE)
                    .delete(UserPermission.PRODUCT_DELETE)
                .done();
    }

    @Bean(name = BeanIds.AUTHENTICATION_MANAGER)
    @Override
    public AuthenticationManager authenticationManagerBean() throws Exception {
        return super.authenticationManagerBean();
    }

    @Bean
    CorsConfigurationSource corsConfigurationSource() {
        CorsConfiguration config = new CorsConfiguration();
        config.setAllowedOrigins(List.of("http://localhost:3001"));
        config.setAllowedMethods(List.of("OPTIONS", "GET", "PUT", "POST", "DELETE"));
        config.setAllowedHeaders(List.of("*"));
        config.setExposedHeaders(Arrays.asList("Authorization"));
        config.setAllowCredentials(true);

        UrlBasedCorsConfigurationSource source = new UrlBasedCorsConfigurationSource();
        source.registerCorsConfiguration("/**", config);
        return source;
    }
}
