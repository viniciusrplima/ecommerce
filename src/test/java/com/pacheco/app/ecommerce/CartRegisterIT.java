package com.pacheco.app.ecommerce;

import com.pacheco.app.ecommerce.api.controller.Routes;
import com.pacheco.app.ecommerce.domain.model.Cart;
import com.pacheco.app.ecommerce.domain.model.CartItem;
import com.pacheco.app.ecommerce.domain.model.Product;
import com.pacheco.app.ecommerce.domain.model.account.Customer;
import com.pacheco.app.ecommerce.domain.repository.CartItemRepository;
import com.pacheco.app.ecommerce.domain.repository.CartRepository;
import com.pacheco.app.ecommerce.domain.repository.ProductRepository;
import com.pacheco.app.ecommerce.domain.repository.UserRepository;
import com.pacheco.app.ecommerce.domain.service.CartService;
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

import javax.transaction.Transactional;
import java.math.BigDecimal;
import java.math.BigInteger;
import java.util.ArrayList;
import java.util.List;
import java.util.stream.Collectors;

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

    @Autowired
    DatabaseCleaner databaseCleaner;

    @Autowired
    AuthenticationUtil authenticationUtil;

    @Autowired
    CartService cartService;

    @Autowired
    ProductRepository productRepository;

    @Autowired
    UserRepository userRepository;

    @Autowired
    CartRepository cartRepository;

    @Autowired
    CartItemRepository cartItemRepository;

    private int numCartItems;
    private Cart cart;
    private Product productOutOfStock;
    private Long product1Id;
    private int product1Count;
    private int product1Stock;

    @Before
    public void setUp() {
        databaseCleaner.clearTablesAndResetSequences();
        authenticationUtil.setUp();
        prepareData();

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
            .body("", hasSize(numCartItems));
    }

    @Test
    public void mustReturnCartWithUpdatedNumberOfProducts_whenAddProductToCart() {
        given()
            .header(AUTH_HEADER_PARAM, authenticationUtil.getCustomerToken())
            .accept(ContentType.JSON)
            .pathParam("productId", product1Id)
        .when()
            .post("/product/{productId}")
        .then()
            .statusCode(HttpStatus.OK.value())
            .body("", hasSize(numCartItems))
            .body("[0].quantity", is(product1Count + 1));

        given()
            .header(AUTH_HEADER_PARAM, authenticationUtil.getCustomerToken())
            .accept(ContentType.JSON)
            .pathParam("productId", product1Id)
            .basePath("/products")
        .when()
            .get("/{productId}")
        .then()
            .statusCode(HttpStatus.OK.value())
            .body("stock", is(product1Stock - 1));
    }

    @Test
    public void mustReturnCartWithUpdatedNumberOfProducts_whenRemoveProductFromCart() {
        given()
            .header(AUTH_HEADER_PARAM, authenticationUtil.getCustomerToken())
            .accept(ContentType.JSON)
            .pathParam("productId", product1Id)
        .when()
            .delete("/product/{productId}")
        .then()
            .statusCode(HttpStatus.OK.value())
            .body("", hasSize(numCartItems - 1));

        given()
            .header(AUTH_HEADER_PARAM, authenticationUtil.getCustomerToken())
            .accept(ContentType.JSON)
            .pathParam("productId", product1Id)
            .basePath("/products")
        .when()
            .get("/{productId}")
        .then()
            .statusCode(HttpStatus.OK.value())
            .body("stock", is(product1Stock + product1Count));
    }

    @Test
    public void mustReturnStatus400_whenAddProductWithoutStockToCart() {
        given()
            .header(AUTH_HEADER_PARAM, authenticationUtil.getCustomerToken())
            .accept(ContentType.JSON)
            .pathParam("productId", productOutOfStock.getId())
        .when()
            .post("/product/{productId}")
        .then()
            .statusCode(HttpStatus.BAD_REQUEST.value())
            .body("detail", containsString("stock"));
    }

    @Test
    public void mustReturnUpdatedQuantityOfItemsInCart_whenAddCartItemQuantity() {
        int quantityAdded = 5;
        int updatedQuantity = product1Count + quantityAdded;

        given()
            .header(AUTH_HEADER_PARAM, authenticationUtil.getCustomerToken())
            .accept(ContentType.JSON)
            .contentType(ContentType.JSON)
            .pathParam("productId", product1Id)
            .body(String.format("{\"quantity\":%d }", updatedQuantity))
        .when()
            .put("/product/{productId}")
        .then()
            .statusCode(HttpStatus.OK.value())
            .body("", hasSize(numCartItems))
            .body("[0].quantity", is(updatedQuantity));

        given()
            .header(AUTH_HEADER_PARAM, authenticationUtil.getCustomerToken())
            .accept(ContentType.JSON)
            .pathParam("productId", product1Id)
            .basePath("/products")
        .when()
            .get("/{productId}")
            .then()
            .statusCode(HttpStatus.OK.value())
            .body("stock", is(product1Stock - quantityAdded));
    }

    @Test
    public void mustReturnUpdatedQuantityOfItemsInCart_whenSubtractCartItemQuantity() {
        int quantitySubtracted = 5;
        int updatedQuantity = product1Count - quantitySubtracted;

        given()
            .header(AUTH_HEADER_PARAM, authenticationUtil.getCustomerToken())
            .accept(ContentType.JSON)
            .contentType(ContentType.JSON)
            .pathParam("productId", product1Id)
            .body(String.format("{\"quantity\":%d }", updatedQuantity))
        .when()
            .put("/product/{productId}")
        .then()
            .statusCode(HttpStatus.OK.value())
            .body("", hasSize(numCartItems))
            .body("[0].quantity", is(updatedQuantity));

        given()
            .header(AUTH_HEADER_PARAM, authenticationUtil.getCustomerToken())
            .accept(ContentType.JSON)
            .pathParam("productId", product1Id)
            .basePath("/products")
        .when()
            .get("/{productId}")
        .then()
            .statusCode(HttpStatus.OK.value())
            .body("stock", is(product1Stock + quantitySubtracted));
    }

    @Test
    public void mustReturnNotEnoughError_whenAddCartItemQuantityWithNotEnoughStock() {
        int quantityAdded = 50;
        int updatedQuantity = product1Count + quantityAdded;

        given()
            .header(AUTH_HEADER_PARAM, authenticationUtil.getCustomerToken())
            .accept(ContentType.JSON)
            .contentType(ContentType.JSON)
            .pathParam("productId", product1Id)
            .body(String.format("{\"quantity\":%d }", updatedQuantity))
        .when()
            .put("/product/{productId}")
        .then()
            .statusCode(HttpStatus.BAD_REQUEST.value())
            .body("detail", containsString("enough"));
    }


    @Transactional
    public void prepareData() {
        product1Stock = 35;

        List<Product> productList = new ArrayList<>();

        Product product1 = new Product();
        product1.setName("Samsung J1 Mini");
        product1.setDescription("RAM: 2GB, CPU: 1.5GHz, HD: 16GB");
        product1.setPrice(BigDecimal.valueOf(650));
        product1.setActive(Boolean.TRUE);
        product1.setStock(BigInteger.valueOf(product1Stock));
        productList.add(product1);

        productOutOfStock = new Product();
        productOutOfStock.setName("Monark aro 27");
        productOutOfStock.setDescription("Aro: 27cm, Marcha: sim, Freio a disco: sim");
        productOutOfStock.setPrice(BigDecimal.valueOf(895));
        productOutOfStock.setActive(Boolean.TRUE);
        productOutOfStock.setStock(BigInteger.valueOf(0));
        productList.add(productOutOfStock);

        productList = productList.stream()
                .map(product -> productRepository.save(product))
                .collect(Collectors.toList());

        numCartItems = productList.size();
        product1Id = productList.get(0).getId();
        product1Count = 7;

        Customer customer = authenticationUtil.getCustomer();
        final Cart cart = cartRepository.save(new Cart(customer));
        cart.setCustomer(customer);
        cart.setItems(productList.stream()
                .map(product -> new CartItem(BigInteger.valueOf(product1Count), product, cart))
                .map(cartItemRepository::save)
                .collect(Collectors.toList()));

        this.cart = cartRepository.save(cart);
        customer.setCart(this.cart);
        userRepository.save(customer);
    }
}
