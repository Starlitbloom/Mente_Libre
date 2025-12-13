package com.mentelibre.virtualpet_service.model;

import java.time.LocalDateTime;

import org.hibernate.annotations.CreationTimestamp;
import org.hibernate.annotations.UpdateTimestamp;

import io.swagger.v3.oas.annotations.media.Schema;
import jakarta.persistence.*;
import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.NotNull;
import jakarta.validation.constraints.Size;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@Entity
@Table(name = "pet_items")
@NoArgsConstructor
@AllArgsConstructor
@Schema(description = "Ítem de personalización para la mascota (ropa, fondo, accesorio, etc.)")
public class PetItem {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    @Schema(description = "Identificador único del ítem", example = "10")
    private Long id;

    @NotBlank
    @Column(nullable = false, length = 80)
    @Schema(description = "Nombre del ítem", example = "Gorro de Navidad")
    private String name;

    @NotBlank
    @Column(nullable = false, length = 30)
    @Schema(description = "Tipo de ítem", example = "ROPA")
    private String type; 
    // ROPA, FONDO, ACCESORIO

    @NotBlank
    @Column(nullable = false, length = 30)
    @Schema(description = "Slot donde se equipa el ítem", example = "CABEZA")
    private String slot; 
    // CABEZA, CUERPO, FONDO, ESPECIAL...

    @NotNull
    @Column(nullable = false)
    @Schema(description = "Precio del ítem en puntos", example = "50")
    private Integer price;

    @Size(max = 255)
    @Schema(description = "URL o clave de recurso de la imagen del ítem")
    private String imageUrl;

    @Schema(description = "Indica si el ítem está activo/disponible en la tienda")
    @Column(nullable = false)
    private boolean active = true;

    @CreationTimestamp
    @Column(updatable = false)
    private LocalDateTime createdAt;

    @UpdateTimestamp
    private LocalDateTime updatedAt;
}
