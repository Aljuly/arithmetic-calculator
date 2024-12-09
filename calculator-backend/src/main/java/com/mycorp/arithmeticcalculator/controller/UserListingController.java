package com.mycorp.arithmeticcalculator.controller;

import java.util.List;

import org.springframework.core.io.Resource;
import org.springframework.data.domain.Page;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.validation.annotation.Validated;
import org.springframework.web.bind.annotation.DeleteMapping;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.PutMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;

import com.mycorp.arithmeticcalculator.dto.UserResponce;
import com.mycorp.arithmeticcalculator.service.IUserListingService;

import io.swagger.annotations.Api;
import io.swagger.annotations.ApiOperation;
import io.swagger.annotations.ApiResponse;
import io.swagger.annotations.ApiResponses;
import lombok.extern.slf4j.Slf4j;

@Validated
@Api(value = "users", description = "the user API")
@RestController
@RequestMapping("/api/users")
@Slf4j
public class UserListingController {
	
	private final IUserListingService userListingService;

	public UserListingController(IUserListingService userListingService) {
		this.userListingService = userListingService;
	}
	
    @ApiOperation(value = "Get User list", nickname = "getAllUsers", response = List.class, 
    		tags = {"user-endpoint"})
    @ApiResponses(value = {
    		@ApiResponse(code = 200, message = "OK", response = Resource.class)
    })
	@GetMapping(produces = { "application/json" })
	public ResponseEntity<Page<UserResponce>> getAllUsers(
			@RequestParam(defaultValue = "") String login,
            @RequestParam(defaultValue = "0") Integer page,
            @RequestParam(defaultValue = "10") Integer size,
            @RequestParam(defaultValue = "id") String sortBy,
            @RequestParam(defaultValue = "ASC") String sortOrder) {
		return ResponseEntity.ok(userListingService.findAll(page, size, sortBy, sortOrder)); 
	}
	
    @ApiOperation(value = "Delete User", nickname = "deleteUser", response = String.class, 
    		tags = {"user-endpoint"})
    @ApiResponses(value = {
    		@ApiResponse(code = 200, message = "OK", response = Resource.class),
    		@ApiResponse(code = 404, message = "Not Found")
    })
    @DeleteMapping(value = "{userId}", produces = { "application/json"})
	public ResponseEntity<Resource> delete(@PathVariable("userId") Long userId) {
    	userListingService.deleteUser(userId);
		return new ResponseEntity<>(HttpStatus.OK);
	}
	
    @ApiOperation(value ="Get user by Login name", nickname = "getByName", response = String.class,
    		tags = {"user-endpoint"})
    @ApiResponses(value = {
    		@ApiResponse(code = 200, message = "OK", response = Resource.class),
    		@ApiResponse(code = 404, message = "User not found", response = Resource.class)
    })
    @GetMapping(value ="/by-name/{name}", produces = {"application/json"})
	public ResponseEntity<UserResponce> getByName(@PathVariable("name") String name) {
    	log.info("Getting User by name: {}", name);
		return ResponseEntity.ok(userListingService.getByNme(name));
	}
	
    @ApiOperation(value = "Create User", nickname = "createUser", response = Resource.class,
    		tags = {"user-endpoint"})
    @ApiResponses(value = {
    		@ApiResponse(code = 200, message = "OK", response = Resource.class)
    })
    @PostMapping(produces = {"application/json"}, consumes = {"application/json"})
	public ResponseEntity<UserResponce> create(@RequestBody UserResponce user) {
		return ResponseEntity.ok(userListingService.createUserRecord(user));
	}
	
    @ApiOperation(value = "Updte User", nickname = "updateUser", response = Resource.class,
    		tags = {"user-endpoint"})
    @ApiResponses(value = {
    		@ApiResponse(code = 200, message = "OK", response = Resource.class),
    		@ApiResponse(code = 400, message = "Invalid status value", response = Resource.class)
    })
    @PutMapping(produces = {"application/json"}, consumes = {"application/json"})
	public ResponseEntity<Resource> update(@RequestBody UserResponce user) {
    	userListingService.updateUserRecord(user);
    	return new ResponseEntity<>(HttpStatus.OK);
	}
	
	@ApiOperation(value = "Check if the User email exists", nickname = "isEmailUnique", response = String.class,
			tags = {"user-endpoint"})
	@ApiResponses(value = {
			@ApiResponse(code = 200, message = "Unique: true/false", response = String.class)
	})
	@GetMapping(value = "/email-check", produces = {"application/json"})
	public ResponseEntity<String> isEmailUnique(@RequestParam("email") String email) {
		return ResponseEntity.ok(userListingService.checkUserEmail(email));
	}
	
	@ApiOperation(value = "Check if the User Login exists", nickname = "isUsernameUnique", response = String.class,
			tags = {"user-endpoint"})
	@ApiResponses(value = {
			@ApiResponse(code = 200, message = "Unique: true/false", response = String.class)
	})
	@GetMapping(value = "/login-check", produces = {"application/json"})
	public ResponseEntity<String> isUsernameUnique(@RequestParam("username") String username) {
		return ResponseEntity.ok(userListingService.checkUserName(username));
	}

}
