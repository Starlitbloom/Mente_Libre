package com.mentelibre.goals_service.service;

import com.mentelibre.goals_service.model.Achievement;
import com.mentelibre.goals_service.repository.AchievementRepository;
import com.mentelibre.goals_service.webclient.AuthClient;
import com.mentelibre.goals_service.webclient.NotificationClient;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.time.LocalDate;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

@Service
@Transactional
public class AchievementService {

    private final AchievementRepository achievementRepository;
    private final AuthClient authClient;
    private final NotificationClient notificationClient; // <-- inyectamos NotificationClient

    public AchievementService(AchievementRepository achievementRepository,
                              AuthClient authClient,
                              NotificationClient notificationClient) {
        this.achievementRepository = achievementRepository;
        this.authClient = authClient;
        this.notificationClient = notificationClient;
    }

    public Achievement createAchievement(Achievement achievement, String token) {
        // Validar usuario
        if (!authClient.usuarioExiste(achievement.getGoal().getUserId(), token)) {
            throw new RuntimeException("Usuario no existe");
        }

        if (!authClient.usuarioPuedeCrearGoal(token)) {
            throw new RuntimeException("No tiene permisos para crear Achievement");
        }

        // Guardar logro
        Achievement saved = achievementRepository.save(achievement);

        // Enviar notificación al usuario
        Map<String, Object> payload = new HashMap<>();
        payload.put("userId", achievement.getGoal().getUserId());
        payload.put("message", "¡Has logrado: " + achievement.getName() + "!");
        payload.put("type", "ACHIEVEMENT");

        notificationClient.createNotification(payload);

        return saved;
    }

    // ... resto de métodos igual

    /**
     * Actualiza un logro existente.
     * @param id ID del logro.
     * @param achievementDetails Nuevos datos.
     * @return Logro actualizado.
     */
    public Achievement updateAchievement(Long id, Achievement achievementDetails) {
        Achievement achievement = achievementRepository.findById(id)
                .orElseThrow(() -> new RuntimeException("Achievement not found with id " + id));

        achievement.setName(achievementDetails.getName());
        achievement.setDescription(achievementDetails.getDescription());
        achievement.setAchievedDate(achievementDetails.getAchievedDate());
        achievement.setGoal(achievementDetails.getGoal());

        return achievementRepository.save(achievement);
    }

    /**
     * Elimina un logro por su ID.
     * @param id ID del logro.
     */
    public void deleteAchievement(Long id) {
        Achievement achievement = achievementRepository.findById(id)
                .orElseThrow(() -> new RuntimeException("Achievement not found with id " + id));
        achievementRepository.delete(achievement);
    }

    /**
     * Obtiene un logro por su ID.
     * @param id ID del logro.
     * @return Logro encontrado.
     */
    public Achievement findAchievementById(Long id) {
        return achievementRepository.findById(id)
                .orElseThrow(() -> new RuntimeException("Achievement not found with id " + id));
    }

    /**
     * Obtiene todos los logros.
     * @return Lista de logros.
     */
    public List<Achievement> findAllAchievements() {
        return achievementRepository.findAll();
    }

    /**
     * Obtiene todos los logros de una meta específica.
     * @param goalId ID de la meta.
     * @return Lista de logros asociados.
     */
    public List<Achievement> findAchievementsByGoalId(Long goalId) {
        return achievementRepository.findByGoalId(goalId);
    }

    /**
     * Busca un logro por nombre dentro de una meta específica.
     * @param name Nombre del logro.
     * @param goalId ID de la meta.
     * @return Logro encontrado.
     */
    public Achievement findByNameAndGoalId(String name, Long goalId) {
        return achievementRepository.findByNameAndGoalId(name, goalId)
                .orElseThrow(() -> new RuntimeException("Achievement not found with name " + name + " for goalId " + goalId));
    }

    /**
     * Obtiene los logros alcanzados entre dos fechas.
     * @param start Fecha inicial.
     * @param end Fecha final.
     * @return Lista de logros en el rango.
     */
    public List<Achievement> findAchievementsBetweenDates(LocalDate start, LocalDate end) {
        return achievementRepository.findByAchievedDateBetween(start, end);
    }

}
