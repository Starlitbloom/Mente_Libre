package com.mentelibre.auth_service.repository;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import com.mentelibre.auth_service.model.User;

@Repository //Se comunica con la base de datos
public interface UserRepository extends JpaRepository<User, Long> {
    User findByUsername(String username); // Metodo para buscar usuario por username
    User findByEmail(String email); // Metodo para buscar por correo electrónico
    boolean existsByUsername(String username); // Verificacion si ya existe con nombre de usuario
    boolean existsByEmail(String email); // Verificacion si ya existe con el correo
}