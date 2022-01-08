package com.pacheco.app.ecommerce.api.exceptionhandler;

import lombok.Getter;

@Getter
public enum ProblemType {

    INTERNAL_ERROR("System Internal Error"),
    RESOURCE_NOT_FOUND("Resource Not Found"),
    INVALID_PARAM("Invalid Param"),
    INVALID_DATA("Invalid Data"),
    INCOMPREHENSIBLE_MESSAGE("Incomprehensible Message\n"),
    ENTITY_USED("Entity Used"),
    BUSINESS_ERROR("Business Rule Violation");

    private String title;

    ProblemType(String title) {
        this.title = title;
    }
}