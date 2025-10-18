package com.mentelibre.goals_service.service;

import com.mentelibre.goals_service.model.Goal;
import com.mentelibre.goals_service.repository.GoalRepository;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.mockito.Mockito;

import java.time.LocalDate;
import java.util.List;
import java.util.Optional;

import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.Mockito.*;

/**
 * Tests unitarios para GoalService.
 */
class GoalServiceTest {

    private GoalRepository goalRepository;
    private GoalService goalService;
    private Goal sampleGoal;

    @BeforeEach
    void setUp() {
        goalRepository = Mockito.mock(GoalRepository.class);
        goalService = new GoalService(goalRepository);

        sampleGoal = new Goal();
        sampleGoal.setId(1L);
        sampleGoal.setTitle("Registrar emociones");
        sampleGoal.setDescription("Seguimiento diario");
        sampleGoal.setStartDate(LocalDate.now());
        sampleGoal.setEndDate(LocalDate.now().plusDays(7));
        sampleGoal.setUserId(1L);
    }

    @Test
    void createGoal_shouldReturnGoal() {
        when(goalRepository.save(sampleGoal)).thenReturn(sampleGoal);

        Goal created = goalService.createGoal(sampleGoal);

        assertEquals("Registrar emociones", created.getTitle());
        verify(goalRepository, times(1)).save(sampleGoal);
    }

    @Test
    void findGoalById_shouldReturnGoal() {
        when(goalRepository.findById(1L)).thenReturn(Optional.of(sampleGoal));

        Goal goal = goalService.findGoalById(1L);

        assertNotNull(goal);
        assertEquals(1L, goal.getId());
    }

    @Test
    void findAllGoals_shouldReturnList() {
        when(goalRepository.findAll()).thenReturn(List.of(sampleGoal));

        List<Goal> goals = goalService.findAllGoals();

        assertEquals(1, goals.size());
    }

    @Test
    void updateGoal_shouldUpdateTitle() {
        Goal updated = new Goal();
        updated.setTitle("Nuevo título");

        when(goalRepository.findById(1L)).thenReturn(Optional.of(sampleGoal));
        when(goalRepository.save(any(Goal.class))).thenReturn(updated);

        Goal result = goalService.updateGoal(1L, updated);

        assertEquals("Nuevo título", result.getTitle());
    }

    @Test
    void deleteGoal_shouldCallDelete() {
        when(goalRepository.findById(1L)).thenReturn(Optional.of(sampleGoal));

        goalService.deleteGoal(1L);

        verify(goalRepository, times(1)).delete(sampleGoal);
    }
}
