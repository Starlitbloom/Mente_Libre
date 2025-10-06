package com.mentelibre.goals_service.repository;

import java.util.List;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import com.mentelibre.goals_service.model.Goal;

/**
 * Repositorio JPA para la entidad Goal.
 * 
 * Provee operaciones básicas CRUD (heredadas de JpaRepository) y consultas
 * adicionales usadas frecuentemente por el servicio.
 */
@Repository
public interface GoalRepository extends JpaRepository<Goal, Long>  {

    /**
     * Obtiene todas las metas creadas por un usuario específico.
     *
     * @param userId id del usuario propietario de las metas
     * @return lista de metas del usuario
     */
    List<Goal> findByUserId(Long userId);

    /**
     * Obtiene todas las metas que aún no se han completado para un usuario.
     *
     * @param userId id del usuario
     * @return lista de metas no completadas
     */
    List<Goal> findByUserIdAndCompletedFalse(Long userId);

    /**
     * Busca metas por título (útil para búsquedas o filtros).
     * Spring Data construye la consulta LIKE internamente si se combina con convenciones
     * en la capa de servicio (ej. pasando %term%).
     *
     * @param title título o parte del título
     * @return lista de metas cuyo título coincide
     */
    List<Goal> findByTitleContainingIgnoreCase(String title);

}
