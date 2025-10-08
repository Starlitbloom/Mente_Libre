package com.mentelibre.notification_service.service;

import com.mentelibre.notification_service.model.NotificationRule;
import com.mentelibre.notification_service.repository.NotificationRuleRepository;
import org.springframework.stereotype.Service;

import java.util.List;
import java.util.Optional;

/**
 * Servicio para la gestión de reglas de notificación.
 * 
 * Permite crear, actualizar, eliminar y obtener reglas,
 * que definen el comportamiento de las notificaciones automáticas.
 */
@Service
public class NotificationRuleService {
    
    private final NotificationRuleRepository ruleRepository;

    public NotificationRuleService(NotificationRuleRepository ruleRepository) {
        this.ruleRepository = ruleRepository;
    }

    /**
     * Crea o actualiza una regla de notificación.
     *
     * @param rule Regla de notificación
     * @return Regla guardada
     */
    public NotificationRule saveRule(NotificationRule rule) {
        return ruleRepository.save(rule);
    }

    /**
     * Obtiene todas las reglas registradas.
     *
     * @return Lista de reglas
     */
    public List<NotificationRule> getAllRules() {
        return ruleRepository.findAll();
    }

    /**
     * Obtiene una regla específica por su ID.
     *
     * @param id Identificador de la regla
     * @return Regla encontrada (si existe)
     */
    public Optional<NotificationRule> getRuleById(Long id) {
        return ruleRepository.findById(id);
    }

    /**
     * Elimina una regla según su ID.
     *
     * @param id Identificador de la regla
     */
    public void deleteRule(Long id) {
        ruleRepository.deleteById(id);
    }

    /**
     * Busca todas las reglas activas.
     *
     * @return Lista de reglas activas
     */
    public List<NotificationRule> getActiveRules() {
        return ruleRepository.findAll()
                .stream()
                .filter(NotificationRule::isActive)
                .toList();
    }

    /**
     * Busca las reglas por tipo de notificación.
     *
     * @param type Tipo de notificación (por ejemplo: RECORDATORIO)
     * @return Lista de reglas que coinciden con el tipo
     */
    public List<NotificationRule> getRulesByType(String type) {
        return ruleRepository.findAll()
                .stream()
                .filter(rule -> rule.getType().equalsIgnoreCase(type))
                .toList();
    }

    /**
     * Activa o desactiva una regla.
     *
     * @param id ID de la regla
     * @param active Nuevo estado (true = activa)
     * @return Regla actualizada, si existe
     */
    public Optional<NotificationRule> setRuleActiveStatus(Long id, boolean active) {
        return ruleRepository.findById(id).map(rule -> {
            rule.setActive(active);
            return ruleRepository.save(rule);
        });
    }

}
