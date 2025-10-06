package com.mentelibre.evaluation_service.config;

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
@EnableMethodSecurity // Habilita la seguridad a nivel de método para controlar el acceso a los endpoints
public class SecurityConfig {
    @Autowired
    private JwtRequestFilter jwtRequestFilter; // Filtro JWT para validar tokens
    
    @Bean
    public SecurityFilterChain filterChain(HttpSecurity http) throws Exception { // Configuración de seguridad HTTP
        http
            .csrf(csrf -> csrf.disable()) // Desactiva CSRF para simplificar las pruebas
            .authorizeHttpRequests(auth -> auth
                .requestMatchers(
                    "/swagger-ui/**",
                    "/v3/api-docs/**",
                    "/swagger-ui.html"
                ).permitAll() // Permite acceso a Swagger y documentación de la API
                .anyRequest().authenticated() // Requiere autenticación para cualquier otra solicitud
            )
            .sessionManagement(sess -> sess.sessionCreationPolicy(SessionCreationPolicy.STATELESS));
        
        http.addFilterBefore(jwtRequestFilter, UsernamePasswordAuthenticationFilter.class);    
        return http.build(); 
    }
    @Bean
    public AuthenticationManager authenticationManager(AuthenticationConfiguration config) throws Exception {
        return config.getAuthenticationManager();
    }

}