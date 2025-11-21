package com.mentelibre.emotion_service.controller;

import com.mentelibre.emotion_service.model.Emotion;
import com.mentelibre.emotion_service.dto.EmotionSummaryDTO;
import com.mentelibre.emotion_service.service.EmotionService;

import io.swagger.v3.oas.annotations.Operation;
import jakarta.validation.Valid;

import org.springframework.http.*;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.multipart.MultipartFile;

import java.util.List;

import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.context.SecurityContextHolder;

@RestController
@RequestMapping("/api/v1/emotions")
public class EmotionController {

    private final EmotionService emotionService;

    public EmotionController(EmotionService emotionService) {
        this.emotionService = emotionService;
    }

    // ---------------- Emotions ----------------

    @PostMapping
    @Operation(summary = "Registrar una nueva emoción del usuario")
    @PreAuthorize("hasRole('CLIENTE')")
    public ResponseEntity<?> crearEmotion(@Valid @RequestBody Emotion emotion) {
        Authentication auth = SecurityContextHolder.getContext().getAuthentication();
        Long userId = (auth == null) ? 1L : Long.parseLong(auth.getName());
        emotion.setUserId(userId);

        Emotion nueva = emotionService.crearEmotion(emotion);
        return ResponseEntity.status(HttpStatus.CREATED).body(nueva);
    }

    @GetMapping("/user/{userId}")
    @Operation(summary = "Obtener todas las emociones de un usuario")
    @PreAuthorize("hasAnyRole('ADMINISTRADOR', 'CLIENTE')")
    public ResponseEntity<?> listarEmotionsPorUsuario(@PathVariable Long userId) {
        Authentication auth = SecurityContextHolder.getContext().getAuthentication();
        if (auth == null) return ResponseEntity.ok(emotionService.obtenerEmotionsPorUsuario(userId));

        Long authUserId = Long.parseLong(auth.getName());
        boolean esAdmin = auth.getAuthorities().stream()
                .anyMatch(a -> a.getAuthority().equals("ROLE_ADMINISTRADOR"));

        if (!userId.equals(authUserId) && !esAdmin) {
            return ResponseEntity.status(HttpStatus.FORBIDDEN)
                    .body("No puedes ver las emociones de otro usuario");
        }
        return ResponseEntity.ok(emotionService.obtenerEmotionsPorUsuario(userId));
    }

    @GetMapping("/summary/week/{userId}")
    @Operation(summary = "Obtener resumen de emociones de la semana actual")
    @PreAuthorize("hasAnyRole('ADMINISTRADOR', 'CLIENTE')")
    public ResponseEntity<?> resumenSemanal(@PathVariable Long userId) {
        Authentication auth = SecurityContextHolder.getContext().getAuthentication();
        if (auth == null) return ResponseEntity.ok(emotionService.obtenerResumenSemanal(userId));

        Long authUserId = Long.parseLong(auth.getName());
        boolean esAdmin = auth.getAuthorities().stream()
                .anyMatch(a -> a.getAuthority().equals("ROLE_ADMINISTRADOR"));

        if (!userId.equals(authUserId) && !esAdmin) {
            return ResponseEntity.status(HttpStatus.FORBIDDEN)
                    .body("No puedes ver el resumen de otro usuario");
        }
        return ResponseEntity.ok(emotionService.obtenerResumenSemanal(userId));
    }

    @GetMapping
    @PreAuthorize("hasRole('ADMINISTRADOR')")
    public ResponseEntity<?> listarTodas() {
        return ResponseEntity.ok(emotionService.obtenerTodas());
    }

    // ---------------- NotificationClient ----------------

    @GetMapping("/notifications/rules")
    @PreAuthorize("hasRole('ADMINISTRADOR')")
    @Operation(summary = "Listar todas las reglas de notificación")
    public ResponseEntity<?> listarReglasNotificacion(@RequestHeader("Authorization") String token) {
        return ResponseEntity.ok(emotionService.listarReglasNotificacion(token));
    }

