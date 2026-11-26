package com.viciobit.security;

import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseToken;
import jakarta.servlet.FilterChain;
import jakarta.servlet.ServletException;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;
import org.springframework.security.authentication.UsernamePasswordAuthenticationToken;
import org.springframework.security.core.authority.SimpleGrantedAuthority;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.web.filter.OncePerRequestFilter;

import java.io.IOException;
import java.util.List;

public class FirebaseAuthenticationFilter extends OncePerRequestFilter {

    private final List<String> admins;

    public FirebaseAuthenticationFilter(List<String> admins) {
        this.admins = admins;
    }

    @Override
    protected void doFilterInternal(
            HttpServletRequest request,
            HttpServletResponse response,
            FilterChain filterChain) throws ServletException, IOException {

        String authorization = request.getHeader("Authorization");

        if (authorization != null && authorization.startsWith("Bearer ")) {

            String token = authorization.substring(7);

            try {
                FirebaseToken firebaseToken =
                        FirebaseAuth.getInstance().verifyIdToken(token);

                //El rol depende de si el correo está en viciobit.admins
                String email = firebaseToken.getEmail();
                String rol = (email != null && admins.contains(email))
                        ? "ROLE_ADMIN"
                        : "ROLE_CLIENTE";

                UsernamePasswordAuthenticationToken authentication =
                        new UsernamePasswordAuthenticationToken(
                                firebaseToken,
                                null,
                                List.of(new SimpleGrantedAuthority(rol))
                        );

                SecurityContextHolder.getContext().setAuthentication(authentication);

            } catch (Exception e) {
                SecurityContextHolder.clearContext();
                response.sendError(401, "Token no válido o caducado");
                return;
            }
        }

        filterChain.doFilter(request, response);
    }
}