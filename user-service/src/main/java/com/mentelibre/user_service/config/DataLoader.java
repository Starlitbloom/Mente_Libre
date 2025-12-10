package com.mentelibre.user_service.config;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;

import com.mentelibre.user_service.model.Genero;
import com.mentelibre.user_service.repository.GeneroRepository;

import jakarta.annotation.PostConstruct;

@Component
public class DataLoader {

    @Autowired
    private GeneroRepository generoRepository;

    @PostConstruct
    public void init() {
        if (generoRepository.count() == 0) {
            generoRepository.save(new Genero(null, "Mujer"));
            generoRepository.save(new Genero(null, "Hombre"));
            generoRepository.save(new Genero(null, "Prefiero no decirlo"));
        }
    }
}
