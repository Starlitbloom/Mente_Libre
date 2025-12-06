package com.mentelibre.user_service.service;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import com.mentelibre.user_service.dto.CreateUserProfileRequestDto;
import com.mentelibre.user_service.dto.GeneroDTO;
import com.mentelibre.user_service.dto.UpdateUserProfileRequestDto;
import com.mentelibre.user_service.dto.UserProfileResponseDto;
import com.mentelibre.user_service.model.Genero;
import com.mentelibre.user_service.model.UserProfile;
import com.mentelibre.user_service.repository.GeneroRepository;
import com.mentelibre.user_service.repository.UserProfileRepository;

import jakarta.transaction.Transactional;

@Service
@Transactional
public class UserProfileService {

    @Autowired
    private UserProfileRepository profileRepository;

    @Autowired
    private GeneroRepository generoRepository;

    // ============================================================
    // CREAR PERFIL
    public UserProfileResponseDto crearPerfil(CreateUserProfileRequestDto dto) {

        // Validar que no exista un perfil para ese usuario
        if (profileRepository.findByUserId(dto.getUserId()).isPresent()) {
            throw new RuntimeException("El usuario ya tiene un perfil creado");
        }

        // Validar género
        Genero genero = generoRepository.findById(dto.getGeneroId())
                .orElseThrow(() -> new RuntimeException("El género no existe"));

        UserProfile perfil = new UserProfile();
        perfil.setUserId(dto.getUserId());
        perfil.setFotoPerfil(dto.getFotoPerfil());
        perfil.setFechaNacimiento(dto.getFechaNacimiento());
        perfil.setDireccion(dto.getDireccion());
        perfil.setNotificaciones(dto.getNotificaciones());
        perfil.setGenero(genero);
        perfil.setObjetivo(dto.getObjetivo());
        perfil.setTema(dto.getTema());
        perfil.setHuellaActiva(dto.getHuellaActiva());

        UserProfile saved = profileRepository.save(perfil);

        return mapToResponse(saved);
    }

    // ============================================================
    // OBTENER PERFIL POR ID DEL USUARIO
    public UserProfileResponseDto obtenerPerfilPorUserId(Long userId) {

        UserProfile perfil = profileRepository.findByUserId(userId)
                .orElseThrow(() -> new RuntimeException("El usuario no tiene perfil registrado"));

        return mapToResponse(perfil);
    }

    // ============================================================
    // ACTUALIZAR PERFIL
    public UserProfileResponseDto actualizarPerfil(Long userId, UpdateUserProfileRequestDto dto) {

        UserProfile perfil = profileRepository.findByUserId(userId)
                .orElseThrow(() -> new RuntimeException("Perfil no encontrado"));

        if (dto.getFotoPerfil() != null) perfil.setFotoPerfil(dto.getFotoPerfil());
        if (dto.getFechaNacimiento() != null) perfil.setFechaNacimiento(dto.getFechaNacimiento());
        if (dto.getDireccion() != null) perfil.setDireccion(dto.getDireccion());
        if (dto.getNotificaciones() != null) perfil.setNotificaciones(dto.getNotificaciones());
        if (dto.getObjetivo() != null) perfil.setObjetivo(dto.getObjetivo());
        if (dto.getTema() != null) perfil.setTema(dto.getTema());
        if (dto.getHuellaActiva() != null) perfil.setHuellaActiva(dto.getHuellaActiva());

        // Actualizar género si se envió
        if (dto.getGeneroId() != null) {
            Genero genero = generoRepository.findById(dto.getGeneroId())
                    .orElseThrow(() -> new RuntimeException("El género no existe"));
            perfil.setGenero(genero);
        }

        UserProfile updated = profileRepository.save(perfil);

        return mapToResponse(updated);
    }

    // ============================================================
    // ELIMINAR PERFIL
    public String eliminarPerfil(Long userId) {

        UserProfile perfil = profileRepository.findByUserId(userId)
                .orElseThrow(() -> new RuntimeException("Perfil no encontrado"));

        profileRepository.delete(perfil);

        return "Perfil eliminado correctamente";
    }

    // ============================================================
    // CONVERTIR ENTIDAD A RESPONSE DTO
    private UserProfileResponseDto mapToResponse(UserProfile perfil) {

        UserProfileResponseDto dto = new UserProfileResponseDto();

        dto.setId(perfil.getId());
        dto.setUserId(perfil.getUserId());
        dto.setFotoPerfil(perfil.getFotoPerfil());
        dto.setFechaNacimiento(perfil.getFechaNacimiento());
        dto.setDireccion(perfil.getDireccion());
        dto.setNotificaciones(perfil.getNotificaciones());
        dto.setObjetivo(perfil.getObjetivo());
        dto.setTema(perfil.getTema());
        dto.setHuellaActiva(perfil.getHuellaActiva());

        // Mapear género
        Genero genero = perfil.getGenero();
        GeneroDTO generoDTO = new GeneroDTO();
        generoDTO.setId(genero.getId());
        generoDTO.setNombre(genero.getNombre());

        dto.setGenero(generoDTO);

        return dto;
    }
}
