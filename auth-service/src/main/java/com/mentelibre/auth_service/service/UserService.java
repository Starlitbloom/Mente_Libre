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
    private BCryptPasswordEncoder passwordEncoder;

    public List<User> obtenerUser(){ // Metodo para obtener todos los usuarios
        return userRepository.findAll();
    }

    public User obtenerUserPorId(Long id){
        return userRepository.findById(id)
        .orElseThrow(()-> new RuntimeException("Usuario no encontrado Id: "+ id));
    }
    @Override
    public UserDetails loadUserByUsername(String username) throws UsernameNotFoundException {
        User user = userRepository.findByUsername(username);

        if (user == null) {
            throw new UsernameNotFoundException("Usuario no encontrado: " + username);
        }

        String rolSpring = "ROLE_" + user.getRol().getNombre().toUpperCase();

        return new org.springframework.security.core.userdetails.User(
            user.getUsername(),
            user.getPassword(),
            Collections.singletonList(new SimpleGrantedAuthority(rolSpring))
        );
    }
    
    public String eliminarUser(Long id){
        User user = userRepository.findById(id)
        .orElseThrow(()-> new RuntimeException("Usuario no encontrado Id: "+ id));
       
        if (id == 1 || id == 2 || id == 3) {
            throw new RuntimeException("No se puede eliminar este usuario base del sistema");    
        }
        userRepository.delete(user);
        return "Usuario eliminado";
    }

    public User actualizarUser(Long id, String username, String email, String password, Long rolId) {
        User user = userRepository.findById(id)
        .orElseThrow(()-> new RuntimeException("Usuario no encontrado"));

        if (username != null && !username.trim().isEmpty() && !username.equals(user.getUsername())) {
            if (userRepository.existsByUsername(username)) {
                throw new RuntimeException("Ya existe un usuario con ese nombre");
            }
            user.setUsername(username);
        }


        if (email != null && !email.trim().isEmpty()) {
            if (userRepository.existsByEmail(email)) {
                throw new RuntimeException("Ya existe un usuario con ese correo");
            }
            user.setEmail(email);
        }

        if (password != null && !password.trim().isEmpty()) {
           user.setPassword(passwordEncoder.encode(password)); 
        }

        if (rolId != null) {
            Rol rol = rolRepository.findById(rolId)
            .orElseThrow(()-> new RuntimeException("Rol no encontrado"));
        user.setRol(rol);
        }
        return userRepository.save(user);
    }

    public User crearUser(String username, String email, String password, Long rolId){ // Metodo para crear un usuario
        if (username == null || email == null || password == null || rolId == null) {
            throw new RuntimeException("Usuario, correo, contraseña y rol son obligatorios");
        }   
        
        if (userRepository.existsByUsername(username)) {
            throw new RuntimeException("El nombre de usuario ya se encuentra en uso");
        }

        if (userRepository.existsByEmail(email)) {
            throw new RuntimeException("El correo electronico ya se encuentra en uso");
        }
            
        Rol rol = rolRepository.findById(rolId) // Verifica si el Rol existe
        .orElseThrow(()-> new RuntimeException("Rol no encontrado ID: "+ rolId));

        User user = new User();
        user.setUsername(username);
        user.setEmail(email);
        user.setPassword(passwordEncoder.encode(password));
        user.setRol(rol);
        return userRepository.save(user);

    }

    public boolean autenticar(String username, String password){
        User user = userRepository.findByUsername(username); // Busca en la base de datos el username
        if (user == null) return false; // Usuario no existe
        return passwordEncoder.matches(password, user.getPassword()); // Verifica la contraseña
    }
}
