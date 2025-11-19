package com.mentelibre.admin_service.controller;

import com.mentelibre.admin_service.model.Rol;
import com.mentelibre.admin_service.service.RolService;
import com.fasterxml.jackson.databind.ObjectMapper;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.autoconfigure.security.servlet.SecurityAutoConfiguration;
import org.springframework.boot.test.autoconfigure.web.servlet.WebMvcTest;
import org.springframework.boot.test.mock.mockito.MockBean;
import org.springframework.http.MediaType;
import org.springframework.test.web.servlet.MockMvc;

import java.util.List;

import static org.mockito.Mockito.*;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.*;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.*;

@WebMvcTest(
    controllers = RolController.class,
    excludeAutoConfiguration = {SecurityAutoConfiguration.class} // deshabilita seguridad
)
class RolControllerTest {

    @Autowired
    private MockMvc mockMvc;

    @MockBean
    private RolService rolService;

    @Autowired
    private ObjectMapper objectMapper;

    private Rol rol1;

    @BeforeEach
    void setUp() {
        rol1 = new Rol(1L, "ADMIN", null, null, null);
    }

    @Test
    void testListarRoles() throws Exception {
        when(rolService.listarRoles()).thenReturn(List.of(rol1));

        mockMvc.perform(get("/api/admin/roles"))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.length()").value(1))
                .andExpect(jsonPath("$[0].nombre").value("ADMIN"));

        verify(rolService, times(1)).listarRoles();
    }

    @Test
    void testCrearRol() throws Exception {
        Rol nuevoRol = new Rol(null, "USER", null, null, null);
        Rol creado = new Rol(2L, "USER", null, null, null);
        when(rolService.crearRol(any(Rol.class))).thenReturn(creado);

        mockMvc.perform(post("/api/admin/roles")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(objectMapper.writeValueAsString(nuevoRol)))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.id").value(2))
                .andExpect(jsonPath("$.nombre").value("USER"));

        verify(rolService, times(1)).crearRol(any(Rol.class));
    }

    @Test
    void testEliminarRol() throws Exception {
        doNothing().when(rolService).eliminarRol(1L);

        mockMvc.perform(delete("/api/admin/roles/1"))
                .andExpect(status().isNoContent());

        verify(rolService, times(1)).eliminarRol(1L);
    }
}
