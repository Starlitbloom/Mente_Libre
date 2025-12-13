package com.mentelibre.virtualpet_service.model;

import java.time.LocalDateTime;

import org.hibernate.annotations.CreationTimestamp;
import org.hibernate.annotations.UpdateTimestamp;

import io.swagger.v3.oas.annotations.media.Schema;
import jakarta.persistence.*;
import jakarta.validation.constraints.NotNull;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@Entity
@Table(
    name = "user_pet_items",
    uniqueConstraints = {
        @UniqueConstraint(columnNames = {"user_id", "item_id"})
    }
)
@NoArgsConstructor
@AllArgsConstructor
@Schema(description = "Relación entre un usuario y los ítems de mascota que posee")
public class UserPetItem {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    @Schema(description = "Identificador único de la relación", example = "100")
    private Long id;

    @NotNull
    @Column(name = "user_id", nullable = false)
    @Schema(description = "ID del usuario dueño del ítem", example = "5")
    private Long userId;

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "item_id", nullable = false)
    @Schema(description = "Ítem adquirido por el usuario")
    private PetItem item;

    @Column(nullable = false)
    @Schema(description = "Indica si este ítem está equipado en la mascota", example = "true")
    private boolean equipped = false;

    @CreationTimestamp
    @Column(updatable = false)
    private LocalDateTime acquiredAt;

    @UpdateTimestamp
    private LocalDateTime updatedAt;
}