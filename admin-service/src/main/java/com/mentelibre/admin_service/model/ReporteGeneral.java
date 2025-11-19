package com.mentelibre.admin_service.model;

import jakarta.persistence.*;

import java.time.LocalDateTime;
import java.util.Map;

import org.hibernate.annotations.CreationTimestamp;

import io.swagger.v3.oas.annotations.media.Schema;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

@Entity
@Table(name = "reportes_generales")
@Data
@NoArgsConstructor
@AllArgsConstructor
@Schema(description = "Reporte general del sistema sin datos sensibles")
public class ReporteGeneral {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;  // ← NECESARIO PARA JPA

    private int totalUsuarios;
    private int usuariosBloqueados;

    @ElementCollection
    private Map<String, Integer> objetivosPorServicio;

    @CreationTimestamp
    @Column(updatable = false)
    private LocalDateTime creadoEn;

}
