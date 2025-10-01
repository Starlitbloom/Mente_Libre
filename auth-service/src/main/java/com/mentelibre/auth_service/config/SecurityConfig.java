package com.mentelibre.auth_service.config;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.security.authentication.AuthenticationManager;
import org.springframework.security.config.annotation.authentication.configuration.AuthenticationConfiguration;
import org.springframework.security.config.annotation.method.configuration.EnableMethodSecurity;
import org.springframework.security.config.annotation.web.builders.HttpSecurity;
import org.springframework.security.config.http.SessionCreationPolicy;
import org.springframework.security.web.SecurityFilterChain;
import org.springframework.security.web.authentication.UsernamePasswordAuthenticationFilter;

@Configuration
@EnableMethodSecurity
public class SecurityConfig {

    @Autowired
    private JwtRequestFilter jwtRequestFilter;

    @Bean
    public AuthenticationManager authenticationManager(AuthenticationConfiguration config) throws Exception {
        return config.getAuthenticationManager();
    }

    @Bean
    public SecurityFilterChain filterChain(HttpSecurity http) throws Exception {
        http
            .csrf(csrf -> csrf.disable()) // Deshabilita CSRF ya que usamos JWT
            .authorizeHttpRequests(auth -> auth
                .requestMatchers( // Rutas públicas
                    "/swagger-ui/**",
                    "/v3/api-docs/**",
                    "/swagger-ui.html",
                    "/api/v1/roles",
                    "/api/v1/auth/login"
                ).permitAll()
                .requestMatchers("/api/v1/users").hasRole("ADMINISTRADOR") // Solo ADMINISTRADOR puede acceder a /api/v1/users
                .anyRequest().authenticated() // El resto de rutas requieren autenticación
            )
            .sessionManagement(sess -> sess.sessionCreationPolicy(SessionCreationPolicy.STATELESS)); // No se crean sesiones, cada solicitud debe ser autenticada

        http.addFilterBefore(jwtRequestFilter, UsernamePasswordAuthenticationFilter.class); // Añade el filtro para validar JWT antes del filtro de autenticación
        return http.build();
    }
}