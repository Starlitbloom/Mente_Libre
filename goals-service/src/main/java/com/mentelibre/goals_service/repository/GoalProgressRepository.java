package com.mentelibre.goals_service.repository;

import java.time.LocalDate;
import java.util.List;
import java.util.Optional;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import com.mentelibre.goals_service.model.GoalProgress;

/**
 * Repositorio JPA para la entidad GoalProgress.
 * 
 * Maneja consultas de progreso diario/por fecha y facilita la verificación de
 * hitos (por ejemplo: existencia de registro en una fecha determinada).
 */
@Repository
public interface GoalProgressRepository extends JpaRepository<GoalProgress, Long> {
    /**
     * Obtiene todos los registros de progreso asociados a una meta,
     * ordenados por fecha ascendente.
     *
     * @param goalId id de la meta
     * @return lista de registros de progreso
     */
    List<GoalProgress> findByGoalIdOrderByDateAsc(Long goalId);

    /**
     * Busca un registro de progreso para una meta en una fecha específica.
     *
     * @param goalId id de la meta
     * @param date fecha del registro
     * @return Optional con el registro si existe
     */
    Optional<GoalProgress> findByGoalIdAndDate(Long goalId, LocalDate date);

    /**
     * Obtiene todos los registros de una meta entre dos fechas (inclusive).
     * Útil para cálculos de rachas consecutivas o métricas semanales.
     *
     * @param goalId id de la meta
     * @param start fecha inicial
     * @param end fecha final
     * @return lista de registros entre las fechas dadas
     */
    List<GoalProgress> findByGoalIdAndDateBetween(Long goalId, LocalDate start, LocalDate end);

    /**
     * Cuenta cuántos días 'done' hay para una meta en un rango dado.
     * Si necesitas un conteo, podrías implementar un @Query personalizado que
     * retorne un long; aquí se deja la forma derivada para obtener la lista.
     *
     * @param goalId id de la meta
     * @param start fecha inicial
     * @param end fecha final
     * @return lista de registros cumplidos entre fechas
     */
    List<GoalProgress> findByGoalIdAndDoneTrueAndDateBetween(Long goalId, LocalDate start, LocalDate end);
    

}
