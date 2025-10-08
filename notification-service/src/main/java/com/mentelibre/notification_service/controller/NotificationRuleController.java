package com.mentelibre.notification_service.controller;

import com.mentelibre.notification_service.model.NotificationRule;
import com.mentelibre.notification_service.service.NotificationRuleService;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.List;

/**
 * Controlador REST para gestionar las reglas de notificación.
 * 
 * Los administradores pueden crear, listar, actualizar o eliminar reglas
 * que definen cuándo y cómo se envían las notificaciones.
 */
@RestController
@RequestMapping("/api/rules")
public class NotificationRuleController {
    
    private final NotificationRuleService ruleService;

    public NotificationRuleController(NotificationRuleService ruleService) {
        this.ruleService = ruleService;
    }

    /**
     * Obtiene todas las reglas de notificación.
     *
     * @return Lista completa de reglas
     */
    @GetMapping
    public ResponseEntity<List<NotificationRule>> getAllRules() {
        return ResponseEntity.ok(ruleService.getAllRules());
    }

    /**
     * Obtiene una regla específica por ID.
     *
     * @param id ID de la regla
     * @return La regla si existe, o 404 si no
     */
    @GetMapping("/{id}")
    public ResponseEntity<NotificationRule> getRuleById(@PathVariable Long id) {
        return ruleService.getRuleById(id)
                .map(ResponseEntity::ok)
                .orElse(ResponseEntity.notFound().build());
    }

    /**
     * Crea una nueva regla de notificación.
     *
     * @param rule Objeto con los datos de la nueva regla
     * @return La regla creada
     */
    @PostMapping
    public ResponseEntity<NotificationRule> createRule(@RequestBody NotificationRule rule) {
        return ResponseEntity.ok(ruleService.saveRule(rule));
    }

    /**
     * Actualiza una regla existente.
     *
     * @param id ID de la regla a actualizar
     * @param updatedRule Datos nuevos de la regla
     * @return La regla actualizada, o 404 si no existe
     */
    @PutMapping("/{id}")
    public ResponseEntity<NotificationRule> updateRule(
            @PathVariable Long id,
            @RequestBody NotificationRule updatedRule) {

        return ruleService.getRuleById(id)
                .map(existing -> {
                    existing.setType(updatedRule.getType());
                    existing.setFrequency(updatedRule.getFrequency());
                    existing.setActive(updatedRule.isActive());
                    return ResponseEntity.ok(ruleService.saveRule(existing));
                })
                .orElse(ResponseEntity.notFound().build());
    }

    /**
     * Activa o desactiva una regla de notificación.
     *
     * @param id ID de la regla
     * @param active Estado nuevo (true o false)
     * @return Regla actualizada
     */
    @PatchMapping("/{id}/active")
    public ResponseEntity<NotificationRule> setRuleActiveStatus(
            @PathVariable Long id,
            @RequestParam boolean active) {
        return ruleService.setRuleActiveStatus(id, active)
                .map(ResponseEntity::ok)
                .orElse(ResponseEntity.notFound().build());
    }

    /**
     * Elimina una regla por ID.
     *
     * @param id ID de la regla a eliminar
     * @return 204 No Content si se elimina correctamente
     */
    @DeleteMapping("/{id}")
    public ResponseEntity<Void> deleteRule(@PathVariable Long id) {
        ruleService.deleteRule(id);
        return ResponseEntity.noContent().build();
    }

}

