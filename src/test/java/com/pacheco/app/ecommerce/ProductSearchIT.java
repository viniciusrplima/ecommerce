package com.pacheco.app.ecommerce;

import com.pacheco.app.ecommerce.api.controller.Routes;
import com.pacheco.app.ecommerce.util.DataUtil;
import com.pacheco.app.ecommerce.util.DatabaseCleaner;
import io.restassured.RestAssured;
import io.restassured.http.ContentType;
import io.restassured.response.ValidatableResponse;
import org.junit.Before;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.boot.web.server.LocalServerPort;
import org.springframework.http.HttpStatus;
import org.springframework.test.context.TestPropertySource;
import org.springframework.test.context.junit4.SpringRunner;

import static io.restassured.RestAssured.given;
import static org.hamcrest.Matchers.equalTo;
import static org.hamcrest.Matchers.hasSize;

@RunWith(SpringRunner.class)
@SpringBootTest(
        classes = EcommerceApplication.class,
        webEnvironment = SpringBootTest.WebEnvironment.RANDOM_PORT
)
@TestPropertySource("/application-test.properties")
public class ProductSearchIT {

    @LocalServerPort
    private int port;

    @Autowired DatabaseCleaner databaseCleaner;
    @Autowired DataUtil dataUtil;

    @Before
    public void setUp() {
        databaseCleaner.clearTablesAndResetSequences();
        dataUtil.prepareProducts();

        RestAssured.enableLoggingOfRequestAndResponseIfValidationFails();
        RestAssured.port = port;
        RestAssured.basePath = Routes.PRODUCTS;
    }

    @Test
    public void mustReturnOnlyTheProductsThatMatchName_whenSearchProductsByName() {
        given()
            .accept(ContentType.JSON)
            .queryParam("q", "sam")
        .when()
            .get()
        .then()
            .statusCode(HttpStatus.OK.value())
            .body("products.name[0]", equalTo(dataUtil.getSamsungProd().getName()))
            .body("products.description[0]", equalTo(dataUtil.getSamsungProd().getDescription()));
    }

    @Test
    public void mustReturnOnlyTheDefinedNumberOfProducts_whenSearchProductsLimited() {
        int limit = 1;

        given()
            .accept(ContentType.JSON)
            .queryParam("limit", limit)
        .when()
            .get()
        .then()
            .statusCode(HttpStatus.OK.value())
            .body("products", hasSize(limit));
    }

    @Test
    public void mustReturnZeroProducts_whenSearchProductsPaginatedWithInvalidPage() {
        int limit = 2;
        int page = 3;

        given()
            .accept(ContentType.JSON)
            .queryParam("limit", limit)
            .queryParam("page", page)
        .when()
            .get()
        .then()
            .statusCode(HttpStatus.OK.value())
            .body("products", hasSize(0));
    }

    @Test
    public void mustReturnOnlyProductsWithDefinedType_whenSearchProductsByType() {
        ValidatableResponse response = given()
            .accept(ContentType.JSON)
            .queryParam("type", dataUtil.getEletronicos().getId())
        .when()
            .get()
        .then()
            .statusCode(HttpStatus.OK.value())
            .body("products.name[0]", equalTo(dataUtil.getSamsungProd().getName()));
    }
}
