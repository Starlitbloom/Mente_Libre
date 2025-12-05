package com.mentelibre.auth_service.service;

import java.util.Collections;
import java.util.List;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.security.core.authority.SimpleGrantedAuthority;
import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.security.core.userdetails.UserDetailsService;
import org.springframework.security.core.userdetails.UsernameNotFoundException;
import org.springframework.security.crypto.bcrypt.BCryptPasswordEncoder;
import org.springframework.stereotype.Service;

import com.mentelibre.auth_service.dto.RegisterUserDTO;
import com.mentelibre.auth_service.dto.UpdateUserDTO;
import com.mentelibre.auth_service.model.Rol;
import com.mentelibre.auth_service.model.User;
import com.mentelibre.auth_service.repository.RolRepository;
import com.mentelibre.auth_service.repository.UserRepository;

import jakarta.transaction.Transactional;

@Service
@Transactional
public class UserService implements UserDetailsService {

    @Autowired
    private RolRepository rolRepository;

    @Autowired
    public UserRepository userRepository;

    @Autowired
    private BCryptPasswordEncoder passwordEncoder;


    // === Spring Security ===
    @Override
    public UserDetails loadUserByUsername(String email) throws UsernameNotFoundException {

        User user = userRepository.findByEmail(email);

        if (user == null) {
            throw new UsernameNotFoundException("Usuario no encontrado: " + email);
        }

        // El rol ya viene como ROLE_ADMIN o ROLE_USER
        String role = user.getRol().getNombre();

        return new org.springframework.security.core.userdetails.User(
                user.getEmail(),
                user.getPassword(),
                Collections.singletonList(new SimpleGrantedAuthority(role))
        );
    }



    // === CRUD de usuarios ===

    public List<User> obtenerUser() {
        return userRepository.findAll();
    }

    public User obtenerUserPorId(Long id) {
        return userRepository.findById(id)
                .orElseThrow(() -> new RuntimeException("Usuario no encontrado Id: " + id));
    }

    public User obtenerUserPorEmail(String email) {
        User user = userRepository.findByEmail(email);
        if (user == null) {
            throw new RuntimeException("Usuario no encontrado: " + email);
        }
        return user;
    }



    // === Registrar usuario (solo rol USER desde el frontend) ===
    public User crearUser(RegisterUserDTO dto) {

        if (userRepository.existsByUsername(dto.getUsername()))
            throw new RuntimeException("El nombre de usuario ya está en uso");

        if (userRepository.existsByEmail(dto.getEmail()))
            throw new RuntimeException("El correo ya está en uso");

        // Siempre asignamos el rol USER en registro
        Rol rolUser = rolRepository.findByNombre("ROLE_USER");
        if (rolUser == null)
            throw new RuntimeException("El rol ROLE_USER no existe en la base de datos");

        User user = new User();
        user.setUsername(dto.getUsername());
        user.setEmail(dto.getEmail());
        user.setPhone(dto.getPhone());
        user.setPassword(passwordEncoder.encode(dto.getPassword()));
        user.setRol(rolUser);

        return userRepository.save(user);
    }

    // === Actualizar usuario (por admin o por el mismo usuario) ===
    public User actualizarUser(Long id, UpdateUserDTO dto) {

        User user = userRepository.findById(id)
                .orElseThrow(() -> new RuntimeException("Usuario no encontrado"));

        if (dto.getUsername() != null && !dto.getUsername().equals(user.getUsername())) {
            if (userRepository.existsByUsername(dto.getUsername()))
                throw new RuntimeException("Nombre de usuario ya en uso");
            user.setUsername(dto.getUsername());
        }

        if (dto.getEmail() != null && !dto.getEmail().equals(user.getEmail())) {
            if (userRepository.existsByEmail(dto.getEmail()))
                throw new RuntimeException("Correo ya en uso");
            user.setEmail(dto.getEmail());
        }

        if (dto.getPhone() != null) {
            user.setPhone(dto.getPhone());
        }

        if (dto.getRolId() != null) {
            Rol rol = rolRepository.findById(dto.getRolId())
                    .orElseThrow(() -> new RuntimeException("Rol no encontrado"));
            user.setRol(rol);
        }

        return userRepository.save(user);
    }

    // === Eliminar usuario ===
    public String eliminarUser(Long id) {

        User user = userRepository.findById(id)
                .orElseThrow(() -> new RuntimeException("Usuario no encontrado Id: " + id));

        if (id == 1) {
            throw new RuntimeException("No se puede eliminar este usuario base del sistema");
        }

        userRepository.delete(user);
        return "Usuario eliminado";
    }

    // === Cambiar contraseña ===
    public String cambiarPassword(Long userId, String oldPassword, String newPassword) {

        User user = userRepository.findById(userId)
                .orElseThrow(() -> new RuntimeException("Usuario no encontrado"));

        // Validar contraseña actual
        if (!passwordEncoder.matches(oldPassword, user.getPassword())) {
            throw new RuntimeException("La contraseña actual no es correcta");
        }

        // Guardar nueva contraseña
        user.setPassword(passwordEncoder.encode(newPassword));
        userRepository.save(user);

        return "Contraseña actualizada correctamente";
    }

}
