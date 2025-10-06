package com.mentelibre.evaluation_service.repository;

import java.util.List;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import com.mentelibre.evaluation_service.model.Question;

@Repository
public interface QuestionRepository extends JpaRepository<Question, Long> {
    List<Question> findByEvaluationId(Long evaluationId); // Obtener preguntas de una evaluación
}
