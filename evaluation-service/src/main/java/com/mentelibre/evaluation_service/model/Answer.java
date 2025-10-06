package com.mentelibre.evaluation_service.model;

import jakarta.persistence.*;
import jakarta.validation.constraints.NotBlank;
import lombok.Data;

@Entity
@Table(name = "respuestas")
@Data
public class Answer {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @ManyToOne
    @JoinColumn(name = "question_id", nullable = false)
    private Question pregunta;

    @ManyToOne
    @JoinColumn(name = "evaluation_result_id", nullable = false)
    private EvaluationResult evaluationResult;

    @NotBlank
    @Column(name = "respuesta_texto", length = 500)
    private String respuestaTexto; // si la respuesta es texto libre

    @Column(name = "respuesta_valor")
    private Double respuestaValor; // si la respuesta tiene puntaje, ej: opción múltiple

}