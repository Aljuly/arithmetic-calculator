package com.mycorp.arithmeticcalculator;

import static org.junit.jupiter.api.Assertions.assertEquals;

import io.restassured.RestAssured;
import io.restassured.response.Response;

import java.util.HashMap;
import java.util.Map;
import java.util.UUID;

import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.boot.test.context.SpringBootTest.WebEnvironment;
import org.springframework.http.HttpStatus;
import org.springframework.http.MediaType;

import com.mycorp.springangularstarter.config.TestDbConfig;
import com.mycorp.springangularstarter.config.TestIntegrationConfig;

@SpringBootTest(classes = { ArithmeticCalculatorApplication.class, TestDbConfig.class, TestIntegrationConfig.class }, webEnvironment = WebEnvironment.RANDOM_PORT)
public class RegistrationPasswordLiveTest {
    private final String BASE_URI = "http://localhost";

    @Value("${local.server.port}")
    int port;
    
    @Test
    public void givenInvalidPassword_thenBadRequest() {
        // too short
        assertEquals(HttpStatus.BAD_REQUEST.value(), getResponseForPassword("123"));

        // no special character
        assertEquals(HttpStatus.BAD_REQUEST.value(), getResponseForPassword("1abZRplYU"));

        // no upper case letter
        assertEquals(HttpStatus.BAD_REQUEST.value(), getResponseForPassword("1_abidpsvl"));

        // no number
        assertEquals(HttpStatus.BAD_REQUEST.value(), getResponseForPassword("abZRYUpl"));

        // alphabet sequence
        assertEquals(HttpStatus.BAD_REQUEST.value(), getResponseForPassword("1_abcdeZRYU"));

        // qwerty sequence
        assertEquals(HttpStatus.BAD_REQUEST.value(), getResponseForPassword("1_abZRTYUI"));

        // numeric sequence
        assertEquals(HttpStatus.BAD_REQUEST.value(), getResponseForPassword("123_zqrtU"));

        // valid password
        assertEquals(HttpStatus.OK.value(), getResponseForPassword("12_zwRHIPKA"));
    }

    private int getResponseForPassword(String pass) {
        final Map<String, String> param = new HashMap<String, String>();
        final String randomName = UUID.randomUUID().toString();
        param.put("firstName", randomName);
        param.put("lastName", "Doe");
        param.put("email", randomName + "@x.com");
        param.put("password", pass);
        param.put("matchingPassword", pass);
        RestAssured.port = port;
        RestAssured.baseURI = BASE_URI;
        final Response response = RestAssured.given().formParams(param).accept(MediaType.APPLICATION_JSON_VALUE).post(BASE_URI + "/user/registration");
        return response.getStatusCode();
    }
}