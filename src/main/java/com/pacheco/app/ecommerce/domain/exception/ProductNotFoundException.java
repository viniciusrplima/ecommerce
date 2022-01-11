package com.pacheco.app.ecommerce.domain.exception;

public class ProductNotFoundException extends NotFoundEntityException {

    private static final String MESSAGE = "Product with id '%d' not found";

    public ProductNotFoundException(Long id) {
        super(String.format(MESSAGE, id));
    }
}
