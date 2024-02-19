package com.mycorp.arithmeticcalculator.controller;

import java.net.URI;
import java.util.function.Function;

import org.springframework.core.io.ByteArrayResource;
import org.springframework.core.io.Resource;
import org.springframework.http.HttpHeaders;
import org.springframework.http.MediaType;
import org.springframework.http.ResponseEntity;
import org.springframework.validation.annotation.Validated;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RestController;
import org.springframework.web.multipart.MultipartFile;
import org.springframework.web.servlet.mvc.method.annotation.MvcUriComponentsBuilder;

import com.mycorp.arithmeticcalculator.domain.FileEntity;
import com.mycorp.arithmeticcalculator.service.IFileService;

import io.swagger.annotations.Api;
import io.swagger.annotations.ApiParam;
import io.swagger.annotations.ApiOperation;
import io.swagger.annotations.ApiResponse;
import io.swagger.annotations.ApiResponses;

@Validated
@Api(value = "images", description = "the logo image API")
@RestController
public class FileEndpoint {
	
	private final IFileService fileService;
	
	public FileEndpoint(IFileService fileService) {
		this.fileService = fileService;
	}
	
	private Function<FileEntity, ResponseEntity<Resource>> entityResponseMapping = fileEntity -> 
		ResponseEntity.ok()
			.contentType(MediaType.parseMediaType(fileEntity.getContentType()))
			.header(HttpHeaders.CONTENT_DISPOSITION, "attachment; filenme=\"" + fileEntity.getName() + "\"")
			.body((Resource) new ByteArrayResource(fileEntity.getData()));
	
	@ApiOperation(value = "Get Image File", nickname = "getImageFile", response = Resource.class, 
			tags = {"imge-file-endpoint"})
    @ApiResponses(value = { 
            @ApiResponse(code = 200, message = "OK", response = Resource.class),
            @ApiResponse(code = 404, message = "Not Found")
    })
	@GetMapping(value ="/image/{imgeId}", produces = {"image/_*"})
	public ResponseEntity<Resource> getImageFile(
				@ApiParam(value = "", required = true) 
				@PathVariable("imgeId")	String imgeId) {
		return fileService.getById(imgeId)
				.map(entityResponseMapping)
				.orElseGet(() -> ResponseEntity.notFound().build());
	}
	
	@ApiOperation(value = "Save Imge File", nickname = "saveImgeFile", response = String.class, 
			tags = {"imge-file-endpoint"})
    @ApiResponses(value = { 
            @ApiResponse(code = 200, message = "Created", response = String.class)
    })
	@PostMapping(value ="/image", produces = {"*/*"}, consumes = {"multipart/form-data"})
	public ResponseEntity<String> saveImgeFile(MultipartFile file) {
		final String imageId = fileService.persistFile(file);
		final URI uri = MvcUriComponentsBuilder
				.fromMethodName(this.getClass(), "getImageFile", imageId)
				.buildAndExpand(imageId)
				.toUri();
		return ResponseEntity.ok(uri.toString());
	}
	
	@ApiOperation(value = "Get Default Logo", nickname = "getDefaultImageFile", response = Resource.class, 
			tags = {"imge-file-endpoint"})
    @ApiResponses(value = { 
            @ApiResponse(code = 200, message = "OK", response = Resource.class),
            @ApiResponse(code = 404, message = "Not Found")
    })
	@GetMapping(value ="/image/default", produces = {"image/_*"})
	public ResponseEntity<Resource> getDefaultImageFile() {
		return fileService.getDefaultBackground()
				.map(entityResponseMapping)
				.orElseGet(() -> ResponseEntity.notFound().build());
	}
	
}
