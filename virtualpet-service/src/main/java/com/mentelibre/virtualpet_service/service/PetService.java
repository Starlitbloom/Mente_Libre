package com.mentelibre.virtualpet_service.service;


import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import com.mentelibre.virtualpet_service.dto.CreatePetRequest;
import com.mentelibre.virtualpet_service.dto.PetDto;
import com.mentelibre.virtualpet_service.dto.UpdatePetRequest;
import com.mentelibre.virtualpet_service.model.Pet;
import com.mentelibre.virtualpet_service.repository.PetRepository;
import jakarta.transaction.Transactional;

@Service
@Transactional
public class PetService {

    @Autowired
    private PetRepository petRepository;

    // ============================================================
    // CREAR MASCOTA (solo si no tenía una)
    public PetDto createPet(Long userId, CreatePetRequest dto) {

        if (petRepository.findByUserId(userId).isPresent()) {
            throw new RuntimeException("El usuario ya tiene una mascota creada");
        }

        Pet pet = new Pet();
        pet.setUserId(userId);
        pet.setName(dto.getName());
        pet.setType(dto.getType());
        pet.setAvatarKey(dto.getAvatarKey());

        // Valores iniciales
        pet.setPoints(0);
        pet.setLevel(1);
        pet.setExperience(0);
        pet.setEnergy(100);
        pet.setAffinity(0);

        Pet saved = petRepository.save(pet);

        return mapToDto(saved);
    }

    // ============================================================
    // OBTENER LA MASCOTA DEL USUARIO
    public PetDto getMyPet(Long userId) {

        Pet pet = petRepository.findByUserId(userId)
                .orElseThrow(() -> new RuntimeException("El usuario no tiene mascota registrada"));

        return mapToDto(pet);
    }

    // ============================================================
    // ACTUALIZAR DATOS DE LA MASCOTA
    public PetDto updatePet(Long userId, UpdatePetRequest dto) {

        Pet pet = petRepository.findByUserId(userId)
                .orElseThrow(() -> new RuntimeException("Mascota no encontrada"));

        if (dto.getName() != null) pet.setName(dto.getName());
        if (dto.getAvatarKey() != null) pet.setAvatarKey(dto.getAvatarKey());

        Pet updated = petRepository.save(pet);

        return mapToDto(updated);
    }

    // ============================================================
    // SUMAR PUNTOS (misiones, actividades, recompensas)
    public PetDto addPoints(Long userId, int points) {

        Pet pet = petRepository.findByUserId(userId)
                .orElseThrow(() -> new RuntimeException("Mascota no encontrada"));

        pet.setPoints(pet.getPoints() + points);
        pet.setExperience(pet.getExperience() + points);

        // Sistema automático de nivel
        while (pet.getExperience() >= 100) {
            pet.setLevel(pet.getLevel() + 1);
            pet.setExperience(pet.getExperience() - 100);

            // Cada nivel sube afinidad
            pet.setAffinity(pet.getAffinity() + 5);
        }

        Pet updated = petRepository.save(pet);

        return mapToDto(updated);
    }

    // ============================================================
    // REDUCIR ENERGÍA
    public PetDto reduceEnergy(Long userId, int amount) {

        Pet pet = petRepository.findByUserId(userId)
                .orElseThrow(() -> new RuntimeException("Mascota no encontrada"));

        pet.setEnergy(Math.max(0, pet.getEnergy() - amount));

        return mapToDto(petRepository.save(pet));
    }

    // ============================================================
    // RESTABLECER ENERGÍA
    public PetDto restoreEnergy(Long userId) {

        Pet pet = petRepository.findByUserId(userId)
                .orElseThrow(() -> new RuntimeException("Mascota no encontrada"));

        pet.setEnergy(100);

        return mapToDto(petRepository.save(pet));
    }

    // ============================================================
    // MAPEAR ENTIDAD -> DTO
    private PetDto mapToDto(Pet pet) {

        PetDto dto = new PetDto();

        dto.setId(pet.getId());
        dto.setUserId(pet.getUserId());
        dto.setName(pet.getName());
        dto.setType(pet.getType());
        dto.setAvatarKey(pet.getAvatarKey());

        dto.setPoints(pet.getPoints());
        dto.setLevel(pet.getLevel());
        dto.setExperience(pet.getExperience());
        dto.setAffinity(pet.getAffinity());
        dto.setEnergy(pet.getEnergy());

        return dto;
    }

}