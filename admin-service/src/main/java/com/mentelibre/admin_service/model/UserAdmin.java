package com.mentelibre.admin_service.model;

import java.time.LocalDateTime;

import org.hibernate.annotations.CreationTimestamp;
import org.hibernate.annotations.UpdateTimestamp;

import com.fasterxml.jackson.annotation.JsonIgnoreProperties;

import io.swagger.v3.oas.annotations.media.Schema;
import jakarta.persistence.*;
import jakarta.validation.constraints.Email;
import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.Size;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@Entity
@Table(name = "users")
@NoArgsConstructor
@AllArgsConstructor
@Schema(description = "Usuario del sistema gestionado por AdminService")
public class UserAdmin {
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @Column(nullable = false, unique = true, length = 30)
    @NotBlank(message = "El nombre de usuario no puede estar vacío")
    @Size(min = 4, message = "El nombre de usuario debe tener al menos 4 caracteres")
    private String username;

    @Column(nullable = false, unique = true, length = 50)
    @NotBlank(message = "El email no puede estar vacío")
    @Email(message = "Debe ingresar un correo válido")
    private String email;

    @Column(nullable = false)
    private Boolean bloqueado = false;

    @ManyToOne
    @JoinColumn(name = "rol_id")
    @JsonIgnoreProperties("usuarios")
    private Rol rol;

    @CreationTimestamp
    @Column(updatable = false)
    private LocalDateTime creadoEn;

    @UpdateTimestamp
    private LocalDateTime actualizadoEn;
}

