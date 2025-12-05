package com.mentelibre.auth_service.controller;

import java.util.HashMap;
import java.util.List;
import java.util.Map;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.security.authentication.AuthenticationManager;
import org.springframework.security.authentication.UsernamePasswordAuthenticationToken;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.web.bind.annotation.DeleteMapping;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.PutMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import com.mentelibre.auth_service.config.JwtUtil;
import com.mentelibre.auth_service.dto.ChangePasswordDTO;
import com.mentelibre.auth_service.dto.RegisterUserDTO;
import com.mentelibre.auth_service.dto.UpdateUserDTO;
import com.mentelibre.auth_service.dto.UserResponseDTO;
import com.mentelibre.auth_service.model.Rol;
import com.mentelibre.auth_service.model.User;
import com.mentelibre.auth_service.service.RolService;
import com.mentelibre.auth_service.service.UserService;

import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.media.Content;
import io.swagger.v3.oas.annotations.media.Schema;
import io.swagger.v3.oas.annotations.responses.ApiResponse;

@RestController // Maneja peticiones HTTP
@RequestMapping("/api/v1") // Define la ruta base
public class AuthController {
    @Autowired // Inyecta el Service para para usar sus métodos
    private UserService userService;

    @Autowired // Inyecta el Service para para usar sus métodos
    private RolService rolService;
    
    @Autowired
    private JwtUtil jwtUtil; // Inyecta el JwtUtil para manejar tokens JWT

    @Autowired
    private AuthenticationManager authenticationManager; // Inyecta el AuthenticationManager para manejar autenticación


    // ============================================================
    // ADMIN: GESTIÓN DE ROLES

    // Endpoint para consultar los roles
    @Operation(summary = "Obtener una lista de todos los roles", description = "Retorna todos los roles registrados en el sistema.")
    @ApiResponse(
        responseCode = "200",
        description = "Lista de roles obtenida correctamente",
        content = @Content(schema = @Schema(implementation = Rol.class))
    )
    @ApiResponse(
        responseCode = "204",
        description = "Lista de roles no encontrado",
        content = @Content(schema = @Schema(implementation = Rol.class))
    )
    @PreAuthorize("hasRole('ADMIN')")
    @GetMapping("/roles")
    public ResponseEntity<List<Rol>> getRoles(){
        List<Rol> roles = rolService.obtenerRolOrdenPorId(); // Obtener todos los roles
        return roles.isEmpty() ? ResponseEntity.noContent().build() : ResponseEntity.ok(roles); // Si hay roles, devuelve la lista de roles
    }

    // Endpoint para consultar un rol por ID
    @Operation(summary = "Obtener rol por ID", description = "Devuelve un rol si el ID existe.")
    @ApiResponse(
        responseCode = "200",
        description = "Rol encontrado",
        content = @Content(schema = @Schema(implementation = Rol.class))
    )
    @ApiResponse(
        responseCode = "404",
        description = "Rol no encontrado",
        content = @Content(schema = @Schema(implementation = Rol.class))
    )
    @PreAuthorize("hasRole('ADMIN')")
    @GetMapping("/roles/{id}")
    public ResponseEntity<?> rolPorId(@PathVariable Long id) {
        try {
            return ResponseEntity.ok(rolService.obtenerRolPorId(id));
        } catch (RuntimeException e) {
            return ResponseEntity.status(HttpStatus.NOT_FOUND).body("Rol no encontrado");
        }
    }

    // Endpoint para crear un rol nuevo
    @Operation(summary = "Crear nuevo rol", description = "Registra un nuevo rol.")
    @ApiResponse(
        responseCode = "201",
        description = "Rol creado exitosamente",
        content = @Content(schema = @Schema(implementation = Rol.class))
    )
    @ApiResponse(
        responseCode = "400",
        description = "Error en los datos enviados",
        content = @Content(schema = @Schema(implementation = Rol.class))
    )
    @PreAuthorize("hasRole('ADMIN')")
    @PostMapping("/roles")
    public ResponseEntity<?> crearRol(@RequestBody Rol rol) {
        try {
            return ResponseEntity.status(HttpStatus.CREATED).body(rolService.crearRol(rol));
        } catch (RuntimeException e) {
            return ResponseEntity.badRequest().body(e.getMessage());
        }
    }

