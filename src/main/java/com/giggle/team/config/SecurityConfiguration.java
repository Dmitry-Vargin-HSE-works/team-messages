package com.giggle.team.config;

import com.giggle.team.services.UserService;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.http.HttpMethod;
import org.springframework.security.config.annotation.authentication.builders.AuthenticationManagerBuilder;
import org.springframework.security.config.annotation.web.builders.HttpSecurity;
import org.springframework.security.config.annotation.web.configuration.EnableWebSecurity;
import org.springframework.security.config.annotation.web.configuration.WebSecurityConfigurerAdapter;
import org.springframework.security.core.userdetails.UserDetailsService;
import org.springframework.security.crypto.bcrypt.BCryptPasswordEncoder;

/*
 * how to setup security roles
 * https://www.marcobehler.com/guides/spring-security#_authorization_with_spring_security
 */

@Configuration
@EnableWebSecurity
public class SecurityConfiguration extends WebSecurityConfigurerAdapter {

    @Bean
    public BCryptPasswordEncoder getPasswordEncoder() {
        return new BCryptPasswordEncoder();
    }

    @Bean
    public UserDetailsService getUserDetailsService() {
        return new UserService();
    }

    @Override
    public void configure(AuthenticationManagerBuilder builder) throws Exception {
        builder
                .userDetailsService(getUserDetailsService())
                .passwordEncoder(getPasswordEncoder());
    }

    @Override
    protected void configure(HttpSecurity http) throws Exception {
        http.authorizeRequests()
                .antMatchers(HttpMethod.POST, "/api/v1/users", "/loginform.html").permitAll()
                .antMatchers(HttpMethod.GET, "/css/**", "/icons/**", "/js/**", "/fonts/**",
                        "/error.html", "/api/v1/users", "/loginform.html").permitAll()
                .anyRequest().authenticated()
                .and()
                .formLogin().loginPage("/loginform.html")
                .usernameParameter("username")
                .passwordParameter("password").permitAll()
                .and()
                .logout().permitAll().logoutSuccessUrl("/loginform.html")
                .and()
                .httpBasic()
                .and()
                .csrf().disable();
    }


}
