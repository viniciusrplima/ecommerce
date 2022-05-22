package com.pacheco.app.ecommerce;

import com.pacheco.app.ecommerce.api.controller.Routes;
import com.pacheco.app.ecommerce.domain.model.Batch;
import com.pacheco.app.ecommerce.domain.model.Cart;
import com.pacheco.app.ecommerce.domain.model.CartItem;
import com.pacheco.app.ecommerce.domain.model.Product;
import com.pacheco.app.ecommerce.domain.model.account.Customer;
import com.pacheco.app.ecommerce.domain.repository.BatchRepository;
import com.pacheco.app.ecommerce.domain.repository.CartItemRepository;
import com.pacheco.app.ecommerce.domain.repository.CartRepository;
import com.pacheco.app.ecommerce.domain.repository.ProductRepository;
import com.pacheco.app.ecommerce.domain.repository.UserRepository;
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
import java.time.LocalDateTime;
import java.util.ArrayList;
import java.util.List;
import java.util.stream.Collectors;

import static io.restassured.RestAssured.given;
import static org.hamcrest.Matchers.containsString;
import static org.hamcrest.Matchers.hasSize;

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

    @Autowired UserRepository userRepository;
    @Autowired ProductRepository productRepository;
    @Autowired CartRepository cartRepository;
    @Autowired CartItemRepository cartItemRepository;
    @Autowired BatchRepository batchRepository;

    private Cart cart;
    private Product product1;
    private Product productOutOfCart;
    private Product productOutOfStock;

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
            .body("", hasSize(cart.getItems().size()));
    }

    @Test
    public void mustReturnCart_whenAddProductToCart() {
        given()
            .header(AUTH_HEADER_PARAM, authenticationUtil.getCustomerToken())
            .accept(ContentType.JSON)
            .pathParam("productId", productOutOfCart.getId())
        .when()
            .post("/product/{productId}")
        .then()
            .statusCode(HttpStatus.OK.value())
            .body("", hasSize(cart.getItems().size() + 1));
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

    public void prepareData() {
        List<Product> products = new ArrayList<>();

        product1 = new Product();
        product1.setName("Samsung J1 Mini");
        product1.setDescription("RAM: 2GB, CPU: 1.5GHz, HD: 16GB");
        product1.setPrice(BigDecimal.valueOf(650));
        product1.setActive(Boolean.TRUE);
        products.add(product1);

        Product product2 = new Product();
        product2.setName("Monark aro 27");
        product2.setDescription("Aro: 27cm, Marcha: sim, Freio a disco: sim");
        product2.setPrice(BigDecimal.valueOf(895));
        product2.setActive(Boolean.TRUE);
        products.add(product2);

        productOutOfCart = new Product();
        productOutOfCart.setName("Out Of Cart");
        productOutOfCart.setDescription("RAM: 2GB, CPU: 1.5GHz, HD: 16GB");
        productOutOfCart.setPrice(BigDecimal.valueOf(650));
        productOutOfCart.setActive(Boolean.TRUE);
        productOutOfCart = productRepository.save(productOutOfCart);
        products.add(productOutOfCart);

        productOutOfStock = new Product();
        productOutOfStock.setName("Monark aro 27");
        productOutOfStock.setDescription("Aro: 27cm, Marcha: sim, Freio a disco: sim");
        productOutOfStock.setPrice(BigDecimal.valueOf(895));
        productOutOfStock.setActive(Boolean.TRUE);
        products.add(productOutOfStock);

        productRepository.saveAll(products);

        Batch batch = new Batch();
        batch.setProduct(productOutOfCart);
        batch.setQuantity(BigInteger.valueOf(10));
        batch = batchRepository.save(batch);

        Customer customer = authenticationUtil.getCustomer();
        final Cart cart = cartRepository.save(new Cart(customer));
        cart.setCustomer(customer);
        cart.setItems(products.stream()
                .map(product -> {
                    CartItem cartItem = new CartItem(BigInteger.valueOf(5), product, cart);
                    cartItem.setReservedUntil(LocalDateTime.now());
                    cartItem.setReserved(false);
                    return cartItem;
                })
                .map(cartItemRepository::save)
                .collect(Collectors.toList()));

        this.cart = cartRepository.save(cart);
        customer.setCart(this.cart);
        userRepository.save(customer);
    }
}
