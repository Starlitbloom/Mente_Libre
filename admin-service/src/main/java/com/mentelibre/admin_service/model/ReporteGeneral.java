package com.mentelibre.admin_service.model;

import java.util.Map;

import io.swagger.v3.oas.annotations.media.Schema;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@NoArgsConstructor
@AllArgsConstructor
@Schema(description = "Reporte general del sistema sin datos sensibles")
public class ReporteGeneral {
    @Schema(description = "Cantidad total de usuarios", example = "150")
    private int totalUsuarios;

    @Schema(description = "Cantidad de usuarios bloqueados", example = "5")
    private int usuariosBloqueados;

    @Schema(description = "Cantidad de objetivos por microservicio", example = "{\"Goals Service\":10,\"Evaluation Service\":5}")
    private Map<String, Integer> objetivosPorServicio; 
}
