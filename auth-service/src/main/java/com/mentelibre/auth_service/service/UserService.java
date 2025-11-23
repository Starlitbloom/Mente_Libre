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
import com.mentelibre.auth_service.mapper.UserMapper;
import com.mentelibre.auth_service.model.Rol;
import com.mentelibre.auth_service.model.User;
import com.mentelibre.auth_service.repository.RolRepository;
import com.mentelibre.auth_service.repository.UserRepository;

import jakarta.transaction.Transactional;

@Service // Contiene la lógica del negocio
@Transactional // Deshace todos los cambios (rollback)

public class UserService implements UserDetailsService{
    @Autowired
    private RolRepository rolRepository;

    @Autowired
    public UserRepository userRepository;

    @Autowired
    private UserMapper mapper;

    @Autowired
    private BCryptPasswordEncoder passwordEncoder;

    public List<User> obtenerUser(){ // Metodo para obtener todos los usuarios
        return userRepository.findAll();
    }

    public User obtenerUserPorId(Long id){
        return userRepository.findById(id)
        .orElseThrow(()-> new RuntimeException("Usuario no encontrado Id: "+ id));
    }
    @Override
    public UserDetails loadUserByUsername(String email) throws UsernameNotFoundException {
        User user = userRepository.findByEmail(email);

        if (user == null) {
            throw new UsernameNotFoundException("Usuario no encontrado: " + email);
        }

        String rolSpring = "ROLE_" + user.getRol().getNombre().toUpperCase();

        return new org.springframework.security.core.userdetails.User(
            user.getEmail(),
            user.getPassword(),
            Collections.singletonList(new SimpleGrantedAuthority(rolSpring))
        );
    }
    
    // En UserService
    public User obtenerUserPorUsername(String username) {
        User user = userRepository.findByUsername(username);
        if (user == null) {
            throw new RuntimeException("Usuario no encontrado: " + username);
        }
        return user;
    }

    public String eliminarUser(Long id){
        User user = userRepository.findById(id)
        .orElseThrow(()-> new RuntimeException("Usuario no encontrado Id: "+ id));
       
        if (id == 1) {
            throw new RuntimeException("No se puede eliminar este usuario base del sistema");    
        }
        userRepository.delete(user);
        return "Usuario eliminado";
    }

    public User actualizarUser(Long id, UpdateUserDTO dto) {

        User user = userRepository.findById(id)
                .orElseThrow(() -> new RuntimeException("Usuario no encontrado"));

        // Validaciones de duplicados
        if (dto.getUsername() != null && !dto.getUsername().equals(user.getUsername())) {
            if (userRepository.existsByUsername(dto.getUsername()))
                throw new RuntimeException("Nombre de usuario ya en uso");
        }

        if (dto.getEmail() != null && !dto.getEmail().equals(user.getEmail())) {
            if (userRepository.existsByEmail(dto.getEmail()))
                throw new RuntimeException("Correo ya en uso");
        }

        Rol rol = null;
        if (dto.getRolId() != null) {
            rol = rolRepository.findById(dto.getRolId())
                    .orElseThrow(() -> new RuntimeException("Rol no encontrado"));
        }

        mapper.updateEntityFromDTO(user, dto, rol);

        return userRepository.save(user);
    }


    public User crearUser(RegisterUserDTO dto, Long rolId) {

        if (userRepository.existsByUsername(dto.getUsername()))
            throw new RuntimeException("El nombre de usuario ya está en uso");

        if (userRepository.existsByEmail(dto.getEmail()))
            throw new RuntimeException("El correo ya está en uso");

        Rol rol = rolRepository.findById(rolId)
                .orElseThrow(() -> new RuntimeException("Rol no encontrado"));

        User user = mapper.toEntity(dto);
        user.setPassword(passwordEncoder.encode(dto.getPassword()));
        user.setRol(rol);

        return userRepository.save(user);
    }

    public boolean autenticar(String email, String password){
        User user = userRepository.findByEmail(email); // Busca en la base de datos el username
        if (user == null) return false; // Usuario no existe
        return passwordEncoder.matches(password, user.getPassword()); // Verifica la contraseña
    }

    public User obtenerUserPorEmail(String email) {
        User user = userRepository.findByEmail(email);
        if (user == null) {
            throw new RuntimeException("Usuario no encontrado: " + email);
        }
        return user;
    }
}
