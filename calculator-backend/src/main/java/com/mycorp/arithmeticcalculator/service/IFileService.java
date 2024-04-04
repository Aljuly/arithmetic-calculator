package com.mycorp.arithmeticcalculator.service;

import java.util.Optional;
import org.springframework.web.multipart.MultipartFile;
import com.mycorp.arithmeticcalculator.domain.FileEntity;

public interface IFileService {
	void setDefaultBackground();
	String persistFile(MultipartFile multipartFile);
	Optional<FileEntity> getById(String id);
	Optional<FileEntity> getDefaultBackground();
}
