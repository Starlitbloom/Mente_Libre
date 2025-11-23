package com.mentelibre.emotion_service.controller;

import com.mentelibre.emotion_service.model.MoodRecord;
import com.mentelibre.emotion_service.service.MoodRecordService;
import lombok.RequiredArgsConstructor;
import org.springframework.web.bind.annotation.*;

import java.time.LocalDate;
import java.util.List;

/**
 * Controller para exponer la API a Android:
 * registrar, obtener rangos, puntaje, etc.
 */
@RestController
@RequestMapping("/api/moods")
@RequiredArgsConstructor
public class MoodRecordController {

    private final MoodRecordService moodService;

    // ========================================
    // 1) Registrar o actualizar ánimo del día
    // ========================================
    @PostMapping("/register")
    public MoodRecord register(
            @RequestParam String userId,
            @RequestParam String emotionLabel,
            @RequestParam(required = false) String context,
            @RequestParam(required = false) String date
    ) {
        LocalDate recordDate = (date == null) ? LocalDate.now() : LocalDate.parse(date);
        return moodService.registerOrUpdateMood(userId, emotionLabel, context, recordDate);
    }

    // ========================================
    // 2) Obtener registros por rango
    // ========================================
    @GetMapping("/range")
    public List<MoodRecord> getRange(
            @RequestParam String userId,
            @RequestParam String start,
            @RequestParam String end
    ) {
        return moodService.getRecordsForRange(
                userId,
                LocalDate.parse(start),
                LocalDate.parse(end)
        );
    }

    // ========================================
    // 3) Últimos N días (Puntaje)
    // ========================================
    @GetMapping("/last-days")
    public MoodRecordService.ScoreSummary getLastDays(
            @RequestParam String userId,
            @RequestParam(defaultValue = "30") int days
    ) {
        return moodService.getScoreSummaryForLastDays(userId, days);
    }

    // ========================================
    // 4) Por mes (para gráfico/PDF)
    // ========================================
    @GetMapping("/month")
    public List<MoodRecord> getMonth(
            @RequestParam String userId,
            @RequestParam int year,
            @RequestParam int month
    ) {
        return moodService.getRecordsForMonth(userId, year, month);
    }

    // ========================================
    // 5) Por año (para gráfico/PDF)
    // ========================================
    @GetMapping("/year")
    public List<MoodRecord> getYear(
            @RequestParam String userId,
            @RequestParam int year
    ) {
        return moodService.getRecordsForYear(userId, year);
    }
}
