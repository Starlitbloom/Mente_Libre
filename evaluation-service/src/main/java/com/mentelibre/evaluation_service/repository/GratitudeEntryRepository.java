package com.mentelibre.evaluation_service.repository;

import com.mentelibre.evaluation_service.model.GratitudeEntry;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import java.time.LocalDate;
import java.util.List;

@Repository
public interface GratitudeEntryRepository extends JpaRepository<GratitudeEntry, Long> {

    // Entradas por usuario y fecha exacta
    List<GratitudeEntry> findByUserIdAndDateOrderByCreatedAtAsc(String userId, LocalDate date);

    // Entradas en rango de fechas
    List<GratitudeEntry> findByUserIdAndDateBetweenOrderByDateAsc(
            String userId,
            LocalDate startDate,
            LocalDate endDate
    );

    // Entradas asociadas a una DailyEvaluation específica
    List<GratitudeEntry> findByDailyEvaluationIdOrderByCreatedAtAsc(Long dailyEvaluationId);

    // ⭐ NUEVO: trae todas las entradas del usuario (lo que te faltaba)
    List<GratitudeEntry> findByUserIdOrderByDateAsc(String userId);
}
