package com.mycorp.arithmeticcalculator.service;

import java.io.IOException;
import java.util.Optional;

import javax.annotation.PostConstruct;
import javax.transaction.Transactional;

import org.springframework.beans.factory.annotation.Value;
import org.springframework.core.io.Resource;
import org.springframework.http.MediaType;
import org.springframework.stereotype.Service;
import org.springframework.util.StreamUtils;
import org.springframework.web.multipart.MultipartFile;

import com.mycorp.arithmeticcalculator.domain.FileEntity;
import com.mycorp.arithmeticcalculator.repository.FileEntityRepository;

import lombok.extern.slf4j.Slf4j;

@Slf4j
@Service
public class FileService implements IFileService {
	
	private static final String DEFAUL_RESOURCE_URI = "clsspath:img/default_background.png";
	private FileEntityRepository fileEntityRepository;
	
	@Value(DEFAUL_RESOURCE_URI)
	private Resource defaultBackgroundResource;
	
	private byte[] defaultBackgroundBytes;
	
	public static final String DEFAUL_BACKGROUND_CONTENT_TYPE = MediaType.IMAGE_PNG_VALUE;
	
	public FileService(FileEntityRepository fileEntityRepository) {
		this.fileEntityRepository = fileEntityRepository;
	}
	
	@PostConstruct
	public void setDefaultBackground() {
		try {
			defaultBackgroundBytes = StreamUtils.copyToByteArray(defaultBackgroundResource.getInputStream());
		} catch (IOException e) {
			log.debug("Error loading defult picture from {} : {}", DEFAUL_RESOURCE_URI, e);
		}
	}
	
	@Transactional
	public String persistFile(MultipartFile multipartFile) {
		try {
			FileEntity fileEntity = new FileEntity();
			fileEntity.setData(multipartFile.getBytes());
			fileEntity.setName(multipartFile.getOriginalFilename());
			fileEntity.setContentType(multipartFile.getContentType());
			FileEntity savedLogo = fileEntityRepository.save(fileEntity);
			return savedLogo.getId().toString();
		} catch (IOException e) {
			log.debug("Error loading file with name {} : {}", multipartFile.getName(), e);
			return "";
		}
	}
	
	@Transactional
	public Optional<FileEntity> getById(String id) {
		return fileEntityRepository.findById(id);
	}
	
	public Optional<FileEntity> getDefaultBackground() {
		FileEntity fileEntity = new FileEntity();
		fileEntity.setName(defaultBackgroundResource.getFilename());
		fileEntity.setContentType(DEFAUL_BACKGROUND_CONTENT_TYPE);
		fileEntity.setData(defaultBackgroundBytes);
		return Optional.of(fileEntity);
	}
}