    // Endpoint para actualizar los roles
    @Operation(summary = "Actualizar rol", description = "Actualiza los datos de un rol dado su ID.")
    @ApiResponse(
        responseCode = "200",
        description = "Rol actualizado exitosamente",
        content = @Content(schema = @Schema(implementation = Rol.class))
    )
    @ApiResponse(
        responseCode = "404",
        description = "Rol no encontrado",
        content = @Content(schema = @Schema(implementation = Rol.class))
    )
    @PreAuthorize("hasRole('ADMIN')")
    @PutMapping("/roles/{id}")
    public ResponseEntity<?> actualizarRol(@PathVariable Long id, @RequestBody Rol rol) {
        try {
            return ResponseEntity.ok(rolService.actualizarRol(id, rol.getNombre()));
        } catch (RuntimeException e) {
            return ResponseEntity.status(HttpStatus.NOT_FOUND).body(e.getMessage());
        }
    }

    // Endpoint para eliminar los roles
    @Operation(summary = "Eliminar rol", description = "Elimina un rol según el ID proporcionado.")
    @ApiResponse(
        responseCode = "200",
        description = "Rol eliminado correctamente",
        content = @Content(schema = @Schema(implementation = Rol.class))
    )
    @ApiResponse(
        responseCode = "404",
        description = "Rol no encontrado",
        content = @Content(schema = @Schema(implementation = Rol.class))
    )
    @PreAuthorize("hasRole('ADMIN')")
    @DeleteMapping("/roles/{id}")
    public ResponseEntity<?> eliminarRol(@PathVariable Long id) {
        try {
            return ResponseEntity.ok(rolService.eliminarRol(id));
        } catch (RuntimeException e) {
            return ResponseEntity.status(HttpStatus.NOT_FOUND).body(e.getMessage());
        }
    }

    // ============================================================
    // ADMIN: GESTIÓN DE USUARIOS
    // Endpoint para consultar los usuarios
    @Operation(summary = "Obtener una lista de todos los Usuarios", description = "Retorna todos los usuarios registrados en el sistema.")
    @ApiResponse(
        responseCode = "200",
        description = "Lista de usuarios obtenida correctamente",
        content = @Content(schema = @Schema(implementation = User.class))
    )
    @ApiResponse(
        responseCode = "204",
        description = "Lista de usuarios no encontrado",
        content = @Content(schema = @Schema(implementation = User.class))
    )
    @PreAuthorize("hasRole('ADMIN')")
    @GetMapping("/users")
    public ResponseEntity<?> listarUsuarios() {
        List<User> lista = userService.obtenerUser();
        return lista.isEmpty() ? ResponseEntity.noContent().build() : ResponseEntity.ok(lista);
    }

    // Endpoint para consultar un usuario por ID
    @Operation(summary = "Obtener usuario por ID", description = "Devuelve un usuario si el ID existe.")
    @ApiResponse(
        responseCode = "200",
        description = "Usuario encontrado",
        content = @Content(schema = @Schema(implementation = User.class))
    )
    @ApiResponse(
        responseCode = "404",
        description = "Usuario no encontrado",
        content = @Content(schema = @Schema(implementation = User.class))
    )
    @ApiResponse(
        responseCode = "403",
        description = "Acceso denegado por falta de permisos",
        content = @Content
    )
    @PreAuthorize("hasRole('ADMIN')")
    @GetMapping("/users/{id}")
    public ResponseEntity<?> obtenerPorId(@PathVariable Long id) {
        try {
            return ResponseEntity.ok(userService.obtenerUserPorId(id));
        } catch (RuntimeException e) {
            return ResponseEntity.status(HttpStatus.NOT_FOUND).body("Usuario no encontrado");
        }
    }

    // Endpoint para actualizar a los usuarios
    @Operation(summary = "Actualizar usuario", description = "Actualiza los datos de un usuario dado su ID.")
    @ApiResponse(
        responseCode = "200",
        description = "Usuario actualizado exitosamente",
        content = @Content(schema = @Schema(implementation = User.class))
    )
    @ApiResponse(
        responseCode = "404",
        description = "Usuario no encontrado",
        content = @Content(schema = @Schema(implementation = User.class))
    )
    @ApiResponse(
        responseCode = "403",
        description = "Acceso denegado por falta de permisos",
        content = @Content
    )
    @PreAuthorize("hasRole('ADMIN')")
    @PutMapping("/users/{id}")
    public ResponseEntity<?> actualizarUser(@PathVariable Long id, @RequestBody UpdateUserDTO dto) {
        try {
            return ResponseEntity.ok(userService.actualizarUser(id, dto));
        } catch (RuntimeException e) {
            return ResponseEntity.status(HttpStatus.NOT_FOUND).body(e.getMessage());
        }
    }

