package com.mentelibre.evaluation_service.repository;

import com.mentelibre.evaluation_service.model.DailyEvaluation;
import com.mentelibre.evaluation_service.model.MoodLabel;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import java.time.LocalDate;
import java.util.List;
import java.util.Optional;

@Repository
public interface DailyEvaluationRepository extends JpaRepository<DailyEvaluation, Long> {

    // 1) Evaluación única por usuario + fecha (ideal si pusiste unique en la tabla)
    Optional<DailyEvaluation> findByUserIdAndDate(String userId, LocalDate date);

    // 2) Todas las evaluaciones de un usuario, ordenadas de la más reciente a la más antigua
    List<DailyEvaluation> findByUserIdOrderByDateDesc(String userId);

    // 3) Rango de fechas (por ejemplo, "últimos 30 días")
    List<DailyEvaluation> findByUserIdAndDateBetweenOrderByDateAsc(
            String userId,
            LocalDate startDate,
            LocalDate endDate
    );

    // 4) (Opcional) Rango + estado de ánimo principal
    List<DailyEvaluation> findByUserIdAndMainMoodAndDateBetweenOrderByDateAsc(
            String userId,
            MoodLabel mainMood,
            LocalDate startDate,
            LocalDate endDate
    );
}
