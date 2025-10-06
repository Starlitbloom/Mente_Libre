package com.mentelibre.user_service.controller;

import java.util.List;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import com.mentelibre.user_service.model.UserProfile;
import com.mentelibre.user_service.service.UserProfileService;

import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.media.Content;
import io.swagger.v3.oas.annotations.media.Schema;
import io.swagger.v3.oas.annotations.responses.ApiResponse;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.context.SecurityContextHolder;

@RestController
@RequestMapping("/api/v1/usuario_perfil")
public class UserProfileController {

    @Autowired
    private UserProfileService userProfileService;

    @Operation(summary = "Obtener todos los perfiles", description = "Retorna todos los perfiles de usuario registrados.")
    @ApiResponse(responseCode = "200", description = "Perfiles encontrados", content = @Content(schema = @Schema(implementation = UserProfile.class)))
    @ApiResponse(responseCode = "204", description = "No hay perfiles disponibles")
    @PreAuthorize("hasRole('ADMINISTRADOR')")
    @GetMapping("/perfiles")
    public ResponseEntity<List<UserProfile>> getPerfiles() {
        List<UserProfile> perfiles = userProfileService.obtenerUserProfile();
        if (perfiles.isEmpty()) {
            return ResponseEntity.noContent().build();
        }
        return ResponseEntity.ok(perfiles);
    }

    @Operation(summary = "Obtener perfil por ID", description = "Devuelve un perfil si el ID existe.")
    @ApiResponse(responseCode = "200", description = "Perfil encontrado", content = @Content(schema = @Schema(implementation = UserProfile.class)))
    @ApiResponse(responseCode = "404", description = "Perfil no encontrado")
    @PreAuthorize("hasAnyRole('ADMINISTRADOR', 'CLIENTE')")
    @GetMapping("/perfiles/{id}")
    public ResponseEntity<?> obtenerUserProfilePorId(@PathVariable Long id) {
        try {
            UserProfile userProfile = userProfileService.obtenerUserProfilePorId(id);

            // Usuario autenticado
            Authentication auth = SecurityContextHolder.getContext().getAuthentication();
            String userIdStr = auth.getName(); // si en el token guardas el userId
            Long userId = Long.parseLong(userIdStr);

            // Solo el dueño o el administrador pueden acceder
            boolean esAdmin = auth.getAuthorities().stream()
                .anyMatch(a -> a.getAuthority().equals("ROLE_ADMINISTRADOR"));

            if (!userProfile.getUserId().equals(userId) && !esAdmin) {
                return ResponseEntity.status(HttpStatus.FORBIDDEN)
                        .body("No puedes acceder a este perfil");
            }

            return ResponseEntity.ok(userProfile);
        } catch (RuntimeException e) {
            return ResponseEntity.status(HttpStatus.NOT_FOUND).body(e.getMessage());
        }
    }

    @Operation(summary = "Obtener perfil por ID de usuario", description = "Devuelve el perfil asociado al userId si existe.")
    @ApiResponse(responseCode = "200", description = "Perfil encontrado", content = @Content(schema = @Schema(implementation = UserProfile.class)))
    @ApiResponse(responseCode = "404", description = "Perfil no encontrado")
    @PreAuthorize("hasAnyRole('ADMINISTRADOR', 'CLIENTE')")
    @GetMapping("/perfiles/user/{userId}")
    public ResponseEntity<?> obtenerPorUserId(@PathVariable Long userId) {
        try {
            return ResponseEntity.ok(userProfileService.obtenerUserProfilePorUserId(userId));
        } catch (RuntimeException e) {
            return ResponseEntity.status(HttpStatus.NOT_FOUND).body(e.getMessage());
        }
    }

    @Operation(summary = "Buscar perfiles por nombre", description = "Retorna perfiles cuyo nombre coincida.")
    @ApiResponse(responseCode = "200", description = "Perfiles encontrados")
    @PreAuthorize("hasRole('ADMINISTRADOR')")
    @GetMapping("/perfiles/nombre/{nombre}")
    public ResponseEntity<?> buscarPorNombre(@PathVariable String nombre) {
        return ResponseEntity.ok(userProfileService.buscarPorNombre(nombre));
    }

    @Operation(summary = "Buscar perfiles por apellido", description = "Retorna perfiles cuyo apellido coincida.")
    @ApiResponse(responseCode = "200", description = "Perfiles encontrados")
    @PreAuthorize("hasRole('ADMINISTRADOR')")
    @GetMapping("/perfiles/apellido/{apellido}")
    public ResponseEntity<?> buscarPorApellido(@PathVariable String apellido) {
        return ResponseEntity.ok(userProfileService.buscarPorApellido(apellido));
    }

    @Operation(summary = "Buscar perfiles por género", description = "Retorna perfiles según el género.")
    @ApiResponse(responseCode = "200", description = "Perfiles encontrados")
    @PreAuthorize("hasRole('ADMINISTRADOR')")
    @GetMapping("/perfiles/genero/{generoId}")
    public ResponseEntity<?> buscarPorGenero(@PathVariable Long generoId) {
        return ResponseEntity.ok(userProfileService.buscarPorGenero(generoId));
    }

    @Operation(summary = "Crear nuevo perfil", description = "Crea un nuevo perfil de usuario.")
    @ApiResponse(responseCode = "201", description = "Perfil creado exitosamente", content = @Content(schema = @Schema(implementation = UserProfile.class)))
    @ApiResponse(responseCode = "400", description = "Datos inválidos")
    @PreAuthorize("hasRole('CLIENTE')")
    @PostMapping("/perfiles")
    public ResponseEntity<?> crearUserProfile(@RequestBody UserProfile userProfile) {
        try {
            UserProfile nuevo = userProfileService.crearPerfil(userProfile);
            return ResponseEntity.status(HttpStatus.CREATED).body(nuevo);
        } catch (RuntimeException e) {
            return ResponseEntity.status(HttpStatus.BAD_REQUEST).body(e.getMessage());
        }
    }

    @Operation(summary = "Actualizar perfil", description = "Actualiza los datos de un perfil de usuario.")
    @ApiResponse(responseCode = "200", description = "Perfil actualizado exitosamente", content = @Content(schema = @Schema(implementation = UserProfile.class)))
    @ApiResponse(responseCode = "404", description = "Perfil no encontrado")
    @PreAuthorize("hasAnyRole('CLIENTE', 'ADMINISTRADOR')")
    @PutMapping("/perfiles/{id}")
    public ResponseEntity<?> actualizarUserProfile(@PathVariable Long id, @RequestBody UserProfile userProfile) {
        try {
            UserProfile actualizado = userProfileService.actualizarUserProfile(id, userProfile);
            return ResponseEntity.ok(actualizado);
        } catch (RuntimeException e) {
            return ResponseEntity.status(HttpStatus.NOT_FOUND).body(e.getMessage());
        }
    }

    @Operation(summary = "Eliminar perfil", description = "Elimina un perfil de usuario por ID.")
    @ApiResponse(responseCode = "200", description = "Perfil eliminado correctamente")
    @ApiResponse(responseCode = "500", description = "Error interno del servidor")
    @PreAuthorize("hasRole('ADMINISTRADOR')")
    @DeleteMapping("/perfiles/{id}")
    public ResponseEntity<?> eliminarPerfil(@PathVariable Long id) {
        try {
            return ResponseEntity.ok(userProfileService.eliminarUserProfile(id));
        } catch (RuntimeException e) {
            return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR).body(e.getMessage());
        }
    }
}