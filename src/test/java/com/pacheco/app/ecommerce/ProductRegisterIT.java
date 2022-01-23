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

import static com.pacheco.app.ecommerce.util.ResourceUtil.getContentFromJsonAsMap;
import static com.pacheco.app.ecommerce.util.RestAssuredUtil.givenMultipartForm;
import static io.restassured.RestAssured.given;
import static org.hamcrest.Matchers.*;
import static org.junit.Assert.assertTrue;

@RunWith(SpringRunner.class)
@SpringBootTest(
        classes = EcommerceApplication.class,
        webEnvironment = SpringBootTest.WebEnvironment.RANDOM_PORT
)
@TestPropertySource("/application-test.properties")
public class ProductRegisterIT {

    public static final String AUTH_HEADER_PARAM = "Authorization";
    public static final String PRODUCT_JSON_FILENAME = "/json/product.json";
    public static final String PRODUCT_VALIDATION_ERROR_JSON_FILENAME = "/json/product-with-validation-errors.json";

    @LocalServerPort
    private int port;

    @Autowired
    DatabaseCleaner databaseCleaner;

    @Autowired
    AuthenticationUtil authenticationUtil;

    @Autowired
    private ProductRepository productRepository;

    @Autowired
    private ProductTypeRepository productTypeRepository;

    private int numProductsRegistred;
    private Product samsungProd;

    @Before
    public void setUp() {
        databaseCleaner.clearTablesAndResetSequences();
        authenticationUtil.setUp();
        prepareData();

        RestAssured.enableLoggingOfRequestAndResponseIfValidationFails();
        RestAssured.port = port;
        RestAssured.basePath = Routes.PRODUCTS;
    }

    @Test
    public void mustReturnAllProductsRegistred_whenGetProducts() {
        given()
            .accept(ContentType.JSON)
        .when()
            .get()
        .then()
            .statusCode(HttpStatus.OK.value())
            .body("", hasSize(numProductsRegistred));
    }

    @Test
    public void mustReturnStatus201AndProductMustBeActive_whenRegisterProduct() throws JsonProcessingException {
        givenMultipartForm(getContentFromJsonAsMap(PRODUCT_JSON_FILENAME))
            .header(AUTH_HEADER_PARAM, authenticationUtil.getBearerToken())
            .accept(ContentType.JSON)
            .contentType(ContentType.MULTIPART)
        .when()
            .post()
        .then()
            .statusCode(HttpStatus.CREATED.value())
            .body("active", is(true));
    }

    @Test
    public void mustReturnProduct_whenGetProductById() {
        given()
            .pathParam("productId", samsungProd.getId())
            .accept(ContentType.JSON)
        .when()
            .get("/{productId}")
        .then()
            .statusCode(HttpStatus.OK.value())
            .body("id", is(samsungProd.getId().intValue()))
            .body("name", is(samsungProd.getName()))
            .body("description", is(samsungProd.getDescription()))
            .body("price", is(samsungProd.getPrice().floatValue()))
            .body("stock", is(samsungProd.getStock().intValue()))
            .body("active", is(samsungProd.getActive().booleanValue()));
    }

    @Test
    public void mustReturnStatus404_whenGetInexistentProduct() {
        int inexistentProductId = 9999;

        given()
            .pathParam("productId", inexistentProductId)
            .accept(ContentType.JSON)
        .when()
            .get("/{productId}")
        .then()
            .statusCode(HttpStatus.NOT_FOUND.value());
    }

    @Test
    public void mustReturnValidationErrors_whenSaveProductWithValidationErrors() throws JsonProcessingException {
        ValidatableResponse response = givenMultipartForm(getContentFromJsonAsMap(PRODUCT_VALIDATION_ERROR_JSON_FILENAME))
            .header(AUTH_HEADER_PARAM, authenticationUtil.getBearerToken())
            .accept(ContentType.JSON)
            .contentType(ContentType.MULTIPART)
        .when()
            .post()
        .then()
            .statusCode(HttpStatus.BAD_REQUEST.value());

        List<String> userResponses = response.extract().jsonPath().getList("objects.userMessage");
        List<String> propertyNames = response.extract().jsonPath().getList("objects.name");

        assertTrue(userResponses.stream().anyMatch(containsString("blank")::matches));
        assertTrue(userResponses.stream().anyMatch(containsString("greater than")::matches));
        assertTrue(propertyNames.containsAll(List.of("name", "description", "stock", "price", "types")));
    }

    @Test
    public void mustReturnProduct_whenUpdateProduct() throws JsonProcessingException {
        int productId = samsungProd.getId().intValue();

        givenMultipartForm(getContentFromJsonAsMap(PRODUCT_JSON_FILENAME))
            .header(AUTH_HEADER_PARAM, authenticationUtil.getBearerToken())
            .pathParam("productId", productId)
            .accept(ContentType.JSON)
            .contentType(ContentType.MULTIPART)
        .when()
            .put("/{productId}")
        .then()
            .statusCode(HttpStatus.OK.value())
            .body("id", is(productId));
    }

    @Test
    public void mustReturnValidationErrors_whenUpdateProductWithValidationErrors() throws JsonProcessingException {
        int productId = samsungProd.getId().intValue();

        ValidatableResponse response = givenMultipartForm(getContentFromJsonAsMap(PRODUCT_VALIDATION_ERROR_JSON_FILENAME))
            .header(AUTH_HEADER_PARAM, authenticationUtil.getBearerToken())
            .pathParam("productId", productId)
            .accept(ContentType.JSON)
            .contentType(ContentType.MULTIPART)
        .when()
            .put("/{productId}")
        .then()
            .statusCode(HttpStatus.BAD_REQUEST.value());

        List<String> userResponses = response.extract().jsonPath().getList("objects.userMessage");
        List<String> propertyNames = response.extract().jsonPath().getList("objects.name");

        assertTrue(userResponses.stream().anyMatch(containsString("blank")::matches));
        assertTrue(userResponses.stream().anyMatch(containsString("greater than")::matches));
        assertTrue(propertyNames.containsAll(List.of("name", "description", "stock", "price")));
    }

    @Test
    public void mustReturnStatus204_whenDeleteProduct() {
        given()
            .header(AUTH_HEADER_PARAM, authenticationUtil.getBearerToken())
            .pathParam("productId", samsungProd.getId())
            .accept(ContentType.JSON)
        .when()
            .delete("/{productId}")
        .then()
            .statusCode(HttpStatus.NO_CONTENT.value());
    }

    private void prepareData() {
        List<Product> productList = new ArrayList<>();

        samsungProd = new Product();
        samsungProd.setName("Samsung J1 Mini");
        samsungProd.setDescription("RAM: 2GB, CPU: 1.5GHz, HD: 16GB");
        samsungProd.setPrice(BigDecimal.valueOf(650));
        samsungProd.setActive(Boolean.TRUE);
        samsungProd.setStock(BigInteger.valueOf(35));
        productList.add(samsungProd);

        Product product2 = new Product();
        product2.setName("Monark aro 27");
        product2.setDescription("Aro: 27cm, Marcha: sim, Freio a disco: sim");
        product2.setPrice(BigDecimal.valueOf(895));
        product2.setActive(Boolean.TRUE);
        product2.setStock(BigInteger.valueOf(12));
        productList.add(product2);

        productRepository.saveAll(productList);
        numProductsRegistred = productList.size();

        ProductType defaultProductType = new ProductType();
        defaultProductType.setName("default");
        defaultProductType.setDescription("default description");

        productTypeRepository.save(defaultProductType);
    }

}
