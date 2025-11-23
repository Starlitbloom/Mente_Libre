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
import org.springframework.security.core.userdetails.UserDetailsService;
import org.springframework.web.bind.annotation.DeleteMapping;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.PutMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;

import com.mentelibre.auth_service.config.JwtUtil;
import com.mentelibre.auth_service.dto.RegisterUserDTO;
import com.mentelibre.auth_service.dto.UpdateUserDTO;
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

    @Autowired
    private UserDetailsService userDetailsService; // Inyecta el UserDetailsService para cargar detalles del usuario
    
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
    @GetMapping("/roles")
    public ResponseEntity<List<Rol>> getRoles(){
        List<Rol> roles = rolService.obtenerRolOrdenPorId(); // Obtener todos los roles
        if (roles.isEmpty()) { // Si la lista esta vacía
            return ResponseEntity.noContent().build();
        }
        return ResponseEntity.ok(roles); // Si hay roles, devuelve la lista de roles
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
    @GetMapping("/roles/{id}")
    public ResponseEntity<?> obtenerRolPorId(@PathVariable Long id) {
        try {
            Rol rol = rolService.obtenerRolPorId(id);
            return ResponseEntity.ok(rol);
        } catch (RuntimeException e) {
            return ResponseEntity.status(HttpStatus.NOT_FOUND).body(e.getMessage());
        }
    }

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
    //@PreAuthorize("hasRole('ADMINISTRADOR')")
    @GetMapping("/users")
    public ResponseEntity<List<User>> getUsers(){
        List<User> users = userService.obtenerUser();
        if (users.isEmpty()) {
            return ResponseEntity.noContent().build();
        }
        return ResponseEntity.ok(users);
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
    //@PreAuthorize("hasRole('ADMINISTRADOR')")
    @GetMapping("/users/{id}")
    public ResponseEntity<?> obtenerUserPorId(@PathVariable Long id) {
        try {
            User user = userService.obtenerUserPorId(id);
            return ResponseEntity.ok(user);
        } catch (RuntimeException e) {
            return ResponseEntity.status(HttpStatus.NOT_FOUND).body(e.getMessage());
        }
    }



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
    //@PreAuthorize("hasRole('ADMINISTRADOR')")
    @PostMapping("/users")
    public ResponseEntity<?> crearUser(@RequestBody RegisterUserDTO userDTO) {

        System.out.println("Llega POST a /users: " + userDTO);

        if (userDTO.getUsername() == null || userDTO.getUsername().trim().isEmpty() ||
            userDTO.getEmail() == null || userDTO.getEmail().trim().isEmpty() ||
            userDTO.getPassword() == null || userDTO.getPassword().trim().isEmpty() ||
            userDTO.getPhone() == null || userDTO.getPhone().trim().isEmpty()) {

            return ResponseEntity.status(HttpStatus.BAD_REQUEST)
                    .body("Faltan datos obligatorios");
        }

        // por defecto asigna el rol 1 (ADMINISTRADOR)
        User user = userService.crearUser(userDTO, 2L);

        return ResponseEntity.status(HttpStatus.CREATED).body(user);
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
    //@PreAuthorize("hasRole('ADMINISTRADOR')")
    @PostMapping("/roles")
    public ResponseEntity<?> crearRol(@RequestBody Rol role) {

        try {
            // Si no hay roles en la base, crear libremente
            if (rolService.obtenerRoles().isEmpty()) {
                Rol nuevo = rolService.crearRol(role);
                return ResponseEntity.status(HttpStatus.CREATED).body(nuevo);
            }

            // Si ya hay roles, verificar token y rol administrador
            var auth = SecurityContextHolder.getContext().getAuthentication();
            if (auth == null || !auth.isAuthenticated() || auth.getAuthorities() == null ||
                auth.getAuthorities().stream().noneMatch(a -> a.getAuthority().equals("ROLE_ADMINISTRADOR"))) {
                return ResponseEntity.status(HttpStatus.FORBIDDEN).body("No autorizado para crear roles");
            }

            // Crear rol normalmente
            Rol nuevo = rolService.crearRol(role);
            return ResponseEntity.status(HttpStatus.CREATED).body(nuevo); 
        } catch (RuntimeException e) {
            return ResponseEntity.status(HttpStatus.BAD_REQUEST).body(e.getMessage());
        }
    
    }

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
    public ResponseEntity<?> autenticar(@RequestBody Map<String, String> loginData) {
        String email = loginData.get("email");
        String password = loginData.get("password");

        if (email == null || password == null) {
            return ResponseEntity.status(HttpStatus.BAD_REQUEST).body("Faltan datos: username o password");
        }

        try {
            authenticationManager.authenticate(
                new UsernamePasswordAuthenticationToken(email, password)
            );

            // Cargar detalles de usuario
            UserDetails userDetails = userDetailsService.loadUserByUsername(email);
            String jwt = jwtUtil.generateToken(userDetails);

            // Buscar usuario en la DB
            User user = userService.obtenerUserPorEmail(email);
            if (user == null) {
                return ResponseEntity.status(HttpStatus.UNAUTHORIZED).body("Usuario no encontrado");
            }

            // Asegurarnos de que tenga rol
            String roleNombre = user.getRol() != null ? user.getRol().getNombre() : "SIN_ROL";

            Map<String, String> response = new HashMap<>();
            response.put("token", jwt);
            response.put("email", email);
            response.put("role", roleNombre);

            return ResponseEntity.ok(response);

        } catch (org.springframework.security.authentication.BadCredentialsException e) {
            return ResponseEntity.status(HttpStatus.UNAUTHORIZED).body("Credenciales incorrectas");
        } catch (Exception e) {
            return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR)
                                .body("Error en autenticación: " + e.getMessage());
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
    //@PreAuthorize("hasRole('ADMINISTRADOR')")
    @PutMapping("/users/{id}")
    public ResponseEntity<?> actualizarUser(@PathVariable Long id, @RequestBody UpdateUserDTO dto) {
        try {
            User actualizado = userService.actualizarUser(id, dto);
            return ResponseEntity.ok(actualizado);
        } catch (RuntimeException e) {
            return ResponseEntity.status(HttpStatus.NOT_FOUND).body(e.getMessage());
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
    @PutMapping("/roles/{id}")
    public ResponseEntity<?> actualizarRol(@PathVariable Long id, @RequestParam(required = false) String nombre) {
        try {
            Rol actualizado = rolService.actualizarRol(id, nombre);
            return ResponseEntity.ok(actualizado);
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
    @DeleteMapping("/roles/{id}")
    public ResponseEntity<?> eliminarRol(@PathVariable Long id) {
        try {
            String mensaje = rolService.eliminarRol(id);
            return ResponseEntity.ok(mensaje);
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
    //@PreAuthorize("hasRole('ADMINISTRADOR')")
    @DeleteMapping("/users/{id}")
    public ResponseEntity<?> eliminarUser(@PathVariable Long id) {
        try {
            String mensaje = userService.eliminarUser(id);
            return ResponseEntity.ok(mensaje);
        } catch (RuntimeException e) {
            return ResponseEntity.status(HttpStatus.NOT_FOUND).body(e.getMessage());
        }
    }

}