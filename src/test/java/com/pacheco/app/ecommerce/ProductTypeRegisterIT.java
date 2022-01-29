package com.pacheco.app.ecommerce;

import com.fasterxml.jackson.core.JsonProcessingException;
import com.pacheco.app.ecommerce.api.controller.Routes;
import com.pacheco.app.ecommerce.domain.model.Product;
import com.pacheco.app.ecommerce.domain.model.ProductType;
import com.pacheco.app.ecommerce.domain.repository.ProductRepository;
import com.pacheco.app.ecommerce.domain.repository.ProductTypeRepository;
import com.pacheco.app.ecommerce.util.AuthenticationUtil;
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

import java.math.BigDecimal;
import java.math.BigInteger;
import java.util.ArrayList;
import java.util.List;

import static com.pacheco.app.ecommerce.util.ResourceUtil.getContentFromResource;
import static io.restassured.RestAssured.given;
import static org.hamcrest.Matchers.*;
import static org.junit.Assert.assertTrue;

@RunWith(SpringRunner.class)
@SpringBootTest(
        classes = EcommerceApplication.class,
        webEnvironment = SpringBootTest.WebEnvironment.RANDOM_PORT
)
@TestPropertySource("/application-test.properties")
public class ProductTypeRegisterIT {

    public static final String AUTH_HEADER_PARAM = "Authorization";
    public static final String PRODUCT_TYPE_FILE = "/json/product-type.json";
    public static final String PRODUCT_TYPE_VALIDATION_ERRORS_FILE = "/json/product-type-with-validation-errors.json";
    @LocalServerPort
    private int port;

    @Autowired
    DatabaseCleaner databaseCleaner;

    @Autowired
    ProductTypeRepository productTypeRepository;

    @Autowired
    ProductRepository productRepository;

    @Autowired
    AuthenticationUtil authenticationUtil;

    private int numProductTypes;
    private ProductType comidaProdType;
    private ProductType withDependentProdType;

    @Before
    public void setUp() {
        databaseCleaner.clearTablesAndResetSequences();
        authenticationUtil.setUp();
        prepareData();

        RestAssured.enableLoggingOfRequestAndResponseIfValidationFails();
        RestAssured.port = port;
        RestAssured.basePath = Routes.PRODUCT_TYPES;
    }

    @Test
    public void mustReturnAllProductTypes_whenGetProductTypes() {
        given()
            .accept(ContentType.JSON)
        .when()
            .get()
        .then()
            .statusCode(HttpStatus.OK.value())
            .body("", hasSize(numProductTypes));
    }

    @Test
    public void mustReturnTheProductType_whenGetProductTypeById() {
        given()
            .pathParam("productTypeId", comidaProdType.getId())
            .accept(ContentType.JSON)
        .when()
            .get("/{productTypeId}")
        .then()
            .statusCode(HttpStatus.OK.value())
            .body("id", is(comidaProdType.getId().intValue()))
            .body("name", is(comidaProdType.getName()))
            .body("description", is(comidaProdType.getDescription()));
    }

    @Test
    public void mustReturnStatus404_whenGetInexistentProductType() {
        long productTypeIdInexistent = 9999L;

        given()
            .pathParam("productTypeId", productTypeIdInexistent)
            .accept(ContentType.JSON)
        .when()
             .get("/{productTypeId}")
        .then()
            .statusCode(HttpStatus.NOT_FOUND.value())
            .body("detail", containsString(Long.toString(productTypeIdInexistent)));
    }

    @Test
    public void mustReturnStatus201_whenSaveProductType() throws JsonProcessingException {
        given()
            .body(getContentFromResource(PRODUCT_TYPE_FILE))
            .header(AUTH_HEADER_PARAM, authenticationUtil.getAdminToken())
            .accept(ContentType.JSON)
            .contentType(ContentType.JSON)
        .when()
            .post()
        .then()
            .statusCode(HttpStatus.CREATED.value());
    }

