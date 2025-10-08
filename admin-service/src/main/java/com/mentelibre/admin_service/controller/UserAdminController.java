package com.mentelibre.admin_service.controller;

import com.mentelibre.admin_service.model.UserAdmin;
import com.mentelibre.admin_service.service.UserAdminService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@RestController
@RequestMapping("/api/admin/users")
public class UserAdminController {

    @Autowired
    private UserAdminService userAdminService;

    // Listar todos los usuarios
    @GetMapping
    //@PreAuthorize("hasRole('ADMIN')")
    public ResponseEntity<List<UserAdmin>> listarUsuarios() {
        return ResponseEntity.ok(userAdminService.listarUsuarios());
    }

    // Bloquear usuario
    @PutMapping("/bloquear/{id}")
    //@PreAuthorize("hasRole('ADMIN')")
    public ResponseEntity<UserAdmin> bloquearUsuario(@PathVariable Long id) {
        return ResponseEntity.ok(userAdminService.bloquearUsuario(id));
    }

    // Desbloquear usuario
    @PutMapping("/desbloquear/{id}")
    //@PreAuthorize("hasRole('ADMIN')")
    public ResponseEntity<UserAdmin> desbloquearUsuario(@PathVariable Long id) {
        return ResponseEntity.ok(userAdminService.desbloquearUsuario(id));
    }

    // Eliminar usuario
    @DeleteMapping("/{id}")
    //@PreAuthorize("hasRole('ADMIN')")
    public ResponseEntity<Void> eliminarUsuario(@PathVariable Long id) {
        userAdminService.eliminarUsuario(id);
        return ResponseEntity.noContent().build();
    }

    // Asignar rol a usuario
    @PutMapping("/rol/{idUsuario}/{idRol}")
    //@PreAuthorize("hasRole('ADMIN')")
    public ResponseEntity<UserAdmin> asignarRol(@PathVariable Long idUsuario, @PathVariable Long idRol) {
        return ResponseEntity.ok(userAdminService.asignarRol(idUsuario, idRol));
    }
}
