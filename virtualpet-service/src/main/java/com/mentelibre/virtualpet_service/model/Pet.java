package com.mentelibre.virtualpet_service.model;

import java.time.LocalDateTime;

import org.hibernate.annotations.CreationTimestamp;
import org.hibernate.annotations.UpdateTimestamp;

import io.swagger.v3.oas.annotations.media.Schema;
import jakarta.persistence.*;
import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.NotNull;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@Entity
@Table(name = "pets")
@NoArgsConstructor
@AllArgsConstructor
@Schema(description = "Mascota virtual asociada a un estudiante")
public class Pet {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    @Schema(description = "Identificador único de la mascota", example = "1")
    private Long id;

    @NotNull
    @Column(nullable = false)
    @Schema(description = "ID del usuario dueño de la mascota", example = "5")
    private Long userId;  // viene del auth-service

    @NotBlank
    @Column(nullable = false, length = 50)
    @Schema(description = "Nombre de la mascota", example = "Nube")
    private String name;

    @NotBlank
    @Column(nullable = false, length = 30)
    @Schema(description = "Tipo de mascota", example = "HAMSTER")
    private String type; // HAMSTER, GATO, PERRO, etc. (string para hacerlo simple)

    @Column(length = 200)
    @Schema(description = "Clave del avatar base de la mascota", example = "avatar_hamster_1")
    private String avatarKey; // ej: cual de los hamsters eligió

    // ---------- SISTEMA DE PUNTOS / NIVEL ----------
    @Column(nullable = false)
    @Schema(description = "Puntos disponibles para canjear", example = "120")
    private int points = 0;

    @Column(nullable = false)
    @Schema(description = "Nivel actual de la mascota", example = "3")
    private int level = 1;

    @Column(nullable = false)
    @Schema(description = "Experiencia acumulada para el siguiente nivel", example = "45")
    private int experience = 0;

    // ---------- ESTADO EMOCIONAL BÁSICO (para IA) ----------
    @Column(nullable = false)
    @Schema(description = "Afinidad con el usuario (0-100)", example = "75")
    private int affinity = 50;

    @Column(nullable = false)
    @Schema(description = "Energía de la mascota (0-100)", example = "80")
    private int energy = 80;

    @CreationTimestamp
    @Column(updatable = false)
    @Schema(description = "Fecha de creación de la mascota")
    private LocalDateTime createdAt;

    @UpdateTimestamp
    @Schema(description = "Fecha de última actualización de la mascota")
    private LocalDateTime updatedAt;
}
