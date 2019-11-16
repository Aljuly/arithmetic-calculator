package com.mycorp.springangularstarter;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertTrue;
import io.restassured.RestAssured;
import io.restassured.authentication.FormAuthConfig;
import io.restassured.response.Response;
import io.restassured.specification.RequestSpecification;

import java.util.HashMap;
import java.util.Map;

import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.boot.test.context.SpringBootTest.WebEnvironment;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.test.context.junit.jupiter.SpringExtension;

import com.mycorp.arithmeticcalculator.ArithmeticCalculatorApplication;
import com.mycorp.arithmeticcalculator.domain.User;
import com.mycorp.arithmeticcalculator.repository.UserRepository;
import com.mycorp.springangularstarter.config.TestDbConfig;
import com.mycorp.springangularstarter.config.TestIntegrationConfig;


@ExtendWith(SpringExtension.class)
@SpringBootTest(classes = { ArithmeticCalculatorApplication.class, TestDbConfig.class, TestIntegrationConfig.class }, webEnvironment = WebEnvironment.RANDOM_PORT)
public class GetLoggedUsersIntegrationTest {

    @Autowired
    private UserRepository userRepository;

    @Autowired
    private PasswordEncoder passwordEncoder;

    @Value("${local.server.port}")
    int port;

    private FormAuthConfig formConfig;
    private String LOGGED_USERS_URL, SESSION_REGISTRY_LOGGED_USERS_URL;

    //

    @BeforeEach
    public void init() {
        User user = userRepository.findByEmail("test@test.com");
        if (user == null) {
            user = new User();
            user.setFirstName("Test");
            user.setLastName("Test");
            user.setPassword(passwordEncoder.encode("test"));
            user.setEmail("test@test.com");
            user.setEnabled(true);
            userRepository.save(user);
        } else {
            user.setPassword(passwordEncoder.encode("test"));
            userRepository.save(user);
        }

        RestAssured.port = port;
        RestAssured.baseURI = "http://localhost";
        LOGGED_USERS_URL = "/loggedUsers";
        SESSION_REGISTRY_LOGGED_USERS_URL = "/loggedUsersFromSessionRegistry";
        formConfig = new FormAuthConfig("/login", "username", "password");
    }

    @Test
    public void givenLoggedInUser_whenGettingLoggedUsersFromActiveUserStore_thenResponseContainsUser() {
        final RequestSpecification request = RestAssured.given().auth().form("test@test.com", "test", formConfig);

        final Map<String, String> params = new HashMap<String, String>();
        params.put("password", "test");

        final Response response = request.with().params(params).get(LOGGED_USERS_URL);

        assertEquals(200, response.statusCode());
        assertTrue(response.body().asString().contains("test@test.com"));
    }

    @Test
    public void givenLoggedInUser_whenGettingLoggedUsersFromSessionRegistry_thenResponseContainsUser() {
        final RequestSpecification request = RestAssured.given().auth().form("test@test.com", "test", formConfig);

        final Map<String, String> params = new HashMap<String, String>();
        params.put("password", "test");

        final Response response = request.with().params(params).get(SESSION_REGISTRY_LOGGED_USERS_URL);

        assertEquals(200, response.statusCode());
        assertTrue(response.body().asString().contains("test@test.com"));
    }

}