    @GetMapping("/notifications/rules/{id}")
    @PreAuthorize("hasRole('ADMINISTRADOR')")
    @Operation(summary = "Obtener una regla de notificación por ID")
    public ResponseEntity<?> obtenerReglaNotificacion(@PathVariable Long id,
                                                      @RequestHeader("Authorization") String token) {
        return ResponseEntity.ok(emotionService.obtenerReglaNotificacion(id, token));
    }

    @PostMapping("/notifications/rules")
    @PreAuthorize("hasRole('ADMINISTRADOR')")
    @Operation(summary = "Crear una nueva regla de notificación")
    public ResponseEntity<?> crearReglaNotificacion(@RequestBody Object regla,
                                                    @RequestHeader("Authorization") String token) {
        return ResponseEntity.status(HttpStatus.CREATED)
                .body(emotionService.crearReglaNotificacion(regla, token));
    }

    @PutMapping("/notifications/rules/{id}")
    @PreAuthorize("hasRole('ADMINISTRADOR')")
    @Operation(summary = "Actualizar una regla de notificación")
    public ResponseEntity<?> actualizarReglaNotificacion(@PathVariable Long id,
                                                         @RequestBody Object regla,
                                                         @RequestHeader("Authorization") String token) {
        return ResponseEntity.ok(emotionService.actualizarReglaNotificacion(id, regla, token));
    }

    @PatchMapping("/notifications/rules/{id}/active")
    @PreAuthorize("hasRole('ADMINISTRADOR')")
    @Operation(summary = "Activar o desactivar una regla de notificación")
    public ResponseEntity<?> activarDesactivarRegla(@PathVariable Long id,
                                                    @RequestParam boolean active,
                                                    @RequestHeader("Authorization") String token) {
        return ResponseEntity.ok(emotionService.activarDesactivarRegla(id, active, token));
    }

    @DeleteMapping("/notifications/rules/{id}")
    @PreAuthorize("hasRole('ADMINISTRADOR')")
    @Operation(summary = "Eliminar una regla de notificación")
    public ResponseEntity<?> eliminarRegla(@PathVariable Long id,
                                           @RequestHeader("Authorization") String token) {
        boolean ok = emotionService.eliminarRegla(id, token);
        return ok ? ResponseEntity.noContent().build()
                  : ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR)
                                  .body("Error eliminando la regla");
    }

    // ---------------- StorageClient ----------------
    @PostMapping("/files/upload")
    @PreAuthorize("hasRole('CLIENTE')")
    @Operation(summary = "Subir un archivo para un usuario")
    public ResponseEntity<?> subirArchivo(@RequestParam("file") MultipartFile file,
                                        @RequestParam("ownerId") Long ownerId,
                                        @RequestParam("category") String category,
                                        @RequestHeader("Authorization") String token) {
        return ResponseEntity.status(HttpStatus.CREATED)
                .body(emotionService.subirArchivo(file, ownerId, category, token));
    }

    @GetMapping("/files/owner/{ownerId}")
    @PreAuthorize("hasAnyRole('ADMINISTRADOR','CLIENTE')")
    @Operation(summary = "Listar archivos de un usuario")
    public ResponseEntity<?> listarArchivos(@PathVariable Long ownerId,
                                            @RequestHeader("Authorization") String token) {
        return ResponseEntity.ok(emotionService.listarArchivos(ownerId, token));
    }

    @PutMapping("/files/{fileId}")
    @PreAuthorize("hasRole('CLIENTE')")
    @Operation(summary = "Actualizar un archivo")
    public ResponseEntity<?> actualizarArchivo(@PathVariable Long fileId,
                                            @RequestParam("file") MultipartFile file,
                                            @RequestHeader("Authorization") String token) {
        return ResponseEntity.ok(emotionService.actualizarArchivo(fileId, file, token));
    }

    @DeleteMapping("/files/{fileId}")
    @PreAuthorize("hasAnyRole('ADMINISTRADOR','CLIENTE')")
    @Operation(summary = "Eliminar un archivo")
    public ResponseEntity<?> eliminarArchivo(@PathVariable Long fileId,
                                            @RequestHeader("Authorization") String token) {
        boolean ok = emotionService.eliminarArchivo(fileId, token);
        return ok ? ResponseEntity.noContent().build()
                : ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR)
                                .body("Error eliminando el archivo");
    }

}
