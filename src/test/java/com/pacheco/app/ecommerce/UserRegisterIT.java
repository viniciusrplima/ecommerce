package com.pacheco.app.ecommerce;

import com.pacheco.app.ecommerce.api.controller.Routes;
import com.pacheco.app.ecommerce.domain.model.Address;
import com.pacheco.app.ecommerce.domain.model.account.Customer;
import com.pacheco.app.ecommerce.domain.model.account.User;
import com.pacheco.app.ecommerce.domain.model.account.UserRole;
import com.pacheco.app.ecommerce.domain.repository.AddressRepository;
import com.pacheco.app.ecommerce.domain.repository.UserRepository;
import com.pacheco.app.ecommerce.domain.security.jwt.JwtTokenUtil;
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

import java.util.ArrayList;
import java.util.List;

import static com.pacheco.app.ecommerce.util.ResourceUtil.getContentFromResource;
import static io.restassured.RestAssured.given;
import static org.hamcrest.Matchers.containsString;
import static org.hamcrest.Matchers.hasSize;
import static org.junit.Assert.assertTrue;

@RunWith(SpringRunner.class)
@SpringBootTest(
        classes = EcommerceApplication.class,
        webEnvironment = SpringBootTest.WebEnvironment.RANDOM_PORT
)
@TestPropertySource("/application-test.properties")
public class UserRegisterIT {

    public static final String AUTH_HEADER_PARAM = "Authorization";
    public static final String USER_JSON_FILE = "/json/user.json";
    public static final String USER_VALIDATION_ERRORS_JSON_FILE = "/json/user-with-validation-errors.json";


    @LocalServerPort
    private int port;

    @Autowired
    DatabaseCleaner databaseCleaner;

    @Autowired
    UserRepository userRepository;

    @Autowired
    JwtTokenUtil jwtTokenUtil;

    private String adminToken;
    private int numUsers;

    @Before
    public void setUp() {
        databaseCleaner.clearTablesAndResetSequences();
        prepareData();

        RestAssured.enableLoggingOfRequestAndResponseIfValidationFails();
        RestAssured.port = port;
        RestAssured.basePath = Routes.MANAGEMENT + Routes.USERS;
    }

    @Test
    public void mustReturnAllUsers_whenGetUsers() {
        given()
            .header(AUTH_HEADER_PARAM, adminToken)
            .accept(ContentType.JSON)
        .when()
            .get()
        .then()
            .statusCode(HttpStatus.OK.value())
            .body("", hasSize(numUsers));
    }

    @Test
    public void mustReturnCode201_whenSaveUser() {
        given()
            .header(AUTH_HEADER_PARAM, adminToken)
            .body(getContentFromResource(USER_JSON_FILE))
            .accept(ContentType.JSON)
            .contentType(ContentType.JSON)
        .when()
            .post()
        .then()
            .statusCode(HttpStatus.CREATED.value());
    }

    @Test
    public void mustReturnValidationErrors_whenSaveUserWithValidationErrors() {
        ValidatableResponse response = given()
            .header(AUTH_HEADER_PARAM, adminToken)
            .body(getContentFromResource(USER_VALIDATION_ERRORS_JSON_FILE))
            .accept(ContentType.JSON)
            .contentType(ContentType.JSON)
        .when()
            .post()
        .then()
            .statusCode(HttpStatus.BAD_REQUEST.value());

        List<String> userResponses = response.extract().jsonPath().getList("objects.userMessage");
        List<String> propertyNames = response.extract().jsonPath().getList("objects.name");

        assertTrue(userResponses.stream().anyMatch(containsString("valid")::matches));
        assertTrue(userResponses.stream().anyMatch(containsString("blank")::matches));
        assertTrue(propertyNames.containsAll(List.of("email", "password", "role")));
    }


    private void prepareData() {
        List<User> userList = new ArrayList<>();

        User admin = User.builder()
                .name("admin")
                .email("admin@admin.com")
                .password("123456")
                .role(UserRole.ADMIN)
                .build();

        userList.add(admin);
        adminToken = "Bearer " + jwtTokenUtil.generateToken(admin.getEmail());

        User customer = Customer.customerBuilder()
                .name("vinicius")
                .email("vinicius@email.com")
                .password("123456")
                .role(UserRole.CUSTOMER)
                .cpf("12345678988")
                .phone("(89) 8888-6933")
                .build();

        userList.add(customer);

        userRepository.saveAll(userList);
        numUsers = userList.size();
    }
}
