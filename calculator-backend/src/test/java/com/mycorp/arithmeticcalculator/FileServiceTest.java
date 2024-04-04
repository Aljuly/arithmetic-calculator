package com.mycorp.arithmeticcalculator;

import static org.junit.Assert.assertEquals;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.doReturn;
import static org.mockito.Mockito.doThrow;
import static org.mockito.Mockito.spy;

import java.io.IOException;

import org.junit.Before;
import org.junit.Test;
import org.mockito.Mock;
import org.mockito.MockitoAnnotations;
import org.springframework.http.MediaType;
import org.springframework.mock.web.MockMultipartFile;
import org.springframework.web.multipart.MultipartFile;

import com.mycorp.arithmeticcalculator.domain.FileEntity;
import com.mycorp.arithmeticcalculator.repository.FileEntityRepository;
import com.mycorp.arithmeticcalculator.service.FileService;

public class FileServiceTest {

	private FileService fileService;
	
	@Mock
	MultipartFile multipartFile;
	
	@Mock
	FileEntityRepository entityRepository;
	
	@Before
	public void setUp() throws Exception {
		MockitoAnnotations.initMocks(this);
		fileService = spy(new FileService(entityRepository));
	}
	
	@Test
	public void testSaveLogo() throws Exception {
		MockMultipartFile logo = new MockMultipartFile(
				"file", 
				"logo.png", 
				MediaType.IMAGE_PNG_VALUE, 
				"some-entry".getBytes());
		FileEntity logoEntity = new FileEntity();
		Long logoId = 10000L;
		logoEntity.setId(logoId);
		doReturn(logoEntity).when(entityRepository).save(any());
		String imageId = fileService.persistFile(logo);
		assertEquals(imageId, logoId.toString());
	}
	
	@Test
	public void shouldReturnEmptyStringWhenExceptionThrowing() throws IOException {
		doThrow(IOException.class).when(multipartFile).getBytes();
		String imageId = fileService.persistFile(multipartFile);
		assertEquals(imageId, "");
	}
}
