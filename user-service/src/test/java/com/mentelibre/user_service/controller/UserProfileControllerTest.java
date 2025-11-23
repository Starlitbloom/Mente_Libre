package com.mentelibre.user_service.controller;

import static org.mockito.Mockito.when;
import static org.mockito.ArgumentMatchers.any;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.get;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.post;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.delete;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.status;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.jsonPath;

import java.time.LocalDate;
import java.util.Arrays;
import java.util.List;

import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.web.servlet.WebMvcTest;
import org.springframework.boot.test.autoconfigure.web.servlet.AutoConfigureMockMvc;
import org.springframework.boot.test.mock.mockito.MockBean;
import org.springframework.security.test.context.support.WithMockUser;
import org.springframework.test.web.servlet.MockMvc;

import com.fasterxml.jackson.databind.ObjectMapper;
import com.fasterxml.jackson.databind.SerializationFeature;
import com.mentelibre.user_service.dto.UserProfileDTO;
import com.mentelibre.user_service.model.Genero;
import com.mentelibre.user_service.model.UserProfile;
import com.mentelibre.user_service.service.UserProfileService;

@WebMvcTest(controllers = UserProfileController.class)
@AutoConfigureMockMvc(addFilters = false)
public class UserProfileControllerTest {

    @MockBean
    private UserProfileService userProfileService;

    @Autowired
    private MockMvc mockMvc;

    @Test
    void obtenerPerfiles_returnOKAndJson() throws Exception {
        UserProfileDTO perfil1 = new UserProfileDTO();
        perfil1.setUsername("usuario1");
        perfil1.setPhone("111111111");

        UserProfileDTO perfil2 = new UserProfileDTO();
        perfil2.setUsername("usuario2");
        perfil2.setPhone("222222222");

        List<UserProfileDTO> perfiles = Arrays.asList(perfil1, perfil2);
        when(userProfileService.obtenerTodosConAuthData()).thenReturn(perfiles);

        mockMvc.perform(get("/api/v1/usuario_perfil/perfiles"))
            .andExpect(status().isOk())
            .andExpect(jsonPath("$[0].username").value("usuario1"))
            .andExpect(jsonPath("$[1].phone").value("222222222"));
    }


    @Test
    void crearPerfil_returnCreatedAndJson() throws Exception {
        UserProfile entrada = new UserProfile();
        entrada.setUserId(123L);
        entrada.setDireccion("Calle Falsa 123");
        entrada.setFechaNacimiento(LocalDate.of(2000, 1, 1));
        entrada.setNotificaciones(true);
        entrada.setGenero(new Genero(1L, "FEMENINO")); // suponiendo que tienes constructor en Genero

        UserProfile salida = new UserProfile();
        salida.setId(1L);
        salida.setUserId(123L);
        salida.setDireccion("Calle Falsa 123");
        salida.setFechaNacimiento(LocalDate.of(2000, 1, 1));
        salida.setNotificaciones(true);
        salida.setGenero(new Genero(1L, "FEMENINO"));

        when(userProfileService.crearPerfil(any(UserProfile.class))).thenReturn(salida);

        ObjectMapper mapper = new ObjectMapper();
        mapper.registerModule(new com.fasterxml.jackson.datatype.jsr310.JavaTimeModule());
        mapper.disable(SerializationFeature.WRITE_DATES_AS_TIMESTAMPS);

        mockMvc.perform(post("/api/v1/usuario_perfil/perfiles")
                .contentType("application/json")
                .content(mapper.writeValueAsString(entrada)))
            .andExpect(status().isCreated())
            .andExpect(jsonPath("$.direccion").value("Calle Falsa 123"));
    }


    @Test
    @WithMockUser(username = "111", roles = {"CLIENTE"})
    void obtenerPerfilPorId_returnOKAndJson() throws Exception {
        UserProfile perfil = new UserProfile();
        perfil.setId(10L);
        perfil.setUserId(111L);
        perfil.setDireccion("Calle Falsa 123");
        perfil.setFechaNacimiento(LocalDate.of(2000, 1, 1));
        perfil.setNotificaciones(true);
        perfil.setGenero(new Genero(1L, "FEMENINO"));

        when(userProfileService.obtenerPerfilPorId(10L)).thenReturn(perfil);

        mockMvc.perform(get("/api/v1/usuario_perfil/perfiles/10"))
            .andExpect(status().isOk())
            .andExpect(jsonPath("$.userId").value(111))
            .andExpect(jsonPath("$.direccion").value("Calle Falsa 123"));
    }


    @Test
    void eliminarPerfil_returnOKMessage() throws Exception {
        when(userProfileService.eliminarUserProfile(7L)).thenReturn("Perfil y usuario eliminados correctamente");

        mockMvc.perform(delete("/api/v1/usuario_perfil/perfiles/7"))
               .andExpect(status().isOk())
               .andExpect(jsonPath("$").value("Perfil y usuario eliminados correctamente"));
    }

    @Test
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

    @Test
    void obtenerPerfilPorId_noExiste_returnNotFound() throws Exception {
        when(userProfileService.obtenerPerfilPorId(99L))
            .thenThrow(new RuntimeException("Perfil no encontrado para ID: 99"));

        mockMvc.perform(get("/api/v1/usuario_perfil/perfiles/99"))
            .andExpect(status().isNotFound())
            .andExpect(jsonPath("$").value("Perfil no encontrado para ID: 99"));
    }
}
