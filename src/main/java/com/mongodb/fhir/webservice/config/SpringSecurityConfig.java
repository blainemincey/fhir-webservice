package com.mongodb.fhir.webservice.config;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.context.annotation.Configuration;
import org.springframework.http.HttpMethod;
import org.springframework.security.config.annotation.authentication.builders.AuthenticationManagerBuilder;
import org.springframework.security.config.annotation.web.builders.HttpSecurity;
import org.springframework.security.config.annotation.web.configuration.EnableWebSecurity;
import org.springframework.security.config.annotation.web.configuration.WebSecurityConfigurerAdapter;
import org.springframework.security.crypto.factory.PasswordEncoderFactories;
import org.springframework.security.crypto.password.PasswordEncoder;

@Configuration
@EnableWebSecurity
/**
 * Spring Security Configuration
 */
public class SpringSecurityConfig extends WebSecurityConfigurerAdapter {

    @Value("${spring.security.user.name}")
    private String username;

    @Value("${spring.security.user.password}")
    private String password;

    @Value("${spring.security.user.roles}")
    private String role;

    // Logger component
    private static final Logger log = LoggerFactory.getLogger(SpringSecurityConfig.class);

    private PasswordEncoder encoder = PasswordEncoderFactories.createDelegatingPasswordEncoder();

    /**
     *
     * @param auth
     * @throws Exception
     */
    @Override
    protected void configure(AuthenticationManagerBuilder auth) throws Exception {

        log.info("Configure auth for web service.");

        auth.inMemoryAuthentication()
                .withUser(this.username).password(encoder.encode(this.password)).roles(this.role);
    }

    /**
     * Secure the endpoints with HTTP Basic authentication
     * @param http
     * @throws Exception
     */
    @Override
    protected void configure(HttpSecurity http) throws Exception {
        http
                //HTTP Basic authentication
                .httpBasic()
                .and()
                .authorizeRequests()
                .antMatchers(HttpMethod.POST, "/processFhir*/**, ").hasRole(this.role)
                .antMatchers(HttpMethod.GET, "/processFhir*/**, ").hasRole(this.role)
                .anyRequest().authenticated()
                .and()
                .csrf().disable()
                .formLogin().disable();
    }
}
