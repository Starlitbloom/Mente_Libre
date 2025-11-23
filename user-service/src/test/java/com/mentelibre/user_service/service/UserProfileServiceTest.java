package com.mentelibre.user_service.service;

import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.Mockito.*;

import java.util.Optional;

import com.mentelibre.user_service.model.Genero;
import com.mentelibre.user_service.model.UserProfile;
import com.mentelibre.user_service.repository.UserProfileRepository;
import com.mentelibre.user_service.webclient.AuthClient;

import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;

@ExtendWith(MockitoExtension.class)
public class UserProfileServiceTest {

    @Mock
    private UserProfileRepository userProfileRepository;

    @Mock
    private AuthClient authClient;

    @Mock
    private GeneroService generoService;

    @InjectMocks
    private UserProfileService userProfileService;

    @Test
    void crearPerfil_userIdNull_lanzaExcepcion() {
        UserProfile perfil = new UserProfile();
        perfil.setUserId(null);

        RuntimeException ex = assertThrows(RuntimeException.class, () -> {
            userProfileService.crearPerfil(perfil);
        });

        assertEquals("El ID del usuario (userId) es obligatorio", ex.getMessage());
    }

    @Test
    void crearPerfil_usuarioNoExiste_lanzaExcepcion() {
        UserProfile perfil = new UserProfile();
        perfil.setUserId(99L);

        when(authClient.existeUsuario(99L)).thenReturn(false);

        RuntimeException ex = assertThrows(RuntimeException.class, () -> {
            userProfileService.crearPerfil(perfil);
        });

        assertEquals("El usuario con ID 99 no existe en el AuthService", ex.getMessage());
    }

    @Test
    void crearPerfil_usuarioValido_guardaPerfil() {
        Genero genero = new Genero();
        genero.setId(1L);

        UserProfile perfil = new UserProfile();
        perfil.setUserId(1L);
        perfil.setGenero(genero);

        when(authClient.existeUsuario(1L)).thenReturn(true);
        when(generoService.existeGenero(1L)).thenReturn(true);
        when(userProfileRepository.findByUserId(1L)).thenReturn(Optional.empty());
        when(userProfileRepository.save(perfil)).thenReturn(perfil);

        UserProfile resultado = userProfileService.crearPerfil(perfil);

        assertEquals(perfil, resultado);
        verify(userProfileRepository, times(1)).save(perfil);
    }

    @Test
    void obtenerPerfil_porIdExiste_devuelvePerfil() {
        UserProfile perfil = new UserProfile();
        perfil.setId(10L);
        perfil.setUserId(1L);

        when(userProfileRepository.findById(10L)).thenReturn(Optional.of(perfil));

        UserProfile resultado = userProfileService.obtenerPerfilPorId(10L);

        assertNotNull(resultado);
        assertEquals(perfil, resultado);
    }

    @Test
    void obtenerPerfil_porIdNoExiste_lanzaExcepcion() {
        Long id = 99L;
        when(userProfileRepository.findById(id)).thenReturn(Optional.empty());

        RuntimeException ex = assertThrows(RuntimeException.class, () -> {
            userProfileService.obtenerPerfilPorId(id);
        });

        assertEquals("Perfil no encontrado para ID: " + id, ex.getMessage());
    }

    @Test
    void eliminarPerfil_existente_ejecucionExitosa() {
        Long id = 5L;
        UserProfile perfil = new UserProfile();
        perfil.setId(id);
        perfil.setUserId(1L);

        when(userProfileRepository.findById(id)).thenReturn(Optional.of(perfil));
        doNothing().when(userProfileRepository).delete(perfil);
        doNothing().when(authClient).eliminarUsuario(1L);

        assertDoesNotThrow(() -> userProfileService.eliminarUserProfile(id));
        verify(userProfileRepository, times(1)).delete(perfil);
        verify(authClient, times(1)).eliminarUsuario(1L);
    }
}
