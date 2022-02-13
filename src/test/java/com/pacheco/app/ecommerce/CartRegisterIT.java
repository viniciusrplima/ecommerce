package com.pacheco.app.ecommerce;

import com.pacheco.app.ecommerce.api.controller.Routes;
import com.pacheco.app.ecommerce.domain.model.Cart;
import com.pacheco.app.ecommerce.domain.model.CartItem;
import com.pacheco.app.ecommerce.util.AuthenticationUtil;
import com.pacheco.app.ecommerce.util.DataUtil;
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

import static io.restassured.RestAssured.given;
import static org.hamcrest.Matchers.containsString;
import static org.hamcrest.Matchers.hasSize;
import static org.hamcrest.Matchers.is;

@RunWith(SpringRunner.class)
@SpringBootTest(
        classes = EcommerceApplication.class,
        webEnvironment = SpringBootTest.WebEnvironment.RANDOM_PORT
)
@TestPropertySource("/application-test.properties")
public class CartRegisterIT {

    public static final String AUTH_HEADER_PARAM = "Authorization";
    public static final String CART_ITEM_JSON_FILE = "/json/cart-item.json";

    @LocalServerPort
    private int port;

    @Autowired DatabaseCleaner databaseCleaner;
    @Autowired AuthenticationUtil authenticationUtil;
    @Autowired DataUtil dataUtil;

    @Before
    public void setUp() {
        databaseCleaner.clearTablesAndResetSequences();
        authenticationUtil.setUp();
        dataUtil.prepareCarts();

        RestAssured.enableLoggingOfRequestAndResponseIfValidationFails();
        RestAssured.port = port;
        RestAssured.basePath = Routes.CART;
    }

    @Test
    public void mustReturnAllItems_whenGetCartItems() {
        given()
            .header(AUTH_HEADER_PARAM, authenticationUtil.getCustomerToken())
            .accept(ContentType.JSON)
        .when()
            .get()
        .then()
            .statusCode(HttpStatus.OK.value())
            .body("", hasSize(dataUtil.getCart().getItems().size()));
    }

    @Test
    public void mustReturnCartWithUpdatedNumberOfProducts_whenAddProductToCart() {
        Cart cart = dataUtil.getCart();
        CartItem cartItem = cart.getItems().get(0);
        int productCount = cartItem.getQuantity().intValue();
        int productStock = cartItem.getProduct().getStock().intValue();

        given()
            .header(AUTH_HEADER_PARAM, authenticationUtil.getCustomerToken())
            .accept(ContentType.JSON)
            .pathParam("productId", cartItem.getProduct().getId())
        .when()
            .post("/product/{productId}")
        .then()
            .statusCode(HttpStatus.OK.value())
            .body("", hasSize(cart.getItems().size()))
            .body("[0].quantity", is(productCount + 1));

        given()
            .header(AUTH_HEADER_PARAM, authenticationUtil.getCustomerToken())
            .accept(ContentType.JSON)
            .pathParam("productId", cartItem.getProduct().getId())
            .basePath("/products")
        .when()
            .get("/{productId}")
        .then()
            .statusCode(HttpStatus.OK.value())
            .body("stock", is(productStock - 1));
    }

    @Test
    public void mustReturnCartWithUpdatedNumberOfProducts_whenRemoveProductFromCart() {
        Cart cart = dataUtil.getCart();
        CartItem cartItem = cart.getItems().get(0);
        int productCount = cartItem.getQuantity().intValue();
        int productStock = cartItem.getProduct().getStock().intValue();

        given()
            .header(AUTH_HEADER_PARAM, authenticationUtil.getCustomerToken())
            .accept(ContentType.JSON)
            .pathParam("productId", cartItem.getProduct().getId())
        .when()
            .delete("/product/{productId}")
        .then()
            .statusCode(HttpStatus.OK.value())
            .body("", hasSize(cart.getItems().size() - 1));

        given()
            .header(AUTH_HEADER_PARAM, authenticationUtil.getCustomerToken())
            .accept(ContentType.JSON)
            .pathParam("productId", cartItem.getProduct().getId())
            .basePath("/products")
        .when()
            .get("/{productId}")
        .then()
            .statusCode(HttpStatus.OK.value())
            .body("stock", is(productStock + productCount));
    }

