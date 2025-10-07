package com.mentelibre.goals_service.service;

import com.mentelibre.goals_service.model.GoalProgress;
import com.mentelibre.goals_service.repository.GoalProgressRepository;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.time.LocalDate;
import java.util.List;

/**
 * Servicio para manejar la lógica de negocio relacionada con el progreso de las metas.
 * Permite registrar, actualizar, eliminar y consultar el progreso diario de cada meta.
 */
@Service
@Transactional
public class GoalProgressService {

    private final GoalProgressRepository progressRepository;

    public GoalProgressService(GoalProgressRepository progressRepository) {
        this.progressRepository = progressRepository;
    }

    /**
     * Crea un nuevo registro de progreso para una meta.
     * @param progress Objeto GoalProgress con los datos del progreso.
     * @return Progreso creado con ID generado.
     */
    public GoalProgress createProgress(GoalProgress progress) {
        return progressRepository.save(progress);
    }

    /**
     * Actualiza un registro de progreso existente.
     * @param id ID del registro.
     * @param progressDetails Nuevos datos del progreso.
     * @return Progreso actualizado.
     * @throws RuntimeException si no se encuentra el registro.
     */
    public GoalProgress updateProgress(Long id, GoalProgress progressDetails) {
        GoalProgress progress = progressRepository.findById(id)
                .orElseThrow(() -> new RuntimeException("Progress not found with id " + id));

        progress.setDate(progressDetails.getDate());
        progress.setDone(progressDetails.isDone());
        progress.setGoal(progressDetails.getGoal());

        return progressRepository.save(progress);
    }

    /**
     * Elimina un registro de progreso por su ID.
     * @param id ID del registro.
     * @throws RuntimeException si no existe.
     */
    public void deleteProgress(Long id) {
        GoalProgress progress = progressRepository.findById(id)
                .orElseThrow(() -> new RuntimeException("Progress not found with id " + id));
        progressRepository.delete(progress);
    }

    /**
     * Obtiene un registro de progreso por su ID.
     * @param id ID del registro.
     * @return Registro de progreso.
     */
    public GoalProgress findProgressById(Long id) {
        return progressRepository.findById(id)
                .orElseThrow(() -> new RuntimeException("Progress not found with id " + id));
    }

    /**
     * Obtiene todos los registros de progreso de una meta, ordenados por fecha ascendente.
     * @param goalId ID de la meta.
     * @return Lista de registros de progreso.
     */
    public List<GoalProgress> findAllProgressByGoalId(Long goalId) {
        return progressRepository.findByGoalIdOrderByDateAsc(goalId);
    }

    /**
     * Obtiene un registro de progreso de una meta en una fecha específica.
     * @param goalId ID de la meta.
     * @param date Fecha del progreso.
     * @return Registro de progreso.
     */
    public GoalProgress findProgressByGoalIdAndDate(Long goalId, LocalDate date) {
        return progressRepository.findByGoalIdAndDate(goalId, date)
                .orElseThrow(() -> new RuntimeException("Progress not found for goalId " + goalId + " and date " + date));
    }

    /**
     * Obtiene todos los registros de progreso de una meta entre dos fechas.
     * @param goalId ID de la meta.
     * @param start Fecha inicial.
     * @param end Fecha final.
     * @return Lista de registros de progreso en el rango.
     */
    public List<GoalProgress> findProgressByGoalIdBetweenDates(Long goalId, LocalDate start, LocalDate end) {
        return progressRepository.findByGoalIdAndDateBetween(goalId, start, end);
    }

    /**
     * Obtiene solo los registros marcados como 'done' de una meta en un rango de fechas.
     * @param goalId ID de la meta.
     * @param start Fecha inicial.
     * @param end Fecha final.
     * @return Lista de registros completados.
     */
    public List<GoalProgress> findCompletedProgressByGoalIdBetweenDates(Long goalId, LocalDate start, LocalDate end) {
        return progressRepository.findByGoalIdAndDoneTrueAndDateBetween(goalId, start, end);
    }

}
