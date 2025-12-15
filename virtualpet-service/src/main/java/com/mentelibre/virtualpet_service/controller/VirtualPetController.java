package com.mentelibre.virtualpet_service.controller;

import com.mentelibre.virtualpet_service.dto.CreatePetRequest;
import com.mentelibre.virtualpet_service.dto.PetDto;
import com.mentelibre.virtualpet_service.dto.UpdatePetRequest;
import com.mentelibre.virtualpet_service.dto.UserProfileResponseDto;
import com.mentelibre.virtualpet_service.service.PetService;
import com.mentelibre.virtualpet_service.webclient.UserClient;

import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.media.Content;
import io.swagger.v3.oas.annotations.media.Schema;
import io.swagger.v3.oas.annotations.responses.ApiResponse;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.ResponseEntity;
import org.springframework.http.HttpStatus;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.web.bind.annotation.*;

@RestController
@RequestMapping("/api/v1/pet")
public class VirtualPetController {

    @Autowired
    private PetService petService;

    @Autowired
    private UserClient userClient; // Inyectar UserClient


    // ============================================================
    // CREAR MASCOTA (al finalizar registro)
    @Operation(
        summary = "Crear mascota",
        description = "Crea la mascota inicial del usuario. Solo puede hacerse una vez."
    )
    @ApiResponse(responseCode = "201", description = "Mascota creada",
        content = @Content(schema = @Schema(implementation = PetDto.class)))
    @ApiResponse(responseCode = "400", description = "El usuario ya tiene mascota")
    @PreAuthorize("hasRole('USER')")
    @PostMapping("/create")
    public ResponseEntity<?> createPet(@RequestBody CreatePetRequest dto) {

        Long userId = (Long) SecurityContextHolder.getContext().getAuthentication().getPrincipal();
        String token = (String) SecurityContextHolder.getContext().getAuthentication().getCredentials();

        try {
            // Obtener el perfil del usuario con el UserClient
            UserProfileResponseDto userProfile = userClient.getMyProfile("Bearer " + token);

            // Crear la mascota
            PetDto response = petService.createPet(userId, dto);


            // Sin modificar el nombre
            return ResponseEntity.ok(response);


        } catch (RuntimeException e) {
            return ResponseEntity.status(HttpStatus.BAD_REQUEST).body(e.getMessage());
        }
    }

    // ============================================================
    // OBTENER LA MASCOTA DEL USUARIO
    @Operation(
        summary = "Obtener mi mascota",
        description = "Devuelve la mascota asignada al usuario autenticado."
    )
    @ApiResponse(responseCode = "200", description = "Mascota encontrada",
        content = @Content(schema = @Schema(implementation = PetDto.class)))
    @ApiResponse(responseCode = "404", description = "El usuario no tiene mascota")
    @PreAuthorize("hasRole('USER')")
    @GetMapping("/me")
    public ResponseEntity<?> getMyPet() {

        Long userId = (Long) SecurityContextHolder.getContext().getAuthentication().getPrincipal();

        try {
            PetDto pet = petService.getMyPet(userId);
            return ResponseEntity.ok(pet);
        } catch (RuntimeException e) {
            return ResponseEntity.status(HttpStatus.NOT_FOUND).body(e.getMessage());
        }
    }

    // ============================================================
    // ACTUALIZAR DATOS DE LA MASCOTA (nombre o avatar)
    @Operation(summary = "Actualizar mascota",
        description = "Permite cambiar el nombre o avatarKey de la mascota.")
    @ApiResponse(responseCode = "200", description = "Mascota actualizada",
        content = @Content(schema = @Schema(implementation = PetDto.class)))
    @ApiResponse(responseCode = "404", description = "Mascota no encontrada")
    @PreAuthorize("hasRole('USER')")
    @PutMapping("/update")
    public ResponseEntity<?> updatePet(@RequestBody UpdatePetRequest dto) {

        Long userId = (Long) SecurityContextHolder.getContext().getAuthentication().getPrincipal();

        try {
            PetDto updated = petService.updatePet(userId, dto);
            return ResponseEntity.ok(updated);

        } catch (RuntimeException e) {
            return ResponseEntity.status(HttpStatus.NOT_FOUND).body(e.getMessage());
        }
    }


    // ============================================================
    // SUMAR PUNTOS
    @Operation(summary = "Agregar puntos a mi mascota",
        description = "Suma puntos y experiencia a la mascota del usuario.")
    @ApiResponse(responseCode = "200", description = "Puntos agregados",
        content = @Content(schema = @Schema(implementation = PetDto.class)))
    @ApiResponse(responseCode = "404", description = "Mascota no encontrada")
    @PreAuthorize("hasRole('USER')")
    @PutMapping("/add-points/{points}")
    public ResponseEntity<?> addPoints(@PathVariable int points) {

        Long userId = (Long) SecurityContextHolder.getContext().getAuthentication().getPrincipal();

        try {
            PetDto pet = petService.addPoints(userId, points);
            return ResponseEntity.ok(pet);

        } catch (RuntimeException e) {
            return ResponseEntity.status(HttpStatus.NOT_FOUND).body(e.getMessage());
        }
    }


    // ============================================================
    // REDUCIR ENERGÍA
    @Operation(summary = "Reducir energía",
        description = "Reduce la energía de la mascota. Nunca baja de 0.")
    @ApiResponse(responseCode = "200", description = "Energía reducida",
        content = @Content(schema = @Schema(implementation = PetDto.class)))
    @PreAuthorize("hasRole('USER')")
    @PutMapping("/energy/reduce/{amount}")
    public ResponseEntity<?> reduceEnergy(@PathVariable int amount) {

        Long userId = (Long) SecurityContextHolder.getContext().getAuthentication().getPrincipal();

        try {
            PetDto pet = petService.reduceEnergy(userId, amount);
            return ResponseEntity.ok(pet);

        } catch (RuntimeException e) {
            return ResponseEntity.badRequest().body(e.getMessage());
        }
    }


    // ============================================================
    // RESTABLECER ENERGÍA AL 100%
    @Operation(summary = "Restablecer energía",
        description = "Restaura la energía de la mascota al 100%.")
    @ApiResponse(responseCode = "200", description = "Energía restablecida",
        content = @Content(schema = @Schema(implementation = PetDto.class)))
    @PreAuthorize("hasRole('USER')")
    @PutMapping("/energy/restore")
    public ResponseEntity<?> restoreEnergy() {

        Long userId = (Long) SecurityContextHolder.getContext().getAuthentication().getPrincipal();

        PetDto pet = petService.restoreEnergy(userId);
        return ResponseEntity.ok(pet);
    }

}