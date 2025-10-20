package com.mentelibre.admin_service.service;

import com.mentelibre.admin_service.model.Rol;
import com.mentelibre.admin_service.model.UserAdmin;
import com.mentelibre.admin_service.repository.RolRepository;
import com.mentelibre.admin_service.repository.UserAdminRepository;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;

import java.util.List;
import java.util.Optional;

import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.Mockito.*;

class UserAdminServiceTest {

    private UserAdminService userAdminService;
    private UserAdminRepository userAdminRepository;
    private RolRepository rolRepository;

    private UserAdmin user;
    private Rol rol;

    @BeforeEach
    void setUp() {
        userAdminRepository = mock(UserAdminRepository.class);
        rolRepository = mock(RolRepository.class);
        userAdminService = new UserAdminService();
        userAdminService.userAdminRepository = userAdminRepository;
        userAdminService.rolRepository = rolRepository;

        rol = new Rol(1L, "ADMIN", null, null, null);
        user = new UserAdmin(1L, "usuario1", "user@mail.com", false, rol, null, null);
    }

    @Test
    void testListarUsuarios() {
        when(userAdminRepository.findAll()).thenReturn(List.of(user));

        List<UserAdmin> result = userAdminService.listarUsuarios();

        assertEquals(1, result.size());
        verify(userAdminRepository, times(1)).findAll();
    }

    @Test
    void testBuscarUsuarioPorId() {
        when(userAdminRepository.findById(1L)).thenReturn(Optional.of(user));

        Optional<UserAdmin> result = userAdminService.buscarUsuarioPorId(1L);

        assertTrue(result.isPresent());
        assertEquals("usuario1", result.get().getUsername());
        verify(userAdminRepository, times(1)).findById(1L);
    }

    @Test
    void testBloquearUsuario() {
        when(userAdminRepository.findById(1L)).thenReturn(Optional.of(user));
        when(userAdminRepository.save(user)).thenReturn(user);

        UserAdmin result = userAdminService.bloquearUsuario(1L);

        assertTrue(result.isBloqueado());
        verify(userAdminRepository, times(1)).findById(1L);
        verify(userAdminRepository, times(1)).save(user);
    }

    @Test
    void testDesbloquearUsuario() {
        user.setBloqueado(true);
        when(userAdminRepository.findById(1L)).thenReturn(Optional.of(user));
        when(userAdminRepository.save(user)).thenReturn(user);

        UserAdmin result = userAdminService.desbloquearUsuario(1L);

        assertFalse(result.isBloqueado());
        verify(userAdminRepository, times(1)).findById(1L);
        verify(userAdminRepository, times(1)).save(user);
    }

    @Test
    void testEliminarUsuario() {
        when(userAdminRepository.existsById(1L)).thenReturn(true);
        doNothing().when(userAdminRepository).deleteById(1L);

        userAdminService.eliminarUsuario(1L);

        verify(userAdminRepository, times(1)).existsById(1L);
        verify(userAdminRepository, times(1)).deleteById(1L);
    }

    @Test
    void testAsignarRol() {
        UserAdmin nuevoUser = new UserAdmin(1L, "usuario1", "user@mail.com", false, null, null, null);
        Rol nuevoRol = new Rol(2L, "USER", null, null, null);

        when(userAdminRepository.findById(1L)).thenReturn(Optional.of(nuevoUser));
        when(rolRepository.findById(2L)).thenReturn(Optional.of(nuevoRol));
        when(userAdminRepository.save(nuevoUser)).thenReturn(nuevoUser);

        UserAdmin result = userAdminService.asignarRol(1L, 2L);

        assertEquals(2L, result.getRol().getId());
        verify(userAdminRepository, times(1)).findById(1L);
        verify(rolRepository, times(1)).findById(2L);
        verify(userAdminRepository, times(1)).save(nuevoUser);
    }
}
