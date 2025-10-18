package com.mentelibre.goals_service.service;

import com.mentelibre.goals_service.model.Goal;
import com.mentelibre.goals_service.model.GoalProgress;
import com.mentelibre.goals_service.repository.GoalProgressRepository;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.mockito.Mockito;

import java.time.LocalDate;
import java.util.List;
import java.util.Optional;

import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.Mockito.*;

/**
 * Tests unitarios para GoalProgressService.
 */
class GoalProgressServiceTest {

    private GoalProgressRepository progressRepository;
    private GoalProgressService progressService;
    private GoalProgress sampleProgress;
    private Goal sampleGoal;

    @BeforeEach
    void setUp() {
        progressRepository = Mockito.mock(GoalProgressRepository.class);
        progressService = new GoalProgressService(progressRepository);

        sampleGoal = new Goal();
        sampleGoal.setId(1L);

        sampleProgress = new GoalProgress();
        sampleProgress.setId(1L);
        sampleProgress.setDate(LocalDate.now());
        sampleProgress.setDone(true);
        sampleProgress.setGoal(sampleGoal);
    }

    @Test
    void createProgress_shouldReturnProgress() {
        when(progressRepository.save(sampleProgress)).thenReturn(sampleProgress);

        GoalProgress created = progressService.createProgress(sampleProgress);

        assertTrue(created.isDone());
        verify(progressRepository, times(1)).save(sampleProgress);
    }

    @Test
    void findProgressById_shouldReturnProgress() {
        when(progressRepository.findById(1L)).thenReturn(Optional.of(sampleProgress));

        GoalProgress progress = progressService.findProgressById(1L);

        assertNotNull(progress);
        assertEquals(1L, progress.getId());
    }

    @Test
    void findAllProgressByGoalId_shouldReturnList() {
        when(progressRepository.findByGoalIdOrderByDateAsc(1L)).thenReturn(List.of(sampleProgress));

        List<GoalProgress> list = progressService.findAllProgressByGoalId(1L);

        assertEquals(1, list.size());
    }

    @Test
    void updateProgress_shouldUpdateDone() {
        GoalProgress updated = new GoalProgress();
        updated.setDone(false);

        when(progressRepository.findById(1L)).thenReturn(Optional.of(sampleProgress));
        when(progressRepository.save(any(GoalProgress.class))).thenReturn(updated);

        GoalProgress result = progressService.updateProgress(1L, updated);

        assertFalse(result.isDone());
    }

    @Test
    void deleteProgress_shouldCallDelete() {
        when(progressRepository.findById(1L)).thenReturn(Optional.of(sampleProgress));

        progressService.deleteProgress(1L);

        verify(progressRepository, times(1)).delete(sampleProgress);
    }
}
