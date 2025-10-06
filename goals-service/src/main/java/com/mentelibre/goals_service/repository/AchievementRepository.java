package com.mentelibre.goals_service.repository;

import java.time.LocalDate;
import java.util.List;
import java.util.Optional;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import com.mentelibre.goals_service.model.Achievement;

/**
 * Repositorio JPA para la entidad Achievement.
 *
 * Permite consultar logros por meta, por nombre, o dentro de un rango de fechas.
 */
@Repository
public interface AchievementRepository extends JpaRepository<Achievement, Long> {
     /**
     * Obtiene todos los logros asociados a una meta.
     *
     * @param goalId id de la meta
     * @return lista de logros
     */
    List<Achievement> findByGoalId(Long goalId);

    /**
     * Busca un logro por nombre dentro de una meta (útil para evitar duplicados).
     *
     * @param name nombre del logro
     * @param goalId id de la meta
     * @return Optional con el logro si existe
     */
    Optional<Achievement> findByNameAndGoalId(String name, Long goalId);

    /**
     * Obtiene todos los logros alcanzados en un rango de fechas.
     * Útil para mostrar estadísticas o notificaciones programadas.
     *
     * @param start fecha inicial
     * @param end fecha final
     * @return lista de logros en el rango
     */
    List<Achievement> findByAchievedDateBetween(LocalDate start, LocalDate end);

}
