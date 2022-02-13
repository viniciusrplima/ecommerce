package com.pacheco.app.ecommerce;

import com.pacheco.app.ecommerce.api.controller.Routes;
import com.pacheco.app.ecommerce.domain.model.CartItem;
import com.pacheco.app.ecommerce.domain.model.Purchase;
import com.pacheco.app.ecommerce.domain.model.PurchaseItem;
import com.pacheco.app.ecommerce.domain.model.PurchaseState;
import com.pacheco.app.ecommerce.util.AuthenticationUtil;
import com.pacheco.app.ecommerce.util.DataUtil;
import com.pacheco.app.ecommerce.util.DatabaseCleaner;
import io.restassured.RestAssured;
import io.restassured.http.ContentType;
import io.restassured.response.ValidatableResponse;
import org.junit.Assert;
import org.junit.Before;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.boot.web.server.LocalServerPort;
import org.springframework.http.HttpStatus;
import org.springframework.test.context.TestPropertySource;
import org.springframework.test.context.junit4.SpringRunner;

import java.math.BigInteger;
import java.util.Comparator;
import java.util.List;
import java.util.Map;

import static com.pacheco.app.ecommerce.util.ResourceUtil.getContentFromResource;
import static io.restassured.RestAssured.given;
import static org.hamcrest.Matchers.containsString;
import static org.hamcrest.Matchers.hasSize;
import static org.hamcrest.Matchers.is;
import static org.hamcrest.Matchers.notNullValue;
import static org.junit.Assert.assertTrue;

@RunWith(SpringRunner.class)
@SpringBootTest(
        classes = EcommerceApplication.class,
        webEnvironment = SpringBootTest.WebEnvironment.RANDOM_PORT
)
@TestPropertySource("/application-test.properties")
public class PurchaseRegisterIT {

    public static final String AUTH_HEADER_PARAM = "Authorization";
    public static final String ADDRESS_JSON_FILE = "/json/address.json";
    public static final String ADDRESS_VALIDATION_ERROR_JSON_FILE = "/json/address-with-validation-errors.json";

    @LocalServerPort
    private int port;

    @Autowired DatabaseCleaner databaseCleaner;
    @Autowired AuthenticationUtil authenticationUtil;
    @Autowired DataUtil dataUtil;

    @Before
    public void setUp() {
        databaseCleaner.clearTablesAndResetSequences();
        authenticationUtil.setUp();
        dataUtil.preparePurchases();

        RestAssured.enableLoggingOfRequestAndResponseIfValidationFails();
        RestAssured.port = port;
        RestAssured.basePath = Routes.PURCHASES;
    }

    @Test
    public void mustReturnAllPurchases_whenGetPurchases() {
        given()
            .header(AUTH_HEADER_PARAM, authenticationUtil.getCustomerToken())
            .accept(ContentType.JSON)
        .when()
            .get()
        .then()
            .statusCode(HttpStatus.OK.value())
            .body("", hasSize(dataUtil.getPurchases().size()));
    }

    @Test
    public void mustReturnPurchase_whenGetPurchaseById() {
        Purchase purchase = dataUtil.getPurchase();

        given()
            .header(AUTH_HEADER_PARAM, authenticationUtil.getCustomerToken())
            .accept(ContentType.JSON)
            .pathParam("purchaseId", purchase.getId())
        .when()
            .get("/{purchaseId}")
        .then()
            .statusCode(HttpStatus.OK.value())
            .body("state", is(purchase.getState().name()))
            .body("id", is(purchase.getId().intValue()))
            .body("address", notNullValue())
            .body("items", hasSize(purchase.getItems().size()));
    }

    @Test
    public void mustReturn404_whenGetInexistentPurchase() {
        long inexistentPurchaseId = 99999999;

        given()
            .header(AUTH_HEADER_PARAM, authenticationUtil.getCustomerToken())
            .accept(ContentType.JSON)
            .pathParam("purchaseId", inexistentPurchaseId)
        .when()
            .get("/{purchaseId}")
        .then()
            .statusCode(HttpStatus.NOT_FOUND.value())
            .body("detail", containsString("Purchase"))
            .body("detail", containsString(Long.toString(inexistentPurchaseId)));
    }