    // Endpoint para eliminar a los usuarios
    @Operation(summary = "Eliminar usuario", description = "Elimina un usuario según el ID proporcionado.")
    @ApiResponse(
        responseCode = "200",
        description = "Usuario eliminado correctamente",
        content = @Content(schema = @Schema(implementation = User.class))
    )
    @ApiResponse(
        responseCode = "404",
        description = "Usuario no encontrado",
        content = @Content(schema = @Schema(implementation = User.class))
    )
    @ApiResponse(
        responseCode = "403",
        description = "Acceso denegado por falta de permisos",
        content = @Content
    )
    @PreAuthorize("hasRole('ADMIN')")
    @DeleteMapping("/users/{id}")
    public ResponseEntity<?> eliminarUser(@PathVariable Long id) {
        try {
            String mensaje = userService.eliminarUser(id);
            return ResponseEntity.ok(mensaje);
        } catch (RuntimeException e) {
            return ResponseEntity.status(HttpStatus.NOT_FOUND).body(e.getMessage());
        }
    }

    // ============================================================
    // REGISTRO
    // Endpoint para crear un usuario nuevo
    @Operation(summary = "Crear nuevo usuario", description = "Registra un nuevo usuario con su respectivo rol.")
    @ApiResponse(
        responseCode = "201",
        description = "Usuario creado exitosamente",
        content = @Content(schema = @Schema(implementation = User.class))
    )
    @ApiResponse(
        responseCode = "400",
        description = "Error en los datos enviados",
        content = @Content(schema = @Schema(implementation = User.class))
    )
    @ApiResponse(
        responseCode = "403",
        description = "Acceso denegado por falta de permisos",
        content = @Content
    )
    @PostMapping("/auth/register")
    public ResponseEntity<?> register(@RequestBody RegisterUserDTO dto) {

        if (dto.getUsername() == null || dto.getEmail() == null ||
            dto.getPhone() == null || dto.getPassword() == null) {
            return ResponseEntity.badRequest().body("Faltan datos obligatorios");
        }

        User user = userService.crearUser(dto);

        UserResponseDTO response = new UserResponseDTO();
        response.setId(user.getId());
        response.setUsername(user.getUsername());
        response.setEmail(user.getEmail());
        response.setPhone(user.getPhone());
        response.setRol(user.getRol().getNombre());

        return ResponseEntity.status(HttpStatus.CREATED).body(response);
    }

    // ============================================================
    // LOGIN
    // Endpoint para autenticar al usuario
    @Operation(summary = "Autenticar un usuario", description = "Devuelve un JWT si las credenciales son válidas.")
    @ApiResponse(
        responseCode = "200",
        description = "Autenticación exitosa",
        content = @Content(schema = @Schema(implementation = User.class))
    )
    @ApiResponse(
        responseCode = "401",
        description = "Credenciales incorrectas",
        content = @Content(schema = @Schema(implementation = User.class))
    )
    @PostMapping("/auth/login")
    public ResponseEntity<?> login(@RequestBody Map<String, String> data) {

        String email = data.get("email");
        String password = data.get("password");

        if (email == null || password == null) {
            return ResponseEntity.badRequest().body("Faltan email o password");
        }

        try {
            authenticationManager.authenticate(
                new UsernamePasswordAuthenticationToken(email, password)
            );

            UserDetails userDetails = userService.loadUserByUsername(email);
            String jwt = jwtUtil.generateToken(userDetails);

            User user = userService.obtenerUserPorEmail(email);

            Map<String, Object> response = new HashMap<>();
            response.put("token", jwt);
            response.put("userId", user.getId());
            response.put("username", user.getUsername());
            response.put("email", user.getEmail());
            response.put("role", user.getRol().getNombre());

            return ResponseEntity.ok(response);

        } catch (Exception e) {
            return ResponseEntity.status(HttpStatus.UNAUTHORIZED).body("Credenciales incorrectas");
        }
    }

