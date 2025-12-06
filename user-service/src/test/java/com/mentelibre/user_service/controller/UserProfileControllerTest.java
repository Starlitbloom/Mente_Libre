package com.mentelibre.user_service.controller;

import com.fasterxml.jackson.databind.ObjectMapper;
import com.mentelibre.user_service.dto.CreateUserProfileRequestDto;
import com.mentelibre.user_service.dto.UpdateUserProfileRequestDto;
import com.mentelibre.user_service.dto.UserProfileResponseDto;
import com.mentelibre.user_service.service.UserProfileService;

import org.junit.jupiter.api.Test;

import org.mockito.Mockito;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.web.servlet.AutoConfigureMockMvc;
import org.springframework.boot.test.autoconfigure.web.servlet.WebMvcTest;

import org.springframework.boot.test.mock.mockito.MockBean;

import org.springframework.context.annotation.ComponentScan;
import org.springframework.context.annotation.FilterType;

import org.springframework.http.MediaType;

import org.springframework.security.core.Authentication;
import org.springframework.security.core.context.SecurityContext;
import org.springframework.security.core.context.SecurityContextHolder;

import org.springframework.test.web.servlet.MockMvc;

import static org.mockito.ArgumentMatchers.any;
import static org.mockito.ArgumentMatchers.eq;

import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.*;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.*;

@WebMvcTest(
        controllers = UserProfileController.class,
        excludeFilters = @ComponentScan.Filter(
                type = FilterType.ASSIGNABLE_TYPE,
                classes = {
                        com.mentelibre.user_service.config.SecurityConfig.class,
                        com.mentelibre.user_service.config.JwtRequestFilter.class
                }
        )
)
@AutoConfigureMockMvc(addFilters = false)
class UserProfileControllerTest {

    @Autowired
    private MockMvc mockMvc;

    @MockBean
    private UserProfileService userProfileService;

    private ObjectMapper mapper = new ObjectMapper();

    // --- Método auxiliar para simular autenticación ---
    private void mockAuth(Long userId) {
        Authentication auth = Mockito.mock(Authentication.class);
        Mockito.when(auth.getPrincipal()).thenReturn(userId);

        SecurityContext context = Mockito.mock(SecurityContext.class);
        Mockito.when(context.getAuthentication()).thenReturn(auth);

        SecurityContextHolder.setContext(context);
    }

    // ====================================================================================
    // TEST CREAR PERFIL
    // ====================================================================================

    @Test
    void createProfile_returnCreated() throws Exception {

        CreateUserProfileRequestDto dto = new CreateUserProfileRequestDto();
        dto.setUserId(1L);
        dto.setDireccion("Casa 123");

        UserProfileResponseDto response = new UserProfileResponseDto();
        response.setId(10L);
        response.setUserId(1L);
        response.setDireccion("Casa 123");

        Mockito.when(userProfileService.crearPerfil(any())).thenReturn(response);

        mockMvc.perform(post("/api/v1/user-profile")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(mapper.writeValueAsString(dto)))
                .andExpect(status().isCreated())
                .andExpect(jsonPath("$.id").value(10))
                .andExpect(jsonPath("$.userId").value(1))
                .andExpect(jsonPath("$.direccion").value("Casa 123"));
    }

    @Test
    void createProfile_userAlreadyHasProfile_returnBadRequest() throws Exception {

        CreateUserProfileRequestDto dto = new CreateUserProfileRequestDto();

        Mockito.when(userProfileService.crearPerfil(any()))
                .thenThrow(new RuntimeException("El usuario ya tiene un perfil creado"));

        mockMvc.perform(post("/api/v1/user-profile")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(mapper.writeValueAsString(dto)))
                .andExpect(status().isBadRequest())
                .andExpect(content().string("El usuario ya tiene un perfil creado"));
    }

    // ====================================================================================
    // TEST GET MY PROFILE
    // ====================================================================================

    @Test
    void getMyProfile_returnOK() throws Exception {

        mockAuth(1L);

        UserProfileResponseDto perfil = new UserProfileResponseDto();
        perfil.setId(5L);
        perfil.setUserId(1L);
        perfil.setDireccion("Casa 123");

        Mockito.when(userProfileService.obtenerPerfilPorUserId(1L))
                .thenReturn(perfil);

        mockMvc.perform(get("/api/v1/user-profile/me"))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.id").value(5))
                .andExpect(jsonPath("$.userId").value(1))
                .andExpect(jsonPath("$.direccion").value("Casa 123"));
    }

    @Test
    void getMyProfile_notFound() throws Exception {

        mockAuth(1L);

        Mockito.when(userProfileService.obtenerPerfilPorUserId(1L))
                .thenThrow(new RuntimeException("Perfil no encontrado"));

        mockMvc.perform(get("/api/v1/user-profile/me"))
                .andExpect(status().isNotFound())
                .andExpect(content().string("Perfil no encontrado"));
    }

    // ====================================================================================
    // TEST UPDATE PROFILE
    // ====================================================================================

    @Test
    void updateProfile_returnOK() throws Exception {

        mockAuth(1L);

        UpdateUserProfileRequestDto dto = new UpdateUserProfileRequestDto();
        dto.setDireccion("Nueva dirección");

        UserProfileResponseDto updated = new UserProfileResponseDto();
        updated.setUserId(1L);
        updated.setDireccion("Nueva dirección");

        Mockito.when(userProfileService.actualizarPerfil(eq(1L), any()))
                .thenReturn(updated);

        mockMvc.perform(put("/api/v1/user-profile/me")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(mapper.writeValueAsString(dto)))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.direccion").value("Nueva dirección"));
    }

    @Test
    void updateProfile_profileNotFound() throws Exception {

        mockAuth(1L);

        UpdateUserProfileRequestDto dto = new UpdateUserProfileRequestDto();

        Mockito.when(userProfileService.actualizarPerfil(eq(1L), any()))
                .thenThrow(new RuntimeException("Perfil no encontrado"));

        mockMvc.perform(put("/api/v1/user-profile/me")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(mapper.writeValueAsString(dto)))
                .andExpect(status().isBadRequest())
                .andExpect(content().string("Perfil no encontrado"));
    }

    // ====================================================================================
    // TEST DELETE PROFILE
    // ====================================================================================

    @Test
    void deleteProfile_returnOK() throws Exception {

        mockAuth(1L);

        Mockito.when(userProfileService.eliminarPerfil(1L))
                .thenReturn("Perfil eliminado correctamente");

        mockMvc.perform(delete("/api/v1/user-profile/me"))
                .andExpect(status().isOk())
                .andExpect(content().string("Perfil eliminado correctamente"));
    }

    @Test
    void deleteProfile_notFound() throws Exception {

        mockAuth(1L);

        Mockito.when(userProfileService.eliminarPerfil(1L))
                .thenThrow(new RuntimeException("Perfil no encontrado"));

        mockMvc.perform(delete("/api/v1/user-profile/me"))
                .andExpect(status().isNotFound())
                .andExpect(content().string("Perfil no encontrado"));
    }
}
