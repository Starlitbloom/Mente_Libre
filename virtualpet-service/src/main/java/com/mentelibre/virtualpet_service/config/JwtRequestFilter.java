package com.mentelibre.virtualpet_service.config;

import com.mentelibre.virtualpet_service.webclient.AuthClient;
import com.mentelibre.virtualpet_service.dto.AuthValidationResponse;

import jakarta.servlet.FilterChain;
import jakarta.servlet.ServletException;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.security.authentication.UsernamePasswordAuthenticationToken;
import org.springframework.security.core.authority.SimpleGrantedAuthority;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.security.web.authentication.WebAuthenticationDetailsSource;
import org.springframework.stereotype.Component;
import org.springframework.web.filter.OncePerRequestFilter;

import java.io.IOException;
import java.util.List;

@Component
public class JwtRequestFilter extends OncePerRequestFilter {

    @Autowired
    private AuthClient authClient;

    @Override
    protected void doFilterInternal(
            HttpServletRequest request,
            HttpServletResponse response,
            FilterChain chain
    ) throws ServletException, IOException {

        String header = request.getHeader("Authorization");

        System.out.println("[PET-SERVICE] Ejecutando JwtRequestFilter...");
        System.out.println("PATH = " + request.getRequestURI());

        if (header == null || !header.startsWith("Bearer ")) {
            System.out.println("No hay token en cabecera.");
            chain.doFilter(request, response);
            return;
        }

        String token = header.substring(7);

        try {
            AuthValidationResponse data = authClient.validateToken(token);

            Long userId = data.getUserId();
            String role = data.getRol();

            System.out.println("Token válido. userId = " + userId + " rol = " + role);

            var authorities = List.of(new SimpleGrantedAuthority(role));

            UsernamePasswordAuthenticationToken authentication =
                new UsernamePasswordAuthenticationToken(userId, token, authorities);


            authentication.setDetails(
                new WebAuthenticationDetailsSource().buildDetails(request)
            );

            SecurityContextHolder.getContext().setAuthentication(authentication);

        } catch (Exception e) {
            System.out.println("[PET-SERVICE] ERROR VALIDANDO TOKEN");
            e.printStackTrace();
            response.setStatus(HttpServletResponse.SC_UNAUTHORIZED);
            return;
        }

        chain.doFilter(request, response);
    }
}