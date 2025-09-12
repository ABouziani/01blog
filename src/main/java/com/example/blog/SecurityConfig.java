package com.example.blog;

import org.springframework.context.annotation.Bean;
import org.springframework.security.config.annotation.web.builders.HttpSecurity;
import org.springframework.security.web.SecurityFilterChain;
import org.springframework.stereotype.Component;

@Component
public class SecurityConfig {

    @Bean
    public SecurityFilterChain securityFilterChain(HttpSecurity http) throws Exception {
        http
            .csrf().disable() // allow curl/Postman requests
            .authorizeHttpRequests()
            .requestMatchers("/api/users/register").permitAll() // allow registration without login
            .anyRequest().authenticated() // protect other endpoints
            .and()
            .httpBasic(); // optional, for testing
        return http.build();
    }
}
