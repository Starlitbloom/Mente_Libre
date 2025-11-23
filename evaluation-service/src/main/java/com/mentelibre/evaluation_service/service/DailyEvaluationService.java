package com.mentelibre.evaluation_service.service;

import com.mentelibre.evaluation_service.dto.DailyEvaluationRequest;
import com.mentelibre.evaluation_service.dto.DailyEvaluationResponse;
import com.mentelibre.evaluation_service.model.DailyEvaluation;
import com.mentelibre.evaluation_service.model.MoodLabel;
import com.mentelibre.evaluation_service.repository.DailyEvaluationRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;

import java.time.LocalDate;
import java.util.List;

@Service
@RequiredArgsConstructor
public class DailyEvaluationService {

    private final DailyEvaluationRepository dailyEvaluationRepository;

    /**
     * Crea o actualiza la evaluación diaria (bitácora) para un userId + date.
     */
    public DailyEvaluationResponse upsertDailyEvaluation(String userId, DailyEvaluationRequest request) {

        LocalDate date = (request.getDate() != null) ? request.getDate() : LocalDate.now();

        MoodLabel mood = null;
        if (request.getMoodLabel() != null) {
            mood = MoodLabel.valueOf(request.getMoodLabel());
        }

        // Buscar si ya existe una evaluación para ese userId+date
        DailyEvaluation entity = dailyEvaluationRepository
                .findByUserIdAndDate(userId, date)
                .orElseGet(() -> DailyEvaluation.builder()
                        .userId(userId)
                        .date(date)
                        .build()
                );

        // Actualizar campos
        entity.setMainMood(mood);
        entity.setGlobalScore(request.getGlobalScore());
        entity.setReflection(request.getReflection());

        DailyEvaluation saved = dailyEvaluationRepository.save(entity);
        return DailyEvaluationResponse.fromEntity(saved);
    }

    /**
     * Obtener evaluación diaria por userId + date.
     */
    public DailyEvaluationResponse getByDate(String userId, LocalDate date) {
        DailyEvaluation entity = dailyEvaluationRepository
                .findByUserIdAndDate(userId, date)
                .orElseThrow(() -> new IllegalArgumentException(
                        "No hay evaluación diaria para ese usuario en la fecha indicada")
                );
        return DailyEvaluationResponse.fromEntity(entity);
    }

    /**
     * Historial de evaluaciones, ordenado por fecha descendente (más reciente primero).
     */
    public List<DailyEvaluationResponse> getHistory(String userId) {
        return dailyEvaluationRepository
                .findByUserIdOrderByDateDesc(userId)
                .stream()
                .map(DailyEvaluationResponse::fromEntity)
                .toList();
    }
}
