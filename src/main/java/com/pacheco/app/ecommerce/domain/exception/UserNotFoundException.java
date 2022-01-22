package com.pacheco.app.ecommerce.domain.exception;

public class UserNotFoundException extends NotFoundEntityException {
    private static final String MESSAGE = "User with email '%s' not found";

    public UserNotFoundException(String email) {
        super(String.format(MESSAGE, email));
    }
}
