package com.pacheco.app.ecommerce.domain.exception;


public class UserAddressNotFound extends NotFoundEntityException {
    private final static String MESSAGE = "address with id '%d' not found for user '%s'";

    public UserAddressNotFound(Long addressId, String email) {
        super(String.format(MESSAGE, addressId, email));
    }
}
