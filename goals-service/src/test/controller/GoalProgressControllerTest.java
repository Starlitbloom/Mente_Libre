package com.mentelibre.goals_service.controller;

import com.fasterxml.jackson.databind.ObjectMapper;
import com.mentelibre.goals_service.model.GoalProgress;
import com.mentelibre.goals_service.service.GoalProgressService;

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
 * Pruebas unitarias para GoalProgressController.
 * MockMvc simula peticiones HTTP y Mockito simula el servicio.
 */
@WebMvcTest(GoalProgressController.class)
class GoalProgressControllerTest {

    @Autowired
    private MockMvc mockMvc;

    @MockBean
    private GoalProgressService progressService;

    @Autowired
    private ObjectMapper objectMapper;

    private GoalProgress sampleProgress;

    @BeforeEach
    void setUp() {
        sampleProgress = new GoalProgress();
        sampleProgress.setId(1L);
        sampleProgress.setDate(LocalDate.of(2025, 10, 6));
        sampleProgress.setDone(true);
        sampleProgress.setGoal(null); // Se puede simular un Goal
    }

    @Test
    @DisplayName("POST /api/progress - Crear progreso")
    void testCreateProgress() throws Exception {
        when(progressService.createProgress(any(GoalProgress.class))).thenReturn(sampleProgress);

        mockMvc.perform(post("/api/progress")
                .contentType(MediaType.APPLICATION_JSON)
                .content(objectMapper.writeValueAsString(sampleProgress)))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.id").value(1))
                .andExpect(jsonPath("$.done").value(true));
    }

    @Test
    @DisplayName("GET /api/progress - Obtener todos los progresos")
    void testGetAllProgress() throws Exception {
        List<GoalProgress> progresses = Arrays.asList(sampleProgress);
        when(progressService.findAllProgressByGoalId(1L)).thenReturn(progresses);

        mockMvc.perform(get("/api/progress/goal/{goalId}", 1L))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.length()").value(1));
    }

    @Test
    @DisplayName("GET /api/progress/{id} - Obtener progreso por ID")
    void testGetProgressById() throws Exception {
        when(progressService.findProgressById(1L)).thenReturn(sampleProgress);

        mockMvc.perform(get("/api/progress/{id}", 1L))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.id").value(1))
                .andExpect(jsonPath("$.done").value(true));
    }

    @Test
    @DisplayName("PUT /api/progress/{id} - Actualizar progreso")
    void testUpdateProgress() throws Exception {
        GoalProgress updated = new GoalProgress();
        updated.setId(1L);
        updated.setDone(false);
        updated.setDate(LocalDate.of(2025, 10, 7));

        when(progressService.updateProgress(any(Long.class), any(GoalProgress.class))).thenReturn(updated);

        mockMvc.perform(put("/api/progress/{id}", 1L)
                .contentType(MediaType.APPLICATION_JSON)
                .content(objectMapper.writeValueAsString(updated)))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.done").value(false));
    }

    @Test
    @DisplayName("DELETE /api/progress/{id} - Eliminar progreso")
    void testDeleteProgress() throws Exception {
        doNothing().when(progressService).deleteProgress(1L);

        mockMvc.perform(delete("/api/progress/{id}", 1L))
                .andExpect(status().isNoContent());
    }

}
