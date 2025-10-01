package com.mentelibre.auth_service.controller;


import static org.mockito.Mockito.*;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.*;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.*;

import java.util.*;

import com.fasterxml.jackson.databind.ObjectMapper;
import com.mentelibre.auth_service.config.JwtUtil;
import com.mentelibre.auth_service.model.Rol;
import com.mentelibre.auth_service.model.User;
import com.mentelibre.auth_service.repository.UserRepository;
import com.mentelibre.auth_service.service.RolService;
import com.mentelibre.auth_service.service.UserService;

import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.web.servlet.WebMvcTest;
import org.springframework.boot.test.mock.mockito.MockBean;
import org.springframework.http.MediaType;
import org.springframework.security.authentication.AuthenticationManager;
import org.springframework.test.web.servlet.MockMvc;

@WebMvcTest(controllers = AuthController.class, excludeAutoConfiguration = {
    org.springframework.boot.autoconfigure.security.servlet.SecurityAutoConfiguration.class
})

class AuthControllerTest {

    @Autowired
    private MockMvc mockMvc;

    @MockBean
    private UserService userService;

    @MockBean
    private RolService rolService;

    @MockBean
    private JwtUtil jwtUtil;

    @MockBean
    private AuthenticationManager authenticationManager;

    @MockBean
    private UserRepository userRepository;

    private final ObjectMapper mapper = new ObjectMapper();

    @Test
    void obtenerRoles_returnOK() throws Exception {
        Rol r1 = new Rol(); r1.setId(1L); r1.setNombre("Admin");
        Rol r2 = new Rol(); r2.setId(2L); r2.setNombre("Estudiante");
        when(rolService.obtenerRolOrdenPorId()).thenReturn(List.of(r1, r2));

        mockMvc.perform(get("/api/v1/roles"))
               .andExpect(status().isOk())
               .andExpect(jsonPath("$[0].nombre").value("Admin"))
               .andExpect(jsonPath("$[1].nombre").value("Estudiante"));
    }

    @Test
    void crearRol_returnCreated() throws Exception {
        Rol input = new Rol(); input.setNombre("Editor");
        Rol output = new Rol(); output.setId(3L); output.setNombre("Editor");

        when(rolService.crearRol(any(Rol.class))).thenReturn(output);

        mockMvc.perform(post("/api/v1/roles")
                .contentType(MediaType.APPLICATION_JSON)
                .content(mapper.writeValueAsString(input)))
                .andExpect(status().isCreated())
                .andExpect(jsonPath("$.id").value(3L))
                .andExpect(jsonPath("$.nombre").value("Editor"));
    }

    @Test
    void eliminarRol_returnOK() throws Exception {
        when(rolService.eliminarRol(1L)).thenReturn("Rol eliminado");

        mockMvc.perform(delete("/api/v1/roles/1"))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$").value("Rol eliminado"));
    }

    @Test
    void crearUsuario_sinUsuariosPrevios_returnCreated() throws Exception {
        Rol rol = new Rol(); rol.setId(1L); rol.setNombre("Admin");
        User user = new User(null, "Juan", "juan@mail.com", "1234", null, null, rol);

        when(userService.obtenerUser()).thenReturn(Collections.emptyList());
        when(userService.crearUser(any(), any(), any(), any())).thenReturn(user);

        mockMvc.perform(post("/api/v1/users")
                .contentType(MediaType.APPLICATION_JSON)
                .content(mapper.writeValueAsString(user)))
                .andExpect(status().isCreated())
                .andExpect(jsonPath("$.username").value("Juan"))
                .andExpect(jsonPath("$.email").value("juan@mail.com"));
    }

    @Test
    void obtenerUserPorId_returnOK() throws Exception {
        User user = new User(); user.setId(1L); user.setUsername("Ana"); user.setEmail("ana@mail.com");
        when(userService.obtenerUserPorId(1L)).thenReturn(user);

        mockMvc.perform(get("/api/v1/users/1"))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.username").value("Ana"))
                .andExpect(jsonPath("$.email").value("ana@mail.com"));
    }

    @Test
    void obtenerUserPorId_notFound_return404() throws Exception {
        when(userService.obtenerUserPorId(5L)).thenThrow(new RuntimeException("Usuario no encontrado Id: 5"));

        mockMvc.perform(get("/api/v1/users/5"))
                .andExpect(status().isNotFound())
                .andExpect(jsonPath("$").value("Usuario no encontrado Id: 5"));
    }
}
