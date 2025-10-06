package com.mentelibre.user_service.controller;

import static org.mockito.Mockito.when;
import static org.mockito.ArgumentMatchers.any;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.get;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.post;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.delete;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.status;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.jsonPath;

import java.util.Arrays;
import java.util.List;

import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.web.servlet.WebMvcTest;
import org.springframework.boot.test.autoconfigure.web.servlet.AutoConfigureMockMvc;
import org.springframework.boot.test.mock.mockito.MockBean;
import org.springframework.security.test.context.support.WithMockUser;
import org.springframework.test.web.servlet.MockMvc;

import com.mentelibre.user_service.model.UserProfile;
import com.mentelibre.user_service.service.UserProfileService;

@WebMvcTest(controllers = UserProfileController.class)
@AutoConfigureMockMvc(addFilters = false)
public class UserProfileControllerTest {

    @MockBean
    private UserProfileService userProfileService;

    @Autowired
    private MockMvc mockMvc;

    @Test // Prueba: obtener todos los perfiles
    void obtenerPerfiles_returnOKAndJson() throws Exception {
        UserProfile perfil1 = new UserProfile();
        perfil1.setId(1L);
        perfil1.setUserId(100L);
        perfil1.setTelefono("111111111");

        UserProfile perfil2 = new UserProfile();
        perfil2.setId(2L);
        perfil2.setUserId(101L);
        perfil2.setTelefono("222222222");

        List<UserProfile> perfiles = Arrays.asList(perfil1, perfil2);
        when(userProfileService.obtenerUserProfile()).thenReturn(perfiles);

        mockMvc.perform(get("/api/v1/usuario_perfil/perfiles"))
               .andExpect(status().isOk())
               .andExpect(jsonPath("$[0].userId").value(100))
               .andExpect(jsonPath("$[1].telefono").value("222222222"));
    }

    @Test // Prueba: crear perfil exitoso
    void crearPerfil_returnCreatedAndJson() throws Exception {
        UserProfile entrada = new UserProfile();
        entrada.setUserId(123L);
        entrada.setTelefono("999999999");

        UserProfile salida = new UserProfile();
        salida.setId(1L);
        salida.setUserId(123L);
        salida.setTelefono("999999999");

        when(userProfileService.crearPerfil(any(UserProfile.class))).thenReturn(salida);

        mockMvc.perform(post("/api/v1/usuario_perfil/perfiles")
                .contentType("application/json")
                .content(new com.fasterxml.jackson.databind.ObjectMapper().writeValueAsString(entrada)))
               .andExpect(status().isCreated())
               .andExpect(jsonPath("$.id").value(1))
               .andExpect(jsonPath("$.telefono").value("999999999"));
    }

    @Test // Prueba: obtener perfil por ID
    @WithMockUser(username = "111", roles = {"CLIENTE"})
    void obtenerPerfilPorId_returnOKAndJson() throws Exception {
        UserProfile perfil = new UserProfile();
        perfil.setId(10L);
        perfil.setUserId(111L);
        perfil.setTelefono("123456789");

        when(userProfileService.obtenerUserProfilePorId(10L)).thenReturn(perfil);

        mockMvc.perform(get("/api/v1/usuario_perfil/perfiles/10"))
            .andExpect(status().isOk())
            .andExpect(jsonPath("$.userId").value(111))
            .andExpect(jsonPath("$.telefono").value("123456789"));
    }

    @Test // Prueba: eliminar perfil por ID
    void eliminarPerfil_returnOKMessage() throws Exception {
        when(userProfileService.eliminarUserProfile(7L)).thenReturn("Perfil eliminado");

        mockMvc.perform(delete("/api/v1/usuario_perfil/perfiles/7"))
               .andExpect(status().isOk())
               .andExpect(jsonPath("$").value("Perfil eliminado"));
    }


    @Test // Prueba: crear perfil inválido
    void crearPerfil_datosInvalidos_returnBadRequest() throws Exception {
        UserProfile entrada = new UserProfile(); // Sin userId ni teléfono

        when(userProfileService.crearPerfil(any(UserProfile.class)))
            .thenThrow(new RuntimeException("El ID del usuario (userId) es obligatorio"));

        mockMvc.perform(post("/api/v1/usuario_perfil/perfiles")
                .contentType("application/json")
                .content(new com.fasterxml.jackson.databind.ObjectMapper().writeValueAsString(entrada)))
               .andExpect(status().isBadRequest())
               .andExpect(jsonPath("$").value("El ID del usuario (userId) es obligatorio"));
    }

    @Test // Prueba: obtener perfil inexistente
    void obtenerPerfilPorId_noExiste_returnNotFound() throws Exception {
        when(userProfileService.obtenerUserProfilePorId(99L))
            .thenThrow(new RuntimeException("Perfil de usuario no encontrado"));

        mockMvc.perform(get("/api/v1/usuario_perfil/perfiles/99"))
            .andExpect(status().isNotFound())
            .andExpect(jsonPath("$").value("Perfil de usuario no encontrado"));
    }

}