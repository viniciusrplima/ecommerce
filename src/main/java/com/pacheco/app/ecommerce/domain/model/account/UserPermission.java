package com.pacheco.app.ecommerce.domain.model.account;

import lombok.Getter;

@Getter
public enum UserPermission {

    USER_READ("user:read"),
    USER_WRITE("user:write"),
    USER_DELETE("user:delete"),

    PRODUCT_READ("product:read"),
    PRODUCT_WRITE("product:write"),
    PRODUCT_DELETE("product:delete");

    private final String permission;

    UserPermission(String permission) {
        this.permission = permission;
    }
}
