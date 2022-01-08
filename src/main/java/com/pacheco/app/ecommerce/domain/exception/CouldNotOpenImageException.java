package com.pacheco.app.ecommerce.domain.exception;

public class CouldNotOpenImageException extends BusinessException {

    private static final String MESSAGE = "Could not open image, please choose another image";

    public CouldNotOpenImageException(Throwable cause) {
        super(MESSAGE, cause);
    }
}
