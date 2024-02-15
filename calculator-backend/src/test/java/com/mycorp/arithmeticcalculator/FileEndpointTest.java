package com.mycorp.arithmeticcalculator;

import static org.junit.Assert.assertNotNull;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.get;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.content;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.status;

import javax.persistence.EntityManager;
import javax.persistence.PersistenceContext;

import org.junit.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.boot.test.autoconfigure.web.servlet.AutoConfigureMockMvc;
import org.springframework.boot.test.context.ConfigFileApplicationContextInitializer;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.boot.test.context.SpringBootTest.WebEnvironment;
import org.springframework.core.io.Resource;
import org.springframework.http.MediaType;
import org.springframework.mock.web.MockMultipartFile;
import org.springframework.test.context.ContextConfiguration;
import org.springframework.test.web.servlet.MockMvc;
import org.springframework.test.web.servlet.request.MockHttpServletRequestBuilder;
import org.springframework.test.web.servlet.request.MockMvcRequestBuilders;
import org.springframework.transaction.annotation.Transactional;
import org.springframework.util.StreamUtils;

import com.mycorp.arithmeticcalculator.domain.FileEntity;
import com.mycorp.springangularstarter.config.TestDbConfig;
import com.mycorp.springangularstarter.config.TestIntegrationConfig;

@SpringBootTest(classes = { TestDbConfig.class, TestIntegrationConfig.class }, webEnvironment = WebEnvironment.RANDOM_PORT)
//@ContextConfiguration(initializers = ConfigFileApplicationContextInitializer.class)
//@AutoConfigureMockMvc
@Transactional
public class FileEndpointTest {
	
	@Value("classpath:img/unknown-person.png")
	private Resource defaultImage;
	
	@Autowired
	private EntityManager entityManager;
	
	private MockMvc mockMvc;
	
	@Test
	public void shouldReturnLogoWhenIdGiven() throws Exception {
		FileEntity fileLogoEntity = new FileEntity();
		fileLogoEntity.setName("test.png");
		fileLogoEntity.setData("test_data".getBytes());
		entityManager.persist(fileLogoEntity);
		entityManager.flush();
		String imgeId = fileLogoEntity.getId().toString();
		entityManager.clear();
		mockMvc.perform(get("/image").param("imgeId", imgeId))
			.andExpect(status().is(200))
			.andExpect(content().bytes("test_data".getBytes()));
		mockMvc.perform(get("/image").param("imgeId", ""))
			.andExpect(status().is(404));
	}
	
	@Test
	public void shouldReturnDefaultImage() throws Exception {
		final byte[] logoBytes = StreamUtils.copyToByteArray(defaultImage.getInputStream());
		mockMvc.perform(get("/image/default"))
			.andExpect(status().isOk())
			.andExpect(content().contentType(MediaType.IMAGE_PNG))
			.andExpect(content().bytes(logoBytes));
	}
	
	@Test
	public void shouldSaveLogoImageAndReturnLoction() throws Exception {
		final MockMultipartFile logo = new MockMultipartFile(
				"file", 
				"logo.png", 
				MediaType.IMAGE_PNG_VALUE, 
				"some-entry".getBytes());
		final MockHttpServletRequestBuilder request = MockMvcRequestBuilders.multipart("/image").file(logo);
		final String location = mockMvc.perform(request)
				.andExpect(status().isOk())
				.andReturn()
				.getResponse()
				.getContentAsString();
		assertNotNull(location);
	}
	
}
