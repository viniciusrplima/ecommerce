package com.pacheco.app.ecommerce;

import com.fasterxml.jackson.core.JsonProcessingException;
import com.pacheco.app.ecommerce.api.controller.Routes;
import com.pacheco.app.ecommerce.domain.model.Product;
import com.pacheco.app.ecommerce.domain.repository.ProductRepository;
import com.pacheco.app.ecommerce.util.DatabaseCleaner;
import com.pacheco.app.ecommerce.util.ResourceUtil;
import io.restassured.RestAssured;
import io.restassured.config.EncoderConfig;
import io.restassured.config.RestAssuredConfig;
import io.restassured.http.ContentType;
import io.restassured.specification.RequestSpecification;
import org.hamcrest.Matchers;
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
import java.util.Map;

import static com.pacheco.app.ecommerce.util.ResourceUtil.getContentFromJsonAsMap;
import static io.restassured.RestAssured.config;
import static io.restassured.RestAssured.given;
import static io.restassured.config.EncoderConfig.encoderConfig;
import static org.hamcrest.Matchers.hasSize;
import static org.hamcrest.Matchers.is;

@RunWith(SpringRunner.class)
@SpringBootTest(
        classes = EcommerceApplication.class,
        webEnvironment = SpringBootTest.WebEnvironment.RANDOM_PORT
)
@TestPropertySource("/application-test.properties")
public class ProductRegisterIT {

    public static final String PRODUCT_JSON_FILENAME = "/json/product.json";

    @LocalServerPort
    private int port;

    @Autowired
    DatabaseCleaner databaseCleaner;

    @Autowired
    private ProductRepository productRepository;

    private int numProductsRegistred;

    @Before
    public void setUp() {
        RestAssured.enableLoggingOfRequestAndResponseIfValidationFails();
        RestAssured.port = port;
        RestAssured.basePath = Routes.PRODUCTS;

        databaseCleaner.clearTablesAndResetSequences();
        prepareData();
    }

    @Test
    public void whenGetProducts_thenReturnAllProductsRegistred() {
        given()
            .accept(ContentType.JSON)
        .when()
            .get()
        .then()
            .body("", hasSize(numProductsRegistred));
    }

    @Test
    public void whenRegisterProduct_thenReturnStatus201AndProductMustBeActive() throws JsonProcessingException {
        givenMultipartForm(getContentFromJsonAsMap(PRODUCT_JSON_FILENAME))
            .accept(ContentType.JSON)
            .contentType(ContentType.MULTIPART)
        .when()
            .post()
        .then()
            .statusCode(HttpStatus.CREATED.value())
            .body("active", is(true));
    }

    private RequestSpecification givenMultipartForm(Map<String, ?> form) {
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

    private RestAssuredConfig multipartConfig() {
        return config().encoderConfig(encoderConfig()
                .encodeContentTypeAs("multipart/form-data", ContentType.TEXT));
    }

    private void prepareData() {
        List<Product> productList = new ArrayList<>();

        Product product1 = new Product();
        product1.setName("Samsung J1 Mini");
        product1.setDescription("RAM: 2GB, CPU: 1.5GHz, HD: 16GB");
        product1.setPrice(BigDecimal.valueOf(650));
        product1.setActive(Boolean.TRUE);
        product1.setStock(BigInteger.valueOf(35));
        productList.add(product1);

        Product product2 = new Product();
        product2.setName("Monark aro 27");
        product2.setDescription("Aro: 27cm, Marcha: sim, Freio a disco: sim");
        product2.setPrice(BigDecimal.valueOf(895));
        product2.setActive(Boolean.TRUE);
        product2.setStock(BigInteger.valueOf(12));
        productList.add(product2);

        productList.forEach(productRepository::save);
        numProductsRegistred = productList.size();
    }

}
