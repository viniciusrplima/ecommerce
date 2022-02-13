package com.pacheco.app.ecommerce;

import com.pacheco.app.ecommerce.api.controller.Routes;
import com.pacheco.app.ecommerce.util.AuthenticationUtil;
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

import java.util.List;

import static com.pacheco.app.ecommerce.util.ResourceUtil.getContentFromResource;
import static io.restassured.RestAssured.given;
import static org.hamcrest.Matchers.hasSize;
import static org.junit.Assert.assertTrue;

@RunWith(SpringRunner.class)
@SpringBootTest(
        classes = EcommerceApplication.class,
        webEnvironment = SpringBootTest.WebEnvironment.RANDOM_PORT
)
@TestPropertySource("/application-test.properties")
public class UserAddressRegisterIT {

    public static final String AUTH_HEADER_PARAM = "Authorization";
    public static final String ADDRESS_JSON_FILE = "/json/address.json";
    public static final String ADDRESS_VALIDATION_ERRORS_JSON_FILE = "/json/address-with-validation-errors.json";

    @Autowired DatabaseCleaner databaseCleaner;
    @Autowired AuthenticationUtil authenticationUtil;
    @Autowired DataUtil dataUtil;

    @LocalServerPort
    private int port;

    @Before
    public void setUp() {
        databaseCleaner.clearTablesAndResetSequences();
        authenticationUtil.setUp();
        dataUtil.prepareAddresses();

        RestAssured.enableLoggingOfRequestAndResponseIfValidationFails();
        RestAssured.port = port;
        RestAssured.basePath = Routes.USERS + "/addresses";
    }

    @Test
    public void mustReturnAllAddresses_whenGetUserAddresses() {
        given()
            .header(AUTH_HEADER_PARAM, authenticationUtil.getCustomerToken())
            .accept(ContentType.JSON)
        .when()
            .get()
        .then()
            .statusCode(HttpStatus.OK.value())
            .body("", hasSize(dataUtil.getAddresses().size()));
    }

    @Test
    public void mustReturnStatus201_whenRegisterAddress() {
        given()
            .header(AUTH_HEADER_PARAM, authenticationUtil.getCustomerToken())
            .body(getContentFromResource(ADDRESS_JSON_FILE))
            .accept(ContentType.JSON)
            .contentType(ContentType.JSON)
        .when()
            .post()
        .then()
            .statusCode(HttpStatus.CREATED.value());
    }

    @Test
    public void mustReturnValidationErrors_whenRegisterAddressWithErrors() {
        ValidatableResponse response = given()
            .header(AUTH_HEADER_PARAM, authenticationUtil.getCustomerToken())
            .body(getContentFromResource(ADDRESS_VALIDATION_ERRORS_JSON_FILE))
            .accept(ContentType.JSON)
            .contentType(ContentType.JSON)
        .when()
            .post()
        .then()
            .statusCode(HttpStatus.BAD_REQUEST.value());

        List<String> objectNames = response.extract().jsonPath().getList("objects.name");

        assertTrue(objectNames.containsAll(List.of("state", "city", "cep", "street", "number", "district")));
    }

}