    @Test
    public void mustReturnStatus400_whenAddProductWithoutStockToCart() {
        given()
            .header(AUTH_HEADER_PARAM, authenticationUtil.getCustomerToken())
            .accept(ContentType.JSON)
            .pathParam("productId", dataUtil.getProductOutOfStock().getId())
        .when()
            .post("/product/{productId}")
        .then()
            .statusCode(HttpStatus.BAD_REQUEST.value())
            .body("detail", containsString("stock"));
    }

    @Test
    public void mustReturnUpdatedQuantityOfItemsInCart_whenAddCartItemQuantity() {
        Cart cart = dataUtil.getCart();
        CartItem cartItem = cart.getItems().get(0);
        int productCount = cartItem.getQuantity().intValue();
        int productStock = cartItem.getProduct().getStock().intValue();
        int quantityAdded = 5;
        int updatedQuantity = productCount + quantityAdded;

        given()
            .header(AUTH_HEADER_PARAM, authenticationUtil.getCustomerToken())
            .accept(ContentType.JSON)
            .contentType(ContentType.JSON)
            .pathParam("productId", cartItem.getProduct().getId())
            .body(String.format("{\"quantity\":%d }", updatedQuantity))
        .when()
            .put("/product/{productId}")
        .then()
            .statusCode(HttpStatus.OK.value())
            .body("", hasSize(cart.getItems().size()))
            .body("[0].quantity", is(updatedQuantity));

        given()
            .header(AUTH_HEADER_PARAM, authenticationUtil.getCustomerToken())
            .accept(ContentType.JSON)
            .pathParam("productId", cartItem.getProduct().getId())
            .basePath("/products")
        .when()
            .get("/{productId}")
            .then()
            .statusCode(HttpStatus.OK.value())
            .body("stock", is(productStock - quantityAdded));
    }

    @Test
    public void mustReturnUpdatedQuantityOfItemsInCart_whenSubtractCartItemQuantity() {
        Cart cart = dataUtil.getCart();
        CartItem cartItem = cart.getItems().get(0);
        int productCount = cartItem.getQuantity().intValue();
        int productStock = cartItem.getProduct().getStock().intValue();
        int quantitySubtracted = 5;
        int updatedQuantity = productCount - quantitySubtracted;

        given()
            .header(AUTH_HEADER_PARAM, authenticationUtil.getCustomerToken())
            .accept(ContentType.JSON)
            .contentType(ContentType.JSON)
            .pathParam("productId", cartItem.getProduct().getId())
            .body(String.format("{\"quantity\":%d }", updatedQuantity))
        .when()
            .put("/product/{productId}")
        .then()
            .statusCode(HttpStatus.OK.value())
            .body("", hasSize(cart.getItems().size()))
            .body("[0].quantity", is(updatedQuantity));

        given()
            .header(AUTH_HEADER_PARAM, authenticationUtil.getCustomerToken())
            .accept(ContentType.JSON)
            .pathParam("productId", cartItem.getProduct().getId())
            .basePath("/products")
        .when()
            .get("/{productId}")
        .then()
            .statusCode(HttpStatus.OK.value())
            .body("stock", is(productStock + quantitySubtracted));
    }

    @Test
    public void mustReturnNotEnoughError_whenAddCartItemQuantityWithNotEnoughStock() {
        Cart cart = dataUtil.getCart();
        CartItem cartItem = cart.getItems().get(0);
        int productCount = cartItem.getQuantity().intValue();
        int productStock = cartItem.getProduct().getStock().intValue();
        int quantityAdded = 50;
        int updatedQuantity = productCount + quantityAdded;

        given()
            .header(AUTH_HEADER_PARAM, authenticationUtil.getCustomerToken())
            .accept(ContentType.JSON)
            .contentType(ContentType.JSON)
            .pathParam("productId", cartItem.getProduct().getId())
            .body(String.format("{\"quantity\":%d }", updatedQuantity))
        .when()
            .put("/product/{productId}")
        .then()
            .statusCode(HttpStatus.BAD_REQUEST.value())
            .body("detail", containsString("enough"));
    }

}
