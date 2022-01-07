package com.pacheco.app.ecommerce.domain.model;

public enum PurchaseState {

    WAITING("W"),
    PAYED("P"),
    DELIVERED("D");

    private final String value;

    private PurchaseState(String value) {
        this.value = value;
    }
}
