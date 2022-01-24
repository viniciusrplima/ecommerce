package com.pacheco.app.ecommerce;

import com.pacheco.app.ecommerce.api.controller.Routes;
import com.pacheco.app.ecommerce.domain.model.Address;
import com.pacheco.app.ecommerce.domain.model.account.Customer;
import com.pacheco.app.ecommerce.domain.model.account.User;
import com.pacheco.app.ecommerce.domain.model.account.UserRole;
import com.pacheco.app.ecommerce.domain.repository.AddressRepository;
import com.pacheco.app.ecommerce.domain.repository.UserRepository;
import com.pacheco.app.ecommerce.domain.security.jwt.JwtTokenUtil;
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

import java.util.ArrayList;
import java.util.List;

import static com.pacheco.app.ecommerce.util.ResourceUtil.getContentFromResource;
import static io.restassured.RestAssured.given;
import static org.hamcrest.Matchers.hasSize;
import static org.junit.Assert.assertTrue;

@RunWith(SpringRunner.class)
@SpringBootTest(
        classes = EcommerceApplication.class,
        webEnvironment = SpringBootTest.WebEnvironment.RANDOM_PORT
)
@TestPropertySource("/application-test.properties")
public class UserAddressRegisterIT {

    public static final String AUTH_HEADER_PARAM = "Authorization";
    public static final String ADDRESS_JSON_FILE = "/json/address.json";
    public static final String ADDRESS_VALIDATION_ERRORS_JSON_FILE = "/json/address-with-validation-errors.json";

    @Autowired
    AddressRepository addressRepository;

    @Autowired
    UserRepository userRepository;

    @Autowired
    DatabaseCleaner databaseCleaner;

    @Autowired
    JwtTokenUtil jwtTokenUtil;

    @LocalServerPort
    private int port;

    private String customerToken;
    private int numAddresses;

    @Before
    public void setUp() {
        databaseCleaner.clearTablesAndResetSequences();
        prepareData();

        RestAssured.enableLoggingOfRequestAndResponseIfValidationFails();
        RestAssured.port = port;
        RestAssured.basePath = Routes.USERS + "/addresses";
    }

    @Test
    public void mustReturnAllAddresses_whenGetUserAddresses() {
        given()
            .header(AUTH_HEADER_PARAM, customerToken)
            .accept(ContentType.JSON)
        .when()
            .get()
        .then()
            .statusCode(HttpStatus.OK.value())
            .body("", hasSize(numAddresses));
    }

    @Test
    public void mustReturnStatus201_whenRegisterAddress() {
        given()
            .header(AUTH_HEADER_PARAM, customerToken)
            .body(getContentFromResource(ADDRESS_JSON_FILE))
            .accept(ContentType.JSON)
            .contentType(ContentType.JSON)
        .when()
            .post()
        .then()
            .statusCode(HttpStatus.CREATED.value());
    }

    @Test
    public void mustReturnValidationErrors_whenRegisterAddressWithErrors() {
        ValidatableResponse response = given()
            .header(AUTH_HEADER_PARAM, customerToken)
            .body(getContentFromResource(ADDRESS_VALIDATION_ERRORS_JSON_FILE))
            .accept(ContentType.JSON)
            .contentType(ContentType.JSON)
        .when()
            .post()
        .then()
            .statusCode(HttpStatus.BAD_REQUEST.value());

        List<String> objectNames = response.extract().jsonPath().getList("objects.name");

        assertTrue(objectNames.containsAll(List.of("state", "city", "cep", "street", "number", "district")));
    }

    private void prepareData() {
        List<Address> addressList = new ArrayList<>();

        Address address1 = Address.entityBuilder()
                .state("PB")
                .city("Campina Grande")
                .cep("58699-222")
                .district("Dinamerica")
                .street("Floriano Peixoto")
                .number("54G")
                .complement("APT O203")
                .build();
        addressList.add(address1);

        Address address2 = Address.entityBuilder()
                .state("PB")
                .city("Campina Grande")
                .cep("58699-222")
                .district("Dinamerica")
                .street("Floriano Peixoto")
                .number("54G")
                .complement("APT O203")
                .build();
        addressList.add(address2);

        addressRepository.saveAll(addressList);
        numAddresses = addressList.size();

        User customer = Customer.customerBuilder()
                .name("vinicius")
                .email("vinicius@email.com")
                .password("123456")
                .role(UserRole.CUSTOMER)
                .cpf("12345678988")
                .phone("(89) 8888-6933")
                .addresses(addressList)
                .build();

        userRepository.save(customer);
        customerToken = "Bearer " + jwtTokenUtil.generateToken(customer.getEmail());
    }

}
