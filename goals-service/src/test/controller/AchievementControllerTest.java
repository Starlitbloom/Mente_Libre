package com.mentelibre.goals_service.controller;

import com.fasterxml.jackson.databind.ObjectMapper;
import com.mentelibre.goals_service.model.Achievement;
import com.mentelibre.goals_service.service.AchievementService;

import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.web.servlet.WebMvcTest;
import org.springframework.boot.test.mock.mockito.MockBean;

import org.springframework.http.MediaType;
import org.springframework.test.web.servlet.MockMvc;

import java.time.LocalDate;
import java.util.Arrays;
import java.util.List;

import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.when;
import static org.mockito.Mockito.doNothing;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.*;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.*;

/**
 * Pruebas unitarias para AchievementController.
 * Se usa MockMvc para simular peticiones HTTP y Mockito para simular el servicio.
 */
@WebMvcTest(AchievementController.class)
class AchievementControllerTest {

    @Autowired
    private MockMvc mockMvc;

    @MockBean
    private AchievementService achievementService;

    @Autowired
    private ObjectMapper objectMapper;

    private Achievement sampleAchievement;

    @BeforeEach
    void setUp() {
        sampleAchievement = new Achievement();
        sampleAchievement.setId(1L);
        sampleAchievement.setName("7 días consecutivos completados");
        sampleAchievement.setDescription("El usuario completó el seguimiento diario por una semana");
        sampleAchievement.setAchievedDate(LocalDate.of(2025, 10, 12));
        sampleAchievement.setGoal(null); // Se puede simular un Goal si quieres
    }

    @Test
    @DisplayName("POST /api/achievements - Crear logro")
    void testCreateAchievement() throws Exception {
        when(achievementService.createAchievement(any(Achievement.class))).thenReturn(sampleAchievement);

        mockMvc.perform(post("/api/achievements")
                .contentType(MediaType.APPLICATION_JSON)
                .content(objectMapper.writeValueAsString(sampleAchievement)))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.id").value(sampleAchievement.getId()))
                .andExpect(jsonPath("$.name").value("7 días consecutivos completados"));
    }

    @Test
    @DisplayName("GET /api/achievements - Obtener todos los logros")
    void testGetAllAchievements() throws Exception {
        List<Achievement> achievements = Arrays.asList(sampleAchievement);
        when(achievementService.findAllAchievements()).thenReturn(achievements);

        mockMvc.perform(get("/api/achievements"))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.length()").value(achievements.size()));
    }

    @Test
    @DisplayName("GET /api/achievements/{id} - Obtener logro por ID")
    void testGetAchievementById() throws Exception {
        when(achievementService.findAchievementById(1L)).thenReturn(sampleAchievement);

        mockMvc.perform(get("/api/achievements/{id}", 1L))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.id").value(1))
                .andExpect(jsonPath("$.name").value("7 días consecutivos completados"));
    }

    @Test
    @DisplayName("PUT /api/achievements/{id} - Actualizar logro")
    void testUpdateAchievement() throws Exception {
        Achievement updated = new Achievement();
        updated.setId(1L);
        updated.setName("Logro actualizado");
        updated.setDescription("Descripción actualizada");
        updated.setAchievedDate(LocalDate.of(2025, 10, 13));

        when(achievementService.updateAchievement(any(Long.class), any(Achievement.class))).thenReturn(updated);

        mockMvc.perform(put("/api/achievements/{id}", 1L)
                .contentType(MediaType.APPLICATION_JSON)
                .content(objectMapper.writeValueAsString(updated)))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.name").value("Logro actualizado"));
    }

    @Test
    @DisplayName("DELETE /api/achievements/{id} - Eliminar logro")
    void testDeleteAchievement() throws Exception {
        doNothing().when(achievementService).deleteAchievement(1L);

        mockMvc.perform(delete("/api/achievements/{id}", 1L))
                .andExpect(status().isNoContent());
    }

    @Test
    @DisplayName("GET /api/achievements/goal/{goalId} - Obtener logros de una meta")
    void testGetAchievementsByGoalId() throws Exception {
        List<Achievement> achievements = Arrays.asList(sampleAchievement);
        when(achievementService.findAchievementsByGoalId(1L)).thenReturn(achievements);

        mockMvc.perform(get("/api/achievements/goal/{goalId}", 1L))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.length()").value(1));
    }

}
