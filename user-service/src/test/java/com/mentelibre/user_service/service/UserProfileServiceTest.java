package com.mentelibre.user_service.service;

import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.*;

import java.time.LocalDate;
import java.util.Optional;

import com.mentelibre.user_service.dto.CreateUserProfileRequestDto;
import com.mentelibre.user_service.dto.UpdateUserProfileRequestDto;
import com.mentelibre.user_service.dto.UserProfileResponseDto;
import com.mentelibre.user_service.model.Genero;
import com.mentelibre.user_service.model.UserProfile;
import com.mentelibre.user_service.repository.GeneroRepository;
import com.mentelibre.user_service.repository.UserProfileRepository;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;

@ExtendWith(MockitoExtension.class)
class UserProfileServiceTest {

    @Mock
    private UserProfileRepository profileRepository;

    @Mock
    private GeneroRepository generoRepository;

    @InjectMocks
    private UserProfileService service;

    @Test
    void crearPerfil_correcto() {

        CreateUserProfileRequestDto dto = new CreateUserProfileRequestDto();
        dto.setUserId(1L);
        dto.setGeneroId(2L);
        dto.setDireccion("Casa 123");
        dto.setFechaNacimiento(LocalDate.of(2006, 5, 10));
        dto.setNotificaciones(true);
        dto.setHuellaActiva(false);

        Genero genero = new Genero(2L, "FEMENINO");

        when(profileRepository.findByUserId(1L)).thenReturn(Optional.empty());
        when(generoRepository.findById(2L)).thenReturn(Optional.of(genero));
        when(profileRepository.save(any(UserProfile.class))).thenAnswer(i -> {
            UserProfile saved = i.getArgument(0);
            saved.setId(10L);
            return saved;
        });

        UserProfileResponseDto response = service.crearPerfil(dto);

        assertEquals(1L, response.getUserId());
        assertEquals("FEMENINO", response.getGenero().getNombre());
    }

    @Test
    void crearPerfil_duplicado_lanzaExcepcion() {

        CreateUserProfileRequestDto dto = new CreateUserProfileRequestDto();
        dto.setUserId(1L);

        when(profileRepository.findByUserId(1L)).thenReturn(Optional.of(new UserProfile()));

        RuntimeException ex = assertThrows(
                RuntimeException.class,
                () -> service.crearPerfil(dto)
        );

        assertEquals("El usuario ya tiene un perfil creado", ex.getMessage());
    }

    @Test
    void crearPerfil_generoNoExiste() {

        CreateUserProfileRequestDto dto = new CreateUserProfileRequestDto();
        dto.setUserId(1L);
        dto.setGeneroId(999L);

        when(profileRepository.findByUserId(1L)).thenReturn(Optional.empty());
        when(generoRepository.findById(999L)).thenReturn(Optional.empty());

        RuntimeException ex = assertThrows(
                RuntimeException.class,
                () -> service.crearPerfil(dto)
        );

        assertEquals("El género no existe", ex.getMessage());
    }

    @Test
    void obtenerPerfil_existe() {

        UserProfile perfil = new UserProfile();
        perfil.setId(10L);
        perfil.setUserId(1L);
        perfil.setGenero(new Genero(1L, "FEMENINO"));

        when(profileRepository.findByUserId(1L)).thenReturn(Optional.of(perfil));

        UserProfileResponseDto response = service.obtenerPerfilPorUserId(1L);

        assertEquals(1L, response.getUserId());
    }

    @Test
    void actualizarPerfil_exitoso() {

        UserProfile perfil = new UserProfile();
        perfil.setUserId(1L);
        perfil.setGenero(new Genero(1L, "FEMENINO"));

        UpdateUserProfileRequestDto dto = new UpdateUserProfileRequestDto();
        dto.setDireccion("Nueva dirección");
        dto.setGeneroId(2L);

        Genero nuevoGenero = new Genero(2L, "MASCULINO");

        when(profileRepository.findByUserId(1L)).thenReturn(Optional.of(perfil));
        when(generoRepository.findById(2L)).thenReturn(Optional.of(nuevoGenero));
        when(profileRepository.save(any())).thenReturn(perfil);

        UserProfileResponseDto response = service.actualizarPerfil(1L, dto);

        assertEquals("Nueva dirección", response.getDireccion());
        assertEquals("MASCULINO", response.getGenero().getNombre());
    }

    @Test
    void eliminarPerfil_exitoso() {

        UserProfile perfil = new UserProfile();
        perfil.setUserId(1L);

        when(profileRepository.findByUserId(1L)).thenReturn(Optional.of(perfil));

        assertDoesNotThrow(() -> service.eliminarPerfil(1L));
        verify(profileRepository).delete(perfil);
    }
}