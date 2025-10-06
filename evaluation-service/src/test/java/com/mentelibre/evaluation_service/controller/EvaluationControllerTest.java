package com.mentelibre.evaluation_service.controller;

import static org.mockito.ArgumentMatchers.any;
import static org.mockito.ArgumentMatchers.anyString;
import static org.mockito.ArgumentMatchers.eq;
import static org.mockito.Mockito.*;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.*;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.*;

import java.util.List;

import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.web.servlet.AutoConfigureMockMvc;
import org.springframework.boot.test.autoconfigure.web.servlet.WebMvcTest;
import org.springframework.boot.test.mock.mockito.MockBean;
import org.springframework.http.MediaType;
import org.springframework.security.test.context.support.WithMockUser;
import org.springframework.test.web.servlet.MockMvc;

import com.mentelibre.evaluation_service.model.Evaluation;
import com.mentelibre.evaluation_service.service.EvaluationService;

@WebMvcTest(controllers = EvaluationController.class)
@AutoConfigureMockMvc(addFilters = false) // <- esto desactiva JwtRequestFilter
public class EvaluationControllerTest {

    @Autowired
    private MockMvc mockMvc;

    @MockBean
    private EvaluationService evaluationService;

    // ------------------- LISTAR EVALUACIONES -------------------
    @Test
    @WithMockUser(username = "admin", roles = {"ADMINISTRADOR"})
    void listarEvaluaciones_returnOK() throws Exception {
        Evaluation e = new Evaluation();
        e.setId(1L);
        e.setTitulo("Evaluación 1");

        when(evaluationService.obtenerEvaluations()).thenReturn(List.of(e));

        mockMvc.perform(get("/api/v1/evaluations"))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$[0].titulo").value("Evaluación 1"));
    }

    // ------------------- OBTENER EVALUACION POR ID -------------------
    @Test
    @WithMockUser(username = "admin", roles = {"ADMINISTRADOR"})
    void obtenerEvaluacionPorId_returnOK() throws Exception {
        Evaluation e = new Evaluation();
        e.setId(1L);
        e.setTitulo("Evaluación 1");

        when(evaluationService.obtenerEvaluationPorId(1L)).thenReturn(e);

        mockMvc.perform(get("/api/v1/evaluations/1"))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.titulo").value("Evaluación 1"));
    }

    // ------------------- CREAR EVALUACION -------------------
    @Test
    @WithMockUser(username = "admin", roles = {"ADMINISTRADOR"})
    void crearEvaluacion_returnCreated() throws Exception {
        Evaluation e = new Evaluation();
        e.setId(1L);
        e.setTitulo("Nueva Eval");

        when(evaluationService.crearEvaluation(any(Evaluation.class), anyString())).thenReturn(e);

        String json = """
            {
              "titulo": "Nueva Eval",
              "descripcion": "Desc"
            }
        """;

        mockMvc.perform(post("/api/v1/evaluations")
                .header("Authorization", "Bearer token")
                .contentType(MediaType.APPLICATION_JSON)
                .content(json))
                .andExpect(status().isCreated())
                .andExpect(jsonPath("$.titulo").value("Nueva Eval"));
    }

    // ------------------- ACTUALIZAR EVALUACION -------------------
    @Test
    @WithMockUser(username = "admin", roles = {"ADMINISTRADOR"})
    void actualizarEvaluacion_returnOK() throws Exception {
        Evaluation e = new Evaluation();
        e.setId(1L);
        e.setTitulo("Actualizado");

        when(evaluationService.actualizarEvaluation(eq(1L), any(Evaluation.class))).thenReturn(e);

        String json = """
            {
              "titulo": "Actualizado",
              "descripcion": "Nueva descripción"
            }
        """;

        mockMvc.perform(put("/api/v1/evaluations/1")
                .header("Authorization", "Bearer token")
                .contentType(MediaType.APPLICATION_JSON)
                .content(json))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.titulo").value("Actualizado"));
    }

    // ------------------- ELIMINAR EVALUACION -------------------
    @Test
    @WithMockUser(username = "admin", roles = {"ADMINISTRADOR"})
    void eliminarEvaluacion_returnOK() throws Exception {
        when(evaluationService.eliminarEvaluation(1L)).thenReturn("Evaluación eliminada correctamente");

        mockMvc.perform(delete("/api/v1/evaluations/1")
                .header("Authorization", "Bearer token"))
                .andExpect(status().isOk())
                .andExpect(content().string("Evaluación eliminada correctamente"));
    }
}