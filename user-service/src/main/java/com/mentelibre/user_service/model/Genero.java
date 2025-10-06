package com.mentelibre.user_service.model;

import io.swagger.v3.oas.annotations.media.Schema;
import jakarta.persistence.*;
import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.Size;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@Entity
@Table(name = "generos")
@NoArgsConstructor
@AllArgsConstructor

@Schema(description = "Género de preferencia del usuario, utilizado para personalización y recomendaciones")
public class Genero {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @Column(nullable = false, unique = true, length = 50)
    @NotBlank(message = "El nombre del género no puede estar vacío")
    @Size(max = 50, message = "El nombre del género no puede exceder los 50 caracteres")
    private String nombre;
}