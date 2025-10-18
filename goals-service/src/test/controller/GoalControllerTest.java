package com.mentelibre.goals_service.controller;

import com.fasterxml.jackson.databind.ObjectMapper;
import com.mentelibre.goals_service.model.Goal;
import com.mentelibre.goals_service.service.GoalService;

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
 * Pruebas unitarias para GoalController.
 * Se utiliza MockMvc para simular peticiones HTTP y Mockito para simular la capa de servicio.
 */
@WebMvcTest(GoalController.class)
class GoalControllerTest {

    @Autowired
    private MockMvc mockMvc; // Simula peticiones HTTP

    @MockBean
    private GoalService goalService; // Se simula el servicio

    @Autowired
    private ObjectMapper objectMapper; // Para convertir objetos a JSON

    private Goal sampleGoal;

    @BeforeEach
    void setUp() {
        // Creamos un objeto Goal de ejemplo
        sampleGoal = new Goal();
        sampleGoal.setId(1L);
        sampleGoal.setTitle("Registrar emociones");
        sampleGoal.setDescription("Seguir registro diario de emociones");
        sampleGoal.setStartDate(LocalDate.of(2025, 10, 5));
        sampleGoal.setEndDate(LocalDate.of(2025, 10, 12));
        sampleGoal.setCompleted(false);
        sampleGoal.setUserId(1L);
    }

    @Test
    @DisplayName("POST /api/goals - Crear meta")
    void testCreateGoal() throws Exception {
        when(goalService.createGoal(any(Goal.class))).thenReturn(sampleGoal);

        mockMvc.perform(post("/api/goals")
                .contentType(MediaType.APPLICATION_JSON)
                .content(objectMapper.writeValueAsString(sampleGoal)))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.id").value(sampleGoal.getId()))
                .andExpect(jsonPath("$.title").value("Registrar emociones"));
    }

    @Test
    @DisplayName("GET /api/goals - Obtener todas las metas")
    void testGetAllGoals() throws Exception {
        List<Goal> goals = Arrays.asList(sampleGoal);
        when(goalService.findAllGoals()).thenReturn(goals);

        mockMvc.perform(get("/api/goals"))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.length()").value(goals.size()))
                .andExpect(jsonPath("$[0].title").value("Registrar emociones"));
    }

    @Test
    @DisplayName("GET /api/goals/{id} - Obtener meta por ID")
    void testGetGoalById() throws Exception {
        when(goalService.findGoalById(1L)).thenReturn(sampleGoal);

        mockMvc.perform(get("/api/goals/{id}", 1L))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.id").value(1))
                .andExpect(jsonPath("$.title").value("Registrar emociones"));
    }

    @Test
    @DisplayName("PUT /api/goals/{id} - Actualizar meta")
    void testUpdateGoal() throws Exception {
        Goal updatedGoal = new Goal();
        updatedGoal.setId(1L);
        updatedGoal.setTitle("Registrar emociones actualizado");
        updatedGoal.setDescription("Descripción actualizada");
        updatedGoal.setStartDate(sampleGoal.getStartDate());
        updatedGoal.setEndDate(sampleGoal.getEndDate());
        updatedGoal.setCompleted(true);
        updatedGoal.setUserId(1L);

        when(goalService.updateGoal(any(Long.class), any(Goal.class))).thenReturn(updatedGoal);

        mockMvc.perform(put("/api/goals/{id}", 1L)
                .contentType(MediaType.APPLICATION_JSON)
                .content(objectMapper.writeValueAsString(updatedGoal)))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.title").value("Registrar emociones actualizado"))
                .andExpect(jsonPath("$.completed").value(true));
    }

    @Test
    @DisplayName("DELETE /api/goals/{id} - Eliminar meta")
    void testDeleteGoal() throws Exception {
        doNothing().when(goalService).deleteGoal(1L);

        mockMvc.perform(delete("/api/goals/{id}", 1L))
                .andExpect(status().isNoContent());
    }

    @Test
    @DisplayName("GET /api/goals/user/{userId} - Obtener metas por usuario")
    void testGetGoalsByUserId() throws Exception {
        List<Goal> userGoals = Arrays.asList(sampleGoal);
        when(goalService.findGoalsByUserId(1L)).thenReturn(userGoals);

        mockMvc.perform(get("/api/goals/user/{userId}", 1L))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.length()").value(1))
                .andExpect(jsonPath("$[0].userId").value(1));
    }

    @Test
    @DisplayName("GET /api/goals/user/{userId}/incomplete - Obtener metas incompletas por usuario")
    void testGetIncompleteGoalsByUserId() throws Exception {
        List<Goal> incompleteGoals = Arrays.asList(sampleGoal);
        when(goalService.findIncompleteGoalsByUserId(1L)).thenReturn(incompleteGoals);

        mockMvc.perform(get("/api/goals/user/{userId}/incomplete", 1L))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.length()").value(1))
                .andExpect(jsonPath("$[0].completed").value(false));
    }

    @Test
    @DisplayName("GET /api/goals/search?title=... - Buscar metas por título")
    void testSearchGoalsByTitle() throws Exception {
        List<Goal> searchResults = Arrays.asList(sampleGoal);
        when(goalService.searchGoalsByTitle("Registrar")).thenReturn(searchResults);

        mockMvc.perform(get("/api/goals/search")
                .param("title", "Registrar"))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.length()").value(1))
                .andExpect(jsonPath("$[0].title").value("Registrar emociones"));
    }
}
