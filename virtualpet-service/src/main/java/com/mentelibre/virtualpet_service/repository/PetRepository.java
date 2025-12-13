package com.mentelibre.virtualpet_service.repository;

import java.util.Optional;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import com.mentelibre.virtualpet_service.model.Pet;

@Repository
public interface PetRepository extends JpaRepository<Pet, Long> {

    // Obtener la mascota por ID de usuario
    Optional<Pet> findByUserId(Long userId);

    // Verificar si el usuario ya tiene mascota
    boolean existsByUserId(Long userId);
}