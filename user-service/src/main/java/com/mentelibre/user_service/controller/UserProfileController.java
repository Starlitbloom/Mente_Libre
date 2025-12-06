package com.mentelibre.user_service.controller;

import com.mentelibre.user_service.dto.CreateUserProfileRequestDto;
import com.mentelibre.user_service.dto.UpdateUserProfileRequestDto;
import com.mentelibre.user_service.dto.UserProfileResponseDto;
import com.mentelibre.user_service.service.UserProfileService;

import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.media.Content;
import io.swagger.v3.oas.annotations.media.Schema;
import io.swagger.v3.oas.annotations.responses.ApiResponse;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;

import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.security.core.context.SecurityContextHolder;

import org.springframework.web.bind.annotation.*;

@RestController
@RequestMapping("/api/v1/user-profile")
public class UserProfileController {

    @Autowired
    private UserProfileService userProfileService;

    // ======================================================
    // CREAR PERFIL
    @Operation(
            summary = "Crear perfil",
            description = "Crea un perfil para el usuario autenticado.",
            responses = {
                @ApiResponse(responseCode = "201",
                    description = "Perfil creado correctamente",
                    content = @Content(schema = @Schema(implementation = UserProfileResponseDto.class))
                ),
                @ApiResponse(responseCode = "400", description = "Datos inválidos"),
                @ApiResponse(responseCode = "409", description = "El usuario ya tiene un perfil creado")
            }
    )
    @PreAuthorize("hasAnyRole('USER','ADMIN')")
    @PostMapping
    public ResponseEntity<?> create(@RequestBody CreateUserProfileRequestDto dto) {

        try {
            UserProfileResponseDto response = userProfileService.crearPerfil(dto);
            return ResponseEntity.status(HttpStatus.CREATED).body(response);

        } catch (RuntimeException e) {
            return ResponseEntity.badRequest().body(e.getMessage());
        }
    }

    // ======================================================
    // OBTENER MI PERFIL
    @Operation(
            summary = "Obtener mi perfil",
            description = "Devuelve el perfil del usuario autenticado.",
            responses = {
                @ApiResponse(responseCode = "200", description = "Perfil obtenido correctamente"),
                @ApiResponse(responseCode = "404", description = "Perfil no encontrado")
            }
    )
    @PreAuthorize("hasAnyRole('USER','ADMIN')")
    @GetMapping("/me")
    public ResponseEntity<?> getMyProfile() {

        Long userId = (Long) SecurityContextHolder.getContext().getAuthentication().getPrincipal();

        try {
            UserProfileResponseDto perfil = userProfileService.obtenerPerfilPorUserId(userId);
            return ResponseEntity.ok(perfil);

        } catch (RuntimeException e) {
            return ResponseEntity.status(HttpStatus.NOT_FOUND).body(e.getMessage());
        }
    }

    // ======================================================
    // ACTUALIZAR MI PERFIL
    @Operation(
            summary = "Actualizar mi perfil",
            description = "Actualiza los datos del perfil del usuario autenticado.",
            responses = {
                @ApiResponse(responseCode = "200", description = "Perfil actualizado correctamente"),
                @ApiResponse(responseCode = "404", description = "Perfil no encontrado")
            }
    )
    @PreAuthorize("hasAnyRole('USER','ADMIN')")
    @PutMapping("/me")
    public ResponseEntity<?> update(
            @RequestBody UpdateUserProfileRequestDto dto) {

        Long userId = (Long) SecurityContextHolder.getContext().getAuthentication().getPrincipal();

        try {
            UserProfileResponseDto updated = userProfileService.actualizarPerfil(userId, dto);
            return ResponseEntity.ok(updated);

        } catch (RuntimeException e) {
            return ResponseEntity.status(HttpStatus.BAD_REQUEST).body(e.getMessage());
        }
    }

    // ======================================================
    // ELIMINAR MI PERFIL
    @Operation(
            summary = "Eliminar mi perfil",
            description = "Elimina completamente el perfil del usuario autenticado.",
            responses = {
                @ApiResponse(responseCode = "200", description = "Perfil eliminado correctamente"),
                @ApiResponse(responseCode = "404", description = "Perfil no encontrado")
            }
    )
    @PreAuthorize("hasAnyRole('USER','ADMIN')")
    @DeleteMapping("/me")
    public ResponseEntity<?> delete() {

        Long userId = (Long) SecurityContextHolder.getContext().getAuthentication().getPrincipal();

        try {
            String msg = userProfileService.eliminarPerfil(userId);
            return ResponseEntity.ok(msg);

        } catch (RuntimeException e) {
            return ResponseEntity.status(HttpStatus.NOT_FOUND).body(e.getMessage());
        }
    }
}
