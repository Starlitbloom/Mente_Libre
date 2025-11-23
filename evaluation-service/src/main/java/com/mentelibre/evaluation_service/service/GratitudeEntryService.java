package com.mentelibre.evaluation_service.service;

import com.mentelibre.evaluation_service.dto.GratitudeEntryRequest;
import com.mentelibre.evaluation_service.dto.GratitudeEntryResponse;
import com.mentelibre.evaluation_service.model.DailyEvaluation;
import com.mentelibre.evaluation_service.model.GratitudeEntry;
import com.mentelibre.evaluation_service.model.MoodLabel;
import com.mentelibre.evaluation_service.repository.DailyEvaluationRepository;
import com.mentelibre.evaluation_service.repository.GratitudeEntryRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;

import java.time.LocalDate;
import java.util.List;

@Service
@RequiredArgsConstructor
public class GratitudeEntryService {

    private final GratitudeEntryRepository gratitudeEntryRepository;
    private final DailyEvaluationRepository dailyEvaluationRepository;

    /**
     * Crear una nueva entrada de gratitud para un usuario.
     */
    public GratitudeEntryResponse createEntry(String userId, GratitudeEntryRequest request) {

        LocalDate date = (request.getDate() != null) ? request.getDate() : LocalDate.now();

        // mood opcional
        MoodLabel mood = null;
        if (request.getMoodLabel() != null) {
            mood = MoodLabel.valueOf(request.getMoodLabel());
        }

        // dailyEvaluation opcional
        DailyEvaluation dailyEvaluation = null;
        if (request.getDailyEvaluationId() != null) {
            dailyEvaluation = dailyEvaluationRepository.findById(request.getDailyEvaluationId())
                    .orElseThrow(() -> new IllegalArgumentException(
                            "No existe DailyEvaluation con id=" + request.getDailyEvaluationId())
                    );
        }

        if (request.getText() == null || request.getText().isBlank()) {
            throw new IllegalArgumentException("El campo 'text' es obligatorio.");
        }

        GratitudeEntry entity = GratitudeEntry.builder()
                .userId(userId)
                .date(date)
                .moodLabel(mood)
                .title(request.getTitle())
                .text(request.getText())
                .dailyEvaluation(dailyEvaluation)
                .build();

        GratitudeEntry saved = gratitudeEntryRepository.save(entity);
        return GratitudeEntryResponse.fromEntity(saved);
    }

    /**
     * Todas las entradas de gratitud de un usuario (ordenadas por fecha ascendente).
     */
    public List<GratitudeEntryResponse> getAllForUser(String userId) {
        return gratitudeEntryRepository.findByUserIdOrderByDateAsc(userId)
                .stream()
                .map(GratitudeEntryResponse::fromEntity)
                .toList();
    }

    /**
     * Entradas de gratitud de un usuario en una fecha específica.
     */
    public List<GratitudeEntryResponse> getForDay(String userId, LocalDate date) {
        return gratitudeEntryRepository.findByUserIdAndDateOrderByCreatedAtAsc(userId, date)
                .stream()
                .map(GratitudeEntryResponse::fromEntity)
                .toList();
    }
}
