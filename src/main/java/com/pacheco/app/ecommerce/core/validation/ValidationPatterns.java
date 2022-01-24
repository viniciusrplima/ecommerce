package com.pacheco.app.ecommerce.core.validation;

public class ValidationPatterns {

    public final static String PHONE_NUMBER = "^(\\([1-9]{2}\\)|[1-9]{2})? ?([2-8]|9[1-9])[0-9]{3}[ -]?[0-9]{4}$";
    public final static String CEP = "^\\d{5}-\\d{3}$";
    public final static String CPF = "(^\\d{3}\\.\\d{3}\\.\\d{3}\\-\\d{2}$|\\d{11})";

}
