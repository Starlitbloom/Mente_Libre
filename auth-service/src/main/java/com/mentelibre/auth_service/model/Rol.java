package com.mentelibre.auth_service.model;

import java.time.LocalDateTime;
import java.util.List;

import org.hibernate.annotations.CreationTimestamp;
import org.hibernate.annotations.UpdateTimestamp;

import com.fasterxml.jackson.annotation.JsonIgnore;

import jakarta.persistence.CascadeType;
import jakarta.persistence.Column;
import jakarta.persistence.Entity;
import jakarta.persistence.GeneratedValue;
import jakarta.persistence.GenerationType;
import jakarta.persistence.Id;
import jakarta.persistence.OneToMany;
import jakarta.persistence.Table;
import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.Size;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

@Data // Metodos, setters, getters, tostring
@Entity // Se reconoce como una entidad JPA
@Table(name = "roles") // Nombre de la tabla para la base de datos
@NoArgsConstructor // Constructor vacio
@AllArgsConstructor // Constructor con todos los datos

public class Rol {
    @Id // Identificador clave primaria
    @GeneratedValue(strategy = GenerationType.IDENTITY) // Autoincrementable en la base de datos
    private Long id;

    @Column(nullable = false, unique = true, length = 30) // Campo obligatorio en base de datos y debe ser único
    @NotBlank(message = "El nombre del rol no puede estar vacío") // Para no aceptar espacio vacios
    @Size(min = 4, message = "El nombre del rol debe tener al menos 4 caracteres")
    private String nombre;

    @CreationTimestamp // Fecha en la que se creo
    @Column(updatable =  false)
    private LocalDateTime creadoEn;

    @UpdateTimestamp
    private LocalDateTime actualizadoEn; // Fecha en la que se actualizado


    @OneToMany(mappedBy = "rol", cascade = CascadeType.ALL) // Rol tiene muchos usuarios, si se borra un rol se borraran todos los usuario
    @JsonIgnore // No se incluiran los usuarios cuando se hagan las consultas
    private List<User> usuarios; // Lista de todos los usuarios que tienen este rol
}
