package com.viciobit.config;

import com.viciobit.security.FirebaseAuthenticationFilter;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.http.HttpStatus;
import org.springframework.security.config.annotation.web.builders.HttpSecurity;
import org.springframework.security.web.SecurityFilterChain;
import org.springframework.security.web.authentication.UsernamePasswordAuthenticationFilter;

//Configura Spring Security y conecta el filtro de Firebase
@Configuration
public class SecurityConfig {
    //Utilización de lambdas por versión de Spring security
    @Bean
    public SecurityFilterChain securityFilterChain(HttpSecurity http) throws Exception {

        http.csrf(csrf -> csrf.disable());

        http.authorizeHttpRequests(auth -> {
            auth.requestMatchers("/api/admin/**").hasRole("ADMIN");
            auth.anyRequest().authenticated();
        });

        http.exceptionHandling(ex -> {
            ex.authenticationEntryPoint((request, response, exception) ->
                    response.sendError(HttpStatus.UNAUTHORIZED.value()));

            ex.accessDeniedHandler((request, response, exception) ->
                    response.sendError(HttpStatus.FORBIDDEN.value()));
        });

        http.addFilterBefore(
                new FirebaseAuthenticationFilter(),
                UsernamePasswordAuthenticationFilter.class
        );

        return http.build();
    }
}
