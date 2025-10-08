package com.mentelibre.notification_service.repository;

import com.mentelibre.notification_service.model.NotificationRule;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import java.util.List;

/**
 * Repositorio para la entidad NotificationRule.
 * 
 * Proporciona operaciones CRUD y consultas personalizadas
 * para gestionar reglas de notificación.
 */
@Repository
public interface NotificationRuleRepository extends JpaRepository<NotificationRule, Long> {

    /**
     * Obtiene todas las reglas activas.
     * 
     * @return Lista de reglas activas
     */
    List<NotificationRule> findByActiveTrue();

    /**
     * Obtiene todas las reglas de un tipo específico.
     * 
     * @param type Tipo de notificación (ej: RECORDATORIO, LOGRO, ALERTA)
     * @return Lista de reglas de ese tipo
     */
    List<NotificationRule> findByType(String type);

    /**
     * Obtiene todas las reglas activas de un tipo específico.
     * 
     * @param type Tipo de notificación
     * @return Lista de reglas activas de ese tipo
     */
    List<NotificationRule> findByTypeAndActiveTrue(String type);
}

