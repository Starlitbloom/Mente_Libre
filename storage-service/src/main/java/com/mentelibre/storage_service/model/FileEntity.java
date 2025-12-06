package com.mentelibre.storage_service.model;

import java.time.LocalDateTime;

import jakarta.persistence.*;
import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.NotNull;
import jakarta.validation.constraints.Size;

import org.hibernate.annotations.CreationTimestamp;
import org.hibernate.annotations.UpdateTimestamp;

import io.swagger.v3.oas.annotations.media.Schema;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@Entity
@Table(name = "archivos")
@NoArgsConstructor
@AllArgsConstructor
@Schema(description = "Archivo subido por un usuario, puede ser foto de perfil o diario de gratitud")
public class FileEntity {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    @Schema(description = "Identificador único del archivo", example = "1")
    private Long id;

    @Column(nullable = false)
    @NotBlank(message = "El nombre del archivo no puede estar vacío")
    @Size(max = 255)
    @Schema(description = "Nombre original del archivo", example = "foto_perfil.png")
    private String fileName;

    @Column(nullable = false)
    @Schema(description = "Tipo MIME del archivo", example = "image/png")
    private String fileType;

    @Column(nullable = false)
    @Schema(description = "Ruta o URL del archivo en almacenamiento", example = "/uploads/foto_perfil.png")
    private String url;

    @Column(nullable = false)
    @NotNull(message = "El ownerId es obligatorio")
    @Schema(description = "ID del usuario dueño del archivo", example = "5")
    private Long ownerId;

    @Enumerated(EnumType.STRING)
    @Column(nullable = false)
    @Schema(description = "Categoría del archivo", example = "PROFILE")
    private FileCategory category;

    @CreationTimestamp
    @Column(updatable = false)
    @Schema(description = "Fecha y hora de subida del archivo")
    private LocalDateTime createdAt;

    @UpdateTimestamp
    @Schema(description = "Fecha y hora de última modificación del archivo")
    private LocalDateTime updatedAt;
}
