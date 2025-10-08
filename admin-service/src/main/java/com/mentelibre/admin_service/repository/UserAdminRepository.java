package com.mentelibre.admin_service.repository;

import com.mentelibre.admin_service.model.UserAdmin;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import java.util.List;
import java.util.Optional;

@Repository
public interface UserAdminRepository extends JpaRepository<UserAdmin, Long> {

    // Buscar usuario por username
    Optional<UserAdmin> findByUsername(String username);

    // Buscar usuario por email
    Optional<UserAdmin> findByEmail(String email);

    // Listar todos los usuarios bloqueados
    List<UserAdmin> findByBloqueadoTrue();

    // Contar usuarios bloqueados
    int countByBloqueadoTrue();

    // Listar todos los usuarios por rol
    List<UserAdmin> findByRolNombre(String rolNombre);
}

