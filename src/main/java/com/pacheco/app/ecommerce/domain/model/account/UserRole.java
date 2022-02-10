package com.pacheco.app.ecommerce.domain.model.account;

import com.pacheco.app.ecommerce.domain.model.account.UserPermission;
import org.springframework.security.core.GrantedAuthority;
import org.springframework.security.core.authority.SimpleGrantedAuthority;

import java.util.Arrays;
import java.util.Set;
import java.util.stream.Collectors;

import static com.pacheco.app.ecommerce.domain.model.account.UserPermission.PRODUCT_DELETE;
import static com.pacheco.app.ecommerce.domain.model.account.UserPermission.PRODUCT_READ;
import static com.pacheco.app.ecommerce.domain.model.account.UserPermission.PRODUCT_WRITE;
import static com.pacheco.app.ecommerce.domain.model.account.UserPermission.USER_DELETE;
import static com.pacheco.app.ecommerce.domain.model.account.UserPermission.USER_READ;
import static com.pacheco.app.ecommerce.domain.model.account.UserPermission.USER_WRITE;

public enum UserRole {

    ADMIN(set(
            USER_READ,
            USER_WRITE,
            USER_DELETE,
            PRODUCT_READ,
            PRODUCT_WRITE,
            PRODUCT_DELETE
    )),
    EMPLOYEE(set(
            USER_READ,
            USER_WRITE,
            PRODUCT_READ,
            PRODUCT_WRITE
    )),
    CUSTOMER(set(
            PRODUCT_READ
    ));

    private final Set<String> permissions;

    UserRole(Set<String> permissions) {
        this.permissions = permissions;
    }

    private static Set<String> set(UserPermission... permissions) {
        return Arrays.asList(permissions).stream()
                .map(UserPermission::getPermission)
                .collect(Collectors.toSet());
    }

    public Set<GrantedAuthority> getAuthorities() {
        Set<GrantedAuthority> authorities = getPermissions();
        authorities.add(new SimpleGrantedAuthority("ROLE_" + name()));
        return authorities;
    }

    public Set<GrantedAuthority> getPermissions() {
        return permissions.stream()
                .map(SimpleGrantedAuthority::new)
                .collect(Collectors.toSet());
    }
}
