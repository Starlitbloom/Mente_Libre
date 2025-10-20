package com.mentelibre.admin_service.service;

import com.mentelibre.admin_service.model.Rol;
import com.mentelibre.admin_service.repository.RolRepository;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;

import java.util.List;
import java.util.Optional;

import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.Mockito.*;

class RolServiceTest {

    private RolService rolService;
    private RolRepository rolRepository;

    @BeforeEach
    void setUp() {
        rolRepository = mock(RolRepository.class);
        rolService = new RolService();
        rolService.rolRepository = rolRepository;
    }

    @Test
    void testListarRoles() {
        Rol r1 = new Rol(1L, "ADMIN", null, null, null);
        Rol r2 = new Rol(2L, "USER", null, null, null);
        when(rolRepository.findAll()).thenReturn(List.of(r1, r2));

        List<Rol> result = rolService.listarRoles();

        assertEquals(2, result.size());
        verify(rolRepository, times(1)).findAll();
    }

    @Test
    void testBuscarRolPorId() {
        Rol r = new Rol(1L, "ADMIN", null, null, null);
        when(rolRepository.findById(1L)).thenReturn(Optional.of(r));

        Optional<Rol> result = rolService.buscarRolPorId(1L);

        assertTrue(result.isPresent());
        assertEquals("ADMIN", result.get().getNombre());
        verify(rolRepository, times(1)).findById(1L);
    }

    @Test
    void testCrearRol() {
        Rol nuevo = new Rol(null, "USER", null, null, null);
        Rol guardado = new Rol(2L, "USER", null, null, null);
        when(rolRepository.save(nuevo)).thenReturn(guardado);

        Rol result = rolService.crearRol(nuevo);

        assertEquals("USER", result.getNombre());
        assertEquals(2L, result.getId());
        verify(rolRepository, times(1)).save(nuevo);
    }

    @Test
    void testEliminarRol() {
        when(rolRepository.existsById(1L)).thenReturn(true);
        doNothing().when(rolRepository).deleteById(1L);

        rolService.eliminarRol(1L);

        verify(rolRepository, times(1)).existsById(1L);
        verify(rolRepository, times(1)).deleteById(1L);
    }

    @Test
    void testEliminarRolNoExistente() {
        when(rolRepository.existsById(1L)).thenReturn(false);

        Exception exception = assertThrows(RuntimeException.class, () -> rolService.eliminarRol(1L));
        assertEquals("Rol no encontrado", exception.getMessage());
    }
}
