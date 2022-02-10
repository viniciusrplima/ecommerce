package com.pacheco.app.ecommerce.domain.security;

import com.pacheco.app.ecommerce.domain.model.account.UserPermission;
import org.springframework.http.HttpMethod;
import org.springframework.security.config.annotation.web.builders.HttpSecurity;
import org.springframework.security.config.annotation.web.configurers.ExpressionUrlAuthorizationConfigurer;

import java.util.Arrays;

public class PermissionsConfigurer {

    private HttpSecurity http;
    private ExpressionUrlAuthorizationConfigurer<HttpSecurity>.ExpressionInterceptUrlRegistry authorization;
    private String[] routes;

    public PermissionsConfigurer(HttpSecurity http) {
        this.http = http;
    }

    public static PermissionsConfigurer configure(HttpSecurity httpSecurity) {
        return new PermissionsConfigurer(httpSecurity);
    }

    public PermissionsConfigurer authorize() throws Exception {
        if (authorization != null) {
            throw new RuntimeException("The authorize method was already called");
        }
        authorization = http.authorizeRequests();
        return this;
    }
    
    public PermissionsConfigurer permissionTo(String... r) {
        routes = r;
        return this;
    }

    private PermissionsConfigurer addRoutes(HttpMethod method, UserPermission permission) {
        authorization.antMatchers(method, generalizeRoutes(routes)).hasAnyAuthority(permission.getPermission());
        return this;
    }

    private PermissionsConfigurer addFreeRoutes(HttpMethod method) {
        authorization.antMatchers(method, generalizeRoutes(routes)).permitAll();
        return this;
    }

    public PermissionsConfigurer read(UserPermission permission) {
        return addRoutes(HttpMethod.GET, permission);
    }

    public PermissionsConfigurer write(UserPermission permission) {
        return addRoutes(HttpMethod.POST, permission)
                .addRoutes(HttpMethod.PUT, permission);
    }

    public PermissionsConfigurer delete(UserPermission permission) {
        return addRoutes(HttpMethod.DELETE, permission);
    }

    public PermissionsConfigurer freeRead() {
        return addFreeRoutes(HttpMethod.GET);
    }

    public PermissionsConfigurer free() {
        return addFreeRoutes(HttpMethod.GET)
                .addFreeRoutes(HttpMethod.POST)
                .addFreeRoutes(HttpMethod.PUT)
                .addFreeRoutes(HttpMethod.DELETE);
    }

    public void done() {
        authorization.anyRequest().authenticated();
    }
    
    private String[] generalizeRoutes(String... routes) {
        return Arrays.asList(routes).stream()
                .map(route -> (route + "/**"))
                .toArray(String[]::new);
    }

}
