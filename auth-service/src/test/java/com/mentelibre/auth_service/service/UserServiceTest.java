package com.mentelibre.auth_service.service;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertThrows;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.when;

import java.util.Optional;

import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;
import org.springframework.security.crypto.bcrypt.BCryptPasswordEncoder;

import com.mentelibre.auth_service.dto.RegisterUserDTO;
import com.mentelibre.auth_service.mapper.UserMapper;
import com.mentelibre.auth_service.model.Rol;
import com.mentelibre.auth_service.model.User;
import com.mentelibre.auth_service.repository.RolRepository;
import com.mentelibre.auth_service.repository.UserRepository;

@ExtendWith(MockitoExtension.class)
public class UserServiceTest {

    @Mock
    private UserRepository repository;

    @Mock
    private RolRepository rolRepository;

    @Mock
    private BCryptPasswordEncoder passwordEncoder;

    @InjectMocks
    private UserService service;

    @Mock
    private UserMapper mapper; // Agregar este mock

    @Test
    void crearUser_Exitoso() {
        RegisterUserDTO dto = new RegisterUserDTO();
        dto.setUsername("Thomas");
        dto.setEmail("thomas@ejemplo.com");
        dto.setPassword("estudiante1234");
        Long rolId = 2L;

        // Configurar mocks
        when(repository.existsByUsername(dto.getUsername())).thenReturn(false);
        when(repository.existsByEmail(dto.getEmail())).thenReturn(false);

        Rol rol = new Rol();
        rol.setId(rolId);
        rol.setNombre("Estudiante");
        when(rolRepository.findById(rolId)).thenReturn(Optional.of(rol));

        when(passwordEncoder.encode(dto.getPassword())).thenReturn("contraseñaEncriptada");

        // Mock del mapper
        User mappedUser = new User();
        mappedUser.setUsername(dto.getUsername());
        mappedUser.setEmail(dto.getEmail());
        mappedUser.setPassword(dto.getPassword());
        when(mapper.toEntity(dto)).thenReturn(mappedUser);

        // Mock de save
        User userSaved = new User();
        userSaved.setUsername(dto.getUsername());
        userSaved.setEmail(dto.getEmail());
        userSaved.setPassword("contraseñaEncriptada");
        userSaved.setRol(rol);
        when(repository.save(any(User.class))).thenReturn(userSaved);

        // Ejecutar el método
        User resultado = service.crearUser(dto, rolId);

        // Verificar resultados
        assertEquals(dto.getUsername(), resultado.getUsername());
        assertEquals(dto.getEmail(), resultado.getEmail());
        assertEquals("contraseñaEncriptada", resultado.getPassword());
        assertEquals(rol, resultado.getRol());
    }

    @Test
    void crearUser_UsernameDuplicado() {
        RegisterUserDTO dto = new RegisterUserDTO();
        dto.setUsername("Thomas");
        dto.setEmail("thomas@ejemplo.com");
        dto.setPassword("estudiante1234");
        Long rolId = 2L;

        when(repository.existsByUsername(dto.getUsername())).thenReturn(true);

        RuntimeException ex = assertThrows(RuntimeException.class, () -> {
            service.crearUser(dto, rolId);
        });

        assertEquals("El nombre de usuario ya está en uso", ex.getMessage());
    }

    @Test
    void crearUser_EmailDuplicado() {
        RegisterUserDTO dto = new RegisterUserDTO();
        dto.setUsername("Thomas");
        dto.setEmail("thomas@ejemplo.com");
        dto.setPassword("estudiante1234");
        Long rolId = 2L;

        when(repository.existsByUsername(dto.getUsername())).thenReturn(false);
        when(repository.existsByEmail(dto.getEmail())).thenReturn(true);

        RuntimeException ex = assertThrows(RuntimeException.class, () -> {
            service.crearUser(dto, rolId);
        });

        assertEquals("El correo ya está en uso", ex.getMessage());
    }

    @Test
    void crearUser_RolNoExiste() {
        RegisterUserDTO dto = new RegisterUserDTO();
        dto.setUsername("Thomas");
        dto.setEmail("thomas@ejemplo.com");
        dto.setPassword("estudiante1234");
        Long rolId = 4L;

        when(repository.existsByUsername(dto.getUsername())).thenReturn(false);
        when(repository.existsByEmail(dto.getEmail())).thenReturn(false);
        when(rolRepository.findById(rolId)).thenReturn(Optional.empty());

        RuntimeException ex = assertThrows(RuntimeException.class, () -> {
            service.crearUser(dto, rolId);
        });

        assertEquals("Rol no encontrado", ex.getMessage());
    }
}
