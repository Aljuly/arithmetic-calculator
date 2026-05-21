package com.mycorp.arithmeticcalculator.security;

import io.jsonwebtoken.Claims;
import io.jsonwebtoken.Jwts;
import io.jsonwebtoken.SignatureAlgorithm;
import io.jsonwebtoken.security.Keys;

import javax.crypto.SecretKey;
import java.time.Duration;
import java.time.Instant;
import java.time.temporal.TemporalAmount;
import java.util.Date;
import java.util.function.Function;
import java.util.stream.Collectors;

import org.springframework.security.core.GrantedAuthority;
import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.security.core.userdetails.UsernameNotFoundException;
import org.springframework.stereotype.Service;

import com.mycorp.arithmeticcalculator.domain.User;
import com.mycorp.arithmeticcalculator.dto.UserResponce;
import com.mycorp.arithmeticcalculator.repository.UserRepository;

@SuppressWarnings("deprecation")
@Service
public class TokenService {
	
	private static final String CLAIM_ROLE = "role";

    private static final SignatureAlgorithm SIGNATURE_ALGORITHM = SignatureAlgorithm.HS256;
	private static final SecretKey SECRET_KEY = Keys.secretKeyFor(SIGNATURE_ALGORITHM);
    private static final TemporalAmount TOKEN_VALIDITY = Duration.ofHours(4L);

    private UserRepository userRepository;
    
    public TokenService(UserRepository userRepository) {
		this.userRepository = userRepository;
	}    
    
    /**
     * Builds a JWT with the given subject and role and returns it as a JWS signed compact String.
     */
	public String createToken(final String subject, final String role) {
        final Instant now = Instant.now();
        final Date expiryDate = Date.from(now.plus(TOKEN_VALIDITY));
        return Jwts.builder()
                .setSubject(subject)
                .claim(CLAIM_ROLE, role)
                .setExpiration(expiryDate)
                .setIssuedAt(Date.from(now))
                .signWith(SECRET_KEY)
                .compact();
    }

	public String createToken(UserDetails userDetails) {
        final Instant now = Instant.now();
        final Date expiryDate = Date.from(now.plus(TOKEN_VALIDITY));
        
        String scope = userDetails.getAuthorities().stream()
                .map(GrantedAuthority::getAuthority)
                .collect(Collectors.joining(" "));
        try {
        	final User user = userRepository.findByEmail(userDetails.getUsername());
        	if (user == null) {
        		throw new UsernameNotFoundException("No user found during tocken generation");
        	} else {
        		UserResponce response = new UserResponce(user);
        		return Jwts.builder()
	                .setSubject(response.toJson())
	                .claim(CLAIM_ROLE, scope)
	                .setExpiration(expiryDate)
	                .setIssuedAt(Date.from(now))
	                .signWith(SECRET_KEY)
	                .compact();
        	}
        } catch (final Exception e) {
        	throw new RuntimeException(e);
        }
	}
	
	/**
	 * Returns user name got from the token 
	 * @param token
	 * @return User name
	 */
	public UserResponce getUserName(String token) {
		String userData = parseSignedClaims(token, Claims::getSubject);
		try {
			return UserResponce.fromJson(userData);
		} catch (final Exception e) {
			throw new RuntimeException(e);
		}
	}
	
	/**
	 * check token validity
	 * @param token
	 * @param userDetails
	 * @return true if token valid
	 */
	public boolean isTokenValid(String token, UserDetails userDetails) {
		final String userName = getUserName(token).getEmail();
		return (userName.equals(userDetails.getUsername())) && !isTokenExpired(token);
	}
	
	/**
	 * check if token is expired
	 * @param token
	 * @return true if token is expired
	 */
	private boolean isTokenExpired(String token) {
		Date tokenExpirationDate = parseSignedClaims(token, Claims::getExpiration);
		return tokenExpirationDate.before(new Date());
	}
	
    /**
     * Parses the given JWS signed compact JWT, returning the claims.
     * If this method returns without throwing an exception, the token can be trusted.
     */
    private Claims parseToken(final String compactToken) {       
        return Jwts.parser()
        		.setSigningKey(SECRET_KEY)
                .build()
                .parseClaimsJws(compactToken)
                .getBody();
    }
    
    private <T> T parseSignedClaims(String token, Function<Claims, T> claimsResolvers) {
    	final Claims claims = parseToken(token);
    	return claimsResolvers.apply(claims);
    }
    
    
}
