package com.mentelibre.auth_service.controller;


import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.*;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.*;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.*;

import java.util.*;

import com.fasterxml.jackson.databind.ObjectMapper;
import com.mentelibre.auth_service.config.JwtUtil;
import com.mentelibre.auth_service.dto.RegisterUserDTO;
import com.mentelibre.auth_service.model.Rol;
import com.mentelibre.auth_service.model.User;
import com.mentelibre.auth_service.repository.UserRepository;
import com.mentelibre.auth_service.service.RolService;
import com.mentelibre.auth_service.service.UserService;

import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.web.servlet.AutoConfigureMockMvc;
import org.springframework.boot.test.autoconfigure.web.servlet.WebMvcTest;
import org.springframework.boot.test.mock.mockito.MockBean;
import org.springframework.context.annotation.ComponentScan;
import org.springframework.context.annotation.FilterType;
import org.springframework.http.MediaType;
import org.springframework.security.authentication.AuthenticationManager;
import org.springframework.security.core.context.SecurityContext;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.security.core.Authentication;
import org.springframework.test.web.servlet.MockMvc;

@WebMvcTest(
    controllers = AuthController.class,
    excludeFilters = {
        @ComponentScan.Filter(
            type = FilterType.ASSIGNABLE_TYPE,
            classes = {
                com.mentelibre.auth_service.config.SecurityConfig.class,
                com.mentelibre.auth_service.config.JwtRequestFilter.class
            }
        )
    }
)
@AutoConfigureMockMvc(addFilters = false) // Seguridad desactivada
class AuthControllerTest {

    @Autowired
    private MockMvc mockMvc;

    @MockBean private UserService userService;
    @MockBean private RolService rolService;
    @MockBean private JwtUtil jwtUtil;
    @MockBean private AuthenticationManager authenticationManager;
    @MockBean private UserRepository userRepository;

    private final ObjectMapper mapper = new ObjectMapper();

    private void mockAuth(String email) {
        Authentication auth = mock(Authentication.class);
        when(auth.getName()).thenReturn(email);

        SecurityContext context = mock(SecurityContext.class);
        when(context.getAuthentication()).thenReturn(auth);

        SecurityContextHolder.setContext(context);
    }


    @Test
    void obtenerRoles_returnOK() throws Exception {
        Rol r1 = new Rol(); r1.setId(1L); r1.setNombre("ROLE_ADMIN");
        Rol r2 = new Rol(); r2.setId(2L); r2.setNombre("ROLE_USER");

        when(rolService.obtenerRolOrdenPorId()).thenReturn(List.of(r1, r2));

        mockMvc.perform(get("/api/v1/roles"))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$[0].nombre").value("ROLE_ADMIN"))
                .andExpect(jsonPath("$[1].nombre").value("ROLE_USER"));
    }

    @Test
    void crearRol_returnCreated() throws Exception {
        Rol input = new Rol(); 
        input.setNombre("ROLE_EDITOR");

        Rol output = new Rol();
        output.setId(10L);
        output.setNombre("ROLE_EDITOR");

        when(rolService.crearRol(any(Rol.class))).thenReturn(output);

        mockMvc.perform(post("/api/v1/roles")
                .contentType(MediaType.APPLICATION_JSON)
                .content(mapper.writeValueAsString(input)))
                .andExpect(status().isCreated())
                .andExpect(jsonPath("$.id").value(10))
                .andExpect(jsonPath("$.nombre").value("ROLE_EDITOR"));
    }

    @Test
    void registerUser_returnCreated() throws Exception {

        RegisterUserDTO dto = new RegisterUserDTO();
        dto.setUsername("Juan");
        dto.setEmail("juan@mail.com");
        dto.setPassword("1234");
        dto.setPhone("123456");

        User user = new User();
        user.setId(1L);
        user.setUsername("Juan");
        user.setEmail("juan@mail.com");
        user.setPhone("123456");

        Rol rol = new Rol();
        rol.setId(2L);
        rol.setNombre("ROLE_USER");
        user.setRol(rol);

        when(userService.crearUser(any(RegisterUserDTO.class))).thenReturn(user);

        mockMvc.perform(post("/api/v1/auth/register")
                .contentType(MediaType.APPLICATION_JSON)
                .content(mapper.writeValueAsString(dto)))
                .andExpect(status().isCreated())
                .andExpect(jsonPath("$.username").value("Juan"))
                .andExpect(jsonPath("$.email").value("juan@mail.com"))
                .andExpect(jsonPath("$.rol").value("ROLE_USER"));
    }

