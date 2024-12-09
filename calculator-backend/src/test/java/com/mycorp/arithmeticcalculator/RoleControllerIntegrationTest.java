package com.mycorp.arithmeticcalculator;

import static org.hamcrest.Matchers.iterableWithSize;
import static org.junit.Assert.assertEquals;
import static org.junit.Assert.assertNotNull;
import static org.junit.Assert.assertTrue;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.delete;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.get;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.post;
import static org.springframework.test.web.servlet.result.MockMvcResultHandlers.print;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.jsonPath;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.status;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.Collection;
import java.util.Optional;
import java.util.stream.Collectors;
import java.util.stream.Stream;

import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
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
import com.mycorp.arithmeticcalculator.error.RoleNotFoundException;
import com.mycorp.arithmeticcalculator.repository.PrivilegeRepository;
import com.mycorp.arithmeticcalculator.repository.RoleRepository;
import com.mycorp.springangularstarter.config.TestDbConfig;
import com.mycorp.springangularstarter.config.TestIntegrationConfig;

import lombok.extern.slf4j.Slf4j;

@SpringBootTest(classes = { TestDbConfig.class, TestIntegrationConfig.class }, webEnvironment = WebEnvironment.RANDOM_PORT)
@Transactional
@Slf4j
public class RoleControllerIntegrationTest {

	@Autowired
	private WebApplicationContext webApplicationContext;

	@Autowired
    private RoleRepository roleRepository;

	@Autowired
    private PrivilegeRepository privilegeRepository;
    
    private MockMvc mockMvc;
    
    @BeforeEach
    public void setUp() {
    	mockMvc = MockMvcBuilders.webAppContextSetup(webApplicationContext).build();
    }
    
    @Test
	public void shouldReturnRolesList() throws Exception {
		final MockHttpServletRequestBuilder requestBuilder = 
				get("/api/roles").contentType(MediaType.APPLICATION_JSON_VALUE);
		mockMvc.perform(requestBuilder)
			.andExpect(status().isOk())
			.andExpect(jsonPath("@", iterableWithSize(2)));	
	}
    	
	@Test
    public void testDeleteRole() throws Exception {
    	Role role = new Role("TEST_ROLE");
    	Privilege privilege = new Privilege("TEST_PRIVILEGE");
    	privilegeRepository.save(privilege);
    	role.setPrivileges(Arrays.asList(privilege));
    	roleRepository.saveAndFlush(role);
    	Long roleId = role.getId();
    	mockMvc.perform(delete("/api/roles/{roleId}", 123456))
    		.andExpect(status().is(404))
    		.andExpect(result -> assertTrue(result.getResolvedException() instanceof RoleNotFoundException))
    		.andExpect(result -> assertEquals("Role to delete not found", result.getResolvedException().getMessage()));
    	mockMvc.perform(delete("/api/roles/{roleId}", roleId)).andExpect(status().is(200));
    	assertTrue(roleRepository.findById(roleId).equals(Optional.empty()));
    }
	
	@Test
	public void shouldCreateRoleWhenPassed() throws Exception {
		String body = "{ \"name\": \"ROLE_MODERATOR\", \"description\": \"moderator\", \"operations\": [\"WRITE_PRIVILEGE\", "
				+ "\"WRONG_PRIVILEGE\"] }";
		mockMvc.perform(post("/api/roles")
				.characterEncoding("utf-8")
				.content(body)
				.header(HttpHeaders.CONTENT_TYPE, MediaType.APPLICATION_JSON))
				.andDo(print())
				.andExpect(status().isOk());
		Role role = roleRepository.findByName("ROLE_MODERATOR");
		assertNotNull(role);
		Collection<Privilege> privileges = role.getPrivileges();
		assertEquals(1, privileges.size());
		assertEquals("WRITE_PRIVILEGE", privileges.stream().findFirst().get().getName());
	}
	
	@Test
	public void shouldCreateNewRoleIfWrongId() throws Exception {
		String body = "{ \"id\": "
				+ 123456L + ", \"name\": \"ROLE_MENTOR\", \"description\": \"mentor\", \"operations\": [\"WRITE_PRIVILEGE\"] }";
		mockMvc.perform(post("/api/roles")
				.characterEncoding("utf-8")
				.content(body)
				.header(HttpHeaders.CONTENT_TYPE, MediaType.APPLICATION_JSON))
				.andDo(print())
				.andExpect(status().isOk());
		Role role = roleRepository.findByName("ROLE_MENTOR");
		assertNotNull(role);
		Collection<Privilege> privileges = role.getPrivileges();
		assertEquals(1, privileges.size());
		assertEquals("WRITE_PRIVILEGE", privileges.stream().findFirst().get().getName());
	}
	
	@Test
	public void shoulUpdateRoleNameAndPrivileges() throws Exception {
    	Role role = new Role("TEST_ROLE");
    	role.setDescription("moderator");
    	Privilege privilege = new Privilege("TEST_PRIVILEGE");
    	privilegeRepository.save(privilege);
    	role.setPrivileges(Stream.of(privilege).collect(Collectors.toCollection(ArrayList::new)));
    	roleRepository.save(role);
    	log.debug("Found in the Repo: {}", roleRepository.findAll());
    	Long roleId = role.getId();
		String body = "{ \"id\": "
				+ roleId.toString() + ", \"name\": \"ROLE_MODER\", \"description\": \"moder\", \"operations\": [\"WRITE_PRIVILEGE\"] }";
		mockMvc.perform(post("/api/roles")
				.characterEncoding("utf-8")
				.content(body)
				.header(HttpHeaders.CONTENT_TYPE, MediaType.APPLICATION_JSON))
				.andDo(print())
				.andExpect(status().isOk());
		Role changedRole = roleRepository.findByName("ROLE_MODER");
		assertNotNull(changedRole);
		Collection<Privilege> privileges = changedRole.getPrivileges();
		assertEquals("moder", changedRole.getDescription());
		assertEquals(1, privileges.size());
		assertEquals("WRITE_PRIVILEGE", privileges.stream().findFirst().get().getName());
	}

}
