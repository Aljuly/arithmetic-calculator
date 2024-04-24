package com.mycorp.arithmeticcalculator.controller;

import org.springframework.http.ResponseEntity;
import org.springframework.security.authentication.UsernamePasswordAuthenticationToken;
import org.springframework.security.authentication.dao.DaoAuthenticationProvider;
import org.springframework.security.core.AuthenticationException;
import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.security.core.userdetails.UsernameNotFoundException;
import org.springframework.validation.annotation.Validated;
import org.springframework.web.bind.annotation.PostMapping;
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
	
	@ApiOperation(value = "Log In User", nickname = "userLogin", response = String.class, 
			tags = {"login-endpoint"})
    @ApiResponses(value = { 
            @ApiResponse(code = 200, message = "Token", response = String.class)
    })
	@PostMapping(value = "/api/auth/oauth/token", produces = {"application/json"}, consumes = {"application/json"})
	public ResponseEntity<LoginResponse> login(LoginRequest loginRequest) 
			throws AuthenticationException, UsernameNotFoundException, Exception {
		UsernamePasswordAuthenticationToken authenticationToken = new UsernamePasswordAuthenticationToken(
				loginRequest.getEmail(), loginRequest.getPassword());
		authManager.authenticate(authenticationToken);
		UserDetails user = usrDetailsService.loadUserByUsername(loginRequest.getEmail());
		String accessToken = tokenService.createToken(user);
		return ResponseEntity.ok(new LoginResponse(user.getUsername(), accessToken));
	}
	
}
