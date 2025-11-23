package com.mentelibre.auth_service.mapper;

import org.springframework.stereotype.Component;

import com.mentelibre.auth_service.dto.RegisterUserDTO;
import com.mentelibre.auth_service.dto.UpdateUserDTO;
import com.mentelibre.auth_service.dto.UserResponseDTO;
import com.mentelibre.auth_service.model.Rol;
import com.mentelibre.auth_service.model.User;

@Component
public class UserMapper {

    // ----------- DTO → ENTITY (Registro) -----------
    public User toEntity(RegisterUserDTO dto) {
        User user = new User();
        user.setUsername(dto.getUsername());
        user.setEmail(dto.getEmail());
        user.setPhone(dto.getPhone());
        user.setPassword(dto.getPassword()); // se encripta en el service
        return user;
    }

    // ----------- DTO → ENTITY (Actualizar) -----------
    public void updateEntityFromDTO(User user, UpdateUserDTO dto, Rol rol) {
        if (dto.getUsername() != null) user.setUsername(dto.getUsername());
        if (dto.getEmail() != null) user.setEmail(dto.getEmail());
        if (dto.getPhone() != null) user.setPhone(dto.getPhone());
        if (rol != null) user.setRol(rol);
    }

    // ----------- ENTITY → DTO (Respuesta al frontend) -----------
    public UserResponseDTO toDTO(User user) {
        UserResponseDTO dto = new UserResponseDTO();
        dto.setId(user.getId());
        dto.setUsername(user.getUsername());
        dto.setEmail(user.getEmail());
        dto.setPhone(String.valueOf(user.getPhone()));
        dto.setRol(user.getRol() != null ? user.getRol().getNombre() : "SIN ROL");
        return dto;
    }
}
