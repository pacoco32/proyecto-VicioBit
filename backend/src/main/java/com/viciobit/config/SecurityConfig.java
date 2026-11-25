package com.viciobit.config;

import com.viciobit.security.FirebaseAuthenticationFilter;
import jakarta.servlet.DispatcherType;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.http.HttpStatus;
import org.springframework.security.config.annotation.web.builders.HttpSecurity;
import org.springframework.security.web.SecurityFilterChain;
import org.springframework.security.web.authentication.UsernamePasswordAuthenticationFilter;

import java.util.List;

@Configuration
public class SecurityConfig {

    //Correos de los administradores (application.properties - viciobit.admins)
    @Value("${viciobit.admins}")
    private List<String> admins;

    //Utilización de lambdas por versión de Spring security
    @Bean
    public SecurityFilterChain securityFilterChain(HttpSecurity http) throws Exception {

        http.csrf(csrf -> csrf.disable());

        http.authorizeHttpRequests(auth -> {
            // Permite el despacho interno a /error; sin esto, sendError(403) acaba devolviendo 401
            auth.dispatcherTypeMatchers(DispatcherType.ERROR).permitAll();
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
                new FirebaseAuthenticationFilter(admins),
                UsernamePasswordAuthenticationFilter.class
        );

        return http.build();
    }
}
