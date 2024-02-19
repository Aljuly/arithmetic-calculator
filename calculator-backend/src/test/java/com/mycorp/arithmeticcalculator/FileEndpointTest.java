package com.mycorp.arithmeticcalculator;

import static org.junit.Assert.assertNotNull;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.get;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.content;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.status;

import javax.persistence.EntityManager;
import javax.persistence.PersistenceContext;

import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.boot.test.context.SpringBootTest.WebEnvironment;
import org.springframework.core.io.ClassPathResource;
import org.springframework.core.io.Resource;
import org.springframework.http.MediaType;
import org.springframework.mock.web.MockMultipartFile;
import org.springframework.test.web.servlet.MockMvc;
import org.springframework.test.web.servlet.request.MockHttpServletRequestBuilder;
import org.springframework.test.web.servlet.request.MockMvcRequestBuilders;
import org.springframework.test.web.servlet.setup.MockMvcBuilders;
import org.springframework.transaction.annotation.Transactional;
import org.springframework.util.StreamUtils;
import org.springframework.web.context.WebApplicationContext;

import com.mycorp.arithmeticcalculator.domain.FileEntity;
import com.mycorp.springangularstarter.config.TestDbConfig;
import com.mycorp.springangularstarter.config.TestIntegrationConfig;

@SpringBootTest(classes = { TestDbConfig.class, TestIntegrationConfig.class }, webEnvironment = WebEnvironment.RANDOM_PORT)
@Transactional
public class FileEndpointTest {
	
    @Autowired
    private WebApplicationContext webApplicationContext;
	
    @PersistenceContext
    private EntityManager entityManager;
	
	private MockMvc mockMvc;	
	
	private Resource defaultImage;
	
	@BeforeEach
	public void setUp() {
		mockMvc = MockMvcBuilders.webAppContextSetup(webApplicationContext).build();
		this.defaultImage = new ClassPathResource("img/unknown-person.png");
	}
	
	@Test
	public void shouldReturnLogoWhenIdGiven() throws Exception {
		FileEntity fileLogoEntity = new FileEntity();
		fileLogoEntity.setName("test.png");
		fileLogoEntity.setData(StreamUtils.copyToByteArray(defaultImage.getInputStream()));
		entityManager.persist(fileLogoEntity);
		entityManager.flush();
        entityManager.clear();
		String imgeId = fileLogoEntity.getId().toString();
		mockMvc.perform(get("/image/{imageId}", imgeId))
			.andExpect(status().is(200))
			.andExpect(content().bytes(StreamUtils.copyToByteArray(defaultImage.getInputStream())));
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
				StreamUtils.copyToByteArray(defaultImage.getInputStream()));
		final MockHttpServletRequestBuilder request = MockMvcRequestBuilders.multipart("/image").file(logo);
		final String location = mockMvc.perform(request)
				.andExpect(status().isOk())
				.andReturn()
				.getResponse()
				.getContentAsString();
		assertNotNull(location);
	}
	
}