    @Test
    public void mustReturnValidationErrors_whenSaveAProductTypeWithParameterErrors() throws JsonProcessingException {
        ValidatableResponse response = given()
                .body(getContentFromResource(PRODUCT_TYPE_VALIDATION_ERRORS_FILE))
            .header(AUTH_HEADER_PARAM, authenticationUtil.getAdminToken())
            .accept(ContentType.JSON)
            .contentType(ContentType.JSON)
        .when()
            .post()
        .then()
            .statusCode(HttpStatus.BAD_REQUEST.value());

        List<String> userMessages = response.extract().jsonPath().getList("objects.userMessage");
        List<String> propertyNames = response.extract().jsonPath().getList("objects.name");

        assertTrue(userMessages.stream().anyMatch(containsString("blank")::matches));
        assertTrue(propertyNames.containsAll(List.of("name", "description")));
    }

    @Test
    public void mustReturnStatus200andSameId_whenUpdateProductType() throws JsonProcessingException {
        given()
            .body(getContentFromResource(PRODUCT_TYPE_FILE))
            .header(AUTH_HEADER_PARAM, authenticationUtil.getAdminToken())
            .pathParam("productTypeId", comidaProdType.getId())
            .accept(ContentType.JSON)
            .contentType(ContentType.JSON)
        .when()
            .put("/{productTypeId}")
        .then()
            .statusCode(HttpStatus.OK.value())
            .body("id", is(comidaProdType.getId().intValue()));
    }

    @Test
    public void mustReturnValidationErrors_whenUpdateAProductTypeWithParameterErrors() throws JsonProcessingException {
        ValidatableResponse response = given()
            .body(getContentFromResource(PRODUCT_TYPE_VALIDATION_ERRORS_FILE))
            .header(AUTH_HEADER_PARAM, authenticationUtil.getAdminToken())
            .pathParam("productTypeId", comidaProdType.getId())
            .accept(ContentType.JSON)
            .contentType(ContentType.JSON)
        .when()
            .put("/{productTypeId}")
        .then()
            .statusCode(HttpStatus.BAD_REQUEST.value());

        List<String> userMessages = response.extract().jsonPath().getList("objects.userMessage");
        List<String> propertyNames = response.extract().jsonPath().getList("objects.name");

        assertTrue(userMessages.stream().anyMatch(containsString("blank")::matches));
        assertTrue(propertyNames.containsAll(List.of("name", "description")));
    }

    @Test
    public void mustReturnStatus204_whenDeleteProduct() {
        long productTypeId = comidaProdType.getId();

        given()
            .header(AUTH_HEADER_PARAM, authenticationUtil.getAdminToken())
            .accept(ContentType.JSON)
            .pathParam("productTypeId", productTypeId)
        .when()
            .delete("/{productTypeId}", productTypeId)
        .then()
            .statusCode(HttpStatus.NO_CONTENT.value());
    }

    @Test
    public void mustReturnStatus409_whenDeleteProductTypeWithDependent() {
        long productTypeId = withDependentProdType.getId();

        given()
            .header(AUTH_HEADER_PARAM, authenticationUtil.getAdminToken())
            .accept(ContentType.JSON)
            .pathParam("productTypeId", productTypeId)
        .when()
            .delete("/{productTypeId}", productTypeId)
        .then()
            .statusCode(HttpStatus.CONFLICT.value());
    }

    public void prepareData() {
        List<ProductType> productTypeList = new ArrayList<>();

        comidaProdType = new ProductType();
        comidaProdType.setName("Comida");
        comidaProdType.setDescription("Laticínios, Carnos, Queijos entre outros");
        productTypeList.add(comidaProdType);

        withDependentProdType = new ProductType();
        withDependentProdType.setName("Artigos Esportivos");
        withDependentProdType.setDescription("Bolas, Chuteiras, Raquetes entre outros");
        productTypeList.add(withDependentProdType);

        productTypeRepository.saveAll(productTypeList);
        numProductTypes = productTypeList.size();

        Product productDependent = new Product();
        productDependent.setName("Bola");
        productDependent.setDescription("diâmetro: 20cm; cor: vermelha");
        productDependent.setActive(Boolean.TRUE);
        productDependent.setStock(BigInteger.valueOf(20));
        productDependent.setPrice(BigDecimal.valueOf(120));
        productDependent.setTypes(List.of(withDependentProdType));
        productRepository.save(productDependent);
    }
}
