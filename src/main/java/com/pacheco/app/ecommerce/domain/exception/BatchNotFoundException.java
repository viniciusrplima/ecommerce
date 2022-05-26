package com.pacheco.app.ecommerce.domain.exception;

public class BatchNotFoundException extends NotFoundEntityException {

    private static final String MESSAGE = "Batch with id '%d' not found";

    public BatchNotFoundException(Long id) {
        super(String.format(MESSAGE, id));
    }
}

