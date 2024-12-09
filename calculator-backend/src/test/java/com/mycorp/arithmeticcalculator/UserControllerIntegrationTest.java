package com.mycorp.arithmeticcalculator;

import static org.hamcrest.CoreMatchers.is;
import static org.hamcrest.Matchers.iterableWithSize;
import static org.junit.Assert.assertEquals;
import static org.junit.Assert.assertNotNull;
import static org.junit.Assert.assertTrue;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.delete;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.get;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.post;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.put;
import static org.springframework.test.web.servlet.result.MockMvcResultHandlers.print;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.jsonPath;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.status;

import java.util.Arrays;
import java.util.Collection;
import java.util.Optional;
import java.util.stream.Stream;

import org.junit.jupiter.api.AfterEach;
import org.junit.jupiter.api.BeforeAll;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.TestInstance;
import org.junit.jupiter.api.TestInstance.Lifecycle;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.boot.test.context.SpringBootTest.WebEnvironment;
import org.springframework.http.MediaType;
import org.springframework.test.web.servlet.MockMvc;
import org.springframework.test.web.servlet.request.MockHttpServletRequestBuilder;
import org.springframework.test.web.servlet.setup.MockMvcBuilders;
import org.springframework.transaction.annotation.Transactional;
import org.springframework.web.context.WebApplicationContext;

import com.google.common.net.HttpHeaders;
import com.mycorp.arithmeticcalculator.domain.Privilege;
import com.mycorp.arithmeticcalculator.domain.Role;
import com.mycorp.arithmeticcalculator.domain.User;
import com.mycorp.arithmeticcalculator.error.UserNotFoundException;
import com.mycorp.arithmeticcalculator.repository.PrivilegeRepository;
import com.mycorp.arithmeticcalculator.repository.RoleRepository;
import com.mycorp.arithmeticcalculator.repository.UserRepository;
import com.mycorp.springangularstarter.config.TestDbConfig;
import com.mycorp.springangularstarter.config.TestIntegrationConfig;

import lombok.extern.slf4j.Slf4j;

@SpringBootTest(classes = { TestDbConfig.class, TestIntegrationConfig.class }, webEnvironment = WebEnvironment.RANDOM_PORT)
@Transactional
@TestInstance(Lifecycle.PER_CLASS)
@Slf4j
public class UserControllerIntegrationTest {
	
	@Autowired
	private WebApplicationContext webApplicationContext;
	
	@Autowired
    private RoleRepository roleRepository;

	@Autowired
    private PrivilegeRepository privilegeRepository;
	
	@Autowired
	private UserRepository userRepository;
	
	private Role role;
	
	private User userOne;
	
	private User userTwo;
	
	private MockMvc mockMvc;
	
	@BeforeAll
	public void initialize() {
		mockMvc = MockMvcBuilders.webAppContextSetup(webApplicationContext).build();
		this.role = new Role("TEST_ROLE");
    	Privilege privilege = new Privilege("TEST_PRIVILEGE");
    	privilegeRepository.save(privilege);
    	role.setPrivileges(Arrays.asList(privilege));
    	roleRepository.saveAndFlush(role);
	}
	
	@BeforeEach
    private void setUpUser() {
		log.info("Creating test user...");
    	this.userOne = new User();
    	this.userOne.setLogin("bob");
    	this.userOne.setFirstName("Bob");
    	this.userOne.setLastName("Barber");
    	this.userOne.setPassword("password");
    	this.userOne.setEmail("bob@test.com");
    	this.userOne.setEnabled(true);
    	this.userOne.setRoles(Stream.of(role).toList());
		this.userOne = userRepository.saveAndFlush(this.userOne);
    }
    
	@AfterEach
    private void cleanUsers() {
		log.info("deleting test users...");
    	doClean(this.userOne);
    	doClean(this.userTwo);
    }
    
	private void doClean(User user) {
		Optional.ofNullable(user).ifPresent(u -> userRepository.delete(u)); ;
	}
	
    @Test
    public void shouldReturnUsersList() throws Exception {
    	final MockHttpServletRequestBuilder requestBuilder = 
				get("/api/users").contentType(MediaType.APPLICATION_JSON_VALUE);
		mockMvc.perform(requestBuilder)
			.andDo(print())
			.andExpect(status().isOk())
			.andExpect(jsonPath("$.content", iterableWithSize(3)))
			.andExpect(jsonPath("$.totalElements", is(3)))
			.andExpect(jsonPath("$.totalPages", is(1)));	
    }
    
    @Test
    public void shouldReturnUserByName() throws Exception {
    	log.info("Get the user with name: " + "\"testUser\"");
    	mockMvc.perform(get("/api/users/by-name/{name}", "bob"))
    		.andDo(print())
    		.andExpect(status().isOk())
    		.andExpect(jsonPath("$.login", is("bob")))
    		.andExpect(jsonPath("$.userRoles", iterableWithSize(1)));
    }    
    
