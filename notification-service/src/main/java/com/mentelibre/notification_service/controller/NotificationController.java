package com.mentelibre.notification_service.controller;

import com.mentelibre.notification_service.model.Notification;
import com.mentelibre.notification_service.service.NotificationService;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.List;

/**
 * Controlador REST para gestionar las notificaciones individuales.
 *
 * Los estudiantes pueden recibir sus notificaciones, mientras que
 * los administradores pueden crear, eliminar o reenviar notificaciones.
 */
@RestController
@RequestMapping("/api/notifications")
public class NotificationController {

    private final NotificationService notificationService;

    // Inyección del servicio
    public NotificationController(NotificationService notificationService) {
        this.notificationService = notificationService;
    }

    // ============================================================
    //  MÉTODOS CRUD Y CONSULTAS
    // ============================================================

    /**
     * Obtiene todas las notificaciones del sistema.
     * 
     * @return Lista completa de notificaciones
     */
    @GetMapping
    public ResponseEntity<List<Notification>> getAllNotifications() {
        return ResponseEntity.ok(notificationService.getAllNotifications());
    }

    /**
     * Obtiene todas las notificaciones de un usuario específico.
     * 
     * @param userId ID del usuario receptor
     * @return Lista de notificaciones del usuario
     */
    @GetMapping("/user/{userId}")
    public ResponseEntity<List<Notification>> getNotificationsByUser(@PathVariable Long userId) {
        return ResponseEntity.ok(notificationService.getNotificationsByUser(userId));
    }

    /**
     * Obtiene una notificación por su ID.
     * 
     * @param id ID de la notificación
     * @return Notificación encontrada o 404 si no existe
     */
    @GetMapping("/{id}")
    public ResponseEntity<Notification> getNotificationById(@PathVariable Long id) {
        return notificationService.getNotificationById(id)
                .map(ResponseEntity::ok)
                .orElse(ResponseEntity.notFound().build());
    }

    /**
     * Crea una nueva notificación.
     * 
     * @param notification Objeto con los datos de la nueva notificación
     * @return Notificación creada
     */
    @PostMapping
    public ResponseEntity<Notification> createNotification(@RequestBody Notification notification) {
        return ResponseEntity.ok(notificationService.saveNotification(notification));
    }

    /**
     * Actualiza una notificación existente.
     * 
     * @param id ID de la notificación a actualizar
     * @param updatedNotification Nuevos datos de la notificación
     * @return Notificación actualizada o 404 si no se encuentra
     */
    @PutMapping("/{id}")
    public ResponseEntity<Notification> updateNotification(
            @PathVariable Long id,
            @RequestBody Notification updatedNotification) {

        return notificationService.getNotificationById(id)
                .map(existing -> {
                    existing.setMessage(updatedNotification.getMessage());
                    existing.setType(updatedNotification.getType());
                    existing.setSent(updatedNotification.isSent());
                    existing.setUserId(updatedNotification.getUserId());
                    return ResponseEntity.ok(notificationService.saveNotification(existing));
                })
                .orElse(ResponseEntity.notFound().build());
    }

    /**
     * Marca una notificación como enviada.
     * 
     * @param id ID de la notificación
     * @return Notificación actualizada como enviada
     */
    @PatchMapping("/{id}/sent")
    public ResponseEntity<Notification> markAsSent(@PathVariable Long id) {
        return notificationService.markAsSent(id)
                .map(ResponseEntity::ok)
                .orElse(ResponseEntity.notFound().build());
    }

    /**
     * Elimina una notificación por ID.
     * 
     * @param id ID de la notificación
     * @return 204 No Content si se elimina correctamente
     */
    @DeleteMapping("/{id}")
    public ResponseEntity<Void> deleteNotification(@PathVariable Long id) {
        notificationService.deleteNotification(id);
        return ResponseEntity.noContent().build();
    }
}
