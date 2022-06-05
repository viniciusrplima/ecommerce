package com.pacheco.app.ecommerce.domain.exception;

public class EntityUsedException extends BusinessException {

    private static final String MESSAGE = "Entity with id '%d' is used by other entity";

    public EntityUsedException(Long id) {
        super(String.format(MESSAGE, id));
    }

}
