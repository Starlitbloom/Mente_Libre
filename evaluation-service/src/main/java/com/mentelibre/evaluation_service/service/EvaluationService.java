package com.mentelibre.evaluation_service.service;

import java.time.LocalDate;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import com.mentelibre.evaluation_service.model.Answer;
import com.mentelibre.evaluation_service.model.Evaluation;
import com.mentelibre.evaluation_service.model.EvaluationResult;
import com.mentelibre.evaluation_service.model.Question;
import com.mentelibre.evaluation_service.repository.AnswerRepository;
import com.mentelibre.evaluation_service.repository.EvaluationRepository;
import com.mentelibre.evaluation_service.repository.EvaluationResultRepository;
import com.mentelibre.evaluation_service.repository.QuestionRepository;
import com.mentelibre.evaluation_service.webclient.AuthClient;
import com.mentelibre.evaluation_service.webclient.EmotionClient;
import com.mentelibre.evaluation_service.webclient.NotificationClient;


import jakarta.transaction.Transactional;

@Service
@Transactional
public class EvaluationService {

    @Autowired
    private EvaluationRepository evaluationRepository;

    @Autowired
    private QuestionRepository questionRepository;

    @Autowired
    private EvaluationResultRepository evaluationResultRepository;

    @Autowired
    private AnswerRepository answerRepository;

    @Autowired
    private AuthClient authClient; // WebClient para AuthService
    @Autowired
    private EmotionClient emotionClient;
    @Autowired
    private NotificationClient notificationClient;

    // ------------------- EVALUATION -------------------
    public Evaluation crearEvaluation(Evaluation evaluation, String authHeader) {
        if (evaluation.getUserId() == null) {
            throw new RuntimeException("El ID del usuario es obligatorio");
        }

        // Validar existencia y autorización del usuario en AuthService usando token
        if (!authClient.usuarioExiste(evaluation.getUserId(), authHeader)) {
            throw new RuntimeException("El usuario no existe o no está autorizado");
        }

        if (evaluation.getTitulo() == null || evaluation.getTitulo().isBlank()) {
            throw new RuntimeException("El título es obligatorio");
        }

        if (evaluation.getDescripcion() == null || evaluation.getDescripcion().isBlank()) {
            throw new RuntimeException("La descripción es obligatoria");
        }

        evaluation.setFechaCreacion(LocalDate.now());
        evaluation.setActivo(true);
        return evaluationRepository.save(evaluation);
    }

    public List<Evaluation> obtenerEvaluations() {
        return evaluationRepository.findAll();
    }

    public Evaluation obtenerEvaluationPorId(Long id) {
        return evaluationRepository.findById(id)
                .orElseThrow(() -> new RuntimeException("Evaluación no encontrada con ID: " + id));
    }

    public Evaluation actualizarEvaluation(Long id, Evaluation nuevosDatos) {
        Evaluation evaluation = obtenerEvaluationPorId(id);

        if (nuevosDatos.getTitulo() != null) evaluation.setTitulo(nuevosDatos.getTitulo());
        if (nuevosDatos.getDescripcion() != null) evaluation.setDescripcion(nuevosDatos.getDescripcion());
        if (nuevosDatos.getTipo() != null) evaluation.setTipo(nuevosDatos.getTipo());
        evaluation.setActivo(nuevosDatos.isActivo());

        return evaluationRepository.save(evaluation);
    }

    public String eliminarEvaluation(Long id) {
        Evaluation evaluation = obtenerEvaluationPorId(id);
        evaluationRepository.delete(evaluation);
        return "Evaluación eliminada correctamente";
    }

    // ------------------- EVALUATION RESULT -------------------
    public EvaluationResult crearEvaluationResult(EvaluationResult result, String authHeader) {
        if (result.getUserId() == null) {
            throw new RuntimeException("El ID del usuario es obligatorio");
        }

        if (!authClient.usuarioExiste(result.getUserId(), authHeader)) {
            throw new RuntimeException("El usuario no existe o no está autorizado");
        }

        if (result.getEvaluation() == null || result.getEvaluation().getId() == null) {
            throw new RuntimeException("La evaluación asociada es obligatoria");
        }

        result.setFechaRealizacion(LocalDate.now());
        EvaluationResult savedResult = evaluationResultRepository.save(result);

        // ----------------- NOTIFICACIÓN -----------------
        try {
            Map<String, Object> notification = new HashMap<>();
            notification.put("userId", result.getUserId());
            notification.put("message", "Has completado la evaluación: " + result.getEvaluation().getTitulo());
            notification.put("type", "INFO");
            notification.put("sent", false); // opcional, depende de tu servicio

            notificationClient.createNotification(notification, authHeader);
        } catch (Exception e) {
            System.out.println("No se pudo enviar la notificación: " + e.getMessage());
        }
                return savedResult;
            }

    public List<EvaluationResult> obtenerResultadosPorUserId(Long userId) {
        return evaluationResultRepository.findByUserId(userId);
    }

    // ------------------- QUESTIONS -------------------
    public List<Question> obtenerPreguntasPorEvaluationId(Long evaluationId) {
        return questionRepository.findByEvaluationId(evaluationId);
    }

    // ------------------- ANSWERS -------------------
    public Answer crearAnswer(Answer answer) {
        if (answer.getPregunta() == null || answer.getPregunta().getId() == null) {
            throw new RuntimeException("La pregunta es obligatoria");
        }

        if (answer.getEvaluationResult() == null || answer.getEvaluationResult().getId() == null) {
            throw new RuntimeException("El resultado de evaluación es obligatorio");
        }

        if ((answer.getRespuestaTexto() == null || answer.getRespuestaTexto().isBlank())
                && answer.getRespuestaValor() == null) {
            throw new RuntimeException("La respuesta no puede estar vacía");
        }

        return answerRepository.save(answer);
    }

    public List<Answer> obtenerRespuestasPorEvaluationResultId(Long resultId) {
        return answerRepository.findByEvaluationResultId(resultId);
    }

    public Object obtenerResumenEmocionesUsuario(Long userId, String authHeader) {
        // Llama al EmotionClient y obtiene el resumen semanal de emociones
        return emotionClient.obtenerResumenSemanal(userId, authHeader);
    }

    
}