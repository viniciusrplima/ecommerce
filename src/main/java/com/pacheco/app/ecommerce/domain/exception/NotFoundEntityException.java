package com.pacheco.app.ecommerce.domain.exception;

public class NotFoundEntityException extends BusinessException {

    private static final String MESSAGE = "Entity with id '%d' not found";

    public NotFoundEntityException(Long id) {
        super(String.format(MESSAGE, id));
    }

    public NotFoundEntityException(String message) {
        super(message);
    }
}
