package com.mycorp.arithmeticcalculator;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.ComponentScan;
import org.springframework.context.annotation.Configuration;
import org.springframework.context.annotation.Profile;
import org.springframework.http.HttpMethod;
import org.springframework.security.authentication.AuthenticationManager;
import org.springframework.security.authentication.dao.DaoAuthenticationProvider;
import org.springframework.security.config.Customizer;
import org.springframework.security.config.annotation.authentication.builders.AuthenticationManagerBuilder;
import org.springframework.security.config.annotation.web.builders.HttpSecurity;
import org.springframework.security.config.annotation.web.builders.WebSecurity;
import org.springframework.security.config.annotation.web.configuration.EnableWebSecurity;
import org.springframework.security.config.annotation.web.configuration.WebSecurityConfigurerAdapter;
import org.springframework.security.config.http.SessionCreationPolicy;
import org.springframework.security.core.session.SessionRegistry;
import org.springframework.security.core.session.SessionRegistryImpl;
import org.springframework.security.core.userdetails.UserDetailsService;
import org.springframework.security.crypto.bcrypt.BCryptPasswordEncoder;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.security.web.SecurityFilterChain;
import org.springframework.security.web.authentication.AuthenticationFailureHandler;
import org.springframework.security.web.authentication.AuthenticationSuccessHandler;
import org.springframework.security.web.authentication.RememberMeServices;
import org.springframework.security.web.authentication.logout.LogoutSuccessHandler;
import org.springframework.security.web.authentication.rememberme.InMemoryTokenRepositoryImpl;

import com.mycorp.arithmeticcalculator.security.CustomAuthenticationProvider;
import com.mycorp.arithmeticcalculator.security.CustomRememberMeServices;
import com.mycorp.arithmeticcalculator.security.CustomWebAuthenticationDetailsSource;

@Configuration
@ComponentScan(basePackages = { "com.mycorp.arithmeticcalculator.security" })
@EnableWebSecurity
public class SecSecurityConfig {

    @Autowired
    private UserDetailsService userDetailsService;

    @Autowired
    private AuthenticationSuccessHandler myAuthenticationSuccessHandler;

    @Autowired
    private LogoutSuccessHandler myLogoutSuccessHandler;

    @Autowired
    private AuthenticationFailureHandler authenticationFailureHandler;

    @Autowired
    private CustomWebAuthenticationDetailsSource authenticationDetailsSource;

    public SecSecurityConfig() {
        super();
    }
    
//    @Bean
//    public AuthenticationManager authenticationManagerBean() throws Exception {
//        return super.authenticationManagerBean();
//    }
    
    protected void configure(final AuthenticationManagerBuilder auth) throws Exception {
        auth.authenticationProvider(authProvider());
    }

    public void configure(final WebSecurity web) {
        web.ignoring().antMatchers("/resources/**");
    }

	@Bean
	//@Profile("test")
    SecurityFilterChain scurityFilterChain(HttpSecurity http) throws Exception {
        http
                .authorizeRequests(requests -> requests
                        .antMatchers("/v1.0/user/register", "/v1.0/registrationConfirm", "/v1.0/user/resendRegistrationToken", "/v1.0/user/resetPassword", "/v1.0/user/changePassword").permitAll()
                        .antMatchers("/v1.0/user/savePassword", "/v1.0/user/updatePassword", "/v1.0/user/update/2fa").permitAll()
                        .antMatchers("/v1.0/login").permitAll()
                        .antMatchers("/v1.0/users/**").hasRole("ADMIN")
                        .antMatchers("/v1.0/roles*").hasRole("ADMIN")
                        .antMatchers("/v1.0/images/**").hasRole("ADMIN")
                        .antMatchers("/v1.0/users/by-name/{userId}").hasAnyRole("USER", "ADMIN")
                        .antMatchers("/v1.0/users/**").hasRole("ADMIN")
                        .antMatchers("/loggedUsers").hasRole("ADMIN")
                        .anyRequest().authenticated())
                .csrf(csrf -> csrf.disable());
		return http.build();
	}
    
    @Bean
    DaoAuthenticationProvider authProvider() {
        final CustomAuthenticationProvider authProvider = new CustomAuthenticationProvider();
        authProvider.setUserDetailsService(userDetailsService);
        authProvider.setPasswordEncoder(encoder());
        return authProvider;
    }

    @Bean
    PasswordEncoder encoder() {
        return new BCryptPasswordEncoder(11);
    }

    @Bean
    SessionRegistry sessionRegistry() {
        return new SessionRegistryImpl();
    }

    @Bean
    RememberMeServices rememberMeServices() {
        return new CustomRememberMeServices("theKey", userDetailsService, new InMemoryTokenRepositoryImpl());
    }
}