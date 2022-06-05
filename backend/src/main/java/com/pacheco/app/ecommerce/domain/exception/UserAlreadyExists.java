package com.pacheco.app.ecommerce.domain.exception;

public class UserAlreadyExists extends BusinessException {

    public final static String MESSAGE = "user with email '%s' already exists";

    public UserAlreadyExists(String email) {
        super(String.format(MESSAGE, email));
    }
}
