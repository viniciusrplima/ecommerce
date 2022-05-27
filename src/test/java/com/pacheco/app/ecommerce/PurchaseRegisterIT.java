package com.pacheco.app.ecommerce;

import com.pacheco.app.ecommerce.api.controller.Routes;
import com.pacheco.app.ecommerce.api.model.output.AddressModel;
import com.pacheco.app.ecommerce.api.model.output.PurchaseModel;
import com.pacheco.app.ecommerce.domain.mapper.CartMapper;
import com.pacheco.app.ecommerce.domain.model.Address;
import com.pacheco.app.ecommerce.domain.model.Cart;
import com.pacheco.app.ecommerce.domain.model.CartItem;
import com.pacheco.app.ecommerce.domain.model.Product;
import com.pacheco.app.ecommerce.domain.model.Purchase;
import com.pacheco.app.ecommerce.domain.model.PurchaseItem;
import com.pacheco.app.ecommerce.domain.model.PurchaseState;
import com.pacheco.app.ecommerce.domain.model.account.Customer;
import com.pacheco.app.ecommerce.domain.repository.AddressRepository;
import com.pacheco.app.ecommerce.domain.repository.CartItemRepository;
import com.pacheco.app.ecommerce.domain.repository.CartRepository;
import com.pacheco.app.ecommerce.domain.repository.ProductRepository;
import com.pacheco.app.ecommerce.domain.repository.PurchaseItemRepository;
import com.pacheco.app.ecommerce.domain.repository.PurchaseRepository;
import com.pacheco.app.ecommerce.domain.repository.UserRepository;
import com.pacheco.app.ecommerce.util.AuthenticationUtil;
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

