package com.pacheco.app.ecommerce.api.exceptionhandler;

import lombok.Getter;

@Getter
public enum ProblemType {

    INTERNAL_ERROR("System Internal Error"),
    RESOURCE_NOT_FOUND("Resource Not Found"),
    AUTHENTICATION_ERROR("Authentication Error"),
    INVALID_PARAM("Invalid Param"),
    INVALID_DATA("Invalid Data"),
    MISSING_REQUEST_PART("Missing Request Part"),
    INCOMPREHENSIBLE_MESSAGE("Incomprehensible Message"),
    ENTITY_USED("Entity Used"),
    BUSINESS_ERROR("Business Rule Violation");

    private String title;

    ProblemType(String title) {
        this.title = title;
    }
}