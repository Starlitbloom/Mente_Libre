package com.mentelibre.auth_service.config;

import org.springframework.boot.CommandLineRunner;
import org.springframework.stereotype.Component;
import org.springframework.beans.factory.annotation.Autowired;

import com.mentelibre.auth_service.model.Rol;
import com.mentelibre.auth_service.repository.RolRepository;

@Component
public class DataInitializer implements CommandLineRunner {

    @Autowired
    private RolRepository rolRepository;

    @Override
    public void run(String... args) throws Exception {
        // Crear rol ADMINISTRADOR si no existe
        if (rolRepository.findByNombre("ADMINISTRADOR") == null) {
            rolRepository.save(new Rol(null, "ADMINISTRADOR", null, null, null));
        }

        // Crear rol CLIENTE si no existe
        if (rolRepository.findByNombre("CLIENTE") == null) {
            rolRepository.save(new Rol(null, "CLIENTE", null, null, null));
        }

        System.out.println("Roles iniciales ADMINISTRADOR y CLIENTE verificados/creados");
    }

}
