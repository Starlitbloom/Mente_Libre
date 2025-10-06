package com.mentelibre.evaluation_service.model;

import jakarta.persistence.*;
import java.time.LocalDate;
import java.util.List;

import com.fasterxml.jackson.annotation.JsonIgnore;

import lombok.Data;

@Entity
@Table(name = "resultados_evaluacion")
@Data
public class EvaluationResult {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    private Long userId; // viene del auth-service

    @ManyToOne
    @JoinColumn(name = "evaluation_id")
    private Evaluation evaluation;

    private LocalDate fechaRealizacion;
    
    private double puntaje; // si aplica

    @OneToMany(mappedBy = "evaluationResult", cascade = CascadeType.ALL)
    @JsonIgnore // No se incluiran los usuarios cuando se hagan las consultas
    private List<Answer> respuestas;
}