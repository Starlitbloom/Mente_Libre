package com.mentelibre.storage_service.controller;

import com.mentelibre.storage_service.model.FileCategory;
import com.mentelibre.storage_service.dto.FileResponse;
import com.mentelibre.storage_service.service.StorageService;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.media.Content;
import io.swagger.v3.oas.annotations.media.Schema;
import io.swagger.v3.oas.annotations.responses.ApiResponse;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.multipart.MultipartFile;

import java.io.IOException;
import java.util.List;

@RestController
@RequestMapping("/api/v1/storage")
public class StorageController {

    @Autowired
    private StorageService storageService;

    // ============================================================
    // Endpoint para subir un archivo
    @Operation(
            summary = "Subir archivo",
            description = "Permite subir un archivo de imagen como foto de perfil o diario de gratitud.",
            responses = {
                    @ApiResponse(responseCode = "201", description = "Archivo subido exitosamente",
                            content = @Content(schema = @Schema(implementation = FileResponse.class))),
                    @ApiResponse(responseCode = "400", description = "Datos inválidos"),
                    @ApiResponse(responseCode = "401", description = "No autorizado"),
            }
    )
    @PreAuthorize("hasAnyRole('USER','ADMIN')")
    @PostMapping("/upload")
    public ResponseEntity<?> uploadFile(
            @RequestParam("file") MultipartFile file,
            @RequestParam("category") FileCategory category) {

        try {
            Long userId = (Long) SecurityContextHolder.getContext().getAuthentication().getPrincipal();
            FileResponse response = storageService.store(file, userId, category);
            return ResponseEntity.status(HttpStatus.CREATED).body(response);

        } catch (RuntimeException | IOException e) {
            return ResponseEntity.badRequest().body(e.getMessage());
        }
    }

    // ============================================================
    // Endpoint para listar todos los archivos
    @Operation(
            summary = "Listar mis archivos",
            description = "Devuelve todos los archivos subidos por el usuario autenticado.",
            responses = {
                    @ApiResponse(responseCode = "200", description = "Lista obtenida correctamente",
                            content = @Content(schema = @Schema(implementation = FileResponse.class))),
                    @ApiResponse(responseCode = "401", description = "No autorizado")
            }
    )
    @PreAuthorize("hasAnyRole('USER','ADMIN')")
    @GetMapping("/me")
    public ResponseEntity<?> myFiles() {

        Long userId = (Long) SecurityContextHolder.getContext().getAuthentication().getPrincipal();

        List<FileResponse> list = storageService.getFilesByOwner(userId);

        return list.isEmpty()
                ? ResponseEntity.noContent().build()
                : ResponseEntity.ok(list);
    }

    // ============================================================
    // // Endpoint para actualizar un archivo
    @Operation(
            summary = "Actualizar archivo",
            description = "Reemplaza el archivo existente con uno nuevo. Solo el dueño puede actualizarlo.",
            responses = {
                    @ApiResponse(responseCode = "200", description = "Archivo actualizado exitosamente",
                            content = @Content(schema = @Schema(implementation = FileResponse.class))),
                    @ApiResponse(responseCode = "400", description = "Datos inválidos"),
                    @ApiResponse(responseCode = "403", description = "No autorizado para modificar el archivo"),
                    @ApiResponse(responseCode = "404", description = "Archivo no encontrado")
            }
    )
    @PreAuthorize("hasRole('USER')")
    @PutMapping("/{fileId}")
    public ResponseEntity<?> updateFile(
            @PathVariable Long fileId,
            @RequestParam("file") MultipartFile file) {

        try {
            Long userId = (Long) SecurityContextHolder.getContext().getAuthentication().getPrincipal();
            FileResponse updated = storageService.updateFile(fileId, file, userId);
            return ResponseEntity.ok(updated);

        } catch (RuntimeException | IOException e) {
            return ResponseEntity.badRequest().body(e.getMessage());
        }
    }

    // ============================================================
    // Endpoint para eliminar un archivo
    @Operation(
            summary = "Eliminar archivo",
            description = "Elimina un archivo si pertenece al usuario autenticado o si es administrador.",
            responses = {
                    @ApiResponse(responseCode = "200", description = "Archivo eliminado correctamente"),
                    @ApiResponse(responseCode = "403", description = "No autorizado"),
                    @ApiResponse(responseCode = "404", description = "Archivo no encontrado")
            }
    )
    @PreAuthorize("hasAnyRole('USER','ADMIN')")
    @DeleteMapping("/{fileId}")
    public ResponseEntity<?> deleteFile(@PathVariable Long fileId) {

        try {
            Long userId = (Long) SecurityContextHolder.getContext().getAuthentication().getPrincipal();
            storageService.deleteFile(fileId, userId);
            return ResponseEntity.ok("Archivo eliminado correctamente");

        } catch (RuntimeException | IOException e) {
            return ResponseEntity.badRequest().body(e.getMessage());
        }
    }
}
