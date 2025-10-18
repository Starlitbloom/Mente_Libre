package com.mentelibre.goals_service.service;

import com.mentelibre.goals_service.model.Achievement;
import com.mentelibre.goals_service.model.Goal;
import com.mentelibre.goals_service.repository.AchievementRepository;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.mockito.Mockito;

import java.time.LocalDate;
import java.util.List;
import java.util.Optional;

import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.Mockito.*;

/**
 * Tests unitarios para AchievementService.
 */
class AchievementServiceTest {

    private AchievementRepository achievementRepository;
    private AchievementService achievementService;
    private Achievement sampleAchievement;
    private Goal sampleGoal;

    @BeforeEach
    void setUp() {
        achievementRepository = Mockito.mock(AchievementRepository.class);
        achievementService = new AchievementService(achievementRepository);

        sampleGoal = new Goal();
        sampleGoal.setId(1L);

        sampleAchievement = new Achievement();
        sampleAchievement.setId(1L);
        sampleAchievement.setName("7 días completados");
        sampleAchievement.setAchievedDate(LocalDate.now());
        sampleAchievement.setGoal(sampleGoal);
    }

    @Test
    void createAchievement_shouldReturnAchievement() {
        when(achievementRepository.save(sampleAchievement)).thenReturn(sampleAchievement);

        Achievement created = achievementService.createAchievement(sampleAchievement);

        assertEquals("7 días completados", created.getName());
        verify(achievementRepository, times(1)).save(sampleAchievement);
    }

    @Test
    void findAchievementById_shouldReturnAchievement() {
        when(achievementRepository.findById(1L)).thenReturn(Optional.of(sampleAchievement));

        Achievement achievement = achievementService.findAchievementById(1L);

        assertNotNull(achievement);
        assertEquals(1L, achievement.getId());
    }

    @Test
    void findAllAchievements_shouldReturnList() {
        when(achievementRepository.findAll()).thenReturn(List.of(sampleAchievement));

        List<Achievement> list = achievementService.findAllAchievements();

        assertEquals(1, list.size());
    }

    @Test
    void updateAchievement_shouldUpdateName() {
        Achievement updated = new Achievement();
        updated.setName("Nuevo logro");

        when(achievementRepository.findById(1L)).thenReturn(Optional.of(sampleAchievement));
        when(achievementRepository.save(any(Achievement.class))).thenReturn(updated);

        Achievement result = achievementService.updateAchievement(1L, updated);

        assertEquals("Nuevo logro", result.getName());
    }

    @Test
    void deleteAchievement_shouldCallDelete() {
        when(achievementRepository.findById(1L)).thenReturn(Optional.of(sampleAchievement));

        achievementService.deleteAchievement(1L);

        verify(achievementRepository, times(1)).delete(sampleAchievement);
    }
}
