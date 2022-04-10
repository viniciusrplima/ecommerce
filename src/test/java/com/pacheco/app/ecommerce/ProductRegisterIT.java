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
import static org.hamcrest.Matchers.containsString;
import static org.hamcrest.Matchers.hasSize;
import static org.hamcrest.Matchers.is;
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

    @Autowired DatabaseCleaner databaseCleaner;
    @Autowired AuthenticationUtil authenticationUtil;
    @Autowired ProductTypeRepository productTypeRepository;
    @Autowired ProductRepository productRepository;

    private int totalProducts;
    private Product samsungProd;
    private Product productOutOfStock;

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
            .body("products", hasSize(totalProducts));
    }

    @Test
    public void mustReturnStatus201AndProductMustBeActive_whenRegisterProduct() throws JsonProcessingException {
        given()
            .body(getContentFromResource(PRODUCT_JSON_FILENAME))
            .header(AUTH_HEADER_PARAM, authenticationUtil.getAdminToken())
            .accept(ContentType.JSON)
            .contentType(ContentType.JSON)
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
        ValidatableResponse response = given()
            .header(AUTH_HEADER_PARAM, authenticationUtil.getAdminToken())
            .accept(ContentType.JSON)
            .contentType(ContentType.JSON)
            .body(getContentFromResource(PRODUCT_VALIDATION_ERROR_JSON_FILENAME))
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

        given()
            .body(getContentFromResource(PRODUCT_JSON_FILENAME))
            .header(AUTH_HEADER_PARAM, authenticationUtil.getAdminToken())
            .pathParam("productId", productId)
            .accept(ContentType.JSON)
            .contentType(ContentType.JSON)
        .when()
            .put("/{productId}")
        .then()
            .statusCode(HttpStatus.OK.value())
            .body("id", is(productId));
    }

    @Test
    public void mustReturnValidationErrors_whenUpdateProductWithValidationErrors() throws JsonProcessingException {
        int productId = samsungProd.getId().intValue();

        ValidatableResponse response = given()
            .body(getContentFromResource(PRODUCT_VALIDATION_ERROR_JSON_FILENAME))
            .header(AUTH_HEADER_PARAM, authenticationUtil.getAdminToken())
            .pathParam("productId", productId)
            .accept(ContentType.JSON)
            .contentType(ContentType.JSON)
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
            .header(AUTH_HEADER_PARAM, authenticationUtil.getAdminToken())
            .pathParam("productId", samsungProd.getId())
            .accept(ContentType.JSON)
        .when()
            .delete("/{productId}")
        .then()
            .statusCode(HttpStatus.NO_CONTENT.value());
    }

    public void prepareData() {
        ProductType eletronicos = new ProductType();
        eletronicos.setName("eletronicos");
        eletronicos.setDescription("celulares, TVs, video games, etc");

        productTypeRepository.save(eletronicos);

        List<Product> products = new ArrayList<>();

        samsungProd = new Product();
        samsungProd.setName("Samsung J1 Mini");
        samsungProd.setDescription("RAM: 2GB, CPU: 1.5GHz, HD: 16GB");
        samsungProd.setPrice(BigDecimal.valueOf(650));
        samsungProd.setActive(Boolean.TRUE);
        samsungProd.setStock(BigInteger.valueOf(35));
        products.add(samsungProd);

        productOutOfStock = new Product();
        productOutOfStock.setName("Monark aro 27");
        productOutOfStock.setDescription("Aro: 27cm, Marcha: sim, Freio a disco: sim");
        productOutOfStock.setPrice(BigDecimal.valueOf(895));
        productOutOfStock.setActive(Boolean.TRUE);
        productOutOfStock.setStock(BigInteger.valueOf(0));
        products.add(productOutOfStock);

        productRepository.saveAll(products);
        totalProducts = products.size();
    }

}
