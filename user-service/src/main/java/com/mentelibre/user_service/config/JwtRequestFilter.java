package com.mentelibre.user_service.config;

import com.mentelibre.user_service.webclient.AuthClient;
import jakarta.servlet.FilterChain;
import java.util.List;
import jakarta.servlet.ServletException;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.security.authentication.UsernamePasswordAuthenticationToken;
import org.springframework.security.core.GrantedAuthority;
import org.springframework.security.core.authority.SimpleGrantedAuthority;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.security.web.authentication.WebAuthenticationDetailsSource;
import org.springframework.stereotype.Component;
import org.springframework.web.filter.OncePerRequestFilter;

import java.io.IOException;

@Component
public class JwtRequestFilter extends OncePerRequestFilter {

    @Autowired
    private AuthClient authClient;

    @Override
    protected void doFilterInternal(HttpServletRequest request,
                                   HttpServletResponse response,
                                   FilterChain chain)
            throws ServletException, IOException {

        String header = request.getHeader("Authorization");

        System.out.println("🔵 [USER-SERVICE] JwtRequestFilter ejecutándose...");
        System.out.println("🔵 Path = " + request.getRequestURI());
        System.out.println("🔵 Method = " + request.getMethod());
        System.out.println("🔵 Authorization recibido = " + header);

        if (header == null || !header.startsWith("Bearer ")) {
            System.out.println("🔴 No se envió token. Bloqueando acceso.");
            chain.doFilter(request, response);
            return;
        }

        String token = header.substring(7);

        try {
            AuthValidationResponse data = authClient.validateToken(token);

            Long userId = data.getUserId();
            String role = data.getRol();

            System.out.println("🟢 Token válido");
            System.out.println("🟢 userId = " + userId);
            System.out.println("🟢 rol = " + role);

            List<GrantedAuthority> authorities =
                    List.of(new SimpleGrantedAuthority(role));

            UsernamePasswordAuthenticationToken authentication =
                    new UsernamePasswordAuthenticationToken(userId, null, authorities);

            authentication.setDetails(
                new WebAuthenticationDetailsSource().buildDetails(request)
            );

            SecurityContextHolder.getContext().setAuthentication(authentication);

        } catch (Exception e) {
            System.out.println("🔴 ERROR VALIDANDO TOKEN EN USER-SERVICE");
            e.printStackTrace();
            response.setStatus(HttpServletResponse.SC_UNAUTHORIZED);
            return;
        }

        chain.doFilter(request, response);
    }
}
