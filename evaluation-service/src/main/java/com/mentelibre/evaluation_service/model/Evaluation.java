package com.mentelibre.evaluation_service.model;

import java.time.LocalDate;
import java.util.List;

import com.fasterxml.jackson.annotation.JsonIgnore;

import io.swagger.v3.oas.annotations.media.Schema;
import jakarta.persistence.CascadeType;
import jakarta.persistence.Column;
import jakarta.persistence.Entity;
import jakarta.persistence.GeneratedValue;
import jakarta.persistence.GenerationType;
import jakarta.persistence.Id;
import jakarta.persistence.OneToMany;
import jakarta.persistence.Table;
import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.NotNull;
import jakarta.validation.constraints.Size;
import lombok.Data;

@Entity
@Table(name = "evaluaciones")
@Data
public class Evaluation {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    @Schema(description = "ID único de la evaluación", example = "1")
    private Long id;

    @NotBlank(message = "El título no puede estar vacío")
    @Size(max = 100, message = "El título no puede tener más de 100 caracteres")
    @Column(name = "titulo", nullable = false, length = 100)
    @Schema(description = "Título de la evaluación", example = "Bienestar emocional 2025")
    private String titulo;

    @NotNull(message = "El ID del usuario es obligatorio")
    @Schema(description = "ID del usuario inscrito", example = "100")
    private Long userId;

    @NotBlank(message = "La descripción no puede estar vacía")
    @Size(max = 500, message = "La descripción no puede tener más de 500 caracteres")
    @Column(name = "descripcion", nullable = false, length = 500)
    @Schema(description = "Descripción detallada de la evaluación", example = "Esta es una evaluación de salud mental.")
    private String descripcion;

    @NotNull
    @Column(name = "fecha_creacion", nullable = false)
    private LocalDate fechaCreacion;

    @Column(name = "activo", nullable = false)
    private boolean activo = true;

    @NotBlank
    @Column(name = "tipo", nullable = false, length = 50)
    private String tipo;

    @OneToMany(mappedBy = "evaluation", cascade = CascadeType.ALL)
    @JsonIgnore // No se incluiran los usuarios cuando se hagan las consultas
    private List<Question> preguntas;

    

}