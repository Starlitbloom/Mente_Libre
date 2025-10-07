package com.mentelibre.goals_service.service;

import com.mentelibre.goals_service.model.Achievement;
import com.mentelibre.goals_service.repository.AchievementRepository;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.time.LocalDate;
import java.util.List;

/**
 * Servicio para manejar la lógica de negocio relacionada con los logros (Achievements).
 * Permite crear, actualizar, eliminar y consultar logros obtenidos por metas.
 */
@Service
@Transactional
public class AchievementService{
    
    private final AchievementRepository achievementRepository;

    public AchievementService(AchievementRepository achievementRepository) {
        this.achievementRepository = achievementRepository;
    }

    /**
     * Crea un nuevo logro.
     * @param achievement Objeto Achievement con los datos del logro.
     * @return Logro creado con ID generado.
     */
    public Achievement createAchievement(Achievement achievement) {
        return achievementRepository.save(achievement);
    }

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
