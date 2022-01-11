package com.pacheco.app.ecommerce.util;

import io.restassured.specification.RequestSpecification;

import java.util.Map;

import static io.restassured.RestAssured.given;

public class RestAssuredUtil {

    public static RequestSpecification givenMultipartForm(Map<String, ?> form) {
        RequestSpecification formSpec = given();

        for (String key : form.keySet()) {
            Object value = form.get(key);

            if (value instanceof Number) {
                formSpec = formSpec.multiPart(key, ((Number) value).toString());
            }
            else if (value instanceof String) {
                formSpec = formSpec.multiPart(key, (String) value);
            }
            else {
                throw new RuntimeException("Invalid param type");
            }
        }

        return formSpec;
    }
}