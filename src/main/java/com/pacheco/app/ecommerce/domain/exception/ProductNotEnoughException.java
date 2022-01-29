package com.pacheco.app.ecommerce.domain.exception;

import java.math.BigInteger;

public class ProductNotEnoughException extends BusinessException {

    private static final String MESSAGE = "Product '%s' not enough. Has only %d items in stock";

    public ProductNotEnoughException(String name, BigInteger stock) {
        super(String.format(MESSAGE, name, stock));
    }
}
