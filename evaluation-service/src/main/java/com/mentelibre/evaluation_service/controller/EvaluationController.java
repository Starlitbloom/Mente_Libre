package com.mentelibre.evaluation_service.controller;

import com.mentelibre.evaluation_service.dto.DailyEvaluationRequest;
import com.mentelibre.evaluation_service.dto.DailyEvaluationResponse;
import com.mentelibre.evaluation_service.dto.GratitudeEntryRequest;
import com.mentelibre.evaluation_service.dto.GratitudeEntryResponse;
import com.mentelibre.evaluation_service.service.DailyEvaluationService;
import com.mentelibre.evaluation_service.service.GratitudeEntryService;
import lombok.RequiredArgsConstructor;
import org.springframework.format.annotation.DateTimeFormat;
import org.springframework.http.HttpStatus;
import org.springframework.web.bind.annotation.*;

import java.time.LocalDate;
import java.util.List;

/**
 * API para Bitácora diaria + Diario de Gratitud.
 *
 * MÁS ADELANTE:
 * - el userId saldrá del token JWT (auth-service).
 * Por ahora lo recibimos por query o header.
 */
@RestController
@RequestMapping("/api/evaluations")
@RequiredArgsConstructor
public class EvaluationController {

    private final DailyEvaluationService dailyEvaluationService;
    private final GratitudeEntryService gratitudeEntryService;

    // ================== DAILY EVALUATION (BITÁCORA) ==================

    @PostMapping("/daily")
    @ResponseStatus(HttpStatus.OK)
    public DailyEvaluationResponse upsertDailyEvaluation(
            @RequestParam("userId") String userId,
            @RequestBody DailyEvaluationRequest request
    ) {
        return dailyEvaluationService.upsertDailyEvaluation(userId, request);
    }

    @GetMapping("/daily")
    public DailyEvaluationResponse getDailyEvaluation(
            @RequestParam("userId") String userId,
            @RequestParam("date")
            @DateTimeFormat(iso = DateTimeFormat.ISO.DATE)
            LocalDate date
    ) {
        return dailyEvaluationService.getByDate(userId, date);
    }

    @GetMapping("/daily/history")
    public List<DailyEvaluationResponse> getHistory(
            @RequestParam("userId") String userId
    ) {
        return dailyEvaluationService.getHistory(userId);
    }

    // ================== DIARIO DE GRATITUD ==================

    @PostMapping("/gratitude")
    @ResponseStatus(HttpStatus.CREATED)
    public GratitudeEntryResponse createGratitudeEntry(
            @RequestParam("userId") String userId,
            @RequestBody GratitudeEntryRequest request
    ) {
        return gratitudeEntryService.createEntry(userId, request);
    }

    @GetMapping("/gratitude")
    public List<GratitudeEntryResponse> getGratitudeEntries(
            @RequestParam("userId") String userId
    ) {
        return gratitudeEntryService.getAllForUser(userId);
    }

    @GetMapping("/gratitude/day")
    public List<GratitudeEntryResponse> getGratitudeEntriesForDay(
            @RequestParam("userId") String userId,
            @RequestParam("date")
            @DateTimeFormat(iso = DateTimeFormat.ISO.DATE)
            LocalDate date
    ) {
        return gratitudeEntryService.getForDay(userId, date);
    }
}
