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

import com.mentelibre.auth_service.model.Rol;
import com.mentelibre.auth_service.model.User;
import com.mentelibre.auth_service.repository.RolRepository;
import com.mentelibre.auth_service.repository.UserRepository;


// Se habilita la inicialización automatica de los mocks
@ExtendWith(MockitoExtension.class)
public class UserServiceTest {
    // Declaramos el mock del repositorio (Se crea la copia)
    @Mock
    private UserRepository repository;

    // Se crea el objeto de prueba con los datos inyectados
    @InjectMocks
    private UserService service;

    // Declaramos el mock del repositorio (Se crea la copia)
    @Mock
    private RolRepository rolRepository;

    // La contraseña esta encriptada
    @Mock
    private BCryptPasswordEncoder passwordEncoder;

    @Test // Prueba: Crear usuario
    void crear_CrearUser() {
        // Se crea un objeto de ejemplo, datos de entrada
        String username = "Thomas";
        String email = "thomas@ejemplo.com";
        String password = "estudiante1234";
        Long rolId = 2L;

        // Configurar el comportamiento del repositorio
        when(repository.existsByUsername(username)).thenReturn(false); // Esto simula que no existe un usuario con ese dato.
        when(repository.existsByEmail(email)).thenReturn(false); // Esto simula que no existe un correo con ese dato.
        Rol rol = new Rol();
        rol.setId(rolId); // Esto crea un rol simulado
        rol.setNombre("Estudiante"); 
        when(rolRepository.findById(rolId)).thenReturn(Optional.of(rol)); // Simulo que si existe un rol con este dato
        when(passwordEncoder.encode(password)).thenReturn("contraseñaEncriptada"); // Simula que al encriptar la contraseña

        User usersaved = new User(); // Se guaradan los datos ingresados
        usersaved.setUsername(username);
        usersaved.setEmail(email);
        usersaved.setPassword("contraseñaEncriptada");
        usersaved.setRol(rol);

        when(repository.save(any(User.class))).thenReturn(usersaved); // Se devuelve el objeto anterior creado

        // Ejecutamos el metodo del servicio que vamos a comprobar
        User resultado = service.crearUser(username, email, password, rolId);

        // Comparamos que se devuelva el mismo objeto
        assertEquals(username, resultado.getUsername());
        assertEquals(email, resultado.getEmail());
        assertEquals("contraseñaEncriptada", resultado.getPassword());
        assertEquals(rol, resultado.getRol());
    }

    @Test // Prueba: Usuario duplicado
    void crear_UserDuplicado() {
        String username = "Thomas";
        String email = "thomas@ejemplo.com";
        String password = "estudiante1234";
        Long rolId = 2L;

        when(repository.existsByUsername(username)).thenReturn(true); // Simula que ya existe

        RuntimeException ex = assertThrows(RuntimeException.class, () -> {
            service.crearUser(username, email, password, rolId);
        });

        assertEquals("El nombre de usuario ya se encuentra en uso", ex.getMessage());
    }

    @Test // Prueba: Correo duplicado
    void crear_CorreoDuplicado() {
        String username = "Thomas";
        String email = "thomas@ejemplo.com";
        String password = "estudiante1234";
        Long rolId = 2L;

        when(repository.existsByEmail(email)).thenReturn(true);

        RuntimeException ex = assertThrows(RuntimeException.class, () -> {
            service.crearUser(username, email, password, rolId);
        });

        assertEquals("El correo electronico ya se encuentra en uso", ex.getMessage());
    }

    @Test // Prueba: Id no existe
    void crear_IdNoExiste() {
        String username = "Thomas";
        String email = "thomas@ejemplo.com";
        String password = "estudiante1234";
        Long rolId = 4L;

        when(repository.existsByUsername(username)).thenReturn(false); // Esto simula que no existe un usuario con ese dato.
        when(repository.existsByEmail(email)).thenReturn(false); // Esto simula que no existe un correo con ese dato.
        when(rolRepository.findById(rolId)).thenReturn(Optional.empty());

        RuntimeException ex = assertThrows(RuntimeException.class, () -> {
            service.crearUser(username, email, password, rolId);
        });

        assertEquals("Rol no encontrado ID: "+ rolId, ex.getMessage());
    }

    @Test // Prueba: Autenticación exitosa
    void autenticar() {
        String username = "Thomas";
        String password = "estudiante1234";
        String passwordEncriptada = "$2a$10$abcdefg1234567890"; // Es un valor simulado

        User user = new User();
        user.setUsername(username);
        user.setPassword(passwordEncriptada);

        when(repository.findByUsername(username)).thenReturn(user);
        when(passwordEncoder.matches(password, passwordEncriptada)).thenReturn(true);

        boolean resultado = service.autenticar(username, password);

        assertEquals(true, resultado);
    }
}
