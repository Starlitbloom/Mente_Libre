package com.mentelibre.auth_service.service;


import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertThrows;
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.when;

import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;

import com.mentelibre.auth_service.model.Rol;
import com.mentelibre.auth_service.repository.RolRepository;


// Se habilita la inicialización automatica de los mocks
@ExtendWith(MockitoExtension.class)
public class RolServiceTest {
    @Mock
    private RolRepository repository;

    @InjectMocks
    private RolService service;

    @Test // Prueba: Crear rol exitoso
    void crear_CrearRol() {
        Rol rol = new Rol();
        rol.setNombre("Profesor");

        when(repository.existsByNombre("Profesor")).thenReturn(false);
        when(repository.save(rol)).thenReturn(rol);

        Rol resultado = service.crearRol(rol);

        assertEquals("Profesor", resultado.getNombre());
        verify(repository).save(rol);

    }

    @Test // Prueba: Rol duplicado
    void crear_RolDuplicado() {
    Rol rol = new Rol();
    rol.setNombre("Administrador");

    when(repository.existsByNombre("Administrador")).thenReturn(true);

    RuntimeException ex = assertThrows(RuntimeException.class, () -> {
        service.crearRol(rol);
    });

    assertEquals("Ya existe un rol con este nombre", ex.getMessage());
    }

    @Test // Prueba: Rol vacio
    void crearRol_nombreInvalido() {
    Rol rol = new Rol();
    rol.setNombre("  "); // Espacios vacíos

    RuntimeException ex = assertThrows(RuntimeException.class, () -> {
        service.crearRol(rol);
    });

    assertEquals("El nombre del rol es obligatorio", ex.getMessage());
    }
}
