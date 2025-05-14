package com.mycorp.arithmeticcalculator;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.ComponentScan;
import org.springframework.context.annotation.Configuration;
import org.springframework.security.authentication.AuthenticationManager;
import org.springframework.security.authentication.dao.DaoAuthenticationProvider;
import org.springframework.security.config.annotation.authentication.builders.AuthenticationManagerBuilder;
import org.springframework.security.config.annotation.authentication.configuration.AuthenticationConfiguration;
import org.springframework.security.config.annotation.method.configuration.EnableMethodSecurity;
import org.springframework.security.config.annotation.web.builders.HttpSecurity;
import org.springframework.security.config.annotation.web.builders.WebSecurity;
import org.springframework.security.config.annotation.web.configuration.EnableWebSecurity;
import org.springframework.security.config.annotation.web.configurers.AbstractHttpConfigurer;
import org.springframework.security.core.userdetails.UserDetailsService;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.security.web.SecurityFilterChain;
import org.springframework.security.web.authentication.RememberMeServices;
import org.springframework.security.web.authentication.UsernamePasswordAuthenticationFilter;
import org.springframework.security.web.authentication.rememberme.InMemoryTokenRepositoryImpl;
import org.springframework.web.cors.CorsConfiguration;

import com.mycorp.arithmeticcalculator.filter.JwtAuthenticationFilter;
import com.mycorp.arithmeticcalculator.security.CustomAuthenticationProvider;
import com.mycorp.arithmeticcalculator.security.CustomRememberMeServices;

import static org.springframework.security.config.http.SessionCreationPolicy.STATELESS;

import java.util.List;

@Configuration
@ComponentScan(basePackages = { "com.mycorp.arithmeticcalculator.security" })
@EnableMethodSecurity
@EnableWebSecurity(debug = true)
public class SecSecurityConfig {

    @Autowired
    private UserDetailsService userDetailsService;

    @Autowired
    private PasswordEncoder passwordEncoder;

    @Autowired
	private JwtAuthenticationFilter jwtAuthenticationFilter;
    
    public SecSecurityConfig() {
        super();
    }
    
    protected void configure(final AuthenticationManagerBuilder auth) throws Exception {
        auth.authenticationProvider(authProvider());
    }

    public void configure(final WebSecurity web) {
        web.ignoring().antMatchers("/resources/**");
    }

	@Bean
    SecurityFilterChain scurityFilterChain(HttpSecurity http) throws Exception {
        http
		        .cors(cors -> cors.configurationSource(request -> {
		        	CorsConfiguration corsConfiguration = new CorsConfiguration();
		            corsConfiguration.setAllowedOriginPatterns(List.of("*"));
		            corsConfiguration.setAllowedMethods(List.of("GET", "POST", "PUT", "DELETE", "OPTIONS"));
		            corsConfiguration.setAllowedHeaders(List.of("*"));
		            corsConfiguration.setAllowCredentials(true);
		            return corsConfiguration;
		        }))
                .authorizeHttpRequests(requests -> requests
                        .antMatchers("/v1.0/user/register", "/v1.0/registrationConfirm", "/v1.0/user/resendRegistrationToken", "/v1.0/user/resetPassword", "/v1.0/user/changePassword").permitAll()
                        .antMatchers("/v1.0/user/savePassword", "/v1.0/user/updatePassword", "/v1.0/user/update/2fa").permitAll()
                        .antMatchers("/v1.0/login").permitAll()
                        .antMatchers("/v1.0/users/**").hasRole("ADMIN")
                        .antMatchers("/v1.0/roles/**").hasRole("ADMIN")
                        .antMatchers("/v1.0/images/**").hasRole("ADMIN")
                        .antMatchers("/v1.0/users/by-name/{userId}").hasAnyRole("USER", "ADMIN")
                        .antMatchers("/v1.0/users/**").hasRole("ADMIN")
                        .antMatchers("/loggedUsers").hasRole("ADMIN")
                        .anyRequest().authenticated())
                .sessionManagement(manager -> manager.sessionCreationPolicy(STATELESS))
                .authenticationProvider(authProvider())
                .addFilterBefore(jwtAuthenticationFilter, UsernamePasswordAuthenticationFilter.class)
                .csrf(AbstractHttpConfigurer::disable);
		return http.build();
	}
    
    @Bean
    DaoAuthenticationProvider authProvider() {
        final CustomAuthenticationProvider authProvider = new CustomAuthenticationProvider();
        authProvider.setUserDetailsService(userDetailsService);
        authProvider.setPasswordEncoder(passwordEncoder);
        return authProvider;
    }

    @Bean
    AuthenticationManager authenticationManager(AuthenticationConfiguration config)
            throws Exception {
        return config.getAuthenticationManager();
    }
    
    @Bean
    RememberMeServices rememberMeServices() {
        return new CustomRememberMeServices("theKey", userDetailsService, new InMemoryTokenRepositoryImpl());
    }
}