    @Test
    public void mustCreatePurchaseWaitingWithAllProductsInCart_whenConfirmPurchase() {
        ValidatableResponse response = given()
            .header(AUTH_HEADER_PARAM, authenticationUtil.getCustomerToken())
            .accept(ContentType.JSON)
            .contentType(ContentType.JSON)
            .body(getContentFromResource(ADDRESS_JSON_FILE))
        .when()
            .post()
        .then()
            .statusCode(HttpStatus.CREATED.value())
            .body("state", is(PurchaseState.WAITING.name()))
            .body("items", hasSize(dataUtil.getCart().getItems().size()));

        // Verifying if it have all products from cart
        List<Map<String, Object>> purchaseItems = response.extract().jsonPath().getList("items");
        List<CartItem> cartItems = dataUtil.getCart().getItems();

        Comparator<Object> comparator = (p, ci) -> {
            Map<String, Object> purchaseItem = (Map<String, Object>) p;
            CartItem cartItem = (CartItem) ci;
            return (cartItem.getQuantity().equals(BigInteger.valueOf((Integer) purchaseItem.get("quantity"))) &&
                    cartItem.getProduct().getId().equals(
                            Long.valueOf((Integer)((Map<String, Object>)purchaseItem.get("product")).get("id"))
                    ))
                    ? 0 : 1;
        };

        Assert.assertTrue(purchaseItems.stream().allMatch(
                purchaseItem -> (cartItems.stream().anyMatch(
                        ci -> (comparator.compare(purchaseItem, ci) == 0))
                )));

        Assert.assertTrue(cartItems.stream().allMatch(
                cartItem -> (purchaseItems.stream().anyMatch(
                        purchaseItem -> (comparator.compare(purchaseItem, cartItem) == 0))
                )));

        // Verify if cart is empty
        RestAssured.basePath = Routes.CART;

        given()
            .accept(ContentType.JSON)
            .header(AUTH_HEADER_PARAM, authenticationUtil.getCustomerToken())
        .when()
            .get()
        .then()
            .statusCode(HttpStatus.OK.value())
            .body("", hasSize(0));
    }

    @Test
    public void mustReturnValidationErrors_whenConfirmPurchaseWithAddressErrors() {
        ValidatableResponse response = given()
            .header(AUTH_HEADER_PARAM, authenticationUtil.getCustomerToken())
            .accept(ContentType.JSON)
            .contentType(ContentType.JSON)
            .body(getContentFromResource(ADDRESS_VALIDATION_ERROR_JSON_FILE))
        .when()
            .post()
        .then()
            .statusCode(HttpStatus.BAD_REQUEST.value())
            .body("objects", notNullValue());

        List<String> objectNames = response.extract().jsonPath().getList("objects.name");

        assertTrue(objectNames.containsAll(List.of("state", "city", "cep", "street", "number", "district")));
    }

    @Test
    public void mustReplaceInStockTheProducts_whenCancelPurchase() {
        Purchase purchase = dataUtil.getPurchase();

        given()
            .accept(ContentType.JSON)
            .header(AUTH_HEADER_PARAM, authenticationUtil.getCustomerToken())
            .pathParam("purchaseId", purchase.getId())
        .when()
            .delete("/{purchaseId}")
        .then()
            .statusCode(HttpStatus.NO_CONTENT.value());

        // Verifying if all products was replaced to stock
        RestAssured.basePath = Routes.PRODUCTS;

        for (PurchaseItem purchaseItem : purchase.getItems()) {
            BigInteger prevProductStock = dataUtil.getProducts().stream()
                    .filter(p -> p.getId().equals(purchaseItem.getProduct().getId()))
                    .findFirst().get().getStock();
            BigInteger expectedStock = prevProductStock.add(purchaseItem.getQuantity());

            given()
                .accept(ContentType.JSON)
                .header(AUTH_HEADER_PARAM, authenticationUtil.getCustomerToken())
                .pathParam("productId", purchaseItem.getProduct().getId())
            .when()
                .get("/{productId}")
            .then()
                .statusCode(HttpStatus.OK.value())
                .body("stock", is(expectedStock.intValue()));
        }
    }
}
