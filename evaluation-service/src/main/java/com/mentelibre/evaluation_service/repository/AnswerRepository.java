package com.mentelibre.evaluation_service.repository;

import java.util.List;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import com.mentelibre.evaluation_service.model.Answer;

@Repository
public interface AnswerRepository extends JpaRepository<Answer, Long> {
    List<Answer> findByEvaluationResultId(Long evaluationResultId); // Respuestas de un resultado
    List<Answer> findByPreguntaId(Long questionId); // Respuestas de una pregunta
}