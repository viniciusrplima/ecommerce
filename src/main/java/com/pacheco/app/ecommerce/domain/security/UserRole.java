package com.pacheco.app.ecommerce.domain.security;

import java.util.Arrays;
import java.util.Set;
import java.util.stream.Collectors;

import static com.pacheco.app.ecommerce.domain.security.UserPermission.*;

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
}
