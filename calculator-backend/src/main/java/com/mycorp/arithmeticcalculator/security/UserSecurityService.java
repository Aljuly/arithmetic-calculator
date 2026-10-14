package com.mycorp.arithmeticcalculator.security;

import java.util.Calendar;

import javax.transaction.Transactional;

import org.springframework.security.authentication.UsernamePasswordAuthenticationToken;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.security.core.userdetails.UserDetailsService;
import org.springframework.stereotype.Service;

import com.mycorp.arithmeticcalculator.domain.PasswordResetToken;
import com.mycorp.arithmeticcalculator.domain.User;
import com.mycorp.arithmeticcalculator.repository.PasswordResetTokenRepository;

@Service
@Transactional
public class UserSecurityService implements ISecurityUserService {

	private final PasswordResetTokenRepository passwordTokenRepository;

	private final UserDetailsService userDetailsService;

	public UserSecurityService(PasswordResetTokenRepository passwordTokenRepository,
			UserDetailsService userDetailsService) {
		this.passwordTokenRepository = passwordTokenRepository;
		this.userDetailsService = userDetailsService;
	}

	// API

	public String validatePasswordResetToken(long id, String token) {
		final PasswordResetToken passToken = passwordTokenRepository.findByToken(token);
		if ((passToken == null) || (passToken.getUser().getId() != id)) {
			return "invalidToken";
		}

		final Calendar cal = Calendar.getInstance();
		if ((passToken.getExpiryDate().getTime() - cal.getTime().getTime()) <= 0) {
			return "expired";
		}

		final User user = passToken.getUser();
		final Authentication auth = new UsernamePasswordAuthenticationToken(user, null,
				userDetailsService.loadUserByUsername(user.getEmail()).getAuthorities());
		SecurityContextHolder.getContext().setAuthentication(auth);
		return null;
	}

}
