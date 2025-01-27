package com.mycorp.arithmeticcalculator.filter;

import java.io.IOException;

import javax.servlet.FilterChain;
import javax.servlet.ServletException;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;

import org.springframework.stereotype.Component;
import org.springframework.web.filter.OncePerRequestFilter;

import com.mycorp.arithmeticcalculator.security.TokenService;
import com.mycorp.arithmeticcalculator.service.IUserAuthService;

@Component
public class JwtAuthenticationFilter extends OncePerRequestFilter {

	private static final String BEARER_PREFIX = "Bearer ";
	private static final String HEADER_NAME = "Authorization";
	
	private final TokenService tokenService;
	private final IUserAuthService userAuthenticationService;
	
	public JwtAuthenticationFilter(TokenService tokenService, IUserAuthService userAuthenticationService) {
		super();
		this.tokenService = tokenService;
		this.userAuthenticationService = userAuthenticationService;
	}

	@Override
	protected void doFilterInternal(HttpServletRequest request, HttpServletResponse response, FilterChain filterChain)
			throws ServletException, IOException {
		
		var authHeader = request.getHeader(HEADER_NAME);
		if (authHeader == null || authHeader.isEmpty() || !authHeader.startsWith(BEARER_PREFIX)) {
			filterChain.doFilter(request, response);
			return;
		}
		
		filterChain.doFilter(request, response);
	}

}
