package com.mentelibre.goals_service.service;

import com.mentelibre.goals_service.model.Goal;
import com.mentelibre.goals_service.repository.GoalRepository;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.List;

/**
 * Servicio para manejar la lógica de negocio relacionada con las metas (Goal).
 * 
 * Proporciona operaciones CRUD y consultas personalizadas usando el repositorio GoalRepository.
 */
@Service
@Transactional // Gestiona las transacciones automáticamente
public class GoalService {

     private final GoalRepository goalRepository;

    // Constructor para inyectar el repositorio mediante Spring
    public GoalService(GoalRepository goalRepository) {
        this.goalRepository = goalRepository;
    }

    /**
     * Crea una nueva meta en la base de datos.
     * @param goal Objeto Goal con los datos de la nueva meta.
     * @return La meta creada con su ID generado automáticamente.
     */
    public Goal createGoal(Goal goal) {
        return goalRepository.save(goal);
    }

    /**
     * Actualiza una meta existente.
     * @param id ID de la meta a actualizar.
     * @param goalDetails Objeto Goal con los nuevos datos.
     * @return La meta actualizada.
     * @throws RuntimeException si no se encuentra la meta con ese ID.
     */
    public Goal updateGoal(Long id, Goal goalDetails) {
        Goal goal = goalRepository.findById(id)
                .orElseThrow(() -> new RuntimeException("Goal not found with id " + id));

        goal.setTitle(goalDetails.getTitle());
        goal.setDescription(goalDetails.getDescription());
        goal.setStartDate(goalDetails.getStartDate());
        goal.setEndDate(goalDetails.getEndDate());
        goal.setCompleted(goalDetails.isCompleted());
        goal.setUserId(goalDetails.getUserId());

        return goalRepository.save(goal);
    }

    /**
     * Elimina una meta por su ID.
     * @param id ID de la meta a eliminar.
     * @throws RuntimeException si no se encuentra la meta.
     */
    public void deleteGoal(Long id) {
        Goal goal = goalRepository.findById(id)
                .orElseThrow(() -> new RuntimeException("Goal not found with id " + id));
        goalRepository.delete(goal);
    }

    /**
     * Obtiene una meta por su ID.
     * @param id ID de la meta.
     * @return La meta encontrada.
     * @throws RuntimeException si no existe.
     */
    public Goal findGoalById(Long id) {
        return goalRepository.findById(id)
                .orElseThrow(() -> new RuntimeException("Goal not found with id " + id));
    }

    /**
     * Obtiene todas las metas en la base de datos.
     * @return Lista de todas las metas.
     */
    public List<Goal> findAllGoals() {
        return goalRepository.findAll();
    }

    /**
     * Obtiene todas las metas de un usuario específico.
     * @param userId ID del usuario.
     * @return Lista de metas del usuario.
     */
    public List<Goal> findGoalsByUserId(Long userId) {
        return goalRepository.findByUserId(userId);
    }

    /**
     * Obtiene todas las metas incompletas de un usuario.
     * @param userId ID del usuario.
     * @return Lista de metas no completadas.
     */
    public List<Goal> findIncompleteGoalsByUserId(Long userId) {
        return goalRepository.findByUserIdAndCompletedFalse(userId);
    }

    /**
     * Busca metas cuyo título contenga un texto específico (ignorando mayúsculas/minúsculas).
     * @param title Texto a buscar.
     * @return Lista de metas que coinciden.
     */
    public List<Goal> searchGoalsByTitle(String title) {
        return goalRepository.findByTitleContainingIgnoreCase(title);
    }

}
