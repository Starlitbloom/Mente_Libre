package com.mentelibre.notification_service.config;

import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.http.HttpMethod;
import org.springframework.security.authentication.AuthenticationManager;
import org.springframework.security.config.annotation.method.configuration.EnableMethodSecurity;
import org.springframework.security.config.annotation.web.builders.HttpSecurity;
import org.springframework.security.config.http.SessionCreationPolicy;
import org.springframework.security.crypto.bcrypt.BCryptPasswordEncoder;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.security.web.SecurityFilterChain;
import org.springframework.security.web.authentication.UsernamePasswordAuthenticationFilter;

/**
 * Configuración de seguridad para NotificationService.
 * 
 * - Protege endpoints según rol (STUDENT, ADMIN)
 * - Habilita JWT para autenticación
 * - Configura CORS y sesiones sin estado
 */
@Configuration
@EnableMethodSecurity
public class SecurityConfig {

    private final JwtRequestFilter jwtRequestFilter;

    public SecurityConfig(JwtRequestFilter jwtRequestFilter) {
        this.jwtRequestFilter = jwtRequestFilter;
    }

    @Bean
    public SecurityFilterChain filterChain(HttpSecurity http) throws Exception {
        http
            // Deshabilita CSRF para APIs REST
            .csrf(csrf -> csrf.disable())

            // Política de sesión sin estado
            .sessionManagement(session -> session.sessionCreationPolicy(SessionCreationPolicy.STATELESS))

            // Configuración de endpoints y roles
            .authorizeHttpRequests(auth -> auth
                // Solo administradores pueden gestionar reglas
                .requestMatchers("/api/rules/**").hasRole("ADMIN")

                // Los estudiantes pueden ver sus notificaciones
                .requestMatchers(HttpMethod.GET, "/api/notifications/user/**").hasRole("STUDENT")

                // Otros endpoints de notificaciones (POST, PUT, PATCH, DELETE) requieren ADMIN
                .requestMatchers(HttpMethod.POST, "/api/notifications/**").hasRole("ADMIN")
                .requestMatchers(HttpMethod.PUT, "/api/notifications/**").hasRole("ADMIN")
                .requestMatchers(HttpMethod.PATCH, "/api/notifications/**").hasRole("ADMIN")
                .requestMatchers(HttpMethod.DELETE, "/api/notifications/**").hasRole("ADMIN")

                // Todos los demás endpoints requieren autenticación
                .anyRequest().authenticated()
            )

            // Agrega el filtro JWT antes de UsernamePasswordAuthenticationFilter
            .addFilterBefore(jwtRequestFilter, UsernamePasswordAuthenticationFilter.class);

        return http.build();
    }

    // Bean para encriptar contraseñas si creas usuarios localmente
    @Bean
    public PasswordEncoder passwordEncoder() {
        return new BCryptPasswordEncoder();
    }

    // Bean de AuthenticationManager para autenticación manual si lo necesitas
    @Bean
    public AuthenticationManager authenticationManager(HttpSecurity http) throws Exception {
        return http.getSharedObject(AuthenticationManager.class);
    }
}
