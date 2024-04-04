package com.mycorp.arithmeticcalculator.service;

import java.util.ArrayList;
import java.util.Collection;
import java.util.List;

import javax.servlet.http.HttpServletRequest;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Qualifier;
import org.springframework.security.core.GrantedAuthority;
import org.springframework.security.core.authority.SimpleGrantedAuthority;
import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.security.core.userdetails.UserDetailsService;
import org.springframework.security.core.userdetails.UsernameNotFoundException;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import com.mycorp.arithmeticcalculator.domain.Privilege;
import com.mycorp.arithmeticcalculator.domain.Role;
import com.mycorp.arithmeticcalculator.domain.User;
import com.mycorp.arithmeticcalculator.repository.UserRepository;
import com.mycorp.arithmeticcalculator.security.LoginAttemptService;

@Service("userDetailsService")
@Transactional
public class MyUserDetailsService implements UserDetailsService {

    @Autowired
    private UserRepository userRepository;

    @Autowired
    @Qualifier("loginAttemptService")
    private LoginAttemptService loginAttemptService;

    @Autowired
    private HttpServletRequest request;

    public MyUserDetailsService() {
        super();
    }

    @Override
    public UserDetails loadUserByUsername(final String email) {
        final String ip = getClientIP();
        if (loginAttemptService.isBlocked(ip)) {
            throw new UsernameNotFoundException("blocked");
        }
        try {
            	final User user = userRepository.findByEmail(email);
            	if (user == null) {
            		throw new UsernameNotFoundException("No user found with username: " + email);
            	}
            	return new org.springframework.security.core.userdetails.User(
            			user.getEmail(), 
            			user.getPassword(), 
            			user.isEnabled(), 
            			true, 
            			true, 
            			true, 
            			getAuthorities(user.getRoles()));
        } catch (final Exception e) {
            throw new RuntimeException(e);
        }
    }

    public final Collection<? extends GrantedAuthority> getAuthorities(final Collection<Role> roles) {
        return getGrantedAuthorities(getPrivileges(roles));
    }

    private final List<String> getPrivileges(final Collection<Role> roles) {
        final List<String> privileges = new ArrayList<>();
        final List<Privilege> collection = new ArrayList<>();
        for (final Role role : roles) {
            collection.addAll(role.getPrivileges());
        }
        for (final Privilege item : collection) {
            privileges.add(item.getName());
        }
        return privileges;
    }

    private final List<GrantedAuthority> getGrantedAuthorities(final List<String> privileges) {
        final List<GrantedAuthority> authorities = new ArrayList<>();
        for (final String privilege : privileges) {
            authorities.add(new SimpleGrantedAuthority(privilege));
        }
        return authorities;
    }

    private String getClientIP() {
        final String xfHeader = request.getHeader("X-Forwarded-For");
        if (xfHeader == null) {
            return request.getRemoteAddr();
        }
        return xfHeader.split(",")[0];
    }
}
