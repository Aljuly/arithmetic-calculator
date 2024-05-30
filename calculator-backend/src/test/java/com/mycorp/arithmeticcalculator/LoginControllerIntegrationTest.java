package com.mycorp.arithmeticcalculator;

import static org.hamcrest.Matchers.containsString;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.post;
import static org.springframework.test.web.servlet.result.MockMvcResultHandlers.print;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.jsonPath;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.status;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.content;

import com.google.common.net.HttpHeaders;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.params.ParameterizedTest;
import org.junit.jupiter.params.provider.Arguments;
import org.junit.jupiter.params.provider.MethodSource;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.boot.test.context.SpringBootTest.WebEnvironment;
import org.springframework.http.MediaType;
import org.springframework.test.web.servlet.MockMvc;
import org.springframework.test.web.servlet.setup.MockMvcBuilders;
import org.springframework.transaction.annotation.Transactional;
import org.springframework.web.context.WebApplicationContext;

import com.mycorp.springangularstarter.config.TestDbConfig;
import com.mycorp.springangularstarter.config.TestIntegrationConfig;

import lombok.extern.slf4j.Slf4j;

import java.util.stream.Stream;

@SpringBootTest(classes = { TestDbConfig.class, TestIntegrationConfig.class }, webEnvironment = WebEnvironment.RANDOM_PORT)
@Transactional
@Slf4j
public class LoginControllerIntegrationTest {
	
	@Autowired
	private WebApplicationContext webApplicationContext;

	private MockMvc mockMvc;
	
    @BeforeEach
    public void setUp() {
		mockMvc = MockMvcBuilders.webAppContextSetup(webApplicationContext).build();
    }

	@ParameterizedTest
	@MethodSource("provideTestData")
    public void shouldReturnToken_WhenUserLogin(String body, int status,
												String titleOne, String valOne,
												String titleTwo, String valTwo) throws Exception {
		mockMvc.perform(post("/login")
				.characterEncoding("utf-8")
				.content(body)
				.header(HttpHeaders.CONTENT_TYPE, MediaType.APPLICATION_JSON))
				.andDo(print())
				.andExpect(status().is(status))
				.andExpect(content().contentType(MediaType.APPLICATION_JSON))
				.andExpect(jsonPath(titleOne, containsString(valOne)))
				.andExpect(jsonPath(titleTwo, containsString(valTwo)));
	}

	private static Stream<Arguments> provideTestData() {
    	return Stream.of(
    			Arguments.of("{\"email\": \"test@test.com\", \"password\": \"Passw0rd!\"}", 200,
						"$.login", "test@test.com",
						"$.token", "."),
				Arguments.of("{\"email\": \"test@test.com\", \"password\": \"123456\"}", 503,
						"$.message", "Unauthorized Access",
						"$.error", "AuthError"),
				Arguments.of("{\"email\": \"noname@test.com\", \"password\": \"123\"}", 404,
						"$.message", "User Not Found",
						"$.error", "UserNotFound")

		);
	}
}
