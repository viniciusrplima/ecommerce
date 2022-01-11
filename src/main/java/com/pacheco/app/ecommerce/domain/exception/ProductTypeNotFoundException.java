package com.pacheco.app.ecommerce.domain.exception;

public class ProductTypeNotFoundException extends NotFoundEntityException {

    private static final String MESSAGE = "Product Type with id '%d' not found";

    public ProductTypeNotFoundException(Long id) {
        super(String.format(MESSAGE, id));
    }
}
