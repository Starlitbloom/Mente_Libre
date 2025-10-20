package com.mentelibre.admin_service.controller;

import com.mentelibre.admin_service.model.Rol;
import com.mentelibre.admin_service.model.UserAdmin;
import com.mentelibre.admin_service.service.UserAdminService;
import com.mentelibre.admin_service.config.JwtRequestFilter;
import com.fasterxml.jackson.databind.ObjectMapper;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.web.servlet.WebMvcTest;
import org.springframework.boot.test.mock.mockito.MockBean;
import org.springframework.http.MediaType;
import org.springframework.test.web.servlet.MockMvc;

import java.util.List;

import static org.mockito.Mockito.*;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.*;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.*;

@WebMvcTest(UserAdminController.class)
class UserAdminControllerTest {

    @Autowired
    private MockMvc mockMvc;

    @MockBean
    private UserAdminService userAdminService;

    @MockBean
    private JwtRequestFilter jwtRequestFilter;

    @Autowired
    private ObjectMapper objectMapper;

    private UserAdmin usuario;

    @BeforeEach
    void setUp() {
        Rol rol = new Rol(1L, "ADMIN", null, null, null);
        usuario = new UserAdmin(1L, "usuario1", "usuario1@mail.com", false, rol, null, null);
    }

    @Test
    void testListarUsuarios() throws Exception {
        when(userAdminService.listarUsuarios()).thenReturn(List.of(usuario));

        mockMvc.perform(get("/api/admin/users"))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.length()").value(1))
                .andExpect(jsonPath("$[0].username").value("usuario1"));

        verify(userAdminService, times(1)).listarUsuarios();
    }

    @Test
    void testBloquearUsuario() throws Exception {
        UserAdmin bloqueado = new UserAdmin(1L, "usuario1", "usuario1@mail.com", true, usuario.getRol(), null, null);
        when(userAdminService.bloquearUsuario(1L)).thenReturn(bloqueado);

        mockMvc.perform(put("/api/admin/users/bloquear/1"))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.bloqueado").value(true));

        verify(userAdminService, times(1)).bloquearUsuario(1L);
    }

    @Test
    void testDesbloquearUsuario() throws Exception {
        UserAdmin desbloqueado = new UserAdmin(1L, "usuario1", "usuario1@mail.com", false, usuario.getRol(), null, null);
        when(userAdminService.desbloquearUsuario(1L)).thenReturn(desbloqueado);

        mockMvc.perform(put("/api/admin/users/desbloquear/1"))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.bloqueado").value(false));

        verify(userAdminService, times(1)).desbloquearUsuario(1L);
    }

    @Test
    void testEliminarUsuario() throws Exception {
        doNothing().when(userAdminService).eliminarUsuario(1L);

        mockMvc.perform(delete("/api/admin/users/1"))
                .andExpect(status().isNoContent());

        verify(userAdminService, times(1)).eliminarUsuario(1L);
    }

    @Test
    void testAsignarRol() throws Exception {
        Rol nuevoRol = new Rol(2L, "USER", null, null, null);
        UserAdmin conRol = new UserAdmin(1L, "usuario1", "usuario1@mail.com", false, nuevoRol, null, null);
        when(userAdminService.asignarRol(1L, 2L)).thenReturn(conRol);

        mockMvc.perform(put("/api/admin/users/rol/1/2"))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.rol.id").value(2));

        verify(userAdminService, times(1)).asignarRol(1L, 2L);
    }
}
