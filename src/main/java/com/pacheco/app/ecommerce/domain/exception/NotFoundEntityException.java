package com.pacheco.app.ecommerce.domain.exception;

public class NotFoundEntityException extends BusinessException {

    private static final String MESSAGE = "Entity with id '%d' not found";

    public NotFoundEntityException(Long id, Throwable cause) {
        this(String.format(MESSAGE, id), cause);
    }

    public NotFoundEntityException(String message, Throwable cause) {
        super(message, cause);
    }
}
