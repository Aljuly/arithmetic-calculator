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
import java.util.stream.Collectors;

import org.springframework.security.core.GrantedAuthority;
import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.stereotype.Service;

@SuppressWarnings("deprecation")
@Service
public class TokenService {
	
    private static final String CLAIM_ROLE = "role";

    private static final SignatureAlgorithm SIGNATURE_ALGORITHM = SignatureAlgorithm.HS256;
	private static final SecretKey SECRET_KEY = Keys.secretKeyFor(SIGNATURE_ALGORITHM);
    private static final TemporalAmount TOKEN_VALIDITY = Duration.ofHours(4L);

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
        
        return Jwts.builder()
                .setSubject(userDetails.getUsername())
                .claim(CLAIM_ROLE, scope)
                .setExpiration(expiryDate)
                .setIssuedAt(Date.from(now))
                .signWith(SECRET_KEY)
                .compact();
	}
	
    /**
     * Parses the given JWS signed compact JWT, returning the claims.
     * If this method returns without throwing an exception, the token can be trusted.
     */
    public Claims parseToken(final String compactToken) {       
        return Jwts.parser()
                .verifyWith(SECRET_KEY)
                .build()
                .parseSignedClaims(compactToken)
                .getPayload();
    }
}
