package com.pacheco.app.ecommerce.domain.exception;

public class CartIsEmptyException extends BusinessException {

    private static final String MESSAGE = "Cart is empty";

    public CartIsEmptyException() {
        super(MESSAGE);
    }
}
