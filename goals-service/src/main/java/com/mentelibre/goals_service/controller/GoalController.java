package com.mentelibre.goals_service.controller;

import com.mentelibre.goals_service.model.Goal;
import com.mentelibre.goals_service.service.GoalService;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.List;

/**
 * Controller REST para manejar las operaciones de metas (Goal).
 * Expone endpoints para crear, actualizar, eliminar y consultar metas.
 */
@RestController
@RequestMapping("/api/goals") // Ruta base para todas las operaciones de Goal
public class GoalController {
    private final GoalService goalService;

    public GoalController(GoalService goalService) {
        this.goalService = goalService;
    }

    /**
     * Crear una nueva meta.
     * POST /api/goals
     */
    @PostMapping
    public ResponseEntity<Goal> createGoal(@RequestBody Goal goal) {
        Goal createdGoal = goalService.createGoal(goal);
        return ResponseEntity.ok(createdGoal);
    }

    /**
     * Actualizar una meta existente por ID.
     * PUT /api/goals/{id}
     */
    @PutMapping("/{id}")
    public ResponseEntity<Goal> updateGoal(@PathVariable Long id, @RequestBody Goal goal) {
        Goal updatedGoal = goalService.updateGoal(id, goal);
        return ResponseEntity.ok(updatedGoal);
    }

    /**
     * Eliminar una meta por ID.
     * DELETE /api/goals/{id}
     */
    @DeleteMapping("/{id}")
    public ResponseEntity<Void> deleteGoal(@PathVariable Long id) {
        goalService.deleteGoal(id);
        return ResponseEntity.noContent().build(); // Retorna 204
    }

    /**
     * Obtener todas las metas.
     * GET /api/goals
     */
    @GetMapping
    public ResponseEntity<List<Goal>> getAllGoals() {
        List<Goal> goals = goalService.findAllGoals();
        return ResponseEntity.ok(goals);
    }

    /**
     * Obtener una meta por ID.
     * GET /api/goals/{id}
     */
    @GetMapping("/{id}")
    public ResponseEntity<Goal> getGoalById(@PathVariable Long id) {
        Goal goal = goalService.findGoalById(id);
        return ResponseEntity.ok(goal);
    }

    /**
     * Obtener todas las metas de un usuario.
     * GET /api/goals/user/{userId}
     */
    @GetMapping("/user/{userId}")
    public ResponseEntity<List<Goal>> getGoalsByUserId(@PathVariable Long userId) {
        List<Goal> goals = goalService.findGoalsByUserId(userId);
        return ResponseEntity.ok(goals);
    }

    /**
     * Obtener todas las metas incompletas de un usuario.
     * GET /api/goals/user/{userId}/incomplete
     */
    @GetMapping("/user/{userId}/incomplete")
    public ResponseEntity<List<Goal>> getIncompleteGoalsByUserId(@PathVariable Long userId) {
        List<Goal> goals = goalService.findIncompleteGoalsByUserId(userId);
        return ResponseEntity.ok(goals);
    }

    /**
     * Buscar metas por título.
     * GET /api/goals/search?title=texto
     */
    @GetMapping("/search")
    public ResponseEntity<List<Goal>> searchGoals(@RequestParam String title) {
        List<Goal> goals = goalService.searchGoalsByTitle(title);
        return ResponseEntity.ok(goals);
    }

}
