package com.mentelibre.auth_service.service;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertThrows;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.when;

import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;
import org.springframework.security.crypto.bcrypt.BCryptPasswordEncoder;

import com.mentelibre.auth_service.dto.RegisterUserDTO;
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

    @Test
    void crearUser_Exitoso() {
        RegisterUserDTO dto = new RegisterUserDTO();
        dto.setUsername("Thomas");
        dto.setEmail("thomas@ejemplo.com");
        dto.setPassword("estudiante1234");
        dto.setPhone("123456789");

        when(repository.existsByUsername(dto.getUsername())).thenReturn(false);
        when(repository.existsByEmail(dto.getEmail())).thenReturn(false);

        Rol rol = new Rol();
        rol.setId(2L);
        rol.setNombre("ROLE_USER");

        when(rolRepository.findByNombre("ROLE_USER")).thenReturn(rol);
        when(passwordEncoder.encode(dto.getPassword())).thenReturn("encoded");

        User saved = new User();
        saved.setId(1L);
        saved.setUsername(dto.getUsername());
        saved.setEmail(dto.getEmail());
        saved.setPassword("encoded");
        saved.setRol(rol);

        when(repository.save(any(User.class))).thenReturn(saved);

        User resultado = service.crearUser(dto);

        assertEquals("Thomas", resultado.getUsername());
        assertEquals("thomas@ejemplo.com", resultado.getEmail());
        assertEquals("encoded", resultado.getPassword());
        assertEquals("ROLE_USER", resultado.getRol().getNombre());
    }

    @Test
    void crearUser_UsernameDuplicado() {

        RegisterUserDTO dto = new RegisterUserDTO();
        dto.setUsername("Thomas");
        dto.setEmail("thomas@ejemplo.com");
        dto.setPassword("1234");

        when(repository.existsByUsername(dto.getUsername())).thenReturn(true);

        RuntimeException ex = assertThrows(RuntimeException.class, () -> {
            service.crearUser(dto);
        });

        assertEquals("El nombre de usuario ya está en uso", ex.getMessage());
    }

    @Test
    void crearUser_EmailDuplicado() {

        RegisterUserDTO dto = new RegisterUserDTO();
        dto.setUsername("Thomas");
        dto.setEmail("thomas@ejemplo.com");
        dto.setPassword("1234");

        when(repository.existsByUsername(dto.getUsername())).thenReturn(false);
        when(repository.existsByEmail(dto.getEmail())).thenReturn(true);

        RuntimeException ex = assertThrows(RuntimeException.class, () -> {
            service.crearUser(dto);
        });

        assertEquals("El correo ya está en uso", ex.getMessage());
    }

    @Test
    void crearUser_RolUserNoExiste() {

        RegisterUserDTO dto = new RegisterUserDTO();
        dto.setUsername("Thomas");
        dto.setEmail("thomas@ejemplo.com");
        dto.setPassword("1234");

        when(repository.existsByUsername(dto.getUsername())).thenReturn(false);
        when(repository.existsByEmail(dto.getEmail())).thenReturn(false);
        when(rolRepository.findByNombre("ROLE_USER")).thenReturn(null);

        RuntimeException ex = assertThrows(RuntimeException.class, () -> {
            service.crearUser(dto);
        });

        assertEquals("El rol ROLE_USER no existe en la base de datos", ex.getMessage());
    }

}
