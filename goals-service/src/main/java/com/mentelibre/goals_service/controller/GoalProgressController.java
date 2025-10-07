package com.mentelibre.goals_service.controller;

import com.mentelibre.goals_service.model.GoalProgress;
import com.mentelibre.goals_service.service.GoalProgressService;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.time.LocalDate;
import java.util.List;

/**
 * Controller REST para manejar las operaciones de progreso de metas (GoalProgress).
 * Expone endpoints para crear, actualizar, eliminar y consultar registros de progreso.
 */
@RestController
@RequestMapping("/api/progress")
public class GoalProgressController {
    private final GoalProgressService progressService;

    public GoalProgressController(GoalProgressService progressService) {
        this.progressService = progressService;
    }

    /**
     * Crear un nuevo registro de progreso.
     * POST /api/progress
     */
    @PostMapping
    public ResponseEntity<GoalProgress> createProgress(@RequestBody GoalProgress progress) {
        GoalProgress created = progressService.createProgress(progress);
        return ResponseEntity.ok(created);
    }

    /**
     * Actualizar un registro de progreso por ID.
     * PUT /api/progress/{id}
     */
    @PutMapping("/{id}")
    public ResponseEntity<GoalProgress> updateProgress(@PathVariable Long id, @RequestBody GoalProgress progress) {
        GoalProgress updated = progressService.updateProgress(id, progress);
        return ResponseEntity.ok(updated);
    }

    /**
     * Eliminar un registro de progreso por ID.
     * DELETE /api/progress/{id}
     */
    @DeleteMapping("/{id}")
    public ResponseEntity<Void> deleteProgress(@PathVariable Long id) {
        progressService.deleteProgress(id);
        return ResponseEntity.noContent().build();
    }

    /**
     * Obtener un registro de progreso por ID.
     * GET /api/progress/{id}
     */
    @GetMapping("/{id}")
    public ResponseEntity<GoalProgress> getProgressById(@PathVariable Long id) {
        GoalProgress progress = progressService.findProgressById(id);
        return ResponseEntity.ok(progress);
    }

    /**
     * Obtener todos los registros de progreso de una meta.
     * GET /api/progress/goal/{goalId}
     */
    @GetMapping("/goal/{goalId}")
    public ResponseEntity<List<GoalProgress>> getProgressByGoalId(@PathVariable Long goalId) {
        List<GoalProgress> list = progressService.findAllProgressByGoalId(goalId);
        return ResponseEntity.ok(list);
    }

    /**
     * Obtener un registro de progreso de una meta en una fecha específica.
     * GET /api/progress/goal/{goalId}/date/{date}
     */
    @GetMapping("/goal/{goalId}/date/{date}")
    public ResponseEntity<GoalProgress> getProgressByGoalAndDate(
            @PathVariable Long goalId,
            @PathVariable String date // Se recibe como String yyyy-MM-dd
    ) {
        LocalDate parsedDate = LocalDate.parse(date);
        GoalProgress progress = progressService.findProgressByGoalIdAndDate(goalId, parsedDate);
        return ResponseEntity.ok(progress);
    }

    /**
     * Obtener todos los registros de progreso de una meta entre dos fechas.
     * GET /api/progress/goal/{goalId}/between?start=yyyy-MM-dd&end=yyyy-MM-dd
     */
    @GetMapping("/goal/{goalId}/between")
    public ResponseEntity<List<GoalProgress>> getProgressBetweenDates(
            @PathVariable Long goalId,
            @RequestParam String start,
            @RequestParam String end
    ) {
        LocalDate startDate = LocalDate.parse(start);
        LocalDate endDate = LocalDate.parse(end);
        List<GoalProgress> list = progressService.findProgressByGoalIdBetweenDates(goalId, startDate, endDate);
        return ResponseEntity.ok(list);
    }

    /**
     * Obtener registros de progreso completados de una meta entre dos fechas.
     * GET /api/progress/goal/{goalId}/completed?start=yyyy-MM-dd&end=yyyy-MM-dd
     */
    @GetMapping("/goal/{goalId}/completed")
    public ResponseEntity<List<GoalProgress>> getCompletedProgressBetweenDates(
            @PathVariable Long goalId,
            @RequestParam String start,
            @RequestParam String end
    ) {
        LocalDate startDate = LocalDate.parse(start);
        LocalDate endDate = LocalDate.parse(end);
        List<GoalProgress> list = progressService.findCompletedProgressByGoalIdBetweenDates(goalId, startDate, endDate);
        return ResponseEntity.ok(list);
    }

}
