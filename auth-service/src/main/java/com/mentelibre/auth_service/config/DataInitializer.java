package com.mentelibre.auth_service.config;

import org.springframework.boot.CommandLineRunner;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;

import com.mentelibre.auth_service.model.Rol;
import com.mentelibre.auth_service.repository.RolRepository;

@Configuration
public class DataInitializer {

    @Bean
    public CommandLineRunner initRoles(RolRepository rolRepository) {
        return args -> {

            if (rolRepository.count() == 0) {

                Rol admin = new Rol();
                admin.setNombre("ROLE_ADMIN");
                rolRepository.save(admin);

                Rol user = new Rol();
                user.setNombre("ROLE_USER");
                rolRepository.save(user);

                System.out.println("Roles creados automáticamente.");
            } else {
                System.out.println("Roles ya existen, no se crearán.");
            }
        };
    }

}
