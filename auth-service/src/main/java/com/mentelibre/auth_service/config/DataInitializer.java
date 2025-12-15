package com.mentelibre.auth_service.config;

import org.springframework.boot.CommandLineRunner;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.security.crypto.password.PasswordEncoder;

import com.mentelibre.auth_service.model.Rol;
import com.mentelibre.auth_service.model.User;
import com.mentelibre.auth_service.repository.RolRepository;
import com.mentelibre.auth_service.repository.UserRepository;

@Configuration
public class DataInitializer {

    @Bean
    public CommandLineRunner initData(
            RolRepository rolRepo,
            UserRepository userRepo,
            PasswordEncoder encoder
    ) {
        return args -> {

            // ------------------------------
            // CREAR ROLES SI NO EXISTEN
            // ------------------------------
            Rol adminRole = rolRepo.findByNombre("ROLE_ADMIN");
            if (adminRole == null) {
                adminRole = new Rol();
                adminRole.setNombre("ROLE_ADMIN");
                adminRole = rolRepo.save(adminRole);
            }

            Rol userRole = rolRepo.findByNombre("ROLE_USER");
            if (userRole == null) {
                userRole = new Rol();
                userRole.setNombre("ROLE_USER");
                userRole = rolRepo.save(userRole);
            }

            // ------------------------------
            // CREAR USUARIO ADMIN SI NO EXISTE
            // ------------------------------
            if (!userRepo.existsByEmail("admin@gmail.com")) {

                User admin = new User();
                admin.setUsername("Administrador");
                admin.setEmail("admin@gmail.com");
                admin.setPhone("99999999");
                admin.setPassword(encoder.encode("Admin123&"));
                admin.setRol(adminRole);

                userRepo.save(admin);

                System.out.println("✔ Usuario ADMIN creado automáticamente.");
            }
        };
    }

}
