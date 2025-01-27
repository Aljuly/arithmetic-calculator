package com.mycorp.arithmeticcalculator;

import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.delete;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.get;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.post;
import static org.springframework.test.web.servlet.result.MockMvcResultHandlers.print;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.status;

import org.junit.jupiter.api.BeforeAll;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.TestInstance;
import org.junit.jupiter.api.TestInstance.Lifecycle;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.security.test.context.support.WithAnonymousUser;
import org.springframework.security.test.context.support.WithUserDetails;
import org.springframework.test.web.servlet.MockMvc;
import org.springframework.test.web.servlet.setup.MockMvcBuilders;
import org.springframework.web.context.WebApplicationContext;

import com.mycorp.springangularstarter.config.TestDbConfig;
import com.mycorp.springangularstarter.config.TestIntegrationConfig;

@SpringBootTest(classes = { TestDbConfig.class, ServiceConfig.class, TestIntegrationConfig.class, LoginNotificationConfig.class})
@TestInstance(Lifecycle.PER_CLASS)
public class SecurityIntegrationTest {
	
	@Autowired
	private WebApplicationContext webApplicationContext;
	
	private MockMvc mockMvc;
	
	@BeforeAll
	public void initialize() {
		mockMvc = MockMvcBuilders.webAppContextSetup(webApplicationContext).build();
	}
	
	@Test
	@WithAnonymousUser
	public void whenAnonymousAccessLogin_thenOk() throws Exception {
		mockMvc.perform(post("/api/login"))
		  .andDo(print())
	      .andExpect(status().isOk());
	}

	@Test
	@WithAnonymousUser
	public void whenAnonymousAccessRestrictedEndpoint_thenIsUnauthorized() throws Exception {
		mockMvc.perform(get("/all"))
	      .andExpect(status().isUnauthorized());
	}
	
	//
	
	@Test
	@WithUserDetails()
	public void whenUserAccessUserSecuredEndpoint_thenOk() throws Exception {
		mockMvc.perform(get("/user"))
	      .andExpect(status().isOk());
	}

	@Test
	@WithUserDetails()
	public void whenUserAccessRestrictedEndpoint_thenOk() throws Exception {
		mockMvc.perform(get("/all"))
	      .andExpect(status().isOk());
	}

	@Test
	@WithUserDetails()
	public void whenUserAccessAdminSecuredEndpoint_thenIsForbidden() throws Exception {
		mockMvc.perform(get("/admin"))
	      .andExpect(status().isForbidden());
	}

	@Test
	@WithUserDetails()
	public void whenUserAccessDeleteSecuredEndpoint_thenIsForbidden() throws Exception {
		mockMvc.perform(delete("/delete"))
	      .andExpect(status().isForbidden());
	}
	
	//
	
	@Test
	@WithUserDetails(value = "testUser")
	public void whenAdminAccessUserEndpoint_thenOk() throws Exception {
		mockMvc.perform(get("/user"))
	      .andExpect(status().isOk());
	}

	@Test
	@WithUserDetails(value = "testUser")
	public void whenAdminAccessAdminSecuredEndpoint_thenIsOk() throws Exception {
		mockMvc.perform(get("/admin"))
	      .andExpect(status().isOk());
	}

	@Test
	@WithUserDetails(value = "testUser")
	public void whenAdminAccessDeleteSecuredEndpoint_thenIsOk() throws Exception {
		mockMvc.perform(delete("/delete").content("{}"))
	      .andExpect(status().isOk());
	}

}
