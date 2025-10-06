package com.mentelibre.evaluation_service.model;

import com.fasterxml.jackson.annotation.JsonIgnoreProperties;

import jakarta.persistence.*;
import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.Size;
import lombok.Data;

@Entity
@Table(name = "preguntas")
@Data
public class Question {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @NotBlank
    @Size(max = 200)
    private String texto;

    @NotBlank
    @Size(max = 50)
    private String tipo; // ejemplo: "opción múltiple", "texto libre"

    @ManyToOne
    @JoinColumn(name = "evaluation_id")
    @JsonIgnoreProperties("preguntas") // ignora la lista de preguntas de Evaluation
    private Evaluation evaluation;
}

