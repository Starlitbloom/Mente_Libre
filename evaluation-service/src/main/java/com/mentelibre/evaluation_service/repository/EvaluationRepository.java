package com.mentelibre.evaluation_service.repository;

import java.util.List;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import com.mentelibre.evaluation_service.model.Evaluation;

@Repository
public interface EvaluationRepository extends JpaRepository<Evaluation, Long> {
    List<Evaluation> findByUserId(Long userId); // Obtener todas las evaluaciones de un usuario
    List<Evaluation> findByActivoTrue(); // Solo las activas
}