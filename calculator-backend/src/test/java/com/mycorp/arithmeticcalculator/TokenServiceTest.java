package com.mycorp.arithmeticcalculator;

import static org.junit.Assert.assertEquals;
import static org.mockito.ArgumentMatchers.anyString;
import static org.mockito.Mockito.doReturn;
import static org.mockito.Mockito.spy;
import static org.junit.jupiter.api.Assertions.assertNotNull;
import static org.junit.jupiter.api.Assertions.assertTrue;

import java.util.stream.Stream;

import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.TestInstance;
import org.junit.jupiter.api.TestInstance.Lifecycle;
import org.mockito.Mock;
import org.mockito.MockitoAnnotations;
import org.springframework.security.core.authority.SimpleGrantedAuthority;
import org.springframework.security.core.userdetails.UserDetails;

import com.mycorp.arithmeticcalculator.domain.Role;
import com.mycorp.arithmeticcalculator.domain.User;
import com.mycorp.arithmeticcalculator.dto.UserResponce;
import com.mycorp.arithmeticcalculator.repository.UserRepository;
import com.mycorp.arithmeticcalculator.security.TokenService;

import lombok.extern.slf4j.Slf4j;

@TestInstance(Lifecycle.PER_CLASS)
@Slf4j
public class TokenServiceTest {
	
	private TokenService tokenService;
	
	@Mock
	private UserRepository userRepository;
	
	private UserDetails userDetails;
	
	private User user;
	
	@SuppressWarnings("deprecation")
	@BeforeEach
	public void setUp() {
		MockitoAnnotations.initMocks(this);
		tokenService = spy(new TokenService(userRepository));
		userDetails = new org.springframework.security.core.userdetails.User(
    			"test", 
    			"Passw0rd!", 
    			true, 
    			true, 
    			true, 
    			true, 
    			Stream.of(new SimpleGrantedAuthority("ADMIN")).toList());
		user = new User();
		user.setLogin("test");
    	user.setFirstName("Test");
    	user.setLastName("Test");
    	user.setPassword("Password!");
    	user.setEmail("test@test.com");
    	user.setEnabled(true);
    	user.setRoles(Stream.of(new Role("ADMIN")).toList());
	}
	
	@Test
	public void shouldCreateTokenFromUserDetails() {
		doReturn(user).when(userRepository).findByEmail(anyString());
		String token = tokenService.createToken(userDetails);
		log.info("Token generted is: {}", token);
		UserResponce responce = tokenService.getUserName(token);
		log.info("User data read from token: {}", responce);
		assertNotNull(token);
		assertTrue(token.length() > 0);
		assertTrue(tokenService.isTokenValid(token, userDetails));
		assertEquals(responce.getLogin(), "test");
	}

}
