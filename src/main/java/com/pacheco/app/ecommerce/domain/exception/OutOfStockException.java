package com.pacheco.app.ecommerce.domain.exception;

public class OutOfStockException extends BusinessException {

    private static final String MESSAGE = "Product '%s' is out of stock";

    public OutOfStockException(String name) {
        super(String.format(MESSAGE, name));
    }

}
