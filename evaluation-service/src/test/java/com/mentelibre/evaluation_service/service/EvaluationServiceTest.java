package com.mentelibre.evaluation_service.service;

import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.Mockito.*;
import java.util.List;
import java.util.Optional;

import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;

import com.mentelibre.evaluation_service.model.Evaluation;
import com.mentelibre.evaluation_service.model.EvaluationResult;
import com.mentelibre.evaluation_service.repository.EvaluationRepository;
import com.mentelibre.evaluation_service.repository.EvaluationResultRepository;
import com.mentelibre.evaluation_service.webclient.AuthClient;

@ExtendWith(MockitoExtension.class)
public class EvaluationServiceTest {

    @Mock
    private EvaluationRepository evaluationRepository;

    @Mock
    private EvaluationResultRepository evaluationResultRepository;

    @Mock
    private AuthClient authClient;

    @InjectMocks
    private EvaluationService evaluationService;

    // ------------------- CREAR EVALUATION -------------------
    @Test
    void crearEvaluation_valida() {
        Evaluation e = new Evaluation();
        e.setUserId(99L);
        e.setTitulo("Test");
        e.setDescripcion("Descripción test");

        when(authClient.usuarioExiste(99L, "token123")).thenReturn(true);
        when(evaluationRepository.save(any(Evaluation.class))).thenReturn(e);

        Evaluation result = evaluationService.crearEvaluation(e, "token123");

        assertEquals(99L, result.getUserId());
        assertEquals("Test", result.getTitulo());
        assertEquals("Descripción test", result.getDescripcion());
        assertNotNull(result.getFechaCreacion());
        assertTrue(result.isActivo());
    }

    @Test
    void crearEvaluation_usuarioNoExiste() {
        Evaluation e = new Evaluation();
        e.setUserId(100L);
        e.setTitulo("Test");
        e.setDescripcion("Desc");

        when(authClient.usuarioExiste(100L, "token123")).thenReturn(false);

        RuntimeException ex = assertThrows(RuntimeException.class, () -> {
            evaluationService.crearEvaluation(e, "token123");
        });

        assertEquals("El usuario no existe o no está autorizado", ex.getMessage());
    }

    // ------------------- ACTUALIZAR EVALUATION -------------------
    @Test
    void actualizarEvaluation_valido() {
        Evaluation existente = new Evaluation();
        existente.setId(1L);
        existente.setTitulo("Antiguo");
        existente.setDescripcion("Antigua desc");

        Evaluation nuevosDatos = new Evaluation();
        nuevosDatos.setTitulo("Nuevo");
        nuevosDatos.setDescripcion("Nueva desc");
        nuevosDatos.setActivo(false);

        when(evaluationRepository.findById(1L)).thenReturn(Optional.of(existente));
        when(evaluationRepository.save(any(Evaluation.class))).thenReturn(existente);

        Evaluation actualizado = evaluationService.actualizarEvaluation(1L, nuevosDatos);

        assertEquals("Nuevo", actualizado.getTitulo());
        assertEquals("Nueva desc", actualizado.getDescripcion());
        assertFalse(actualizado.isActivo());
    }

    @Test
    void actualizarEvaluation_noExiste() {
        Evaluation nuevosDatos = new Evaluation();
        when(evaluationRepository.findById(1L)).thenReturn(Optional.empty());

        RuntimeException ex = assertThrows(RuntimeException.class, () -> {
            evaluationService.actualizarEvaluation(1L, nuevosDatos);
        });

        assertEquals("Evaluación no encontrada con ID: 1", ex.getMessage());
    }

    // ------------------- ELIMINAR EVALUATION -------------------
    @Test
    void eliminarEvaluation_valido() {
        Evaluation e = new Evaluation();
        e.setId(1L);

        when(evaluationRepository.findById(1L)).thenReturn(Optional.of(e));
        doNothing().when(evaluationRepository).delete(e);

        String result = evaluationService.eliminarEvaluation(1L);
        assertEquals("Evaluación eliminada correctamente", result);
        verify(evaluationRepository).delete(e);
    }

    @Test
    void eliminarEvaluation_noExiste() {
        when(evaluationRepository.findById(1L)).thenReturn(Optional.empty());

        RuntimeException ex = assertThrows(RuntimeException.class, () -> {
            evaluationService.eliminarEvaluation(1L);
        });

        assertEquals("Evaluación no encontrada con ID: 1", ex.getMessage());
    }

    // ------------------- OBTENER EVALUATION POR ID -------------------
    @Test
    void obtenerEvaluationPorId_valido() {
        Evaluation e = new Evaluation();
        e.setId(2L);

        when(evaluationRepository.findById(2L)).thenReturn(Optional.of(e));

        Evaluation result = evaluationService.obtenerEvaluationPorId(2L);
        assertEquals(2L, result.getId());
    }

    @Test
    void obtenerEvaluationPorId_noExiste() {
        when(evaluationRepository.findById(2L)).thenReturn(Optional.empty());

        RuntimeException ex = assertThrows(RuntimeException.class, () -> {
            evaluationService.obtenerEvaluationPorId(2L);
        });

        assertEquals("Evaluación no encontrada con ID: 2", ex.getMessage());
    }

    // ------------------- LISTAR EVALUATIONS -------------------
    @Test
    void obtenerEvaluations_retornaLista() {
        when(evaluationRepository.findAll()).thenReturn(List.of(new Evaluation(), new Evaluation()));

        List<Evaluation> result = evaluationService.obtenerEvaluations();
        assertEquals(2, result.size());
    }

    // ------------------- CREAR EVALUATION RESULT -------------------
    @Test
    void crearEvaluationResult_valido() {
        EvaluationResult r = new EvaluationResult();
        r.setUserId(50L);
        Evaluation eval = new Evaluation();
        eval.setId(1L);
        r.setEvaluation(eval);

        when(authClient.usuarioExiste(50L, "tokenABC")).thenReturn(true);
        when(evaluationResultRepository.save(any(EvaluationResult.class))).thenReturn(r);

        EvaluationResult result = evaluationService.crearEvaluationResult(r, "tokenABC");

        assertEquals(50L, result.getUserId());
        assertEquals(1L, result.getEvaluation().getId());
        assertNotNull(result.getFechaRealizacion());
    }

    @Test
    void crearEvaluationResult_usuarioNoExiste() {
        EvaluationResult r = new EvaluationResult();
        r.setUserId(51L);
        Evaluation eval = new Evaluation();
        eval.setId(1L);
        r.setEvaluation(eval);

        when(authClient.usuarioExiste(51L, "tokenABC")).thenReturn(false);

        RuntimeException ex = assertThrows(RuntimeException.class, () -> {
            evaluationService.crearEvaluationResult(r, "tokenABC");
        });

        assertEquals("El usuario no existe o no está autorizado", ex.getMessage());
    }
}