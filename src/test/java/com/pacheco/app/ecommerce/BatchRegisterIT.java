package com.pacheco.app.ecommerce;

import com.pacheco.app.ecommerce.api.controller.Routes;
import com.pacheco.app.ecommerce.domain.model.Batch;
import com.pacheco.app.ecommerce.domain.model.Product;
import com.pacheco.app.ecommerce.domain.repository.BatchRepository;
import com.pacheco.app.ecommerce.domain.repository.ProductRepository;
import com.pacheco.app.ecommerce.util.AuthenticationUtil;
import com.pacheco.app.ecommerce.util.DatabaseCleaner;
import io.restassured.RestAssured;
import io.restassured.http.ContentType;
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
import static org.hamcrest.Matchers.containsStringIgnoringCase;
import static org.hamcrest.Matchers.hasSize;
import static org.hamcrest.Matchers.is;

@RunWith(SpringRunner.class)
@SpringBootTest(
        classes = EcommerceApplication.class,
        webEnvironment = SpringBootTest.WebEnvironment.RANDOM_PORT
)
@TestPropertySource("/application-test.properties")
public class BatchRegisterIT {

    public static final String AUTH_HEADER_PARAM = "Authorization";
    public static final String BATCH_JSON_FILENAME = "/json/batch.json";
    public static final String BATCH_INVALID_PROD_JSON_FILENAME = "/json/batch-with-invalid-product.json";

    @LocalServerPort
    private int port;

    @Autowired DatabaseCleaner databaseCleaner;
    @Autowired AuthenticationUtil authenticationUtil;

    @Autowired ProductRepository productRepository;
    @Autowired BatchRepository batchRepository;

    private List<Product> products;
    private Batch batch;

    @Before
    public void setUp() {
        databaseCleaner.clearTablesAndResetSequences();
        authenticationUtil.setUp();
        prepareData();

        RestAssured.enableLoggingOfRequestAndResponseIfValidationFails();
        RestAssured.port = port;
        RestAssured.basePath = Routes.BATCHES;
    }

    @Test
    public void mustReturnAllBatches_whenGetBatches() {
        given()
            .header(AUTH_HEADER_PARAM, authenticationUtil.getAdminToken())
            .accept(ContentType.JSON)
        .when()
            .get()
        .then()
            .statusCode(HttpStatus.OK.value())
            .body("", hasSize(1));
    }

    @Test
    public void mustReturnBatch_whenGetBatch() {
        given()
            .header(AUTH_HEADER_PARAM, authenticationUtil.getAdminToken())
            .accept(ContentType.JSON)
            .pathParam("batchId", batch.getId())
        .when()
            .get("/{batchId}")
        .then()
            .statusCode(HttpStatus.OK.value())
            .body("id", is(batch.getId().intValue()))
            .body("quantity", is(batch.getQuantity().intValue()))
            .body("product.id", is(batch.getProduct().getId().intValue()))
            .body("registeredAt", is(batch.getRegisteredAt().toString()));
    }

    @Test
    public void mustReturn201_whenSaveBatch() {
        given()
            .header(AUTH_HEADER_PARAM, authenticationUtil.getAdminToken())
            .accept(ContentType.JSON)
            .body(getContentFromResource(BATCH_JSON_FILENAME))
            .contentType(ContentType.JSON)
        .when()
            .post()
        .then()
            .statusCode(HttpStatus.CREATED.value());
    }

    @Test
    public void mustReturn400_whenSaveBatchWithInvalidProduct() {
        given()
            .header(AUTH_HEADER_PARAM, authenticationUtil.getAdminToken())
            .accept(ContentType.JSON)
            .body(getContentFromResource(BATCH_INVALID_PROD_JSON_FILENAME))
            .contentType(ContentType.JSON)
        .when()
            .post()
        .then()
            .statusCode(HttpStatus.BAD_REQUEST.value())
            .body("detail", containsStringIgnoringCase("product"))
            .body("userMessage", containsStringIgnoringCase("product"));
    }

    public void prepareData() {
        products = new ArrayList<>();

        Product product1 = new Product();
        product1.setActive(true);
        product1.setDescription("aaaa");
        product1.setName("bbbbb");
        product1.setPrice(BigDecimal.valueOf(999));
        products.add(product1);

        Product product2 = new Product();
        product2.setActive(true);
        product2.setDescription("aaaa");
        product2.setName("bbbbb");
        product2.setPrice(BigDecimal.valueOf(999));
        products.add(product2);

        Product product3 = new Product();
        product3.setActive(true);
        product3.setDescription("aaaa");
        product3.setName("bbbbb");
        product3.setPrice(BigDecimal.valueOf(999));
        products.add(product3);

        productRepository.saveAll(products);

        batch = new Batch();
        batch.setProduct(product1);
        batch.setQuantity(BigInteger.valueOf(5));

        batchRepository.save(batch);
    }
}
