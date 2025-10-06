package com.mentelibre.evaluation_service.controller;

import java.util.List;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.web.bind.annotation.*;

import com.mentelibre.evaluation_service.model.Answer;
import com.mentelibre.evaluation_service.model.Evaluation;
import com.mentelibre.evaluation_service.model.EvaluationResult;
import com.mentelibre.evaluation_service.model.Question;
import com.mentelibre.evaluation_service.service.EvaluationService;

import io.swagger.v3.oas.annotations.parameters.RequestBody;

@RestController
@RequestMapping("/api/v1/evaluations")
public class EvaluationController {

    @Autowired
    private EvaluationService evaluationService;

    // ------------------- EVALUATION -------------------
    @PreAuthorize("hasRole('ADMINISTRADOR')")
    @GetMapping
    public ResponseEntity<List<Evaluation>> listarEvaluaciones() {
        List<Evaluation> evaluations = evaluationService.obtenerEvaluations();
        return ResponseEntity.ok(evaluations);
    }

    @PreAuthorize("hasRole('ADMINISTRADOR')")
    @PostMapping
    public ResponseEntity<?> crearEvaluation(@RequestBody Evaluation evaluation,
                                            @RequestHeader("Authorization") String authHeader) {
        try {
            Evaluation nueva = evaluationService.crearEvaluation(evaluation, authHeader);
            return ResponseEntity.status(HttpStatus.CREATED).body(nueva);
        } catch (RuntimeException e) {
            return ResponseEntity.status(HttpStatus.BAD_REQUEST).body(e.getMessage());
        }
    }

    @PreAuthorize("hasAnyRole('ADMINISTRADOR', 'CLIENTE')")
    @GetMapping("/{id}")
    public ResponseEntity<?> obtenerEvaluationPorId(@PathVariable Long id) {
        try {
            Evaluation evaluation = evaluationService.obtenerEvaluationPorId(id);
            return ResponseEntity.ok(evaluation);
        } catch (RuntimeException e) {
            return ResponseEntity.status(HttpStatus.NOT_FOUND).body(e.getMessage());
        }
    }

    @PreAuthorize("hasRole('ADMINISTRADOR')")
    @PutMapping("/{id}")
    public ResponseEntity<?> actualizarEvaluation(@PathVariable Long id, @RequestBody Evaluation evaluation) {
        try {
            Evaluation actualizado = evaluationService.actualizarEvaluation(id, evaluation);
            return ResponseEntity.ok(actualizado);
        } catch (RuntimeException e) {
            return ResponseEntity.status(HttpStatus.NOT_FOUND).body(e.getMessage());
        }
    }

    @PreAuthorize("hasRole('ADMINISTRADOR')")
    @DeleteMapping("/{id}")
    public ResponseEntity<?> eliminarEvaluation(@PathVariable Long id) {
        try {
            return ResponseEntity.ok(evaluationService.eliminarEvaluation(id));
        } catch (RuntimeException e) {
            return ResponseEntity.status(HttpStatus.NOT_FOUND).body(e.getMessage());
        }
    }

    // ------------------- EVALUATION RESULT -------------------
    @PreAuthorize("hasRole('CLIENTE')")
    @PostMapping("/results")
    public ResponseEntity<?> crearEvaluationResult(@RequestBody EvaluationResult result,
                                                @RequestHeader("Authorization") String authHeader) {
        try {
            EvaluationResult nuevo = evaluationService.crearEvaluationResult(result, authHeader);
            return ResponseEntity.status(HttpStatus.CREATED).body(nuevo);
        } catch (RuntimeException e) {
            return ResponseEntity.status(HttpStatus.BAD_REQUEST).body(e.getMessage());
        }
    }

    @PreAuthorize("hasAnyRole('ADMINISTRADOR', 'CLIENTE')")
    @GetMapping("/results/user/{userId}")
    public ResponseEntity<?> obtenerResultadosPorUserId(@PathVariable Long userId) {
        Authentication auth = SecurityContextHolder.getContext().getAuthentication();
        Long authUserId = Long.parseLong(auth.getName());
        boolean esAdmin = auth.getAuthorities().stream()
                .anyMatch(a -> a.getAuthority().equals("ROLE_ADMINISTRADOR"));

        if (!esAdmin && !authUserId.equals(userId)) {
            return ResponseEntity.status(HttpStatus.FORBIDDEN)
                    .body("No puedes ver resultados de otro usuario");
        }

        List<EvaluationResult> resultados = evaluationService.obtenerResultadosPorUserId(userId);
        return ResponseEntity.ok(resultados);
    }

    // ------------------- QUESTIONS -------------------
    @PreAuthorize("hasAnyRole('ADMINISTRADOR', 'CLIENTE')")
    @GetMapping("/{evaluationId}/questions")
    public ResponseEntity<List<Question>> obtenerPreguntasPorEvaluationId(@PathVariable Long evaluationId) {
        return ResponseEntity.ok(evaluationService.obtenerPreguntasPorEvaluationId(evaluationId));
    }

    // ------------------- ANSWERS -------------------
    @PreAuthorize("hasRole('CLIENTE')")
    @PostMapping("/answers")
    public ResponseEntity<?> crearAnswer(@RequestBody Answer answer) {
        try {
            Answer nuevo = evaluationService.crearAnswer(answer);
            return ResponseEntity.status(HttpStatus.CREATED).body(nuevo);
        } catch (RuntimeException e) {
            return ResponseEntity.status(HttpStatus.BAD_REQUEST).body(e.getMessage());
        }
    }

    @PreAuthorize("hasAnyRole('ADMINISTRADOR', 'CLIENTE')")
    @GetMapping("/answers/result/{resultId}")
    public ResponseEntity<?> obtenerRespuestasPorEvaluationResultId(@PathVariable Long resultId) {
        Authentication auth = SecurityContextHolder.getContext().getAuthentication();
        Long authUserId = Long.parseLong(auth.getName());
        boolean esAdmin = auth.getAuthorities().stream()
                .anyMatch(a -> a.getAuthority().equals("ROLE_ADMINISTRADOR"));

        // Solo el dueño del resultado o administrador
        List<Answer> respuestas = evaluationService.obtenerRespuestasPorEvaluationResultId(resultId);
        if (!esAdmin) {
            boolean esPropietario = respuestas.stream()
                    .allMatch(a -> a.getEvaluationResult().getUserId().equals(authUserId));
            if (!esPropietario) {
                return ResponseEntity.status(HttpStatus.FORBIDDEN)
                        .body("No puedes ver respuestas de un resultado que no es tuyo");
            }
        }

        return ResponseEntity.ok(respuestas);
    }
}