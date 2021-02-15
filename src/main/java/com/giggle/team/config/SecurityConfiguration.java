package com.giggle.team.config;

import org.springframework.boot.context.properties.EnableConfigurationProperties;
import org.springframework.context.annotation.Configuration;
import org.springframework.http.HttpMethod;
import org.springframework.security.config.annotation.authentication.builders.AuthenticationManagerBuilder;
import org.springframework.security.config.annotation.web.builders.HttpSecurity;
import org.springframework.security.config.annotation.web.configuration.WebSecurityConfigurerAdapter;

@Configuration
@EnableConfigurationProperties
public class SecurityConfiguration extends WebSecurityConfigurerAdapter {

    /*
     * more about spring security
     * https://mainul35.medium.com/spring-mvc-spring-security-in-memory-user-details-configuration-90d106b53d23
     */

    @Override
    protected void configure(HttpSecurity http) throws Exception {
        http
                .authorizeRequests()
                .antMatchers(HttpMethod.GET, "/").permitAll()
                .anyRequest().authenticated()
                .and().httpBasic()
                .and().csrf().disable()
                .formLogin().disable();
    }

    /*
     * I commented this, due it I must use encode password in
     * inMemoryAuthentication config
    @Bean
    public PasswordEncoder passwordEncoder() {
         // about password encoders
         // https://reflectoring.io/spring-security-password-handling/
        return new BCryptPasswordEncoder();
    }
    */

    @Override
    public void configure(AuthenticationManagerBuilder builder) throws Exception {
        builder.inMemoryAuthentication().withUser("admin").password("{noop}root").roles("ADMIN");
        builder.inMemoryAuthentication().withUser("spring").password("{noop}root").roles("USER");
    }
}
