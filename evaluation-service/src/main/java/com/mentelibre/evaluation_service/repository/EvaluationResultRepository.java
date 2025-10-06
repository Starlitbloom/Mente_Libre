package com.mentelibre.evaluation_service.repository;

import java.util.List;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import com.mentelibre.evaluation_service.model.EvaluationResult;

@Repository
public interface EvaluationResultRepository extends JpaRepository<EvaluationResult, Long> {
    List<EvaluationResult> findByUserId(Long userId); // Resultados de un usuario
    List<EvaluationResult> findByEvaluationId(Long evaluationId); // Resultados de una evaluación
}