    @Test
    void eliminarRol_returnOK() throws Exception {
        when(rolService.eliminarRol(1L)).thenReturn("Rol eliminado");

        mockMvc.perform(delete("/api/v1/roles/1"))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$").value("Rol eliminado"));
    }

    @Test
    void getUserById_returnOK() throws Exception {
        User user = new User();
        user.setId(1L);
        user.setUsername("Ana");
        user.setEmail("ana@mail.com");

        when(userService.obtenerUserPorId(1L)).thenReturn(user);

        mockMvc.perform(get("/api/v1/users/1"))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.username").value("Ana"));
    }

    @Test
    void getUserById_notFound() throws Exception {

        when(userService.obtenerUserPorId(5L))
                .thenThrow(new RuntimeException("Usuario no encontrado"));

        mockMvc.perform(get("/api/v1/users/5"))
                .andExpect(status().isNotFound())
                .andExpect(content().string("Usuario no encontrado"));
    }

    @Test
    void login_exitoso() throws Exception {

        Map<String, String> data = new HashMap<>();
        data.put("email", "juan@mail.com");
        data.put("password", "1234");

        User user = new User();
        user.setId(1L);
        user.setUsername("Juan");
        user.setEmail("juan@mail.com");

        Rol rol = new Rol();
        rol.setId(2L);
        rol.setNombre("ROLE_USER");
        user.setRol(rol);

        when(authenticationManager.authenticate(any())).thenReturn(null);
        when(userService.loadUserByUsername("juan@mail.com"))
                .thenReturn(org.springframework.security.core.userdetails.User
                    .withUsername("juan@mail.com")
                    .password("1234")
                    .roles("USER")
                    .build()
                );

        when(jwtUtil.generateToken(any())).thenReturn("FAKE_JWT_TOKEN");
        when(userService.obtenerUserPorEmail("juan@mail.com")).thenReturn(user);

        mockMvc.perform(post("/api/v1/auth/login")
                .contentType(MediaType.APPLICATION_JSON)
                .content(mapper.writeValueAsString(data)))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.token").value("FAKE_JWT_TOKEN"))
                .andExpect(jsonPath("$.username").value("Juan"));
    }

    @Test
    void login_credencialesIncorrectas() throws Exception {

        Map<String, String> data = new HashMap<>();
        data.put("email", "malo@mail.com");
        data.put("password", "1111");

        when(authenticationManager.authenticate(any()))
                .thenThrow(new RuntimeException("Bad credentials"));

        mockMvc.perform(post("/api/v1/auth/login")
                .contentType(MediaType.APPLICATION_JSON)
                .content(mapper.writeValueAsString(data)))
                .andExpect(status().isUnauthorized())
                .andExpect(content().string("Credenciales incorrectas"));
    }

    @Test
    void login_faltanCampos() throws Exception {

        Map<String, String> data = new HashMap<>();
        data.put("email", null);
        data.put("password", "1234");

        mockMvc.perform(post("/api/v1/auth/login")
                .contentType(MediaType.APPLICATION_JSON)
                .content(mapper.writeValueAsString(data)))
                .andExpect(status().isBadRequest())
                .andExpect(content().string("Faltan email o password"));
    }

    @Test
    void getRolPorId_returnOK() throws Exception {

        Rol rol = new Rol();
        rol.setId(1L);
        rol.setNombre("ROLE_ADMIN");

        when(rolService.obtenerRolPorId(1L)).thenReturn(rol);

        mockMvc.perform(get("/api/v1/roles/1"))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.nombre").value("ROLE_ADMIN"));
    }

