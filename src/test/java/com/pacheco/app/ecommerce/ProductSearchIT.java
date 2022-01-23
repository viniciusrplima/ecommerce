package com.pacheco.app.ecommerce;

import com.pacheco.app.ecommerce.api.controller.Routes;
import com.pacheco.app.ecommerce.domain.model.Product;
import com.pacheco.app.ecommerce.domain.model.ProductType;
import com.pacheco.app.ecommerce.domain.repository.ProductRepository;
import com.pacheco.app.ecommerce.domain.repository.ProductTypeRepository;
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

    @Autowired
    DatabaseCleaner databaseCleaner;

    @Autowired
    private ProductRepository productRepository;

    @Autowired
    private ProductTypeRepository productTypeRepository;

    private int numProductsRegistred;
    private Product samsungProd;
    private ProductType eletronicos;

    @Before
    public void setUp() {
        databaseCleaner.clearTablesAndResetSequences();
        prepareData();

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
            .body("name[0]", equalTo(samsungProd.getName()))
            .body("description[0]", equalTo(samsungProd.getDescription()));
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
            .body("", hasSize(limit));
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
            .body("", hasSize(0));
    }

    @Test
    public void mustReturnOnlyProductsWithDefinedType_whenSearchProductsByType() {
        ValidatableResponse response = given()
            .accept(ContentType.JSON)
            .queryParam("type", eletronicos.getId())
        .when()
            .get()
        .then()
            .statusCode(HttpStatus.OK.value())
            .body("name[0]", equalTo(samsungProd.getName()));
    }

    private void prepareData() {
        List<ProductType> productTypeList = new ArrayList<>();

        eletronicos = new ProductType();
        eletronicos.setName("eletronicos");
        eletronicos.setDescription("celulares, TVs, video games, etc");
        eletronicos = productTypeRepository.save(eletronicos);

        ProductType artigosEsportivos = new ProductType();
        artigosEsportivos.setName("artigos esportivos");
        artigosEsportivos.setDescription("Bolas, raquetes, chuteiras, etc");
        productTypeList.add(artigosEsportivos);

        productTypeRepository.saveAll(productTypeList);

        List<Product> productList = new ArrayList<>();

        samsungProd = new Product();
        samsungProd.setName("Samsung J1 Mini");
        samsungProd.setDescription("RAM: 2GB, CPU: 1.5GHz, HD: 16GB");
        samsungProd.setPrice(BigDecimal.valueOf(650));
        samsungProd.setActive(Boolean.TRUE);
        samsungProd.setStock(BigInteger.valueOf(35));
        samsungProd.setTypes(List.of(eletronicos));
        productList.add(samsungProd);

        Product product2 = new Product();
        product2.setName("Monark aro 27");
        product2.setDescription("Aro: 27cm, Marcha: sim, Freio a disco: sim");
        product2.setPrice(BigDecimal.valueOf(895));
        product2.setActive(Boolean.TRUE);
        product2.setStock(BigInteger.valueOf(12));
        product2.setTypes(List.of(eletronicos));
        productList.add(product2);

        productRepository.saveAll(productList);
        numProductsRegistred = productList.size();

    }
}