     // ============================================================
    //  PERFIL DE USUARIO (PROPIA CUENTA)
    // Endpoint para consultar un usuario por ID
    @Operation(summary = "Obtener mi perfil", description = "Devuelve los datos del usuario autenticado según el token JWT.")
    @ApiResponse(
        responseCode = "200",
        description = "Perfil obtenido correctamente",
        content = @Content(schema = @Schema(implementation = UserResponseDTO.class))
    )
    @ApiResponse(
        responseCode = "404",
        description = "Perfil no encontrado",
        content = @Content(schema = @Schema(implementation = UserResponseDTO.class))
    )
    @ApiResponse(
        responseCode = "403",
        description = "Acceso denegado por falta de autenticación",
        content = @Content
    )
    @PreAuthorize("hasAnyRole('USER','ADMIN')")
    @GetMapping("/auth/me")
    public ResponseEntity<?> getMe() {

        String email = SecurityContextHolder.getContext().getAuthentication().getName();
        User user = userService.obtenerUserPorEmail(email);

        UserResponseDTO response = new UserResponseDTO();
        response.setId(user.getId());
        response.setUsername(user.getUsername());
        response.setEmail(user.getEmail());
        response.setPhone(user.getPhone());
        response.setRol(user.getRol().getNombre());

        return ResponseEntity.ok(response);
    }

    // Endpoint para actualizar a los usuarios
    @Operation(summary = "Actualizar mi perfil", description = "Permite al usuario autenticado actualizar sus propios datos.")
    @ApiResponse(
        responseCode = "200",
        description = "Perfil actualizado correctamente",
        content = @Content(schema = @Schema(implementation = UserResponseDTO.class))
    )
    @ApiResponse(
        responseCode = "400",
        description = "Datos inválidos",
        content = @Content(schema = @Schema(implementation = UserResponseDTO.class))
    )
    @ApiResponse(
        responseCode = "403",
        description = "Acceso denegado por falta de permisos",
        content = @Content
    )
    @PreAuthorize("hasAnyRole('USER','ADMIN')")
    @PutMapping("/auth/me")
    public ResponseEntity<?> updateMe(@RequestBody UpdateUserDTO dto) {

        String email = SecurityContextHolder.getContext().getAuthentication().getName();
        User user = userService.obtenerUserPorEmail(email);

        User actualizado = userService.actualizarUser(user.getId(), dto);

        UserResponseDTO response = new UserResponseDTO();
        response.setId(actualizado.getId());
        response.setUsername(actualizado.getUsername());
        response.setEmail(actualizado.getEmail());
        response.setPhone(actualizado.getPhone());
        response.setRol(actualizado.getRol().getNombre());

        return ResponseEntity.ok(response);
    }

    // Endpoint para eliminar su propia cuenta
    @Operation(summary = "Eliminar mi cuenta", description = "Elimina la cuenta del usuario autenticado basándose en el token JWT.")
    @ApiResponse(
        responseCode = "200",
        description = "Cuenta eliminada correctamente",
        content = @Content(schema = @Schema(implementation = Void.class))
    )
    @ApiResponse(
        responseCode = "404",
        description = "Perfil no encontrado",
        content = @Content(schema = @Schema(implementation = UserResponseDTO.class))
    )
    @ApiResponse(
        responseCode = "403",
        description = "Acceso denegado por falta de permisos",
        content = @Content
    )
    @PreAuthorize("hasAnyRole('USER','ADMIN')")
    @DeleteMapping("/auth/me")
    public ResponseEntity<?> deleteMe() {

        String email = SecurityContextHolder.getContext().getAuthentication().getName();
        User user = userService.obtenerUserPorEmail(email);

        return ResponseEntity.ok(userService.eliminarUser(user.getId()));
    }

    @Operation(summary = "Cambiar contraseña", description = "Permite al usuario autenticado modificar su contraseña.")
    @ApiResponse(responseCode = "200", description = "Contraseña cambiada correctamente")
    @ApiResponse(responseCode = "400", description = "Datos inválidos")
    @ApiResponse(responseCode = "403", description = "Usuario no autorizado")
    @ApiResponse(responseCode = "404", description = "Usuario no encontrado")
    @PreAuthorize("hasAnyRole('USER','ADMIN')")
    @PutMapping("/auth/me/password")
    public ResponseEntity<?> changePassword(@RequestBody ChangePasswordDTO dto) {

        if (dto.getOldPassword() == null || dto.getNewPassword() == null) {
            return ResponseEntity.badRequest().body("Debe ingresar las contraseñas.");
        }

        String email = SecurityContextHolder.getContext().getAuthentication().getName();
        User user = userService.obtenerUserPorEmail(email);

        try {
            String mensaje = userService.cambiarPassword(user.getId(), dto.getOldPassword(), dto.getNewPassword());
            return ResponseEntity.ok(mensaje);
        } catch (RuntimeException e) {
            return ResponseEntity.status(HttpStatus.BAD_REQUEST).body(e.getMessage());
        }
    }

}