import java.math.BigDecimal;
import java.math.BigInteger;
import java.time.LocalDateTime;
import java.util.ArrayList;
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
    public static final String ADDRESS_WITH_ID_JSON_FILE = "/json/address-with-id.json";

    @LocalServerPort
    private int port;

    @Autowired DatabaseCleaner databaseCleaner;
    @Autowired AuthenticationUtil authenticationUtil;

    @Autowired UserRepository userRepository;
    @Autowired ProductRepository productRepository;
    @Autowired CartRepository cartRepository;
    @Autowired CartItemRepository cartItemRepository;
    @Autowired AddressRepository addressRepository;
    @Autowired PurchaseRepository purchaseRepository;
    @Autowired PurchaseItemRepository purchaseItemRepository;

    private int totalPurchases;
    private Purchase purchase;
    private Address address;
    private Cart cart;
    private List<Product> products;

    // class to compare puchase items in map format and the cart items
    public class PurchaseCartItemsComparator implements Comparator<Object> {
        @Override
        public int compare(Object purchaseItemMapObj, Object cartItemObj) {
            int result = 1;
            Map<String, Object> purchaseItemMap = (Map<String, Object>) purchaseItemMapObj;
            CartItem cartItem = (CartItem) cartItemObj;

            BigInteger purchaseQuantity = BigInteger.valueOf((Integer) purchaseItemMap.get("quantity"));
            Long purchaseProductId = Long.valueOf((Integer)((Map<String, Object>)purchaseItemMap.get("product")).get("id"));

            if (cartItem.getQuantity().equals(purchaseQuantity) &&
                    cartItem.getProduct().getId().equals(purchaseProductId)) {
                result = 0;
            }

            return result;
        }
    }

    @Before
    public void setUp() {
        databaseCleaner.clearTablesAndResetSequences();
        authenticationUtil.setUp();
        prepareData();

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
            .body("", hasSize(totalPurchases));
    }

    @Test
    public void mustReturnPurchase_whenGetPurchaseById() {
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
            .body("items", hasSize(cart.getItems().size()));

        // Verifying if it have all products from cart
        List<Map<String, Object>> purchaseItems = response.extract().jsonPath().getList("items");
        List<CartItem> cartItems = cart.getItems();
        PurchaseCartItemsComparator comparator = new PurchaseCartItemsComparator();

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
    public void mustNotUpdatePurchaseAddress_whenUpdateUserAddress() {
        PurchaseModel expectedPurchase = given()
            .accept(ContentType.JSON)
            .header(AUTH_HEADER_PARAM, authenticationUtil.getCustomerToken())
            .body(getContentFromResource(ADDRESS_JSON_FILE))
            .pathParam("purchaseId", purchase.getId())
        .when()
            .get("/{purchaseId}", purchase.getId())
        .then()
            .statusCode(HttpStatus.OK.value())
            .extract().as(PurchaseModel.class);
        AddressModel expectedAddress = expectedPurchase.getAddress();

        given()
            .accept(ContentType.JSON)
            .contentType(ContentType.JSON)
            .basePath(Routes.USER_ADDRESSES)
            .header(AUTH_HEADER_PARAM, authenticationUtil.getCustomerToken())
            .pathParam("addressId", address.getId())
            .body(getContentFromResource(ADDRESS_JSON_FILE))
        .when()
            .put("/{addressId}", address.getId())
        .then()
            .statusCode(HttpStatus.OK.value());

        given()
            .accept(ContentType.JSON)
            .header(AUTH_HEADER_PARAM, authenticationUtil.getCustomerToken())
            .body(getContentFromResource(ADDRESS_JSON_FILE))
            .pathParam("purchaseId", purchase.getId())
        .when()
            .get("/{purchaseId}", purchase.getId())
        .then()
            .statusCode(HttpStatus.OK.value())
            .body("address.cep", is(expectedAddress.getCep()))
            .body("address.city", is(expectedAddress.getCity()))
            .body("address.street", is(expectedAddress.getStreet()))
            .body("address.district", is(expectedAddress.getDistrict()))
            .body("address.number", is(expectedAddress.getNumber()))
            .body("address.referencePoint", is(expectedAddress.getReferencePoint()))
            .body("address.state", is(expectedAddress.getState()))
            .body("address.complement", is(expectedAddress.getComplement()));
    }

    public void prepareData() {
        products = new ArrayList<>();

        Product product1 = new Product();
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

        productRepository.saveAll(products);

        Customer customer = authenticationUtil.getCustomer();
        cart = cartRepository.save(new Cart(customer));

        List<CartItem> cartItems = new ArrayList<>();
        for (Product product : products) {
            CartItem cartItem = CartItem.builder()
                    .quantity(BigInteger.valueOf(5))
                    .cart(cart)
                    .product(product)
                    .reserved(true)
                    .reservedUntil(LocalDateTime.now())
                    .build();
            cartItem = cartItemRepository.save(cartItem);
            cartItems.add(cartItem);
        }

        cart.setCustomer(customer);
        cart.setItems(cartItems);
        cart = cartRepository.save(cart);
        customer.setCart(cart);
        userRepository.save(customer);

        address = Address.builder()
                .state("PB")
                .city("Campina Grande")
                .cep("58699-222")
                .district("Dinamerica")
                .street("Floriano Peixoto")
                .number("54G")
                .complement("APT O203")
                .build();
        Address purchaseAddress = new Address(address);

        addressRepository.save(address);
        addressRepository.save(purchaseAddress);
        customer.setAddresses(List.of(address));
        userRepository.save(customer);

        purchase = Purchase.builder()
                .state(PurchaseState.WAITING)
                .address(purchaseAddress)
                .customer(authenticationUtil.getCustomer())
                .shipping(BigDecimal.valueOf(10.0))
                .build();
        purchaseRepository.save(purchase);
        totalPurchases = 1;

        List<PurchaseItem> purchaseItems = CartMapper.toPurchaseItems(cart, purchase);
        purchaseItemRepository.saveAll(purchaseItems);
        purchase.setItems(purchaseItems);
    }
}
