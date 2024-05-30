package com.mycorp.arithmeticcalculator.controller;

import org.springframework.http.ResponseEntity;
import org.springframework.security.authentication.BadCredentialsException;
import org.springframework.security.authentication.UsernamePasswordAuthenticationToken;
import org.springframework.security.authentication.dao.DaoAuthenticationProvider;
import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.security.core.userdetails.UsernameNotFoundException;
import org.springframework.validation.annotation.Validated;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RestController;

import com.mycorp.arithmeticcalculator.dto.LoginRequest;
import com.mycorp.arithmeticcalculator.dto.LoginResponse;
import com.mycorp.arithmeticcalculator.security.TokenService;
import com.mycorp.arithmeticcalculator.service.MyUserDetailsService;

import io.swagger.annotations.Api;
import io.swagger.annotations.ApiOperation;
import io.swagger.annotations.ApiResponse;
import io.swagger.annotations.ApiResponses;

@Validated
@Api(value = "login", description = "the Login API")
@RestController
public class LoginController {

	private final TokenService tokenService;
	private final DaoAuthenticationProvider authManager;
	private final MyUserDetailsService usrDetailsService;
	
	public LoginController(TokenService tokenService, DaoAuthenticationProvider authManager,
			MyUserDetailsService usrDetailsService) {
		super();
		this.tokenService = tokenService;
		this.authManager = authManager;
		this.usrDetailsService = usrDetailsService;
	}	
	
	/**
	 * Response tocken example
		{
		  "sub": "{\"id\":\"6\",\"login\":\"\",\"password\":\"\",\"email\":\"test@test.com\",\"firstName\":\"Test\",\"lastName\":\"Test\",\"avatar\":\"\",\"lastlogin\":\"\",\"enabled\":true,\"banned\":false,\"verified\":true,\"banReason\":\"\",\"userRoles\":[{\"id\":4,\"description\":null,\"name\":\"ROLE_ADMIN\",\"operations\":[\"READ_PRIVILEGE\",\"WRITE_PRIVILEGE\",\"CHANGE_PASSWORD_PRIVILEGE\"]}]}",
		  "role": "CHANGE_PASSWORD_PRIVILEGE READ_PRIVILEGE WRITE_PRIVILEGE",
		  "exp": 1717111122,
		  "iat": 1717096722
		}
	 */
	
	@ApiOperation(value = "Log In User", nickname = "userLogin", response = String.class, 
			tags = {"login-endpoint"})
    @ApiResponses(value = { 
            @ApiResponse(code = 200, message = "Token", response = String.class)
    })
	@PostMapping(value = "/login", produces = {"application/json"}, consumes = {"application/json"})
	public ResponseEntity<LoginResponse> login(@RequestBody LoginRequest loginRequest)
			throws BadCredentialsException, UsernameNotFoundException {
		UsernamePasswordAuthenticationToken authenticationToken = new UsernamePasswordAuthenticationToken(
				loginRequest.getEmail(), loginRequest.getPassword());
		authManager.authenticate(authenticationToken);
		UserDetails user = usrDetailsService.loadUserByUsername(loginRequest.getEmail());
		String accessToken = tokenService.createToken(user);
		return ResponseEntity.ok(new LoginResponse(user.getUsername(), accessToken));
	}

}