    @Test
    public void shouldDeleteUser() throws Exception {
    	mockMvc.perform(delete("/api/users/{userId}", 123456))
		.andExpect(status().is(404))
		.andExpect(result -> assertTrue(result.getResolvedException() instanceof UserNotFoundException))
		.andExpect(result -> assertEquals("User to delete not found", result.getResolvedException().getMessage()));
		mockMvc.perform(delete("/api/users/{userId}", this.userOne.getId())).andExpect(status().is(200));
		assertTrue(userRepository.findById(this.userOne.getId()).equals(Optional.empty()));
    }
       
    @Test
    public void testCreateUser() throws Exception {
    	String body = "{\r\n"
    			+ "  \"login\": \"admin\",  \r\n"
    			+ "  \"firstName\": \"admin_first\",\r\n"
    			+ "  \"lastName\": \"admin_last\",\r\n"
    			+ "  \"avatar\": \"assets/fake/storage/adminId-1.png\",\r\n"
    			+ "  \"email\": \"admin@admin\",\r\n"
    			+ "  \"password\": \"123456\",\r\n"
    			+ "  \"lastlogin\": \"Thu Feb 07 2019\",\r\n"
    			+ "  \"enabled\": true,\r\n"
    			+ "  \"banned\": false,\r\n"
    			+ "  \"verified\": true,\r\n"
    			+ "  \"userRoles\": [\r\n"
    			+ "    {\r\n"
    			+ "      \"name\": \"ROLE_ADMIN\",\r\n"
    			+ "      \"description\": \"admin\",\r\n"
    			+ "      \"operations\": [\r\n"
    			+ "        \"OP_USER_VIEW\",\r\n"
    			+ "        \"OP_USER_EXTEND\",\r\n"
    			+ "        \"OP_USER_MANAGE\"\r\n"
    			+ "      ]\r\n"
    			+ "    },\r\n"
    			+ "    {\r\n"
    			+ "      \"name\": \"TEST_ROLE\",\r\n"
    			+ "      \"description\": \"test\",\r\n"
    			+ "      \"operations\": []\r\n"
    			+ "    }\r\n"
    			+ "  ],  \r\n"
    			+ "  \"banReason\": \"\"\r\n"
    			+ "}";
		mockMvc.perform(post("/api/users")
				.characterEncoding("utf-8")
				.content(body)
				.header(HttpHeaders.CONTENT_TYPE, MediaType.APPLICATION_JSON))
				.andDo(print())
				.andExpect(status().isOk());
		this.userTwo = userRepository.findByEmail("admin@admin");
		assertNotNull(userTwo);
		Collection<Role> roles = userTwo.getRoles();
		assertEquals(2, roles.size());
		assertEquals("ROLE_ADMIN", roles.stream().findFirst().get().getName());
    }
    
    @Test
    public void testUpdateUser() throws Exception {
    	Long userId = userRepository.findByEmail("test@test.com").getId();
    	String body = "{\r\n"
    			+ "  \"id\": " + userId.toString() + ",\r\n"
    			+ "  \"login\": \"testUser\",  \r\n"
    			+ "  \"firstName\": \"Bobus\",\r\n"
    			+ "  \"lastName\": \"Barber\",\r\n"
    			+ "  \"avatar\": \"assets/fake/storage/adminId-1.png\",\r\n"
    			+ "  \"email\": \"test@test.com\",\r\n"
    			+ "  \"password\": \"123456\",\r\n"
    			+ "  \"lastlogin\": \"Thu Feb 07 2019\",\r\n"
    			+ "  \"enabled\": true,\r\n"
    			+ "  \"banned\": false,\r\n"
    			+ "  \"verified\": true,\r\n"
    			+ "  \"userRoles\": [\r\n"
    			+ "    {\r\n"
    			+ "      \"name\": \"ROLE_USER\",\r\n"
    			+ "      \"description\": \"user\",\r\n"
    			+ "      \"operations\": []\r\n"
    			+ "    }\r\n"
    			+ "  ],  \r\n"
    			+ "  \"banReason\": \"\"\r\n"
    			+ "}";
		mockMvc.perform(put("/api/users")
				.characterEncoding("utf-8")
				.content(body)
				.header(HttpHeaders.CONTENT_TYPE, MediaType.APPLICATION_JSON))
				.andDo(print())
				.andExpect(status().isOk());
		
		mockMvc.perform(get("/api/users/by-name/{name}", "testUser"))
				.andDo(print())
				.andExpect(status().isOk())
				.andExpect(jsonPath("$.firstName", is("Bobus")))
				.andExpect(jsonPath("$.userRoles", iterableWithSize(1)))
				.andExpect(jsonPath("$.userRoles[0].name", is("ROLE_USER")));
    }
    
    @Test
    public void gotTrueWhenEmailUnique() throws Exception {
    	mockMvc.perform(get("/api/users/email-check").param("email", "bob@test.com"))
    			.andDo(print())
    			.andExpect(status().isOk())
    			.andExpect(jsonPath("$.isUniqueEmail", is(false)));
    }
    
    @Test
    public void gotTrueWhenUserLoginUnique() throws Exception {
    	mockMvc.perform(get("/api/users/login-check").param("username", "bob"))
    			.andDo(print())
    	    	.andExpect(status().isOk())
    	    	.andExpect(jsonPath("$.isUniqueUsername", is(false)));
    }
    
}
