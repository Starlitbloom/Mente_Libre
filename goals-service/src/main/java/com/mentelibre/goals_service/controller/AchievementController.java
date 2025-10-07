package com.mentelibre.goals_service.controller;

import com.mentelibre.goals_service.model.Achievement;
import com.mentelibre.goals_service.service.AchievementService;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.time.LocalDate;
import java.util.List;

/**
 * Controller REST para manejar las operaciones de logros (Achievement).
 * Permite crear, actualizar, eliminar y consultar logros asociados a metas.
 */
@RestController
@RequestMapping("/api/achievements")
public class AchievementController {
    private final AchievementService achievementService;

    public AchievementController(AchievementService achievementService) {
        this.achievementService = achievementService;
    }

    /**
     * Crear un nuevo logro.
     * POST /api/achievements
     */
    @PostMapping
    public ResponseEntity<Achievement> createAchievement(@RequestBody Achievement achievement) {
        Achievement created = achievementService.createAchievement(achievement);
        return ResponseEntity.ok(created);
    }

    /**
     * Actualizar un logro por ID.
     * PUT /api/achievements/{id}
     */
    @PutMapping("/{id}")
    public ResponseEntity<Achievement> updateAchievement(@PathVariable Long id, @RequestBody Achievement achievement) {
        Achievement updated = achievementService.updateAchievement(id, achievement);
        return ResponseEntity.ok(updated);
    }

    /**
     * Eliminar un logro por ID.
     * DELETE /api/achievements/{id}
     */
    @DeleteMapping("/{id}")
    public ResponseEntity<Void> deleteAchievement(@PathVariable Long id) {
        achievementService.deleteAchievement(id);
        return ResponseEntity.noContent().build();
    }

    /**
     * Obtener un logro por ID.
     * GET /api/achievements/{id}
     */
    @GetMapping("/{id}")
    public ResponseEntity<Achievement> getAchievementById(@PathVariable Long id) {
        Achievement achievement = achievementService.findAchievementById(id);
        return ResponseEntity.ok(achievement);
    }

    /**
     * Obtener todos los logros.
     * GET /api/achievements
     */
    @GetMapping
    public ResponseEntity<List<Achievement>> getAllAchievements() {
        List<Achievement> list = achievementService.findAllAchievements();
        return ResponseEntity.ok(list);
    }

    /**
     * Obtener todos los logros de una meta específica.
     * GET /api/achievements/goal/{goalId}
     */
    @GetMapping("/goal/{goalId}")
    public ResponseEntity<List<Achievement>> getAchievementsByGoalId(@PathVariable Long goalId) {
        List<Achievement> list = achievementService.findAchievementsByGoalId(goalId);
        return ResponseEntity.ok(list);
    }

    /**
     * Buscar un logro por nombre dentro de una meta.
     * GET /api/achievements/goal/{goalId}/name?name=Nombre
     */
    @GetMapping("/goal/{goalId}/name")
    public ResponseEntity<Achievement> getAchievementByNameAndGoalId(
            @PathVariable Long goalId,
            @RequestParam String name
    ) {
        Achievement achievement = achievementService.findByNameAndGoalId(name, goalId);
        return ResponseEntity.ok(achievement);
    }

    /**
     * Obtener todos los logros entre dos fechas.
     * GET /api/achievements/between?start=yyyy-MM-dd&end=yyyy-MM-dd
     */
    @GetMapping("/between")
    public ResponseEntity<List<Achievement>> getAchievementsBetweenDates(
            @RequestParam String start,
            @RequestParam String end
    ) {
        LocalDate startDate = LocalDate.parse(start);
        LocalDate endDate = LocalDate.parse(end);
        List<Achievement> list = achievementService.findAchievementsBetweenDates(startDate, endDate);
        return ResponseEntity.ok(list);
    }

}