    @Test
    void getRolPorId_notFound() throws Exception {

        when(rolService.obtenerRolPorId(5L))
                .thenThrow(new RuntimeException("Rol no encontrado"));

        mockMvc.perform(get("/api/v1/roles/5"))
                .andExpect(status().isNotFound())
                .andExpect(content().string("Rol no encontrado"));
    }

    @Test
    void updateRol_returnOK() throws Exception {

        Rol rol = new Rol();
        rol.setId(1L);
        rol.setNombre("ROLE_USER");

        when(rolService.actualizarRol(1L, "ROLE_USER")).thenReturn(rol);

        mockMvc.perform(put("/api/v1/roles/1")
                .contentType(MediaType.APPLICATION_JSON)
                .content(mapper.writeValueAsString(rol)))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.nombre").value("ROLE_USER"));
    }

    @Test
    void updateRol_notFound() throws Exception {

        Rol rol = new Rol();
        rol.setNombre("ROLE_USER");

        when(rolService.actualizarRol(99L, "ROLE_USER"))
                .thenThrow(new RuntimeException("Rol no encontrado"));

        mockMvc.perform(put("/api/v1/roles/99")
                .contentType(MediaType.APPLICATION_JSON)
                .content(mapper.writeValueAsString(rol)))
                .andExpect(status().isNotFound())
                .andExpect(content().string("Rol no encontrado"));
    }

    @Test
    void listarUsuarios_returnOK() throws Exception {

        User u1 = new User(); u1.setId(1L); u1.setUsername("Ana");
        User u2 = new User(); u2.setId(2L); u2.setUsername("Luis");

        when(userService.obtenerUser()).thenReturn(List.of(u1, u2));

        mockMvc.perform(get("/api/v1/users"))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$[0].username").value("Ana"))
                .andExpect(jsonPath("$[1].username").value("Luis"));
    }

    @Test
    void listarUsuarios_returnNoContent() throws Exception {

        when(userService.obtenerUser()).thenReturn(Collections.emptyList());

        mockMvc.perform(get("/api/v1/users"))
                .andExpect(status().isNoContent());
    }

    @Test
    void getMe_returnOK() throws Exception {

        User user = new User();
        user.setId(1L);
        user.setUsername("Maria");
        user.setEmail("maria@mail.com");

        Rol rol = new Rol();
        rol.setNombre("ROLE_USER");
        user.setRol(rol);

        when(userService.obtenerUserPorEmail("maria@mail.com"))
                .thenReturn(user);

        // mock autenticación
        mockAuth("maria@mail.com");

        mockMvc.perform(get("/api/v1/auth/me"))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.username").value("Maria"));
    }

    @Test
    void changePassword_ok() throws Exception {

        Map<String, String> data = new HashMap<>();
        data.put("oldPassword", "1234");
        data.put("newPassword", "5678");

        User user = new User();
        user.setId(1L);
        user.setEmail("test@mail.com");

        when(userService.obtenerUserPorEmail("test@mail.com")).thenReturn(user);
        when(userService.cambiarPassword(1L, "1234", "5678"))
                .thenReturn("Contraseña actualizada correctamente");

        mockAuth("test@mail.com");

        mockMvc.perform(put("/api/v1/auth/me/password")
                .contentType(MediaType.APPLICATION_JSON)
                .content(mapper.writeValueAsString(data)))
                .andExpect(status().isOk())
                .andExpect(content().string("Contraseña actualizada correctamente"));
    }

    @Test
    void changePassword_oldPasswordWrong() throws Exception {

        Map<String, String> data = new HashMap<>();
        data.put("oldPassword", "mala");
        data.put("newPassword", "5678");

        User user = new User();
        user.setId(1L);
        user.setEmail("test@mail.com");

        when(userService.obtenerUserPorEmail("test@mail.com")).thenReturn(user);
        when(userService.cambiarPassword(1L, "mala", "5678"))
                .thenThrow(new RuntimeException("La contraseña actual no es correcta"));

        mockAuth("test@mail.com");

        mockMvc.perform(put("/api/v1/auth/me/password")
                .contentType(MediaType.APPLICATION_JSON)
                .content(mapper.writeValueAsString(data)))
                .andExpect(status().isBadRequest())
                .andExpect(content().string("La contraseña actual no es correcta"));
    }
}
