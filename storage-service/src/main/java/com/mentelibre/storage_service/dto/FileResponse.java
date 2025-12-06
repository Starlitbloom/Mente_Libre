package com.mentelibre.storage_service.dto;

import com.mentelibre.storage_service.model.FileCategory;
import io.swagger.v3.oas.annotations.media.Schema;
import lombok.AllArgsConstructor;
import lombok.Data;

@Data
@AllArgsConstructor
@Schema(description = "Respuesta que se envía al frontend al subir un archivo")
public class FileResponse {

    @Schema(description = "ID del archivo", example = "1")
    private Long id;

    @Schema(description = "Nombre original del archivo", example = "foto_perfil.png")
    private String fileName;

    @Schema(description = "URL pública del archivo", example = "/uploads/foto_perfil.png")
    private String url;

    @Schema(description = "Categoría del archivo", example = "PROFILE")
    private FileCategory category;
}
