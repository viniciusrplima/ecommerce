package com.pacheco.app.ecommerce.domain.exception;

public class PurchaseNotFoundException extends NotFoundEntityException {

    private static final String MESSAGE = "Purchase with id '%d' not found";

    public PurchaseNotFoundException(Long id) {
        super(String.format(MESSAGE, id));
    }
}
