package com.mentelibre.notification_service.repository;


import com.mentelibre.notification_service.model.Notification;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import java.util.List;

/**
 * Repositorio para la entidad Notification.
 * 
 * Extiende JpaRepository para proporcionar operaciones CRUD básicas:
 * guardar, buscar, eliminar y actualizar notificaciones.
 * 
 * Además, permite definir consultas personalizadas automáticamente
 * usando la convención de nombres de Spring Data JPA.
 */
@Repository
public interface NotificationRepository extends JpaRepository<Notification, Long> {
    /**
     * Busca todas las notificaciones pendientes (no enviadas) de un usuario.
     * 
     * @param userId ID del estudiante receptor
     * @return Lista de notificaciones pendientes
     */
    List<Notification> findByUserIdAndSentFalse(Long userId);

    /**
     * Busca todas las notificaciones enviadas de un usuario.
     * 
     * @param userId ID del estudiante receptor
     * @return Lista de notificaciones enviadas
     */
    List<Notification> findByUserIdAndSentTrue(Long userId);

    /**
     * Busca todas las notificaciones de un tipo específico.
     * 
     * @param type Tipo de notificación (ej: RECORDATORIO, LOGRO, ALERTA)
     * @return Lista de notificaciones de ese tipo
     */
    List<Notification> findByType(String type);

}
