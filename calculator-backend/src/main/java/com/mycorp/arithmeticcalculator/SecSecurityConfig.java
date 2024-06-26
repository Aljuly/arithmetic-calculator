package com.mycorp.arithmeticcalculator;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.ComponentScan;
import org.springframework.context.annotation.Configuration;
import org.springframework.security.authentication.AuthenticationManager;
import org.springframework.security.authentication.dao.DaoAuthenticationProvider;
import org.springframework.security.config.annotation.authentication.builders.AuthenticationManagerBuilder;
import org.springframework.security.config.annotation.web.builders.HttpSecurity;
import org.springframework.security.config.annotation.web.builders.WebSecurity;
import org.springframework.security.config.annotation.web.configuration.EnableWebSecurity;
import org.springframework.security.config.annotation.web.configuration.WebSecurityConfigurerAdapter;
import org.springframework.security.core.session.SessionRegistry;
import org.springframework.security.core.session.SessionRegistryImpl;
import org.springframework.security.core.userdetails.UserDetailsService;
import org.springframework.security.crypto.bcrypt.BCryptPasswordEncoder;
import org.springframework.security.crypto.password.PasswordEncoder;
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
public class SecSecurityConfig extends WebSecurityConfigurerAdapter {

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
    
    @Bean
    @Override
    public AuthenticationManager authenticationManagerBean() throws Exception {
        return super.authenticationManagerBean();
    }
    
    @Override
    protected void configure(final AuthenticationManagerBuilder auth) throws Exception {
        auth.authenticationProvider(authProvider());
    }

    @Override
    public void configure(final WebSecurity web) {
        web.ignoring().antMatchers("/resources/**");
    }

    @Override
    protected void configure(final HttpSecurity http) throws Exception {
        // @formatter:off
        http
                .csrf(csrf -> csrf.disable())
                .authorizeRequests(requests -> requests
                        .antMatchers("/login*", "/logout*", "/signin/**", "/signup/**", "/customLogin",
                                "/user/registration*", "/registrationConfirm*", "/expiredAccount*", "/registration*",
                                "/badUser*", "/user/resendRegistrationToken*", "/forgetPassword*", "/user/resetPassword*",
                                "/user/changePassword*", "/emailError*", "/resources/**", "/old/user/registration*", "/successRegister*", "/qrcode*").permitAll()
                        .antMatchers("/invalidSession*").anonymous()
                        .antMatchers("/user/updatePassword*", "/user/savePassword*", "/updatePassword*").hasAuthority("CHANGE_PASSWORD_PRIVILEGE")
                        .anyRequest().hasAuthority("READ_PRIVILEGE"))
                .formLogin(login -> login
                        .loginPage("/login")
                        .defaultSuccessUrl("/homepage.html")
                        .failureUrl("/login?error=true")
                        .successHandler(myAuthenticationSuccessHandler)
                        .failureHandler(authenticationFailureHandler)
                        .authenticationDetailsSource(authenticationDetailsSource)
                        .permitAll())
                .sessionManagement(management -> management
                        .invalidSessionUrl("/invalidSession.html")
                        .maximumSessions(1).sessionRegistry(sessionRegistry()).and()
                        .sessionFixation().none())
                .logout(logout -> logout
                        .logoutSuccessHandler(myLogoutSuccessHandler)
                        .invalidateHttpSession(false)
                        .logoutSuccessUrl("/logout.html?logSucc=true")
                        .deleteCookies("JSESSIONID")
                        .permitAll())
                .rememberMe(me -> me.rememberMeServices(rememberMeServices()).key("theKey"));
    // @formatter:on
    }

    // beans

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